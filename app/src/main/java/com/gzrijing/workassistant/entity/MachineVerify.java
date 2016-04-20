package com.gzrijing.workassistant.entity;

import java.util.ArrayList;
import java.util.List;

public class MachineVerify {
    private String id;          //机械申请单号
    private String applicant;   //申请人
    private String useTime;     //领用时间
    private String returnTime;  //退回时间
    private String useAdress;   //使用地点
    private String remarks;     //备注
    private String state;       //状态
    private ArrayList<MachineVerifyInfo> machineVerifyInfoList = new ArrayList<MachineVerifyInfo>();

    public MachineVerify() {
    }

    public MachineVerify(String id, String applicant, String useTime, String returnTime, String useAdress, String remarks, String state, ArrayList<MachineVerifyInfo> machineVerifyInfoList) {
        this.id = id;
        this.applicant = applicant;
        this.useTime = useTime;
        this.returnTime = returnTime;
        this.useAdress = useAdress;
        this.remarks = remarks;
        this.state = state;
        this.machineVerifyInfoList = machineVerifyInfoList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public String getUseAdress() {
        return useAdress;
    }

    public void setUseAdress(String useAdress) {
        this.useAdress = useAdress;
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

    public ArrayList<MachineVerifyInfo> getMachineVerifyInfoList() {
        return machineVerifyInfoList;
    }

    public void setMachineVerifyInfoList(ArrayList<MachineVerifyInfo> machineVerifyInfoList) {
        this.machineVerifyInfoList = machineVerifyInfoList;
    }
}
