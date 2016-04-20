package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class LeaderMachineApplyBillByMachine implements Parcelable {
    private String name;        //机械名称
    private String unit;        //机械单位
    private String applyNum;    //申请数量
    private String sendNum;     //安排数量

    public LeaderMachineApplyBillByMachine() {
    }

    public LeaderMachineApplyBillByMachine(String name, String unit, String applyNum, String sendNum) {
        this.name = name;
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
        dest.writeString(this.unit);
        dest.writeString(this.applyNum);
        dest.writeString(this.sendNum);
    }

    protected LeaderMachineApplyBillByMachine(Parcel in) {
        this.name = in.readString();
        this.unit = in.readString();
        this.applyNum = in.readString();
        this.sendNum = in.readString();
    }

    public static final Parcelable.Creator<LeaderMachineApplyBillByMachine> CREATOR = new Parcelable.Creator<LeaderMachineApplyBillByMachine>() {
        public LeaderMachineApplyBillByMachine createFromParcel(Parcel source) {
            return new LeaderMachineApplyBillByMachine(source);
        }

        public LeaderMachineApplyBillByMachine[] newArray(int size) {
            return new LeaderMachineApplyBillByMachine[size];
        }
    };
}
