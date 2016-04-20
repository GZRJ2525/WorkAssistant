package com.gzrijing.workassistant.entity;

import java.util.ArrayList;

public class SuppliesVerify {
    private String id;
    private String No;          //单号
    private String applicant;   //申请人
    private String useTime;     //领用时间
    private String remarks;     //备注
    private String state;       //状态
    private ArrayList<SuppliesVerifyInfo> suppliesVerifyInfoList = new ArrayList<SuppliesVerifyInfo>();

    public SuppliesVerify() {
    }

    public SuppliesVerify(String id, String no, String applicant, String useTime, String remarks, String state, ArrayList<SuppliesVerifyInfo> suppliesVerifyInfoList) {
        this.id = id;
        No = no;
        this.applicant = applicant;
        this.useTime = useTime;
        this.remarks = remarks;
        this.state = state;
        this.suppliesVerifyInfoList = suppliesVerifyInfoList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ArrayList<SuppliesVerifyInfo> getSuppliesVerifyInfoList() {
        return suppliesVerifyInfoList;
    }

    public void setSuppliesVerifyInfoList(ArrayList<SuppliesVerifyInfo> suppliesVerifyInfoList) {
        this.suppliesVerifyInfoList = suppliesVerifyInfoList;
    }
}
