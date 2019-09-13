package com.au.berthaau.Models;

public class Sensorer {
    private int ID;
    private String SensorId;
    private long Telefon;

    public Sensorer(){

    }

    public Sensorer(int ID, String sensorId, long telefon) {
        this.ID = ID;
        SensorId = sensorId;
        Telefon = telefon;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getSensorId() {
        return SensorId;
    }

    public void setSensorId(String sensorId) {
        SensorId = sensorId;
    }

    public long getTelefon() {
        return Telefon;
    }

    public void setTelefon(int telefon) {
        Telefon = telefon;
    }
}
