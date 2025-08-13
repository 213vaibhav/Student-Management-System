package com.scms.service;

public class UndoAction {
    private final Runnable undo;
    private final String description;

    public UndoAction(Runnable undo, String description) {
        this.undo = undo;
        this.description = description;
    }

    public void run() { undo.run(); }

    public String description() { return description; }
}
