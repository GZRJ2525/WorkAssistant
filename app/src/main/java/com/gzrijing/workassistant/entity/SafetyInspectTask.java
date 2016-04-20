package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class SafetyInspectTask implements Parcelable {
    private String id;          //工程编号
    private String name;        //工程名称
    private String type;        //工程类型
    private double longitude;   //经度
    private double latitude;    //纬度
    private String formId;      //巡查表ID

    public SafetyInspectTask() {
    }

    public SafetyInspectTask(String id, String name, String type, double longitude, double latitude, String formId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.longitude = longitude;
        this.latitude = latitude;
        this.formId = formId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeString(this.formId);
    }

    protected SafetyInspectTask(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.type = in.readString();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.formId = in.readString();
    }

    public static final Creator<SafetyInspectTask> CREATOR = new Creator<SafetyInspectTask>() {
        public SafetyInspectTask createFromParcel(Parcel source) {
            return new SafetyInspectTask(source);
        }

        public SafetyInspectTask[] newArray(int size) {
            return new SafetyInspectTask[size];
        }
    };
}
