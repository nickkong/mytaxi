package com.zhtaxi.haodi.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 后台调用获取位置信息
 * Created by NickKong on 16/7/16.
 */
public class GetLocationService extends Service{

    @Override
    public IBinder onBind(Intent intent) {

        return null;

    }
}
