package com.gzrijing.workassistant.db;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class BusinessData extends DataSupport {
    private int id;
    private String user;                    //用户名
    private String orderId;                 //工程编码
    private String type;                    //工程类型
    private String state;                   //工程状态
    private String receivedTime;            //接单时间
    private String deadline;                //工程期限
    private boolean urgent;                 //工程是否紧急
    private int temInfoNum;                 //标记有多少条临时信息
    private String flag;                    //状态标识
    private String recordFileName;          //录音文件名
    private List<SuppliesNoData> suppliesNoList = new ArrayList<SuppliesNoData>();  //材料单号
    private List<MachineNoData> machineNoList = new ArrayList<MachineNoData>(); //机械单号
    private List<SuppliesData> suppliesDataList = new ArrayList<SuppliesData>();//材料清单
    private List<MachineData> machineDataList = new ArrayList<MachineData>();   //机械清单
    private List<DetailedInfoData> detailedInfoList = new ArrayList<DetailedInfoData>();//工程详细信息
    private List<ImageData> imageDataList = new ArrayList<ImageData>();     //工程图片

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public String getRecordFileName() {
        return recordFileName;
    }

    public void setRecordFileName(String recordFileName) {
        this.recordFileName = recordFileName;
    }

    public List<SuppliesNoData> getSuppliesNoList() {
        return suppliesNoList;
    }

    public void setSuppliesNoList(List<SuppliesNoData> suppliesNoList) {
        this.suppliesNoList = suppliesNoList;
    }

    public List<MachineNoData> getMachineNoList() {
        return machineNoList;
    }

    public void setMachineNoList(List<MachineNoData> machineNoList) {
        this.machineNoList = machineNoList;
    }

    public List<SuppliesData> getSuppliesDataList() {
        return suppliesDataList;
    }

    public void setSuppliesDataList(List<SuppliesData> suppliesDataList) {
        this.suppliesDataList = suppliesDataList;
    }

    public List<MachineData> getMachineDataList() {
        return machineDataList;
    }

    public void setMachineDataList(List<MachineData> machineDataList) {
        this.machineDataList = machineDataList;
    }

    public List<DetailedInfoData> getDetailedInfoList() {
        return detailedInfoList;
    }

    public void setDetailedInfoList(List<DetailedInfoData> detailedInfoList) {
        this.detailedInfoList = detailedInfoList;
    }

    public List<ImageData> getImageDataList() {
        return imageDataList;
    }

    public void setImageDataList(List<ImageData> imageDataList) {
        this.imageDataList = imageDataList;
    }
}
