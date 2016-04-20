package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class LeaderMachineApplyBill implements Parcelable {
    private String billNo;          //单据编号
    private String billType;        //单据类型
    private String orderId;         //工程编号
    private String useAddress;      //使用地点
    private String useDate;         //使用时间
    private String finishDate;      //预计归还日期
    private String applyName;       //申请人
    private String applyDate;       //申请时间
    private String remark;          //备注
    private String state;           //状态（未审核，已审核，不通过）
    private ArrayList<LeaderMachineApplyBillByMachine> machineList = new ArrayList<LeaderMachineApplyBillByMachine>(); //申请单的机械列表

    public LeaderMachineApplyBill() {
    }

    public LeaderMachineApplyBill(String billNo, String billType, String orderId, String useAddress, String useDate, String finishDate, String applyName, String applyDate, String remark, String state, ArrayList<LeaderMachineApplyBillByMachine> machineList) {
        this.billNo = billNo;
        this.billType = billType;
        this.orderId = orderId;
        this.useAddress = useAddress;
        this.useDate = useDate;
        this.finishDate = finishDate;
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

    public String getUseAddress() {
        return useAddress;
    }

    public void setUseAddress(String useAddress) {
        this.useAddress = useAddress;
    }

    public String getUseDate() {
        return useDate;
    }

    public void setUseDate(String useDate) {
        this.useDate = useDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
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

    public ArrayList<LeaderMachineApplyBillByMachine> getMachineList() {
        return machineList;
    }

    public void setMachineList(ArrayList<LeaderMachineApplyBillByMachine> machineList) {
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
        dest.writeString(this.useAddress);
        dest.writeString(this.useDate);
        dest.writeString(this.finishDate);
        dest.writeString(this.applyName);
        dest.writeString(this.applyDate);
        dest.writeString(this.remark);
        dest.writeString(this.state);
        dest.writeTypedList(machineList);
    }

    protected LeaderMachineApplyBill(Parcel in) {
        this.billNo = in.readString();
        this.billType = in.readString();
        this.orderId = in.readString();
        this.useAddress = in.readString();
        this.useDate = in.readString();
        this.finishDate = in.readString();
        this.applyName = in.readString();
        this.applyDate = in.readString();
        this.remark = in.readString();
        this.state = in.readString();
        this.machineList = in.createTypedArrayList(LeaderMachineApplyBillByMachine.CREATOR);
    }

    public static final Creator<LeaderMachineApplyBill> CREATOR = new Creator<LeaderMachineApplyBill>() {
        public LeaderMachineApplyBill createFromParcel(Parcel source) {
            return new LeaderMachineApplyBill(source);
        }

        public LeaderMachineApplyBill[] newArray(int size) {
            return new LeaderMachineApplyBill[size];
        }
    };
}
