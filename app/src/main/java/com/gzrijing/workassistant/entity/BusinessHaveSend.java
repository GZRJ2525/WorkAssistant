package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class BusinessHaveSend implements Parcelable {
    private String Id;              //已派工程id
    private String content;         //已派工程内容
    private String state;           //已派工程状态
    private String deadline;        //已派工程期限
    private String executors;       //已派工程执行者

    public BusinessHaveSend() {
    }

    public BusinessHaveSend(String id, String content, String state, String deadline, String executors) {
        Id = id;
        this.content = content;
        this.state = state;
        this.deadline = deadline;
        this.executors = executors;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getExecutors() {
        return executors;
    }

    public void setExecutors(String executors) {
        this.executors = executors;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Id);
        dest.writeString(this.content);
        dest.writeString(this.state);
        dest.writeString(this.deadline);
        dest.writeString(this.executors);
    }

    protected BusinessHaveSend(Parcel in) {
        this.Id = in.readString();
        this.content = in.readString();
        this.state = in.readString();
        this.deadline = in.readString();
        this.executors = in.readString();
    }

    public static final Parcelable.Creator<BusinessHaveSend> CREATOR = new Parcelable.Creator<BusinessHaveSend>() {
        public BusinessHaveSend createFromParcel(Parcel source) {
            return new BusinessHaveSend(source);
        }

        public BusinessHaveSend[] newArray(int size) {
            return new BusinessHaveSend[size];
        }
    };
}
