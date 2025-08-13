package com.scms.model;

import java.util.*;

public class Course {
    private final String courseCode;
    private String courseName;
    private int capacity;

    private Instructor instructor;
    private final List<Student> enrolledStudents = new ArrayList<>();
    private final Deque<Student> waitlist = new ArrayDeque<>(); // Queue behavior

    public Course(String courseCode, String courseName, int capacity) {
        this.courseCode = courseCode.toUpperCase(Locale.ROOT);
        this.courseName = courseName;
        this.capacity = capacity;
    }

    public String getCourseCode() { return courseCode; }

    public String getCourseName() { return courseName; }

    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getCapacity() { return capacity; }

    public void setCapacity(int capacity) { this.capacity = capacity; }

    public Instructor getInstructor() { return instructor; }

    public void setInstructor(Instructor instructor) { this.instructor = instructor; }

    public List<Student> getEnrolledStudents() { return Collections.unmodifiableList(enrolledStudents); }

    public Collection<Student> getWaitlistView() {
        return Collections.unmodifiableCollection(waitlist);
    }

    public boolean isFull() { return enrolledStudents.size() >= capacity; }

    public boolean isEnrolled(Student s) {
        return enrolledStudents.stream().anyMatch(x -> x.getId() == s.getId());
    }

    public boolean isWaitlisted(Student s) {
        return waitlist.stream().anyMatch(x -> x.getId() == s.getId());
    }

    /** Enrolls or waitlists the student. Returns "ENROLLED" or "WAITLISTED" or "ALREADY". */
    public String enroll(Student s) {
        if (isEnrolled(s)) return "ALREADY";
        if (!isFull()) {
            enrolledStudents.add(s);
            enrolledStudents.sort(Comparator.naturalOrder()); // Keep sorted by name
            return "ENROLLED";
        } else if (!isWaitlisted(s)) {
            waitlist.addLast(s);
            return "WAITLISTED";
        } else {
            return "ALREADY";
        }
    }

    /** Unenrolls student if present; if waitlist has someone, auto-promote them. Returns promoted student or null. */
    public Student unenroll(Student s) {
        boolean removed = enrolledStudents.removeIf(x -> x.getId() == s.getId());
        if (removed) {
            if (!waitlist.isEmpty()) {
                Student promoted = waitlist.removeFirst();
                enrolledStudents.add(promoted);
                enrolledStudents.sort(Comparator.naturalOrder());
                return promoted;
            }
        } else {
            // If not enrolled, try removing from waitlist
            waitlist.removeIf(x -> x.getId() == s.getId());
        }
        return null;
    }

    @Override
    public String toString() {
        return "Course{code='%s', name='%s', capacity=%d, instructor=%s, enrolled=%d, waitlist=%d}"
                .formatted(courseCode, courseName, capacity,
                        instructor != null ? instructor.getName() : "None",
                        enrolledStudents.size(), waitlist.size());
    }
}
