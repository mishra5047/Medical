package com.example.nirogo.User;

public class UserUploadInfo {

    public String name;
    public String age;
    public String city;
    public String imageUrl;
    public String id, number;

    public UserUploadInfo() {
    }

    public UserUploadInfo(String id, String name, String age, String city, String imageName, String number) {
        this.name = name;
        this.id = id;
        this.age = age;
        this.city = city;
        this.imageUrl = imageName;
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}