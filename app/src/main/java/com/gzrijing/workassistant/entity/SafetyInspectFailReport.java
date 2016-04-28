package com.gzrijing.workassistant.entity;

public class SafetyInspectFailReport {
    private String recordId;
    private String content;         //安全检查不合格项
    private String time;            //整改时间
    private String worker;          //整改人
    private String remark;          //备注信息
    private String feedback;        //反馈信息 yycq

    public SafetyInspectFailReport() {
    }

    public SafetyInspectFailReport(String recordId, String content, String time, String worker, String remark,String feedback) {
        this.recordId = recordId;
        this.content = content;
        this.time = time;
        this.worker = worker;
        this.remark = remark;
        this.feedback = feedback;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
