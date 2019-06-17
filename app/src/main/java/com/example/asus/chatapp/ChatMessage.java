package com.example.asus.chatapp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ChatMessage {
    private String userName;
    private String message;
    private String deliveryTime;
    private String image;

    public ChatMessage(String userName, String message, String deliveryTime, String image) {
        this.userName = userName;
        this.message = message;
        this.deliveryTime = deliveryTime;
        this.image = image;
    }

    public ChatMessage() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

//    public CharSequence getDeliveryTime() {
//        return deliveryTime  + "";
//    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
