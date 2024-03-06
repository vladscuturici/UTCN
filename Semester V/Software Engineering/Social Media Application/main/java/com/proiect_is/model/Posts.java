package com.proiect_is.model;

import java.sql.Timestamp;

public class Posts {
    private int id;
    private int user_id;
    private int pet_id;
    private String description;
    private int photo_id;
    private String photo_url;
    private int like_count;
    private Timestamp post_time;
    public Posts(){}

    public Posts(int id, int user_id, int pet_id, String description, int photo_id, String photo_url, int like_count, Timestamp post_time) {
        this.id = id;
        this.user_id = user_id;
        this.pet_id = pet_id;
        this.description = description;
        this.photo_id = photo_id;
        this.photo_url = photo_url;
        this.like_count = like_count;
        this.post_time = post_time;
    }

    // Getteri și setteri pentru câmpurile clasei
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getPet_id() {
        return pet_id;
    }

    public void setPet_id(int pet_id) {
        this.pet_id = pet_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(int photo_id) {
        this.photo_id = photo_id;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public Timestamp getPost_time() {
        return post_time;
    }

    public void setPost_time(Timestamp post_time) {
        this.post_time = post_time;}
}