package com.example.monitoringsystem.Patient;

public class Pressure {

    private String login;
    private String date;
    private String time;
    private int pressureS;
    private int pressureR;
    private int pulse;

    public Pressure() {
    }

    public Pressure(String login, String date, String time, int pressureS, int pressureR, int pulse) {
        this.login = login;
        this.date = date;
        this.time = time;
        this.pressureS = pressureS;
        this.pressureR = pressureR;
        this.pulse = pulse;
    }


    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getPressureS() {
        return pressureS;
    }

    public int getPressureR() {
        return pressureR;
    }

    public int getPulse() {
        return pulse;
    }

    public String getLogin() {
        return login;
    }
}
