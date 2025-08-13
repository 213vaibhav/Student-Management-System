package com.scms.store;

import com.scms.model.*;

import java.util.*;

public class DataStore {
    // Quick lookups (HashMap)
    private final Map<Integer, Student> students = new HashMap<>();
    private final Map<Integer, Instructor> instructors = new HashMap<>();
    private final Map<String, Course> courses = new HashMap<>();

    // ID generators for demo
    private int nextPersonId = 1000;

    public int nextId() { return nextPersonId++; }

    public Map<Integer, Student> students() { return students; }
    public Map<Integer, Instructor> instructors() { return instructors; }
    public Map<String, Course> courses() { return courses; }

    // Seed with a few records for quick testing
    public void seed() {
        Instructor i1 = new Instructor(nextId(), "Dr. Bhathal", "bhathal@uni.edu", "I-001");
        Instructor i2 = new Instructor(nextId(), "Prof. Kaur", "kaur@uni.edu", "I-002");

        instructors.put(i1.getId(), i1);
        instructors.put(i2.getId(), i2);

        Student s1 = new Student(nextId(), "Dakshdeep Singh", "daksh@mail.com", "CS22-001");
        Student s2 = new Student(nextId(), "Udaibir Singh Bhathal", "udaibir@mail.com", "CS22-002");
        Student s3 = new Student(nextId(), "Vaibhav Goyal", "goyal@mail.com", "CS22-003");
        students.put(s1.getId(), s1);
        students.put(s2.getId(), s2);
        students.put(s3.getId(), s3);

        Course c1 = new Course("CS101", "Data Structures", 2);
        Course c2 = new Course("CS102", "OOP with Java", 2);
        c1.setInstructor(i1);
        c2.setInstructor(i2);

        courses.put(c1.getCourseCode(), c1);
        courses.put(c2.getCourseCode(), c2);
    }
}
