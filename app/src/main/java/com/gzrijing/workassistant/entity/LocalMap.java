package com.gzrijing.workassistant.entity;

public class LocalMap {
    private int cityId;
    private String cityName;
    private int size;
    private int ratio;

    public LocalMap() {
    }

    public LocalMap(int cityId, String cityName, int size, int ratio) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.size = size;
        this.ratio = ratio;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }
}
