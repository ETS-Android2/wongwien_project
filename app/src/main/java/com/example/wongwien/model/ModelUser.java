package com.example.wongwien.model;

public class ModelUser {
    private String uid;
    private String name;
    private String email;
    private String image;
    private String cover_image;

    public ModelUser() {
    }

    public ModelUser(String uid, String name, String email, String image, String cover_image) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.image = image;
        this.cover_image = cover_image;
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

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    @Override
    public String toString() {
        return "ModelUser{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", image='" + image + '\'' +
                ", cover_image='" + cover_image + '\'' +
                '}';
    }
}
