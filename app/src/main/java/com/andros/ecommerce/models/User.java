package com.andros.ecommerce.models;

public class User {
    public String images,name, email, password;

    public User(){

    }

    public User(String name, String email, String password) {
        this.images=null;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, String password,String images) {
        this.images = images;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
