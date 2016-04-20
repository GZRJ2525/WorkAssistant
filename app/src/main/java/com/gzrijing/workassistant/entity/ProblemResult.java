package com.gzrijing.workassistant.entity;

public class ProblemResult {
    private String reason;      //问题原因
    private String result;      //处理结果

    public ProblemResult() {
    }

    public ProblemResult(String reason, String result) {
        this.reason = reason;
        this.result = result;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
