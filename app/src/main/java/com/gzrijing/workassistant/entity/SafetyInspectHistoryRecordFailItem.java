package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class SafetyInspectHistoryRecordFailItem implements Parcelable {
    private String failContent;     //不合格内容
    private String handleName;      //处理人
    private String handleTime;      //处理时间

    public SafetyInspectHistoryRecordFailItem() {
    }

    public SafetyInspectHistoryRecordFailItem(String failContent, String handleName, String handleTime) {
        this.failContent = failContent;
        this.handleName = handleName;
        this.handleTime = handleTime;
    }

    public String getFailContent() {
        return failContent;
    }

    public void setFailContent(String failContent) {
        this.failContent = failContent;
    }

    public String getHandleName() {
        return handleName;
    }

    public void setHandleName(String handleName) {
        this.handleName = handleName;
    }

    public String getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(String handleTime) {
        this.handleTime = handleTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.failContent);
        dest.writeString(this.handleName);
        dest.writeString(this.handleTime);
    }

    protected SafetyInspectHistoryRecordFailItem(Parcel in) {
        this.failContent = in.readString();
        this.handleName = in.readString();
        this.handleTime = in.readString();
    }

    public static final Parcelable.Creator<SafetyInspectHistoryRecordFailItem> CREATOR = new Parcelable.Creator<SafetyInspectHistoryRecordFailItem>() {
        @Override
        public SafetyInspectHistoryRecordFailItem createFromParcel(Parcel source) {
            return new SafetyInspectHistoryRecordFailItem(source);
        }

        @Override
        public SafetyInspectHistoryRecordFailItem[] newArray(int size) {
            return new SafetyInspectHistoryRecordFailItem[size];
        }
    };
}
