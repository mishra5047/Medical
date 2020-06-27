package com.example.nirogo;

public class MyAppointments {

    String drName, Date, Time, mode;

    public MyAppointments() {
    }

    public MyAppointments(String drName, String date, String time, String mode) {
        this.drName = drName;
        Date = date;
        Time = time;
        this.mode = mode;

    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDrName() {
        return drName;
    }

    public void setDrName(String drName) {
        this.drName = drName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
