package com.proiect_is.model;

public class Friends {
    private int friendship_id, user1_id,user2_id;

    public Friends (int friendship_id,int user1_id,int user2_id){
        this.friendship_id=friendship_id;
        this.user1_id=user1_id;
        this.user2_id=user2_id;

    }

    public Friends (int user1_id,int user2_id){
        this.user1_id=user1_id;
        this.user2_id=user2_id;

    }

    public int getUser1_id() {
        return user1_id;
    }

    public int getFriendship_id() {
        return friendship_id;
    }

    public int getUser2_id() {
        return user2_id;
    }

    public void setUser1_id(int user1_id) {
        this.user1_id = user1_id;
    }

    public void setUser2_id(int user2_id) {
        this.user2_id = user2_id;
    }


}