package com.zhtaxi.haodi.domain;

/**
 * 显示附近司机数据
 * Created by NickKong on 16/8/25.
 */
public class Drivers {

    private String carId;
    private String distance;
    private String id;
    private String isTrip;
    private String lat;
    private String licensePlate;
    private String lng;
    private String mapType;
    private String updateTime;
    private String updateTimeLong;
    private String userId;
    private String userType;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsTrip() {
        return isTrip;
    }

    public void setIsTrip(String isTrip) {
        this.isTrip = isTrip;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateTimeLong() {
        return updateTimeLong;
    }

    public void setUpdateTimeLong(String updateTimeLong) {
        this.updateTimeLong = updateTimeLong;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
