package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Accident implements Parcelable {
    private String id;          //问题id号
    private String orderId;     //工程编号
    private String type;        //问题类型
    private String state;       //工程状态
    private String handleName;  //负责人
    private String reason;      //问题描述
    private String problemName; //问题发起人
    private String problemtime; //问题发起时间

    public Accident() {
    }

    public Accident(String id, String orderId, String type, String state, String handleName, String reason, String problemName, String problemtime) {
        this.id = id;
        this.orderId = orderId;
        this.type = type;
        this.state = state;
        this.handleName = handleName;
        this.reason = reason;
        this.problemName = problemName;
        this.problemtime = problemtime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getHandleName() {
        return handleName;
    }

    public void setHandleName(String handleName) {
        this.handleName = handleName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public String getProblemtime() {
        return problemtime;
    }

    public void setProblemtime(String problemtime) {
        this.problemtime = problemtime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.orderId);
        dest.writeString(this.type);
        dest.writeString(this.state);
        dest.writeString(this.handleName);
        dest.writeString(this.reason);
        dest.writeString(this.problemName);
        dest.writeString(this.problemtime);
    }

    protected Accident(Parcel in) {
        this.id = in.readString();
        this.orderId = in.readString();
        this.type = in.readString();
        this.state = in.readString();
        this.handleName = in.readString();
        this.reason = in.readString();
        this.problemName = in.readString();
        this.problemtime = in.readString();
    }

    public static final Parcelable.Creator<Accident> CREATOR = new Parcelable.Creator<Accident>() {
        public Accident createFromParcel(Parcel source) {
            return new Accident(source);
        }

        public Accident[] newArray(int size) {
            return new Accident[size];
        }
    };
}
