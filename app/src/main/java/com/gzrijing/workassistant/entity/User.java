package com.gzrijing.workassistant.entity;

public class User {
    private String userNo;          //账号
    private String userName;        //用户名称
    private String userDept;        //部门
    private String userSit;         //职位
    private String userRank;        //1表示是组长；0表示施工员；2表示主任

    public User() {
    }

    public User(String userNo, String userName, String userDept, String userSit, String userRank) {
        this.userNo = userNo;
        this.userName = userName;
        this.userDept = userDept;
        this.userSit = userSit;
        this.userRank = userRank;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDept() {
        return userDept;
    }

    public void setUserDept(String userDept) {
        this.userDept = userDept;
    }

    public String getUserSit() {
        return userSit;
    }

    public void setUserSit(String userSit) {
        this.userSit = userSit;
    }

    public String getUserRank() {
        return userRank;
    }

    public void setUserRank(String userRank) {
        this.userRank = userRank;
    }
}
