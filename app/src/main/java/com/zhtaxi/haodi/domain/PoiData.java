package com.zhtaxi.haodi.domain;

/**
 * 利用百度地图place api，获取poi信息
 * Created by NickKong on 16/7/29.
 */
public class PoiData {

    private String name;
//    private String location;
    private String address;
    private String street_id;
    private String telephone;
    private String detail;
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getLocation() {
//        return location;
//    }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStreet_id() {
        return street_id;
    }

    public void setStreet_id(String street_id) {
        this.street_id = street_id;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

}
