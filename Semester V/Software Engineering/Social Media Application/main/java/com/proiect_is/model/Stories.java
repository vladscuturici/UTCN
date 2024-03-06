package com.proiect_is.model;
import java.sql.Timestamp;

public class Stories {
    private int story_id;
    private int user_id;
    private Timestamp post_time;
    private String storyDescription;
    public Stories(){}
    public Stories(int story_id, int user_id, Timestamp post_time, String storyDescription) {
        this.story_id = story_id;
        this.user_id = user_id;
        this.post_time = post_time;
        this.storyDescription = storyDescription;
    }

    // Getters
    public int getUserId() {
        return user_id;
    }

    public int getStoryId() {
        return story_id;
    }

    public Timestamp getPostTime() {
        return post_time;
    }

    public String getStoryDescription() {
        return storyDescription;
    }

    // Setters
    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public void setStoryId(int story_id) {
        this.story_id = story_id;
    }

    public void setPostTime(Timestamp post_time) {
        this.post_time = post_time;
    }

    public void setStoryDescription(String storyDescription) {
        this.storyDescription = storyDescription;
    }
}
