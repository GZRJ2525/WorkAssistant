package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ReturnMachine implements Parcelable {
    private String sendId;
    private String orderId;         //工程单号
    private String billNo;          //机械申请单号
    private String sendAddress;     //送取机械地址
    private String address;         //取机械地址
    private String type;            //退回类型
    private String machineNo;       //机械编号
    private String machineName;     //机械名称
    private String machineNum;      //机械数量
    private String returnName;      //退机人
    private String returnTiem;      //退机时间

    public ReturnMachine() {
    }

    public ReturnMachine(String sendId, String orderId, String billNo, String sendAddress, String address, String type, String machineNo, String machineName, String machineNum, String returnName, String returnTiem) {
        this.sendId = sendId;
        this.orderId = orderId;
        this.billNo = billNo;
        this.sendAddress = sendAddress;
        this.address = address;
        this.type = type;
        this.machineNo = machineNo;
        this.machineName = machineName;
        this.machineNum = machineNum;
        this.returnName = returnName;
        this.returnTiem = returnTiem;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getSendAddress() {
        return sendAddress;
    }

    public void setSendAddress(String sendAddress) {
        this.sendAddress = sendAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getMachineNum() {
        return machineNum;
    }

    public void setMachineNum(String machineNum) {
        this.machineNum = machineNum;
    }

    public String getReturnName() {
        return returnName;
    }

    public void setReturnName(String returnName) {
        this.returnName = returnName;
    }

    public String getReturnTiem() {
        return returnTiem;
    }

    public void setReturnTiem(String returnTiem) {
        this.returnTiem = returnTiem;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sendId);
        dest.writeString(this.orderId);
        dest.writeString(this.billNo);
        dest.writeString(this.sendAddress);
        dest.writeString(this.address);
        dest.writeString(this.type);
        dest.writeString(this.machineNo);
        dest.writeString(this.machineName);
        dest.writeString(this.machineNum);
        dest.writeString(this.returnName);
        dest.writeString(this.returnTiem);
    }

    protected ReturnMachine(Parcel in) {
        this.sendId = in.readString();
        this.orderId = in.readString();
        this.billNo = in.readString();
        this.sendAddress = in.readString();
        this.address = in.readString();
        this.type = in.readString();
        this.machineNo = in.readString();
        this.machineName = in.readString();
        this.machineNum = in.readString();
        this.returnName = in.readString();
        this.returnTiem = in.readString();
    }

    public static final Creator<ReturnMachine> CREATOR = new Creator<ReturnMachine>() {
        @Override
        public ReturnMachine createFromParcel(Parcel source) {
            return new ReturnMachine(source);
        }

        @Override
        public ReturnMachine[] newArray(int size) {
            return new ReturnMachine[size];
        }
    };
}
