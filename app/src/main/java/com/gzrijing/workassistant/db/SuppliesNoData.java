package com.gzrijing.workassistant.db;

import org.litepal.crud.DataSupport;

public class SuppliesNoData extends DataSupport {
    private int id;
    private String applyId;         //材料申请单号
    private String receivedId;      //材料发放单号
    private String returnId;        //材料退回单号
    private String applyState;      //申请状态 (申请中，已审批，不批准)
    private String receivedState;   //发放状态 (可领用，已领出)
    private String returnState;     //退回状态 (申请中，可退回，已退回)
    private String applyTime;       //材料申请时间
    private String useTime;         //材料使用时间
    private String approvalTime;    //材料审批时间
    private String receivedTime;    //领料时间
    private String returnTime;      //退回申请时间
    private String reason;          //不批准原因
    private String remarks;         //备注
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

    public String getReceivedId() {
        return receivedId;
    }

    public void setReceivedId(String receivedId) {
        this.receivedId = receivedId;
    }

    public String getReturnId() {
        return returnId;
    }

    public void setReturnId(String returnId) {
        this.returnId = returnId;
    }

    public String getApplyState() {
        return applyState;
    }

    public void setApplyState(String applyState) {
        this.applyState = applyState;
    }

    public String getReceivedState() {
        return receivedState;
    }

    public void setReceivedState(String receivedState) {
        this.receivedState = receivedState;
    }

    public String getReturnState() {
        return returnState;
    }

    public void setReturnState(String returnState) {
        this.returnState = returnState;
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

    public String getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(String approvalTime) {
        this.approvalTime = approvalTime;
    }

    public String getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(String receivedTime) {
        this.receivedTime = receivedTime;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public BusinessData getBusinessData() {
        return businessData;
    }

    public void setBusinessData(BusinessData businessData) {
        this.businessData = businessData;
    }
}
