package com.gzrijing.workassistant.entity;

import java.util.ArrayList;

public class OrderTypeSupplies {
    private String orderId;         //工程编号
    private boolean isCheck;        //是否选中
    private ArrayList<Supplies> suppliesList = new ArrayList<Supplies>();   //材料列表

    public OrderTypeSupplies() {
    }

    public OrderTypeSupplies(String orderId, boolean isCheck, ArrayList<Supplies> suppliesList) {
        this.orderId = orderId;
        this.isCheck = isCheck;
        this.suppliesList = suppliesList;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public ArrayList<Supplies> getSuppliesList() {
        return suppliesList;
    }

    public void setSuppliesList(ArrayList<Supplies> suppliesList) {
        this.suppliesList = suppliesList;
    }
}
