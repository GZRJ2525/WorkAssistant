package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class LeaderMachineState implements Parcelable {
    private String machineNo;
    private String machineName;
    private String machineUnit;
    private String state;
    private String address;
    private String businessNo;//yycq 添加 使用该机械的项目编号


    public LeaderMachineState() {
    }

    public LeaderMachineState(String machineNo, String machineName, String machineUnit, String state, String address,String businessNo) {//yycq 添加businessNo
        this.machineNo = machineNo;
        this.machineName = machineName;
        this.machineUnit = machineUnit;
        this.state = state;
        this.address = address;
        this.businessNo = businessNo;////yycq 添加 businessNo
    }

    public String getMachineNo() {
        return machineNo;
    }

    public void setMachineNo(String machineNo) {
        this.machineNo = machineNo;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getMachineUnit() {
        return machineUnit;
    }

    public void setMachineUnit(String machineUnit) {
        this.machineUnit = machineUnit;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBusinessNo(String businessNo) {
        this.businessNo = businessNo;
    }//yycq 添加

    public String getBusinessNo() {
        return businessNo;
    }//yycq 添加


    @Override
    public int describeContents() {
        return 0;
    }//【

    @Override
    public void writeToParcel(Parcel dest, int flags) {//【
        dest.writeString(this.businessNo);//yycq 添加
        dest.writeString(this.machineNo);
        dest.writeString(this.machineName);
        dest.writeString(this.machineUnit);
        dest.writeString(this.state);
        dest.writeString(this.address);
    }

    protected LeaderMachineState(Parcel in) {//【
        this.businessNo = in.readString();//yycq 添加
        this.machineNo = in.readString();
        this.machineName = in.readString();
        this.machineUnit = in.readString();
        this.state = in.readString();
        this.address = in.readString();
    }

    public static final Parcelable.Creator<LeaderMachineState> CREATOR = new Parcelable.Creator<LeaderMachineState>() {//【
        public LeaderMachineState createFromParcel(Parcel source) {
            return new LeaderMachineState(source);
        }

        public LeaderMachineState[] newArray(int size) {
            return new LeaderMachineState[size];
        }//【

    };
}
