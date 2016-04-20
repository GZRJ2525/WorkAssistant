package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class SafetyInspectHistoryRecord implements Parcelable {
    private String situation;       //巡查情况
    private String process;         //问题处理
    private String submitDate;      //提交日期
    private String flag;            //是否处理完（0：未处理完  1：处理完）
    private ArrayList<SafetyInspectHistoryRecordFailItem> failure = new ArrayList<SafetyInspectHistoryRecordFailItem>(); //不合格项
    private ArrayList<PicUrl> picUrls = new ArrayList<PicUrl>(); //图片

    public SafetyInspectHistoryRecord() {
    }

    public SafetyInspectHistoryRecord(String situation, String process, String submitDate, String flag, ArrayList<SafetyInspectHistoryRecordFailItem> failure, ArrayList<PicUrl> picUrls) {
        this.situation = situation;
        this.process = process;
        this.submitDate = submitDate;
        this.flag = flag;
        this.failure = failure;
        this.picUrls = picUrls;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(String submitDate) {
        this.submitDate = submitDate;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public ArrayList<SafetyInspectHistoryRecordFailItem> getFailure() {
        return failure;
    }

    public void setFailure(ArrayList<SafetyInspectHistoryRecordFailItem> failure) {
        this.failure = failure;
    }

    public ArrayList<PicUrl> getPicUrls() {
        return picUrls;
    }

    public void setPicUrls(ArrayList<PicUrl> picUrls) {
        this.picUrls = picUrls;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.situation);
        dest.writeString(this.process);
        dest.writeString(this.submitDate);
        dest.writeString(this.flag);
        dest.writeList(this.failure);
        dest.writeTypedList(picUrls);
    }

    protected SafetyInspectHistoryRecord(Parcel in) {
        this.situation = in.readString();
        this.process = in.readString();
        this.submitDate = in.readString();
        this.flag = in.readString();
        this.failure = new ArrayList<SafetyInspectHistoryRecordFailItem>();
        in.readList(this.failure, SafetyInspectHistoryRecordFailItem.class.getClassLoader());
        this.picUrls = in.createTypedArrayList(PicUrl.CREATOR);
    }

    public static final Parcelable.Creator<SafetyInspectHistoryRecord> CREATOR = new Parcelable.Creator<SafetyInspectHistoryRecord>() {
        @Override
        public SafetyInspectHistoryRecord createFromParcel(Parcel source) {
            return new SafetyInspectHistoryRecord(source);
        }

        @Override
        public SafetyInspectHistoryRecord[] newArray(int size) {
            return new SafetyInspectHistoryRecord[size];
        }
    };
}
