package com.gzrijing.workassistant.entity;

public class Marker {
    private String id;
    private double latitude;
    private double longitude;
    private String type;    //管网维护四种类型：供水阀门井，供水消防栓，供水管刷油，污水井
    private String area;
    private String address;

    public Marker() {
    }

    public Marker(String id, double latitude, double longitude, String type, String area, String address) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.area = area;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
