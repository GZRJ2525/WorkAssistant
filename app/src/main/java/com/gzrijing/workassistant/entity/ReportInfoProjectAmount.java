package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ReportInfoProjectAmount implements Parcelable {
    private String id;
    private String feeType;         //收费性质 （客户， 水务）
    private String content;         //施工内容
    private String civil;           //土建内容
    private String state;           //状态（未审核，已审核，不通过）
    private String reportName;      //汇报人
    private String reportDate;      //汇报日期
    private String checkData;       //审核日期

    public ReportInfoProjectAmount() {
    }

    public ReportInfoProjectAmount(String id, String feeType, String content, String civil, String state, String reportName, String reportDate, String checkData) {
        this.id = id;
        this.feeType = feeType;
        this.content = content;
        this.civil = civil;
        this.state = state;
        this.reportName = reportName;
        this.reportDate = reportDate;
        this.checkData = checkData;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getCheckData() {
        return checkData;
    }

    public void setCheckData(String checkData) {
        this.checkData = checkData;
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
        dest.writeString(this.state);
        dest.writeString(this.reportName);
        dest.writeString(this.reportDate);
        dest.writeString(this.checkData);
    }

    protected ReportInfoProjectAmount(Parcel in) {
        this.id = in.readString();
        this.feeType = in.readString();
        this.content = in.readString();
        this.civil = in.readString();
        this.state = in.readString();
        this.reportName = in.readString();
        this.reportDate = in.readString();
        this.checkData = in.readString();
    }

    public static final Creator<ReportInfoProjectAmount> CREATOR = new Creator<ReportInfoProjectAmount>() {
        public ReportInfoProjectAmount createFromParcel(Parcel source) {
            return new ReportInfoProjectAmount(source);
        }

        public ReportInfoProjectAmount[] newArray(int size) {
            return new ReportInfoProjectAmount[size];
        }
    };
}
