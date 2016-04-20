package com.gzrijing.workassistant.entity;

public class SafetyInspectFail {
    private String orderId;     //工程编号
    private String name;        //工程名称
    private String type;        //工程类型

    public SafetyInspectFail() {
    }

    public SafetyInspectFail(String orderId, String name, String type) {
        this.orderId = orderId;
        this.name = name;
        this.type = type;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
