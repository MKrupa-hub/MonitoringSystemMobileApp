package com.example.monitoringsystem;

public class User {

    public String name;
    public String surname;
    public int phone;
    public String login;
    public String password;
    public double pesel;

    public User() {
    }

    public User(String name, String surname, int phone, String login, String password, double pesel) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.login = login;
        this.password = password;
        this.pesel = pesel;
    }

}
