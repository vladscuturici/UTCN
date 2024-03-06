package com.proiect_is.model;

public class Notifications {
    private int notificationId;
    private int userId;
    private String text;

    public Notifications(int notificationId, int userId, String text) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.text = text;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}