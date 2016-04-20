package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MachineNo implements Parcelable {
    private String applyId;         //机械申请单号
    private String returnId;        //机械退回单号
    private String applyTime;       //机械申请时间
    private String useTime;         //机械使用时间
    private String returnTime;      //机械预计退回时间
    private String useAddress;      //机械使用地点
    private String remarks;         //备注
    private String approvalTime;    //机械单审批时间
    private String applyState;      //机械申请状态（申请中，已审批，不批准）
    private String returnState;     //机械退回状态（申请中，已安排，已退回）
    private String returnType;      //机械退回性质（正常，损坏）
    private String returnApplyTime; //机械退回申请时间
    private String returnAddress;   //机械退回地点
    private String reason;          //不批准原因
    private ArrayList<Machine> machineList = new ArrayList<Machine>();

    public MachineNo() {
    }

    public MachineNo(String applyId, String returnId, String applyTime, String useTime, String returnTime, String useAddress, String remarks, String approvalTime, String applyState, String returnState, String returnType, String returnApplyTime, String returnAddress, String reason, ArrayList<Machine> machineList) {
        this.applyId = applyId;
        this.returnId = returnId;
        this.applyTime = applyTime;
        this.useTime = useTime;
        this.returnTime = returnTime;
        this.useAddress = useAddress;
        this.remarks = remarks;
        this.approvalTime = approvalTime;
        this.applyState = applyState;
        this.returnState = returnState;
        this.returnType = returnType;
        this.returnApplyTime = returnApplyTime;
        this.returnAddress = returnAddress;
        this.reason = reason;
        this.machineList = machineList;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public String getReturnId() {
        return returnId;
    }

    public void setReturnId(String returnId) {
        this.returnId = returnId;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public String getUseAddress() {
        return useAddress;
    }

    public void setUseAddress(String useAddress) {
        this.useAddress = useAddress;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(String approvalTime) {
        this.approvalTime = approvalTime;
    }

    public String getApplyState() {
        return applyState;
    }

    public void setApplyState(String applyState) {
        this.applyState = applyState;
    }

    public String getReturnState() {
        return returnState;
    }

    public void setReturnState(String returnState) {
        this.returnState = returnState;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getReturnApplyTime() {
        return returnApplyTime;
    }

    public void setReturnApplyTime(String returnApplyTime) {
        this.returnApplyTime = returnApplyTime;
    }

    public String getReturnAddress() {
        return returnAddress;
    }

    public void setReturnAddress(String returnAddress) {
        this.returnAddress = returnAddress;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ArrayList<Machine> getMachineList() {
        return machineList;
    }

    public void setMachineList(ArrayList<Machine> machineList) {
        this.machineList = machineList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.applyId);
        dest.writeString(this.returnId);
        dest.writeString(this.applyTime);
        dest.writeString(this.useTime);
        dest.writeString(this.returnTime);
        dest.writeString(this.useAddress);
        dest.writeString(this.remarks);
        dest.writeString(this.approvalTime);
        dest.writeString(this.applyState);
        dest.writeString(this.returnState);
        dest.writeString(this.returnType);
        dest.writeString(this.returnApplyTime);
        dest.writeString(this.returnAddress);
        dest.writeString(this.reason);
        dest.writeTypedList(machineList);
    }

    protected MachineNo(Parcel in) {
        this.applyId = in.readString();
        this.returnId = in.readString();
        this.applyTime = in.readString();
        this.useTime = in.readString();
        this.returnTime = in.readString();
        this.useAddress = in.readString();
        this.remarks = in.readString();
        this.approvalTime = in.readString();
        this.applyState = in.readString();
        this.returnState = in.readString();
        this.returnType = in.readString();
        this.returnApplyTime = in.readString();
        this.returnAddress = in.readString();
        this.reason = in.readString();
        this.machineList = in.createTypedArrayList(Machine.CREATOR);
    }

    public static final Creator<MachineNo> CREATOR = new Creator<MachineNo>() {
        public MachineNo createFromParcel(Parcel source) {
            return new MachineNo(source);
        }

        public MachineNo[] newArray(int size) {
            return new MachineNo[size];
        }
    };
}
