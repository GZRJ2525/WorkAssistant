package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Machine implements Parcelable {
    private String applyId;     //申请单号
    private String id;          //机械编号
    private String name;        //机械名称
    private String unit;        //机械单位
    private int applyNum;       //申请数量
    private int sendNum;        //安排数量
    private String state;       //机械状态(查询时的机械状态，已安排，已送达)

    public Machine() {
    }

    public Machine(String applyId, String id, String name, String unit, int applyNum, int sendNum, String state) {
        this.applyId = applyId;
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.applyNum = applyNum;
        this.sendNum = sendNum;
        this.state = state;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(int applyNum) {
        this.applyNum = applyNum;
    }

    public int getSendNum() {
        return sendNum;
    }

    public void setSendNum(int sendNum) {
        this.sendNum = sendNum;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.applyId);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.unit);
        dest.writeInt(this.applyNum);
        dest.writeInt(this.sendNum);
        dest.writeString(this.state);
    }

    protected Machine(Parcel in) {
        this.applyId = in.readString();
        this.id = in.readString();
        this.name = in.readString();
        this.unit = in.readString();
        this.applyNum = in.readInt();
        this.sendNum = in.readInt();
        this.state = in.readString();
    }

    public static final Creator<Machine> CREATOR = new Creator<Machine>() {
        public Machine createFromParcel(Parcel source) {
            return new Machine(source);
        }

        public Machine[] newArray(int size) {
            return new Machine[size];
        }
    };
}
