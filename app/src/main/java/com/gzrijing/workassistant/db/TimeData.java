package com.gzrijing.workassistant.db;

import org.litepal.crud.DataSupport;

public class TimeData extends DataSupport{
    private int id;
    private String userNo;      //用户名
    private String time;        //上次获取工程的时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
