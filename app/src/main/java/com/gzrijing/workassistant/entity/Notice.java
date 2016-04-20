package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Notice implements Parcelable {
    private String title;           //标题
    private String content;         //内容
    private String promulgator;     //发布者
    private String date;            //发布时间
    private String department;      //发布部门

    public Notice() {
    }

    public Notice(String title, String content, String promulgator, String date, String department) {
        this.title = title;
        this.content = content;
        this.promulgator = promulgator;
        this.date = date;
        this.department = department;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPromulgator() {
        return promulgator;
    }

    public void setPromulgator(String promulgator) {
        this.promulgator = promulgator;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.promulgator);
        dest.writeString(this.date);
        dest.writeString(this.department);
    }

    protected Notice(Parcel in) {
        this.title = in.readString();
        this.content = in.readString();
        this.promulgator = in.readString();
        this.date = in.readString();
        this.department = in.readString();
    }

    public static final Creator<Notice> CREATOR = new Creator<Notice>() {
        public Notice createFromParcel(Parcel source) {
            return new Notice(source);
        }

        public Notice[] newArray(int size) {
            return new Notice[size];
        }
    };
}
