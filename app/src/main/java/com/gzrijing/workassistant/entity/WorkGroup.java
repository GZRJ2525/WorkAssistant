package com.gzrijing.workassistant.entity;

public class WorkGroup {
    private String groupNo;     //工作组编号
    private String groupName;   //工作组名称

    public WorkGroup() {
    }

    public WorkGroup(String groupNo, String groupName) {
        this.groupNo = groupNo;
        this.groupName = groupName;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
