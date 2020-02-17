package com.example.loginform.PersonModel;

public class PersonModel {
    private String Name,Email,Password,Phone,ImageURL;

    public PersonModel(String userName, String name, String email, String password, String phone, String imageURL) {
        Name = name;
        Email = email;
        Password = password;
        Phone = phone;
        ImageURL = imageURL;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }
}
