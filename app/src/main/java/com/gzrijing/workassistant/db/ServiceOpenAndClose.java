package com.gzrijing.workassistant.db;

import org.litepal.crud.DataSupport;

/**
 * 个推服务开与关记录
 */
public class ServiceOpenAndClose extends DataSupport {
    private int id;
    private String pushService;     //0=关  1=开

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPushService() {
        return pushService;
    }

    public void setPushService(String pushService) {
        this.pushService = pushService;
    }
}
