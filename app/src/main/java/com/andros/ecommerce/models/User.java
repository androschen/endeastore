package com.andros.ecommerce.models;

import java.util.ArrayList;

public class User {
    private String images,name, email, password;
    private Double money;
    private ArrayList<UserAddress> userAddresses;

    public User(){

    }

    public User(String name, String email, String password) {
        this.money=0.0;
        this.images=null;
        this.name = name;
        this.email = email;
        this.password = password;
        this.userAddresses = new ArrayList<>();
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

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public ArrayList<UserAddress> getUserAddresses() {
        return userAddresses;
    }

    public void setUserAddresses(ArrayList<UserAddress> userAddresses) {
        this.userAddresses = userAddresses;
    }
}
