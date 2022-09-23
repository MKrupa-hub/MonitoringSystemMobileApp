package com.example.monitoringsystem;

public class User {

    private String name;
    private String surname;
    private int phone;
    private String login;
    private String password;
    private long pesel;

    public User() {
    }

    public User(String name, String surname, int phone, String login, String password, long pesel) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.login = login;
        this.password = password;
        this.pesel = pesel;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getPhone() {
        return phone;
    }

    public String getLogin() {
        return login;
    }

    public long getPesel() {
        return pesel;
    }
}
