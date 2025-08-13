package com.scms.service;

import com.scms.model.Student;
import com.scms.store.DataStore;

import java.util.*;

public class StudentService {
    private final DataStore db;

    public StudentService(DataStore db) { this.db = db; }

    public Student addStudent(String name, String email, String roll) {
        int id = db.nextId();
        Student s = new Student(id, name, email, roll);
        db.students().put(id, s);
        return s;
    }

    public boolean removeStudent(int id) {
        return db.students().remove(id) != null;
    }

    public Student get(int id) { return db.students().get(id); }

    public List<Student> listSorted() {
        TreeSet<Student> sorted = new TreeSet<>(db.students().values()); // TreeSet keeps sorted
        return new ArrayList<>(sorted);
    }

    public Collection<Student> all() { return db.students().values(); }
}
