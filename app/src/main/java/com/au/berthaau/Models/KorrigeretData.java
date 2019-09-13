package com.au.berthaau.Models;

import java.util.Date;

public class KorrigeretData {

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public double getPM25() {
        return PM25;
    }

    public void setPM25(double PM25) {
        this.PM25 = PM25;
    }

    public double getPM10() {
        return PM10;
    }

    public void setPM10(double PM10) {
        this.PM10 = PM10;
    }

    public double getNO2() {
        return NO2;
    }

    public void setNO2(double NO2) {
        this.NO2 = NO2;
    }

    public double getO3() {
        return O3;
    }

    public void setO3(double o3) {
        O3 = o3;
    }

    public String getSensorId() {
        return SensorId;
    }

    public void setSensorId(String sensorId) {
        SensorId = sensorId;
    }

    public int getKorrektionIdNO2() {
        return KorrektionIdNO2;
    }

    public void setKorrektionIdNO2(int korrektionIdNO2) {
        KorrektionIdNO2 = korrektionIdNO2;
    }

    public int getKorrektionIdO3() {
        return KorrektionIdO3;
    }

    public void setKorrektionIdO3(int korrektionIdO3) {
        KorrektionIdO3 = korrektionIdO3;
    }

    public int getKorrektionIdPM25() {
        return KorrektionIdPM25;
    }

    public void setKorrektionIdPM25(int korrektionIdPM25) {
        KorrektionIdPM25 = korrektionIdPM25;
    }

    public int getKorrektionIdPM10() {
        return KorrektionIdPM10;
    }

    public void setKorrektionIdPM10(int korrektionIdPM10) {
        KorrektionIdPM10 = korrektionIdPM10;
    }

    public Date getTidspunkt() {
        return Tidspunkt;
    }

    public void setTidspunkt(Date tidspunkt) {
        Tidspunkt = tidspunkt;
    }

    public KorrigeretData(){

    }

    public KorrigeretData(int ID, Date tidspunkt, double PM25, double PM10, double NO2, double o3, String sensorId, int korrektionIdNO2, int korrektionIdO3, int korrektionIdPM25, int korrektionIdPM10) {
        this.ID = ID;
        this.Tidspunkt = tidspunkt;
        this.PM25 = PM25;
        this.PM10 = PM10;
        this.NO2 = NO2;
        O3 = o3;
        SensorId = sensorId;
        KorrektionIdNO2 = korrektionIdNO2;
        KorrektionIdO3 = korrektionIdO3;
        KorrektionIdPM25 = korrektionIdPM25;
        KorrektionIdPM10 = korrektionIdPM10;
    }


    private int ID;
    private Date Tidspunkt;
    private double PM25;
    private double PM10;
    private double NO2;
    private double O3;
    private String SensorId;
    private int KorrektionIdNO2;
    private int KorrektionIdO3;
    private int KorrektionIdPM25;
    private int KorrektionIdPM10;


}
