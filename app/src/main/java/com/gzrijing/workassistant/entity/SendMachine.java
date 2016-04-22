package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class SendMachine implements Parcelable {
    private String sendId;
    private String orderId;         //工程单号
    private String billNo;          //机械申请单号
    private String getAddress;      //取机地址
    private String sendAddress;     //用机械地址
    private String machineNo;       //机械编号
    private String machineName;     //机械名称
    private String applyName;       //申请人
    private String sendData;        //发放时间
    private String machineNum;      //机械数量
    private String remark;          //备注 yycq添加

    public SendMachine() {
    }

    public SendMachine(String sendId, String orderId, String billNo, String getAddress, String sendAddress, String machineNo, String machineName, String applyName, String sendData, String machineNum,String remark) {//remark yycq添加
        this.sendId = sendId;
        this.orderId = orderId;
        this.billNo = billNo;
        this.getAddress = getAddress;
        this.sendAddress = sendAddress;
        this.machineNo = machineNo;
        this.machineName = machineName;
        this.applyName = applyName;
        this.sendData = sendData;
        this.machineNum = machineNum;
        this.remark = remark;//yycq 添加
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

    public String getGetAddress() {
        return getAddress;
    }

    public void setGetAddress(String getAddress) {
        this.getAddress = getAddress;
    }

    public String getSendAddress() {
        return sendAddress;
    }

    public void setSendAddress(String sendAddress) {
        this.sendAddress = sendAddress;
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

    public String getApplyName() {
        return applyName;
    }

    public void setApplyName(String applyName) {
        this.applyName = applyName;
    }

    public String getSendData() {
        return sendData;
    }

    public void setSendData(String sendData) {
        this.sendData = sendData;
    }

    public String getMachineNum() {
        return machineNum;
    }

    public void setMachineNum(String machineNum) {
        this.machineNum = machineNum;
    }

    public void setRemark(String remark){this.remark = remark;};// yycq 添加

    public String  getRemark(){return remark;};// yycq 添加
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sendId);
        dest.writeString(this.orderId);
        dest.writeString(this.billNo);
        dest.writeString(this.getAddress);
        dest.writeString(this.sendAddress);
        dest.writeString(this.machineNo);
        dest.writeString(this.machineName);
        dest.writeString(this.applyName);
        dest.writeString(this.sendData);
        dest.writeString(this.machineNum);
        dest.writeString(this.remark);//yycq 添加
    }

    protected SendMachine(Parcel in) {
        this.sendId = in.readString();
        this.orderId = in.readString();
        this.billNo = in.readString();
        this.getAddress = in.readString();
        this.sendAddress = in.readString();
        this.machineNo = in.readString();
        this.machineName = in.readString();
        this.applyName = in.readString();
        this.sendData = in.readString();
        this.machineNum = in.readString();
        this.remark = in.readString();// yycq 添加
    }

    public static final Creator<SendMachine> CREATOR = new Creator<SendMachine>() {
        public SendMachine createFromParcel(Parcel source) {
            return new SendMachine(source);
        }

        public SendMachine[] newArray(int size) {
            return new SendMachine[size];
        }
    };
}
