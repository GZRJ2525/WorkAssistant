package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class QueryProjectAmount implements Parcelable {
    private String id;
    private String feeType;         //收费性质 (客户，水务）
    private String content;         //施工内容
    private String civil;           //土建项目
    private String approvalName;    //审核人
    private String approvalTime;    //审核时间
    private String state;           //状态（未审核，已审核，不通过）

    public QueryProjectAmount() {
    }

    public QueryProjectAmount(String id, String feeType, String content, String civil, String approvalName, String approvalTime, String state) {
        this.id = id;
        this.feeType = feeType;
        this.content = content;
        this.civil = civil;
        this.approvalName = approvalName;
        this.approvalTime = approvalTime;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCivil() {
        return civil;
    }

    public void setCivil(String civil) {
        this.civil = civil;
    }

    public String getApprovalName() {
        return approvalName;
    }

    public void setApprovalName(String approvalName) {
        this.approvalName = approvalName;
    }

    public String getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(String approvalTime) {
        this.approvalTime = approvalTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.feeType);
        dest.writeString(this.content);
        dest.writeString(this.civil);
        dest.writeString(this.approvalName);
        dest.writeString(this.approvalTime);
        dest.writeString(this.state);
    }

    protected QueryProjectAmount(Parcel in) {
        this.id = in.readString();
        this.feeType = in.readString();
        this.content = in.readString();
        this.civil = in.readString();
        this.approvalName = in.readString();
        this.approvalTime = in.readString();
        this.state = in.readString();
    }

    public static final Parcelable.Creator<QueryProjectAmount> CREATOR = new Parcelable.Creator<QueryProjectAmount>() {
        public QueryProjectAmount createFromParcel(Parcel source) {
            return new QueryProjectAmount(source);
        }

        public QueryProjectAmount[] newArray(int size) {
            return new QueryProjectAmount[size];
        }
    };
}
