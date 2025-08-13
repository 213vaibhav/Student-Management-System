package com.scms.model;

public class Student extends Person implements Comparable<Student> {
    private final String rollNumber;

    public Student(int id, String name, String email, String rollNumber) {
        super(id, name, email);
        this.rollNumber = rollNumber;
    }

    public String getRollNumber() { return rollNumber; }

    @Override
    public String toString() {
        return super.toString().replace("}", ", rollNumber='%s'}".formatted(rollNumber));
    }

    @Override
    public int compareTo(Student o) {
        // Sort by name, then roll
        int byName = this.getName().compareToIgnoreCase(o.getName());
        return byName != 0 ? byName : this.rollNumber.compareToIgnoreCase(o.rollNumber);
    }
}
