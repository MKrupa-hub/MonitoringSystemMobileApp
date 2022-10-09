package com.example.monitoringsystem.Patient;

public class Temperature {

    private String login;
    private float temperature;
    private long timestamp;

    public Temperature() {

    }

    public Temperature(String login, float temperature, long timestamp) {
        this.login = login;
        this.temperature = temperature;
        this.timestamp = timestamp;
    }

    public String getLogin() {
        return login;
    }

    public float getTemperature() {
        return temperature;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
