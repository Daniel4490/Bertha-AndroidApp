package com.au.berthaau.Models;

import java.util.Date;

public class Position {

    public String getTidspunkt() {
        return Tidspunkt;
    }

    public void setTidspunkt(String tidspunkt) {
        Tidspunkt = tidspunkt;
    }

    public double getLængdegrad() {
        return Længdegrad;
    }

    public void setLængdegrad(double længdegrad) {
        Længdegrad = længdegrad;
    }

    public double getBreddegrad() {
        return Breddegrad;
    }

    public void setBreddegrad(double breddegrad) {
        Breddegrad = breddegrad;
    }

    public String getSensorId() {
        return SensorId;
    }

    public void setSensorId(String sensorId) {
        SensorId = sensorId;
    }

    public Position(){

    }

    public Position(String tidspunkt, double længdegrad, double breddegrad, String sensorId) {
        Tidspunkt = tidspunkt;
        Længdegrad = længdegrad;
        Breddegrad = breddegrad;
        SensorId = sensorId;
    }

    private String Tidspunkt;
    private double Længdegrad;
    private double Breddegrad;
    private String SensorId;

}
