package com.gzrijing.workassistant.entity;

import java.util.ArrayList;

public class SafetyInspectForm {
    private String situation;       //巡查情况
    private String process;         //问题处理
    private String checkDate;       //检查日期
    private String flag;            //是否处理完成（0未处理完，1处理完）
    private ArrayList<SafetyInspectSecondItem> failure = new ArrayList<SafetyInspectSecondItem>(); //不合格项
    private ArrayList<PicUrl> picUrls = new ArrayList<PicUrl>(); //图片

    public SafetyInspectForm() {
    }

    public SafetyInspectForm(String situation, String process, String checkDate, String flag, ArrayList<SafetyInspectSecondItem> failure, ArrayList<PicUrl> picUrls) {
        this.situation = situation;
        this.process = process;
        this.checkDate = checkDate;
        this.flag = flag;
        this.failure = failure;
        this.picUrls = picUrls;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public ArrayList<SafetyInspectSecondItem> getFailure() {
        return failure;
    }

    public void setFailure(ArrayList<SafetyInspectSecondItem> failure) {
        this.failure = failure;
    }

    public ArrayList<PicUrl> getPicUrls() {
        return picUrls;
    }

    public void setPicUrls(ArrayList<PicUrl> picUrls) {
        this.picUrls = picUrls;
    }

}
