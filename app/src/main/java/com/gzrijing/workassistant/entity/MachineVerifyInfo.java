package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class MachineVerifyInfo implements Parcelable {
    private String name;
    private String applyNum;
    private String sendNum;

    public MachineVerifyInfo() {
    }

    public MachineVerifyInfo(String name, String applyNum, String sendNum) {
        this.name = name;
        this.applyNum = applyNum;
        this.sendNum = sendNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        dest.writeString(this.applyNum);
        dest.writeString(this.sendNum);
    }

    protected MachineVerifyInfo(Parcel in) {
        this.name = in.readString();
        this.applyNum = in.readString();
        this.sendNum = in.readString();
    }

    public static final Creator<MachineVerifyInfo> CREATOR = new Creator<MachineVerifyInfo>() {
        public MachineVerifyInfo createFromParcel(Parcel source) {
            return new MachineVerifyInfo(source);
        }

        public MachineVerifyInfo[] newArray(int size) {
            return new MachineVerifyInfo[size];
        }
    };
}
