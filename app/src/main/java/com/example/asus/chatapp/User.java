package com.example.asus.chatapp;

public class User {

    public String image;
    public String name;
    public String status;
    public String thumb_image;
    public String key;

    public User() {

    }

    public User(String image, String name, String status, String thumb_image) {
        this.image = image;
        this.name = name;
        this.status = status;
        this.thumb_image = thumb_image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumbImage() {
        return thumb_image;
    }

    public void setThumbImage(String thumb_image) {
        this.thumb_image = thumb_image;
    }
}
