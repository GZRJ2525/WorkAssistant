package com.gzrijing.workassistant.entity;


import com.gzrijing.workassistant.widget.SlideView;

import java.util.ArrayList;

public class BusinessByLeader {
    private int id;                         //数据库ID
    private String orderId;                 //工程编码
    private String type;                    //工程类型
    private String state;                   //工程状态
    private String receivedTime;            //接单时间
    private String deadline;                //工程期限
    private boolean urgent;                 //工程是否紧急
    private int temInfoNum;                 //标记有多少条临时信息
    private String flag;                     //施工组长对的操作标识：“确认收到”，“派工”
    private ArrayList<DetailedInfo> detailedInfos = new ArrayList<DetailedInfo>();   //详细信息
    private ArrayList<PicUrl> picUrls = new ArrayList<PicUrl>();     //图片URL
    private SlideView slideView;

    public BusinessByLeader() {
    }

    public BusinessByLeader(int id, String orderId, String type, String state, String receivedTime, String deadline, boolean urgent, int temInfoNum, String flag, ArrayList<DetailedInfo> detailedInfos, ArrayList<PicUrl> picUrls, SlideView slideView) {
        this.id = id;
        this.orderId = orderId;
        this.type = type;
        this.state = state;
        this.receivedTime = receivedTime;
        this.deadline = deadline;
        this.urgent = urgent;
        this.temInfoNum = temInfoNum;
        this.flag = flag;
        this.detailedInfos = detailedInfos;
        this.picUrls = picUrls;
        this.slideView = slideView;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(String receivedTime) {
        this.receivedTime = receivedTime;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public int getTemInfoNum() {
        return temInfoNum;
    }

    public void setTemInfoNum(int temInfoNum) {
        this.temInfoNum = temInfoNum;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public ArrayList<DetailedInfo> getDetailedInfos() {
        return detailedInfos;
    }

    public void setDetailedInfos(ArrayList<DetailedInfo> detailedInfos) {
        this.detailedInfos = detailedInfos;
    }

    public ArrayList<PicUrl> getPicUrls() {
        return picUrls;
    }

    public void setPicUrls(ArrayList<PicUrl> picUrls) {
        this.picUrls = picUrls;
    }

    public SlideView getSlideView() {
        return slideView;
    }

    public void setSlideView(SlideView slideView) {
        this.slideView = slideView;
    }
}
