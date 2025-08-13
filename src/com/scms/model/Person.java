package com.scms.model;

public abstract class Person {
    private final int id;
    private String name;
    private String email;

    protected Person(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "%s{id=%d, name='%s', email='%s'}".formatted(
                this.getClass().getSimpleName(), id, name, email);
    }
}
