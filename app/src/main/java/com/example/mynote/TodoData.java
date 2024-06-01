package com.example.mynote;

public class TodoData {
    int id;
    String title, todo;
    boolean completed;

    public TodoData(int id, String title, String todo, boolean completed) {
        this.id = id;
        this.title = title;
        this.todo = todo;
        this.completed = completed;
    }

    public TodoData() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
