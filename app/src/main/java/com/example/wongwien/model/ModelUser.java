package com.example.wongwien.model;

public class ModelUser {
    private String uid,name,email,image,status,cover_image,typingStatus;

    public ModelUser() {
    }

    public ModelUser(String uid, String name, String email, String image, String status, String cover_image, String typingStatus) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.image = image;
        this.status = status;
        this.cover_image = cover_image;
        this.typingStatus = typingStatus;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getTypingStatus() {
        return typingStatus;
    }

    public void setTypingStatus(String typingStatus) {
        this.typingStatus = typingStatus;
    }
}
