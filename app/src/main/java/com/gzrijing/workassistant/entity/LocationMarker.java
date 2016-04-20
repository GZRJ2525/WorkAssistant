package com.gzrijing.workassistant.entity;

public class LocationMarker {
    private String name;
    private String time;
    private String tel;
    private double latitude;
    private double longitude;

    public LocationMarker() {
    }

    public LocationMarker(String name, String time, String tel, double latitude, double longitude) {
        this.name = name;
        this.time = time;
        this.tel = tel;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
