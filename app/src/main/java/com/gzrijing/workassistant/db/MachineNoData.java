package com.gzrijing.workassistant.db;

import org.litepal.crud.DataSupport;

public class MachineNoData extends DataSupport {
    private int id;
    private String applyId;         //机械申请单号
    private String returnId;        //机械退回单号
    private String applyTime;       //机械申请时间
    private String useTime;         //机械使用时间
    private String returnTime;      //机械预计退回时间
    private String useAddress;      //机械使用地点
    private String remarks;         //备注
    private String approvalTime;    //机械单审批时间
    private String applyState;      //机械申请状态（申请中，已审批，不批准）
    private String returnState;     //机械退回状态（申请中，已安排，已退回）
    private String returnType;      //机械退回性质（正常，损坏）
    private String returnApplyTime; //机械退回申请时间
    private String returnAddress;   //机械退回地点
    private String reason;          //不批准原因
    private BusinessData businessData;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public String getReturnId() {
        return returnId;
    }

    public void setReturnId(String returnId) {
        this.returnId = returnId;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
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

    public String getUseAddress() {
        return useAddress;
    }

    public void setUseAddress(String useAddress) {
        this.useAddress = useAddress;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(String approvalTime) {
        this.approvalTime = approvalTime;
    }

    public String getApplyState() {
        return applyState;
    }

    public void setApplyState(String applyState) {
        this.applyState = applyState;
    }

    public String getReturnState() {
        return returnState;
    }

    public void setReturnState(String returnState) {
        this.returnState = returnState;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getReturnApplyTime() {
        return returnApplyTime;
    }

    public void setReturnApplyTime(String returnApplyTime) {
        this.returnApplyTime = returnApplyTime;
    }

    public String getReturnAddress() {
        return returnAddress;
    }

    public void setReturnAddress(String returnAddress) {
        this.returnAddress = returnAddress;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public BusinessData getBusinessData() {
        return businessData;
    }

    public void setBusinessData(BusinessData businessData) {
        this.businessData = businessData;
    }
}
