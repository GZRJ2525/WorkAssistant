package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class LeaderMachineReturnBill implements Parcelable {
    private String billNo;          //单据编号
    private String billType;        //单据类型
    private String orderId;         //工程编号
    private String returnAddress;   //退机地点
    private String returnDate;      //退机时间
    private String applyName;       //申请人
    private String applyDate;       //申请时间
    private String remark;          //备注
    private String state;           //状态（未审核，已审核，不通过）
    private ArrayList<LeaderMachineReturnBillByMachine> machineList = new ArrayList<LeaderMachineReturnBillByMachine>(); //退机单的机械列表

    public LeaderMachineReturnBill() {
    }

    public LeaderMachineReturnBill(String billNo, String billType, String orderId, String returnAddress, String returnDate, String applyName, String applyDate, String remark, String state, ArrayList<LeaderMachineReturnBillByMachine> machineList) {
        this.billNo = billNo;
        this.billType = billType;
        this.orderId = orderId;
        this.returnAddress = returnAddress;
        this.returnDate = returnDate;
        this.applyName = applyName;
        this.applyDate = applyDate;
        this.remark = remark;
        this.state = state;
        this.machineList = machineList;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReturnAddress() {
        return returnAddress;
    }

    public void setReturnAddress(String returnAddress) {
        this.returnAddress = returnAddress;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getApplyName() {
        return applyName;
    }

    public void setApplyName(String applyName) {
        this.applyName = applyName;
    }

    public String getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(String applyDate) {
        this.applyDate = applyDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ArrayList<LeaderMachineReturnBillByMachine> getMachineList() {
        return machineList;
    }

    public void setMachineList(ArrayList<LeaderMachineReturnBillByMachine> machineList) {
        this.machineList = machineList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.billNo);
        dest.writeString(this.billType);
        dest.writeString(this.orderId);
        dest.writeString(this.returnAddress);
        dest.writeString(this.returnDate);
        dest.writeString(this.applyName);
        dest.writeString(this.applyDate);
        dest.writeString(this.remark);
        dest.writeString(this.state);
        dest.writeTypedList(machineList);
    }

    protected LeaderMachineReturnBill(Parcel in) {
        this.billNo = in.readString();
        this.billType = in.readString();
        this.orderId = in.readString();
        this.returnAddress = in.readString();
        this.returnDate = in.readString();
        this.applyName = in.readString();
        this.applyDate = in.readString();
        this.remark = in.readString();
        this.state = in.readString();
        this.machineList = in.createTypedArrayList(LeaderMachineReturnBillByMachine.CREATOR);
    }

    public static final Creator<LeaderMachineReturnBill> CREATOR = new Creator<LeaderMachineReturnBill>() {
        public LeaderMachineReturnBill createFromParcel(Parcel source) {
            return new LeaderMachineReturnBill(source);
        }

        public LeaderMachineReturnBill[] newArray(int size) {
            return new LeaderMachineReturnBill[size];
        }
    };
}
