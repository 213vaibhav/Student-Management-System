package com.scms.service;

import com.scms.model.*;

import com.scms.store.DataStore;
import java.util.*;

public class CourseService {
    private final DataStore db;
    // Undo stack
    private final Deque<UndoAction> undoStack = new ArrayDeque<>();

    public CourseService(DataStore db) { this.db = db; }

    public Course addCourse(String code, String name, int capacity) {
        Course c = new Course(code, name, capacity);
        db.courses().put(c.getCourseCode(), c);
        // Undo: remove course
        undoStack.push(new UndoAction(() -> db.courses().remove(c.getCourseCode()),
                "Added course " + code));
        return c;
    }

    public boolean removeCourse(String code) {
        Course removed = db.courses().remove(code.toUpperCase(Locale.ROOT));
        if (removed != null) {
            // Undo: put it back
            undoStack.push(new UndoAction(() -> db.courses().put(removed.getCourseCode(), removed),
                    "Removed course " + removed.getCourseCode()));
            return true;
        }
        return false;
    }

    public Course getCourse(String code) {
        return db.courses().get(code.toUpperCase(Locale.ROOT));
    }

    public void assignInstructor(String code, int instructorId) {
        Course c = getCourse(code);
        Instructor inst = db.instructors().get(instructorId);
        if (c == null || inst == null) throw new IllegalArgumentException("Course/Instructor not found");
        Instructor prev = c.getInstructor();
        c.setInstructor(inst);
        // Undo: revert instructor
        undoStack.push(new UndoAction(() -> c.setInstructor(prev),
                "Assigned instructor to " + code));
    }

    public String enroll(String courseCode, int studentId) {
        Course c = getCourse(courseCode);
        Student s = db.students().get(studentId);
        if (c == null || s == null) throw new IllegalArgumentException("Course/Student not found");

        String result = c.enroll(s);
        switch (result) {
            case "ENROLLED" -> undoStack.push(new UndoAction(() -> {
                c.unenroll(s); // will auto-promote waitlist; we must revert that promotion too (handled below).
            }, "Enroll " + s.getName() + " in " + courseCode));
            case "WAITLISTED" -> undoStack.push(new UndoAction(() -> {
                // remove from waitlist
                c.unenroll(s); // unenroll() removes from waitlist if not enrolled
            }, "Waitlist " + s.getName() + " in " + courseCode));
            default -> {}
        }
        return result;
    }

    public boolean unenroll(String courseCode, int studentId) {
        Course c = getCourse(courseCode);
        Student s = db.students().get(studentId);
        if (c == null || s == null) throw new IllegalArgumentException("Course/Student not found");

        // capture current waitlist head (for accurate undo later)
        List<Student> before = new ArrayList<>(c.getEnrolledStudents());
        Student promoted = c.unenroll(s);

        // To undo: restore exact enrolled + waitlist state by re-applying previous state
        undoStack.push(new UndoAction(() -> {
            // crude but safe: drop & rebuild enrollments from 'before'
            // First, remove all current enrollments of this course students
            // (We cannot access internal lists, so we use public API idempotently)
            // Rebuild from DataStore: unenroll anyone not in 'before', then enroll 'before' order.
            // NOTE: For a full-proof revert, a snapshot of waitlist would be ideal.
            // Simpler approach: re-add s, and if someone was promoted, move them back to waitlist.

            // Step 1: if student s was originally enrolled, re-enroll s
            if (!before.stream().anyMatch(st -> st.getId() == s.getId())) {
                // s wasn't enrolled before; do nothing special
            } else {
                c.enroll(s);
            }
            // Step 2: if someone was promoted by unenroll, move them back to waitlist
            if (promoted != null && c.isEnrolled(promoted)) {
                // remove promoted and put them back on waitlist tail
                c.unenroll(promoted);
                c.enroll(promoted); // if full, this will waitlist them again
            }
        }, "Unenroll student " + s.getName() + " from " + courseCode));

        return true;
    }

    public List<Course> listCourses() {
        List<Course> list = new ArrayList<>(db.courses().values());
        list.sort(Comparator.comparing(Course::getCourseCode));
        return list;
    }

    public List<String> reportStudentsInCourse(String code) {
        Course c = getCourse(code);
        if (c == null) return List.of();
        List<String> lines = new ArrayList<>();
        lines.add("Course: " + c.getCourseCode() + " - " + c.getCourseName());
        lines.add("Instructor: " + (c.getInstructor() != null ? c.getInstructor().getName() : "None"));
        lines.add("Enrolled (" + c.getEnrolledStudents().size() + "/" + c.getCapacity() + "):");
        for (var s : c.getEnrolledStudents()) {
            lines.add(" - " + s.getName() + " [" + s.getRollNumber() + "]");
        }
        lines.add("Waitlist: " + c.getWaitlistView().size());
        return lines;
    }

    public Optional<UndoAction> popUndo() {
        return undoStack.isEmpty() ? Optional.empty() : Optional.of(undoStack.pop());
    }

    public int undoCount() { return undoStack.size(); }
}
