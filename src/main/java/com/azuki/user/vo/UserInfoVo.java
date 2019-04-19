package com.azuki.user.vo;

import org.springframework.stereotype.Component;


public class UserInfoVo {
    String userName;
    String password;
    String name;
    String phone;
    String email;
    String qqNum;
    String province;
    String city;
    String postCode;
    String address;
    int userId;
    //验证码
    String code;
    //记住密码状态，1为选中记住密码，0为未选中
    int rememberStatus;

    public int getRememberStatus() {
        return rememberStatus;
    }

    public void setRememberStatus(int rememberStatus) {
        this.rememberStatus = rememberStatus;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQqNum() {
        return qqNum;
    }

    public void setQqNum(String qqNum) {
        this.qqNum = qqNum;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserInfoVo{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", qqNum='" + qqNum + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", postCode='" + postCode + '\'' +
                ", address='" + address + '\'' +
                ", userId='" + userId + '\'' +
                ", code='" + code + '\'' +
                ", rememberStatus=" + rememberStatus +
                '}';
    }
}
