package com.proiect_is.model;

public class Pets {
    private int pet_id;
    private int user_id;
    private String name;
    private String description;

    public Pets(int pet_id, int user_id, String name, String description) {
        this.pet_id = pet_id;
        this.user_id = user_id;
        this.name = name;
        this.description = description;
    }

    public int getPet_id() {
        return pet_id;
    }

    public void setPet_id(int pet_id) {
        this.pet_id = pet_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

