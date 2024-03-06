package com.proiect_is.model;

import java.sql.Timestamp;

public class Messages {
    private int message_id;
    private int sender_id;
    private int receiver_id;
    private String message_text;
    private Timestamp time;

    public Messages(int message_id, int sender_id, int receiver_id, String message_text, Timestamp time) {
        this.message_id = message_id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.message_text = message_text;
        this.time = time;
    }
    public Messages(int sender_id, int receiver_id, String message_text) {
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.message_text = message_text;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}