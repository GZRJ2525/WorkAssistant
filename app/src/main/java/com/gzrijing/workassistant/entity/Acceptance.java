package com.gzrijing.workassistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Acceptance implements Parcelable {
    private String orderId;             //工程编号
    private String orderType;           //工程类型
    private String KHContent;           //客户施工内容
    private String KHCivil;             //客户土建项目
    private String SWContent;           //水务施工内容
    private String SWCivil;             //水务土建项目
    private String KHPayUnit;//客户收款
    private String SWPayUnit;//水务收款
    private ArrayList<Supplies> suppliesByClient = new ArrayList<Supplies>();       //客户收费材料
    private ArrayList<Supplies> suppliesByWater = new ArrayList<Supplies>();        //水务收费材料
    private ArrayList<DetailedInfo> detailedInfos = new ArrayList<DetailedInfo>();  //详细信息

    public Acceptance() {
    }

    public Acceptance(String orderId, String orderType, String KHContent, String KHCivil, String SWContent, String SWCivil,String KHPayUnit,String SWPayUnit, ArrayList<Supplies> suppliesByClient, ArrayList<Supplies> suppliesByWater, ArrayList<DetailedInfo> detailedInfos) {
        this.orderId = orderId;
        this.orderType = orderType;
        this.KHContent = KHContent;
        this.KHCivil = KHCivil;
        this.SWContent = SWContent;
        this.SWCivil = SWCivil;
        this.KHPayUnit = KHPayUnit;
        this.SWPayUnit = SWPayUnit;
        this.suppliesByClient = suppliesByClient;
        this.suppliesByWater = suppliesByWater;
        this.detailedInfos = detailedInfos;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getKHContent() {
        return KHContent;
    }

    public void setKHContent(String KHContent) {
        this.KHContent = KHContent;
    }

    public String getKHCivil() {
        return KHCivil;
    }

    public void setKHCivil(String KHCivil) {
        this.KHCivil = KHCivil;
    }

    public String getSWContent() {
        return SWContent;
    }

    public void setSWContent(String SWContent) {
        this.SWContent = SWContent;
    }

    public String getSWCivil() {
        return SWCivil;
    }

    public void setSWCivil(String SWCivil) {
        this.SWCivil = SWCivil;
    }

    public String getKHPayUnit() {return KHPayUnit;}

    public void setKHPayUnit(String KHPayUnit) {this.KHPayUnit = KHPayUnit;}

    public String getSWPayUnit() {
        return SWPayUnit;
    }

    public void setSWPayUnit(String SWPayUnit) {
        this.SWPayUnit = SWPayUnit;
    }

    public ArrayList<Supplies> getSuppliesByClient() {
        return suppliesByClient;
    }

    public void setSuppliesByClient(ArrayList<Supplies> suppliesByClient) {
        this.suppliesByClient = suppliesByClient;
    }

    public ArrayList<Supplies> getSuppliesByWater() {
        return suppliesByWater;
    }

    public void setSuppliesByWater(ArrayList<Supplies> suppliesByWater) {
        this.suppliesByWater = suppliesByWater;
    }

    public ArrayList<DetailedInfo> getDetailedInfos() {
        return detailedInfos;
    }

    public void setDetailedInfos(ArrayList<DetailedInfo> detailedInfos) {
        this.detailedInfos = detailedInfos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.orderId);
        dest.writeString(this.orderType);
        dest.writeString(this.KHContent);
        dest.writeString(this.KHCivil);
        dest.writeString(this.SWContent);
        dest.writeString(this.SWCivil);
        dest.writeString(this.KHPayUnit);
        dest.writeString(this.SWPayUnit);
        dest.writeTypedList(suppliesByClient);
        dest.writeTypedList(suppliesByWater);
        dest.writeTypedList(detailedInfos);
    }

    protected Acceptance(Parcel in) {
        this.orderId = in.readString();
        this.orderType = in.readString();
        this.KHContent = in.readString();
        this.KHCivil = in.readString();
        this.SWContent = in.readString();
        this.SWCivil = in.readString();
        this.KHPayUnit = in.readString();
        this.SWPayUnit = in.readString();
        this.suppliesByClient = in.createTypedArrayList(Supplies.CREATOR);
        this.suppliesByWater = in.createTypedArrayList(Supplies.CREATOR);
        this.detailedInfos = in.createTypedArrayList(DetailedInfo.CREATOR);
    }

    public static final Creator<Acceptance> CREATOR = new Creator<Acceptance>() {
        @Override
        public Acceptance createFromParcel(Parcel source) {
            return new Acceptance(source);
        }

        @Override
        public Acceptance[] newArray(int size) {
            return new Acceptance[size];
        }
    };
}
