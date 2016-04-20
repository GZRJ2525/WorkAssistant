package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ReportInfo implements Parcelable {
    private String id;
    private String fileNo;
    private String content;         //汇报内容
    private String picNum;          //图片数量
    private String reportor;        //汇报人
    private String reportTime;      //汇报时间
    private String handleName;      //处理人
    private String handleResult;    //处理结果
    private String state;           //问题是否解决 0为未解决，1为解决

    public ReportInfo() {
    }

    public ReportInfo(String id, String fileNo, String content, String picNum, String reportor, String reportTime, String handleName, String handleResult, String state) {
        this.id = id;
        this.fileNo = fileNo;
        this.content = content;
        this.picNum = picNum;
        this.reportor = reportor;
        this.reportTime = reportTime;
        this.handleName = handleName;
        this.handleResult = handleResult;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileNo() {
        return fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicNum() {
        return picNum;
    }

    public void setPicNum(String picNum) {
        this.picNum = picNum;
    }

    public String getReportor() {
        return reportor;
    }

    public void setReportor(String reportor) {
        this.reportor = reportor;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getHandleName() {
        return handleName;
    }

    public void setHandleName(String handleName) {
        this.handleName = handleName;
    }

    public String getHandleResult() {
        return handleResult;
    }

    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
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
        dest.writeString(this.fileNo);
        dest.writeString(this.content);
        dest.writeString(this.picNum);
        dest.writeString(this.reportor);
        dest.writeString(this.reportTime);
        dest.writeString(this.handleName);
        dest.writeString(this.handleResult);
        dest.writeString(this.state);
    }

    protected ReportInfo(Parcel in) {
        this.id = in.readString();
        this.fileNo = in.readString();
        this.content = in.readString();
        this.picNum = in.readString();
        this.reportor = in.readString();
        this.reportTime = in.readString();
        this.handleName = in.readString();
        this.handleResult = in.readString();
        this.state = in.readString();
    }

    public static final Parcelable.Creator<ReportInfo> CREATOR = new Parcelable.Creator<ReportInfo>() {
        @Override
        public ReportInfo createFromParcel(Parcel source) {
            return new ReportInfo(source);
        }

        @Override
        public ReportInfo[] newArray(int size) {
            return new ReportInfo[size];
        }
    };
}
