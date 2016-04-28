package com.gzrijing.workassistant.entity;

public class SafetyInspectSecondItem {
    private String id;              //不合格项ID
    private String name;            //不合格项名称
    private String isHandle;        //是否处理完（0：未处理完 1：处理完）
    private boolean isCheck;        //是否选择
    private boolean isSubmit;       //是否提交过
    private String feedback;         //0426 yycq

    public SafetyInspectSecondItem() {
    }

    public SafetyInspectSecondItem(String id, String name, String isHandle, boolean isCheck, boolean isSubmit,String feedback) {
        this.id = id;
        this.name = name;
        this.isHandle = isHandle;
        this.isCheck = isCheck;
        this.isSubmit = isSubmit;
        this.feedback = feedback;

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

    public String getIsHandle() {
        return isHandle;
    }

    public void setIsHandle(String isHandle) {
        this.isHandle = isHandle;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isSubmit() {
        return isSubmit;
    }

    public void setSubmit(boolean submit) {
        isSubmit = submit;
    }

    public void  setFeedback(String feedback){this.feedback = feedback;}

    public String getFeedback(){return feedback;}
}
