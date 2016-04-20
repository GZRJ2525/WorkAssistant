package com.gzrijing.workassistant.entity;

public class SafetyInspectFailItem {
    private String content;         //不合格项
    private String recordId;
    private String isHandle;        //是否处理（0：未处理  1：已处理）
    private String isDistributed;   //是否派工（0：未派发  1：已派发）
    private String workerNo;        //整改人编号
    private String worker;          //整改人
    private String remark;          //备注信息

    public SafetyInspectFailItem() {
    }

    public SafetyInspectFailItem(String content, String recordId, String isHandle, String isDistributed, String workerNo, String worker, String remark) {
        this.content = content;
        this.recordId = recordId;
        this.isHandle = isHandle;
        this.isDistributed = isDistributed;
        this.workerNo = workerNo;
        this.worker = worker;
        this.remark = remark;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getIsHandle() {
        return isHandle;
    }

    public void setIsHandle(String isHandle) {
        this.isHandle = isHandle;
    }

    public String getIsDistributed() {
        return isDistributed;
    }

    public void setIsDistributed(String isDistributed) {
        this.isDistributed = isDistributed;
    }

    public String getWorkerNo() {
        return workerNo;
    }

    public void setWorkerNo(String workerNo) {
        this.workerNo = workerNo;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
