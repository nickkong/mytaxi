package com.zhtaxi.haodi.util;

import android.os.Environment;

import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.text.SimpleDateFormat;

/**
 * 常量
 * Created by NickKong on 16/7/2.
 */
public class Constant {

    public final static boolean DEVELOPER_MODE = false;

    public static String LESSO_USERNAME = "LESSO_USERNAME";
    public static String LESSO_USERPASSWORD = "LESSO_USERPASSWORD";

    public static String LESSO_USER_SHAREDPREFERENCES = "USER";

    public static String BASE_DIR = Environment.getExternalStorageDirectory().toString() + "/lessomall/";

    //商城正式地址
    public final static String LESSOMALL_IP = "http://www.lessomall.com/";
    //商城测试地址
//    public final static String LESSOMALL_IP = "http://192.168.4.232:9011/";

    //江文机器
//    public final static String SERVER_IP = "http://10.10.7.150:8080/";

    //江文测试机器
//    public final static String SERVER_IP = "http://172.16.0.55:8080/";

    //开发服务器(停用)
//    public final static String SERVER_IP = "http://10.10.7.145:8080/";

    //开发服务器
//    public final static String SERVER_IP = "http://10.10.7.159:8080/";

    //测试服务器(内网)
//    public final static String SERVER_IP = "http://10.10.7.147:8080/";

    //测试服务器(外网)(停用)
//    public final static String SERVER_IP = "http://lotstest.lessoald.cn/";

    //测试服务器(外网)
    public final static String SERVER_IP = "http://lessopaytest.lessomall.com/";
    //public final static String SERVER_IP = "http://10.10.7.198:8080/";

    //生产环境(外网)
//    public final static String SERVER_IP = "http://www.lessomall.com/";

    public final static String BASE_URL_WEIXIN_DISTRIBUTOR = "http://weixin.lessomall.com/distributor/";

    public final static String BASE_URL = SERVER_IP + "lots-web/";
    public final static String APP_URL = BASE_URL + "app/";
    public final static String PICTURE_URL = BASE_URL + "fileUp/img/?path=";
    public final static String URL_LOGIN = APP_URL + "login/";
    public final static String URL_UPDATE = APP_URL + "assistant/androidversion/";
    public final static String URL_MAINCOUNT = APP_URL + "mainCount/";

    //测试：wxtest.lessomall.com  正式：weixin.lessomall.com
    public final static String WEIXIN_GOODS_DEATAIL = "http://wxtest.lessomall.com/expose-pages/p/";
//    public final static String WEIXIN_GOODS_DEATAIL = "http://weixin.lessomall.com/expose-pages/p/";

    public final static String APP_KEY_ANDROID = "ba25623f";     //对应数据库字段DEVICETYPE = 1;

    public final static String SECRET_KEY = "db1e358753b4fe1735dcf50dc1bf465b";
    public final static int INIT_PAGENO = 1;
    public final static int INIT_PAGESIZE = 10;
    public final static int INTENT_REQUESTCODE = 9999;
    public final static int ADDGOODS_REQUESTCODE = 9998;
    public final static int GOODSDETAIL_REQUESTCODE = 9997;
    public final static int SAVEINFO_REQUESTCODE = 9996;
    public final static int RETURNBACK_REQUESTCODE = 9995;
    public final static int EXPRESSCOMPANY_REQUESTCODE = 9994;
    public final static int HOTGOODSLIST_REQUESTCODE = 9993;
    public final static int ORDERDETAIL_REQUESTCODE = 9992;
    public final static int ORDERDLIST_REQUESTCODE = 9991;

    public final static int INITDATA = 7777;
    public final static int HANDLER_GOODSPROPERTY = 7776;
    public final static int HANDLER_GOODSPRICE = 7775;
    public final static int HANDLER_GOODSCONTENT = 7774;
    public final static int HANDLER_LOG = 7773;

    public final static int INTENT_RESULTCODE = 8888;

    public final static String[] IMG_SUFFIX = {".jpg", ".jpeg", ".png", ".bmp"};
    public final static long IMG_MAX_SIZE = 512 * 1024;  //   500k

    public static final SimpleDateFormat DATE_FORMAT_1 = new SimpleDateFormat("yyyy-MM-dd");

    public static final SimpleDateFormat DATE_FORMAT_2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final SimpleDateFormat DATE_FORMAT_3 = new SimpleDateFormat("yyyy-MM");

    public static final SimpleDateFormat DATE_FORMAT_4 = new SimpleDateFormat("HHmmss");

    public static final SimpleDateFormat DATE_FORMAT_5 = new SimpleDateFormat("mm/dd/yyyy");

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

    public static final String FINISH_ACTION = "com.lessomall.assistant.FINISH_ACTION";
    public static final String FINISH_SPLASH_ACTION = "com.lessomall.assistant.FINISH_SPLASH_ACTION";
    public static final String REFRESH_ACTION = "com.lessomall.assistant.REFRESH_ACTION";
    public static final String UPDATE_GOODS_ISADD_ACTION = "com.lessomall.assistant.UPDATE_GOODS_ISADD_ACTION";

    public static final int GOODSTYPE_CHECKING = 1;
    public static final int GOODSTYPE_RETURNBACK = 3;
    public static final int GOODSTYPE_SALING = 4;
    public static final int GOODSTYPE_PULLOFF = 5;
    public static final int GOODSTYPE_PRICE = 6;

    public static final int UPLOAD_SUCCESS_CODE = 1001;
    public static final int UPLOAD_SERVER_ERROR_CODE = 1002;
    public static final int UPLOAD_FILE_DONE = 1003;

    public static final int TAKE_PICTURE = 1;
    public static final int REQUESTCODE = 2;
    public static final int TYPE_SEARCH = 3;
    public static final int TYPE_GOODSKIND = 4;
    public static final int TYPE_MALLCLASSIFY = 5;
    public static final int TO_SELECT_PHOTO = 6;
    public static final int VALIDATE_CHANGE_NAME = 7;
    public static final int VALIDATE_CHANGE_ADDRESS = 8;

    public static final int PULLDOWN_INITDATA = 1;
    public static final int PULLDOWN_REFRESH = 2;
    public static final int PULLDOWN_LOADMORE = 3;

    public static final String ORDERLIST_ALL = "1";
    public static final String ORDERLIST_QUOTEDPRICE = "2";
    public static final String ORDERLIST_PAY = "3";
    public static final String ORDERLIST_DELIVER = "4";
    public static final String ORDERLIST_RECEIPT = "5";
    public static final String ORDERLIST_FINISH = "6";

    public static final String ORDERSTATUS_SERVER_QUOTEDPRICE = "14";
    public static final String ORDERSTATUS_SERVER_PAY = "16";
    public static final String ORDERSTATUS_SERVER_DELIVER = "20";
    public static final String ORDERSTATUS_SERVER_RECEIPT = "30";
    public static final String ORDERSTATUS_SERVER_FINISH = "0";
    public static final String ORDERSTATUS_SERVER_CLOSE = "40";

    public static final String TRANSPORT_CAR = "10";
    public static final String TRANSPORT_EXPRESS = "20";
    public static final String TRANSPORT_LSMALL = "30";
    public static final String TRANSPORT_SELF = "40";

    public static final String MESSAGE_ORDER = "10"; //订单消息
    public static final String MESSAGE_GOODS = "20"; //商品消息
    public static final String MESSAGE_OTHER = "30"; //其他消息

    public static String BARCODE_ACTION;

    public static double latitude = 0;
    public static double lontitude = 0;

    public static final String PUSH_API_KEY = "uYPbKR6ZEPLMOvjNBS5gCiaG";
    public static String CHANNELID = "";

    public static ImageSize imageSize = new ImageSize(700, 700, 90);
    public static final int VALIDATECODE_TIME = 60;
    public static final int VALIDATECODE_UI = 9999;

    public static final int SWIPEMENU_WIDTH = 70;
    public static final int SWIPEMENU_WIDTH_B = 100;
    public static final int SWIPEMENU_TITLESIZE = 18;

}
