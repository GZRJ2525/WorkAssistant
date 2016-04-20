package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Subordinate implements Parcelable {
    private String userNo;
    private String name;
    private boolean isCheck;
    private ArrayList<BusinessHaveSendByAll> businessList = new ArrayList<BusinessHaveSendByAll>();

    public Subordinate() {
    }

    public Subordinate(String userNo, String name, boolean isCheck, ArrayList<BusinessHaveSendByAll> businessList) {
        this.userNo = userNo;
        this.name = name;
        this.isCheck = isCheck;
        this.businessList = businessList;
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

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public ArrayList<BusinessHaveSendByAll> getBusinessList() {
        return businessList;
    }

    public void setBusinessList(ArrayList<BusinessHaveSendByAll> businessList) {
        this.businessList = businessList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userNo);
        dest.writeString(this.name);
        dest.writeByte(isCheck ? (byte) 1 : (byte) 0);
        dest.writeTypedList(businessList);
    }

    protected Subordinate(Parcel in) {
        this.userNo = in.readString();
        this.name = in.readString();
        this.isCheck = in.readByte() != 0;
        this.businessList = in.createTypedArrayList(BusinessHaveSendByAll.CREATOR);
    }

    public static final Creator<Subordinate> CREATOR = new Creator<Subordinate>() {
        public Subordinate createFromParcel(Parcel source) {
            return new Subordinate(source);
        }

        public Subordinate[] newArray(int size) {
            return new Subordinate[size];
        }
    };
}
