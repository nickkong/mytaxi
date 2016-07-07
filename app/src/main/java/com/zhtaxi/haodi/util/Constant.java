package com.zhtaxi.haodi.util;

import android.os.Environment;

/**
 * 常量
 * Created by NickKong on 16/7/2.
 */
public class Constant {

    public final static boolean DEVELOPER_MODE = false;

    public static String BASE_DIR = Environment.getExternalStorageDirectory().toString() + "/haodi/";

    public final static int INIT_PAGENO = 1;
    public final static int INIT_PAGESIZE = 10;

    public final static int INTENT_RESULTCODE = 8888;

    public static final int CONNECT_TIMEOUT = 10000;

    public static int HTTP_STATUS_CODE_SUCCESS = 200;

    public final static String RECODE_SUCCESS = "0000";
    public final static String RECODE_FAILED = "0001";
    public final static String RECODE_FAILED_NODATA = "1001";
    public final static String RECODE_FAILED_PARAM_WRONG = "1002";
    public final static String RECODE_FAILED_APPKEY_WRONG = "1003";
    public final static String RECODE_FAILED_TOKEN_WRONG = "1004";
    public final static String RECODE_FAILED_TIMESTAMP_WRONG = "1005";

    public final static String RECODE_FAILED_USER_LOGIN = "2001";
    public final static String RECODE_FAILED_USER_NOTEXIST = "2002";
    public final static String RECODE_FAILED_PASSWORD_WRONG = "2003";
    public final static String RECODE_FAILED_SESSION_WRONG = "2004";

    public final static String RECODE_ERROR_SYSTEM = "9999";
    public final static String RECODE_ERROR_TIPS = "10000";

    public static final int PULLDOWN_INITDATA = 1;
    public static final int PULLDOWN_REFRESH = 2;
    public static final int PULLDOWN_LOADMORE = 3;

    public static double latitude = 0;
    public static double lontitude = 0;

}
