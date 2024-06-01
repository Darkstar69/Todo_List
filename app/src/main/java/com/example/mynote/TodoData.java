package com.example.mynote;

public class TodoData {
    int id;
    String todo;
    boolean completed;

    public TodoData(int id, String todo, boolean completed) {
        this.id = id;
        this.todo = todo;
        this.completed = completed;
    }

    public TodoData() {
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }
}
