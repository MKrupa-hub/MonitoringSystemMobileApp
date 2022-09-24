package com.example.monitoringsystem.Patient;

public class Temperature {

    private String login;
    private String date;
    private String time;
    private float temperature;

    public Temperature() {

    }

    public Temperature(String login, String date, String time, float temperature) {
        this.login = login;
        this.date = date;
        this.time = time;
        this.temperature = temperature;
    }

    public String getLogin() {
        return login;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public float getTemperature() {
        return temperature;
    }
}
