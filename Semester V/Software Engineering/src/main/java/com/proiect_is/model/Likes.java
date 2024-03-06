package com.proiect_is.model;

public class Likes {
    private int post_id;
    private int user_id;

    public Likes(int post_id, int user_id) {
        this.post_id = post_id;
        this.user_id = user_id;
    }

    public int getPostId() {
        return post_id;
    }

    public void setPostId(int post_id) {
        this.post_id = post_id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }
}