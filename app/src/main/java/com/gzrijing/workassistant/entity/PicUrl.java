package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class PicUrl implements Parcelable {
    private String picUrl;
    private String picTime;//【
    private boolean isCheck;//【

    public PicUrl() {
    }

    public PicUrl(String picUrl, String picTime, boolean isCheck) {
        this.picUrl = picUrl;
        this.picTime = picTime;
        this.isCheck = isCheck;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPicTime() {
        return picTime;
    }

    public void setPicTime(String picTime) {
        this.picTime = picTime;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public static Creator<PicUrl> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.picUrl);
        dest.writeString(this.picTime);
        dest.writeByte(isCheck ? (byte) 1 : (byte) 0);
    }

    protected PicUrl(Parcel in) {
        this.picUrl = in.readString();
        this.picTime = in.readString();
        this.isCheck = in.readByte() != 0;
    }

    public static final Creator<PicUrl> CREATOR = new Creator<PicUrl>() {
        public PicUrl createFromParcel(Parcel source) {
            return new PicUrl(source);
        }

        public PicUrl[] newArray(int size) {
            return new PicUrl[size];
        }
    };
}
