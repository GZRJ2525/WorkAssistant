package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Inspection implements Parcelable {
    private String No;          //编号
    private String name;        //名称
    private String model;       //型号
    private String address;     //地址
    private double latitude;    //纬度
    private double longitude;   //经度
    private String valveNo;     //阀门型号
    private String valveGNo;    //阀门井型号
    private String type;        //1是阀门 0是消防栓
    private String checkFlag;   //1是已检查，0未检查

    public Inspection() {
    }

    public Inspection(String no, String name, String model, String address, double latitude, double longitude, String valveNo, String valveGNo, String type, String checkFlag) {
        No = no;
        this.name = name;
        this.model = model;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.valveNo = valveNo;
        this.valveGNo = valveGNo;
        this.type = type;
        this.checkFlag = checkFlag;
    }

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getValveNo() {
        return valveNo;
    }

    public void setValveNo(String valveNo) {
        this.valveNo = valveNo;
    }

    public String getValveGNo() {
        return valveGNo;
    }

    public void setValveGNo(String valveGNo) {
        this.valveGNo = valveGNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCheckFlag() {
        return checkFlag;
    }

    public void setCheckFlag(String checkFlag) {
        this.checkFlag = checkFlag;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.No);
        dest.writeString(this.name);
        dest.writeString(this.model);
        dest.writeString(this.address);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.valveNo);
        dest.writeString(this.valveGNo);
        dest.writeString(this.type);
        dest.writeString(this.checkFlag);
    }

    protected Inspection(Parcel in) {
        this.No = in.readString();
        this.name = in.readString();
        this.model = in.readString();
        this.address = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.valveNo = in.readString();
        this.valveGNo = in.readString();
        this.type = in.readString();
        this.checkFlag = in.readString();
    }

    public static final Creator<Inspection> CREATOR = new Creator<Inspection>() {
        public Inspection createFromParcel(Parcel source) {
            return new Inspection(source);
        }

        public Inspection[] newArray(int size) {
            return new Inspection[size];
        }
    };
}
