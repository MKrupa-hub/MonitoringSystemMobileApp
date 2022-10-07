package com.example.monitoringsystem.Doctor;

import java.util.Objects;

public class Patient {

    private String pesel;

    public Patient(String pesel) {
        this.pesel = pesel;
    }

    public Patient() {
    }

    public String getPesel() {
        return pesel;
    }

    @Override
    public String toString() {
        return pesel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return pesel.equals(patient.pesel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pesel);
    }
}
