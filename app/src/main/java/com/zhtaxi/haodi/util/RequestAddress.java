package com.zhtaxi.haodi.util;

/**
 * 请求服务端地址和接口名
 * Created by NickKong on 16/7/13.
 */
public class RequestAddress {

    public final static String APP_URL = "http://120.76.29.5:8080/taxi_web/app/";
    public final static String BAIDU_WEB_URL = "http://api.map.baidu.com/place/v2/";
//    public final static String APP_URL = "http://119.120.234.148:8080/taxi_web/app/";

    public final static String suggestion = BAIDU_WEB_URL + "suggestion";
    public final static String search = BAIDU_WEB_URL + "search";

    public final static String login = APP_URL + "login";
    public final static String getCertCode = APP_URL + "getCertCode";
    public final static String logout = APP_URL + "logout";
    public final static String uploadGps = APP_URL + "uploadGps";
    public final static String queryNearByUsers = APP_URL + "queryNearByUsers";
    public final static String startWave = APP_URL + "startWave";
    public final static String stopWave = APP_URL + "stopWave";
    public final static String confirmWave = APP_URL + "confirmWave";

}
