package com.scms.app;

import com.scms.model.Student;
import com.scms.service.CourseService;
import com.scms.service.StudentService;
import com.scms.service.UndoAction;
import com.scms.store.DataStore;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class SCMSApp {

    public static void main(String[] args) {
        DataStore db = new DataStore();
        db.seed();

        StudentService studentSvc = new StudentService(db);
        CourseService courseSvc = new CourseService(db);

        Scanner sc = new Scanner(System.in);
        System.out.println("== Student Course Management System (SCMS) ==");
        boolean running = true;

        while (running) {
            System.out.println("""
                    \nMenu:
                    1) List Students (sorted)
                    2) Add Student
                    3) List Courses
                    4) Add Course
                    5) Assign Instructor to Course
                    6) Enroll Student in Course
                    7) Unenroll Student from Course
                    8) Course Report
                    9) Undo Last Action
                    0) Exit
                    """);
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> {
                        List<Student> sorted = studentSvc.listSorted();
                        System.out.println("Students:");
                        sorted.forEach(s -> System.out.println(" - " + s));
                    }
                    case "2" -> {
                        System.out.print("Name: ");
                        String name = sc.nextLine();
                        System.out.print("Email: ");
                        String email = sc.nextLine();
                        System.out.print("Roll number: ");
                        String roll = sc.nextLine();
                        Student s = studentSvc.addStudent(name, email, roll);
                        System.out.println("Added: " + s);
                    }
                    case "3" -> {
                        System.out.println("Courses:");
                        courseSvc.listCourses().forEach(c -> System.out.println(" - " + c));
                    }
                    case "4" -> {
                        System.out.print("Course code: ");
                        String code = sc.nextLine();
                        System.out.print("Course name: ");
                        String name = sc.nextLine();
                        System.out.print("Capacity: ");
                        int cap = Integer.parseInt(sc.nextLine());
                        var c = courseSvc.addCourse(code, name, cap);
                        System.out.println("Added: " + c);
                    }
                    case "5" -> {
                        System.out.print("Course code: ");
                        String code = sc.nextLine();
                        System.out.print("Instructor ID: ");
                        int iid = Integer.parseInt(sc.nextLine());
                        courseSvc.assignInstructor(code, iid);
                        System.out.println("Instructor assigned.");
                    }
                    case "6" -> {
                        System.out.print("Course code: ");
                        String code = sc.nextLine();
                        System.out.print("Student ID: ");
                        int sid = Integer.parseInt(sc.nextLine());
                        String res = courseSvc.enroll(code, sid);
                        System.out.println("Result: " + res);
                    }
                    case "7" -> {
                        System.out.print("Course code: ");
                        String code = sc.nextLine();
                        System.out.print("Student ID: ");
                        int sid = Integer.parseInt(sc.nextLine());
                        boolean ok = courseSvc.unenroll(code, sid);
                        System.out.println(ok ? "Unenrolled." : "Not found.");
                    }
                    case "8" -> {
                        System.out.print("Course code: ");
                        String code = sc.nextLine();
                        courseSvc.reportStudentsInCourse(code).forEach(System.out::println);
                    }
                    case "9" -> {
                        Optional<UndoAction> act = courseSvc.popUndo();
                        if (act.isPresent()) {
                            System.out.println("Undo: " + act.get().description());
                            act.get().run();
                            System.out.println("Undone.");
                        } else {
                            System.out.println("Nothing to undo.");
                        }
                    }
                    case "0" -> running = false;
                    default -> System.out.println("Invalid choice");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }

        System.out.println("Goodbye.");
    }
}
