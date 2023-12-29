package myApp.models;

import java.time.LocalDate;

public class User {
    private int userId;
    private String fname;
    private String lname;
    private String email;
    private String gender;
    private LocalDate dateOfBirth;
    private String country;

    public User(int userId, String fname, String lname, String email, String gender, LocalDate dateOfBirth, String country) {
        this.userId = userId;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.country = country;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
