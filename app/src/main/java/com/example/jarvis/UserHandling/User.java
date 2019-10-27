package com.example.jarvis.UserHandling;

public class User {
    private String email;
    private String password;
    // private int isSignedIN;

    public User(){

    }

    public User(String email, String password, int isSignedIN) {
        this.email = email;
        this.password = password;
        // this.isSignedIN = isSignedIN;
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

    /*public int getisSignedIN() {
        return isSignedIN;
    }*/

    /*public void setIsSignedIN (int isSignedIN) {
        this.isSignedIN = isSignedIN;
    }*/
}
