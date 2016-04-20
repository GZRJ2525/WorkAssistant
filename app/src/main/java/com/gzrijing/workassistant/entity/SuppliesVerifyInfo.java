package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class SuppliesVerifyInfo implements Parcelable {
    private String name;
    private String spec;
    private String unit;
    private String applyNum;
    private String sendNum;

    public SuppliesVerifyInfo() {
    }

    public SuppliesVerifyInfo(String name, String spec, String unit, String applyNum, String sendNum) {
        this.name = name;
        this.spec = spec;
        this.unit = unit;
        this.applyNum = applyNum;
        this.sendNum = sendNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(String applyNum) {
        this.applyNum = applyNum;
    }

    public String getSendNum() {
        return sendNum;
    }

    public void setSendNum(String sendNum) {
        this.sendNum = sendNum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.spec);
        dest.writeString(this.unit);
        dest.writeString(this.applyNum);
        dest.writeString(this.sendNum);
    }

    protected SuppliesVerifyInfo(Parcel in) {
        this.name = in.readString();
        this.spec = in.readString();
        this.unit = in.readString();
        this.applyNum = in.readString();
        this.sendNum = in.readString();
    }

    public static final Creator<SuppliesVerifyInfo> CREATOR = new Creator<SuppliesVerifyInfo>() {
        public SuppliesVerifyInfo createFromParcel(Parcel source) {
            return new SuppliesVerifyInfo(source);
        }

        public SuppliesVerifyInfo[] newArray(int size) {
            return new SuppliesVerifyInfo[size];
        }
    };
}
