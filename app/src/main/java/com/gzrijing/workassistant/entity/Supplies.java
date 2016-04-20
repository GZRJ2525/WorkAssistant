package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Supplies implements Parcelable {
    private String id;          //材料编号
    private String name;        //材料名称
    private String spec;        //材料规格
    private String unit;        //材料单位
    private String applyNum;    //申请数量
    private String sendNum;     //发放数量
    private String num;         //数量

    public Supplies() {
    }

    public Supplies(String id, String name, String spec, String unit, String applyNum, String sendNum, String num) {
        this.id = id;
        this.name = name;
        this.spec = spec;
        this.unit = unit;
        this.applyNum = applyNum;
        this.sendNum = sendNum;
        this.num = num;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(String applyNum) {
        this.applyNum = applyNum;
    }

    public String getSendNum() {
        return sendNum;
    }

    public void setSendNum(String sendNum) {
        this.sendNum = sendNum;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.spec);
        dest.writeString(this.unit);
        dest.writeString(this.applyNum);
        dest.writeString(this.sendNum);
        dest.writeString(this.num);
    }

    protected Supplies(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.spec = in.readString();
        this.unit = in.readString();
        this.applyNum = in.readString();
        this.sendNum = in.readString();
        this.num = in.readString();
    }

    public static final Creator<Supplies> CREATOR = new Creator<Supplies>() {
        public Supplies createFromParcel(Parcel source) {
            return new Supplies(source);
        }

        public Supplies[] newArray(int size) {
            return new Supplies[size];
        }
    };
}
