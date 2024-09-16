package com.example.homelandernotes.model;

public class Task {

    public enum Status {
        PENDING, IN_PROGRESS, COMPLETED
    }

    private String title;
    private String description;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private Status status;  // Thêm thuộc tính trạng thái

    public Task(String title, String description, int startHour, int startMinute, int endHour, int endMinute, Status status) {
        this.title = title;
        this.description = description;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.status = status;  // Khởi tạo trạng thái
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
