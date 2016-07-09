package com.zhtaxi.haodi;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.zhtaxi.haodi.service.LocationService;

/**
 * 全局Application
 * Created by NickKong on 16/7/2.
 */
public class HaodiApplication extends Application {

    public LocationService locationService;

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化百度地图定位
        locationService = new LocationService(this);
        //百度地图，在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);


    }

}