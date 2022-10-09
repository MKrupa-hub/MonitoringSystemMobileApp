package com.example.monitoringsystem.Patient;

public class Pressure {

    private String login;
    private int pressureS;
    private int pressureR;
    private int pulse;
    private long timestamp;

    public Pressure() {
    }

    public Pressure(String login, int pressureS, int pressureR, int pulse, long timestamp) {
        this.login = login;
        this.pressureS = pressureS;
        this.pressureR = pressureR;
        this.pulse = pulse;
        this.timestamp = timestamp;
    }


    public long getTimestamp() {
        return timestamp;
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
