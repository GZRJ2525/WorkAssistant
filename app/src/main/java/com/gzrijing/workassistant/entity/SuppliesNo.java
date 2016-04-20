package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class SuppliesNo implements Parcelable {
    private String applyId;         //申请单号
    private String receivedId;      //发放单号
    private String returnId;        //退回单号
    private String applyState;      //申请状态 (申请中，已审批，不批准)
    private String receivedState;   //发放状态 (可领用，已领出)
    private String returnState;     //退回状态 (申请中，可退回，已退回)
    private String applyTime;       //申请时间
    private String useTime;         //使用时间
    private String approvalTime;    //审批时间
    private String receivedTime;    //领料时间
    private String returnTime;      //退回申请时间
    private String reason;          //不批准原因
    private String remarks;         //备注
    private ArrayList<Supplies> suppliesList = new ArrayList<Supplies>();   //材料

    public SuppliesNo() {
    }

    public SuppliesNo(String applyId, String receivedId, String returnId, String applyState, String receivedState, String returnState, String applyTime, String useTime, String approvalTime, String receivedTime, String returnTime, String reason, String remarks, ArrayList<Supplies> suppliesList) {
        this.applyId = applyId;
        this.receivedId = receivedId;
        this.returnId = returnId;
        this.applyState = applyState;
        this.receivedState = receivedState;
        this.returnState = returnState;
        this.applyTime = applyTime;
        this.useTime = useTime;
        this.approvalTime = approvalTime;
        this.receivedTime = receivedTime;
        this.returnTime = returnTime;
        this.reason = reason;
        this.remarks = remarks;
        this.suppliesList = suppliesList;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public String getReceivedId() {
        return receivedId;
    }

    public void setReceivedId(String receivedId) {
        this.receivedId = receivedId;
    }

    public String getReturnId() {
        return returnId;
    }

    public void setReturnId(String returnId) {
        this.returnId = returnId;
    }

    public String getApplyState() {
        return applyState;
    }

    public void setApplyState(String applyState) {
        this.applyState = applyState;
    }

    public String getReceivedState() {
        return receivedState;
    }

    public void setReceivedState(String receivedState) {
        this.receivedState = receivedState;
    }

    public String getReturnState() {
        return returnState;
    }

    public void setReturnState(String returnState) {
        this.returnState = returnState;
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

    public String getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(String approvalTime) {
        this.approvalTime = approvalTime;
    }

    public String getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(String receivedTime) {
        this.receivedTime = receivedTime;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public ArrayList<Supplies> getSuppliesList() {
        return suppliesList;
    }

    public void setSuppliesList(ArrayList<Supplies> suppliesList) {
        this.suppliesList = suppliesList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.applyId);
        dest.writeString(this.receivedId);
        dest.writeString(this.returnId);
        dest.writeString(this.applyState);
        dest.writeString(this.receivedState);
        dest.writeString(this.returnState);
        dest.writeString(this.applyTime);
        dest.writeString(this.useTime);
        dest.writeString(this.approvalTime);
        dest.writeString(this.receivedTime);
        dest.writeString(this.returnTime);
        dest.writeString(this.reason);
        dest.writeString(this.remarks);
        dest.writeTypedList(suppliesList);
    }

    protected SuppliesNo(Parcel in) {
        this.applyId = in.readString();
        this.receivedId = in.readString();
        this.returnId = in.readString();
        this.applyState = in.readString();
        this.receivedState = in.readString();
        this.returnState = in.readString();
        this.applyTime = in.readString();
        this.useTime = in.readString();
        this.approvalTime = in.readString();
        this.receivedTime = in.readString();
        this.returnTime = in.readString();
        this.reason = in.readString();
        this.remarks = in.readString();
        this.suppliesList = in.createTypedArrayList(Supplies.CREATOR);
    }

    public static final Creator<SuppliesNo> CREATOR = new Creator<SuppliesNo>() {
        public SuppliesNo createFromParcel(Parcel source) {
            return new SuppliesNo(source);
        }

        public SuppliesNo[] newArray(int size) {
            return new SuppliesNo[size];
        }
    };
}
