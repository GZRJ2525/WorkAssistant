package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ProblemType implements Parcelable {
    private String type;        //问题类型
    private String userNo;      //该问题负责人

    public ProblemType() {
    }

    public ProblemType(String type, String userNo) {
        this.type = type;
        this.userNo = userNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.userNo);
    }

    protected ProblemType(Parcel in) {
        this.type = in.readString();
        this.userNo = in.readString();
    }

    public static final Parcelable.Creator<ProblemType> CREATOR = new Parcelable.Creator<ProblemType>() {
        public ProblemType createFromParcel(Parcel source) {
            return new ProblemType(source);
        }

        public ProblemType[] newArray(int size) {
            return new ProblemType[size];
        }
    };
}
