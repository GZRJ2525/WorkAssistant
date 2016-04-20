package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ReportComplete implements Parcelable {
    private String key;
    private String value;

    public ReportComplete() {
    }

    public ReportComplete(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.value);
    }

    protected ReportComplete(Parcel in) {
        this.key = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<ReportComplete> CREATOR = new Parcelable.Creator<ReportComplete>() {
        public ReportComplete createFromParcel(Parcel source) {
            return new ReportComplete(source);
        }

        public ReportComplete[] newArray(int size) {
            return new ReportComplete[size];
        }
    };
}
