package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class BusinessHaveSendByAll implements Parcelable {
    private String orderId;             //工程Id
    private String branchId;            //工程支线Id
    private String workerName;          //施工员姓名
    private String workContent;         //施工内容
    private String deadline;            //工程期限

    public BusinessHaveSendByAll() {
    }

    public BusinessHaveSendByAll(String orderId, String branchId, String workerName, String workContent, String deadline) {
        this.orderId = orderId;
        this.branchId = branchId;
        this.workerName = workerName;
        this.workContent = workContent;
        this.deadline = deadline;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getWorkContent() {
        return workContent;
    }

    public void setWorkContent(String workContent) {
        this.workContent = workContent;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.orderId);
        dest.writeString(this.branchId);
        dest.writeString(this.workerName);
        dest.writeString(this.workContent);
        dest.writeString(this.deadline);
    }

    protected BusinessHaveSendByAll(Parcel in) {
        this.orderId = in.readString();
        this.branchId = in.readString();
        this.workerName = in.readString();
        this.workContent = in.readString();
        this.deadline = in.readString();
    }

    public static final Parcelable.Creator<BusinessHaveSendByAll> CREATOR = new Parcelable.Creator<BusinessHaveSendByAll>() {
        public BusinessHaveSendByAll createFromParcel(Parcel source) {
            return new BusinessHaveSendByAll(source);
        }

        public BusinessHaveSendByAll[] newArray(int size) {
            return new BusinessHaveSendByAll[size];
        }
    };
}
