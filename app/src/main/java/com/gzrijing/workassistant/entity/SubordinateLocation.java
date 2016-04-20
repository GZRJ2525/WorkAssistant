package com.gzrijing.workassistant.entity;

public class SubordinateLocation {
    private double latitude;        //纬度
    private double longitude;       //经度
    private String userNo;          //下属账号
    private String name;            //姓名
    private String tel;             //电话
    private String lastTime;        //最后坐标记录时间
    private String doingTask;       //正在处理的任务

    public SubordinateLocation() {
    }

    public SubordinateLocation(double latitude, double longitude, String userNo, String name, String tel, String lastTime, String doingTask) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.userNo = userNo;
        this.name = name;
        this.tel = tel;
        this.lastTime = lastTime;
        this.doingTask = doingTask;
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

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getDoingTask() {
        return doingTask;
    }

    public void setDoingTask(String doingTask) {
        this.doingTask = doingTask;
    }
}
