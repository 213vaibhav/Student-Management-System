package com.scms.model;

public class Instructor extends Person {
    private final String employeeId;

    public Instructor(int id, String name, String email, String employeeId) {
        super(id, name, email);
        this.employeeId = employeeId;
    }

    public String getEmployeeId() { return employeeId; }

    @Override
    public String toString() {
        return super.toString().replace("}", ", employeeId='%s'}".formatted(employeeId));
    }
}
