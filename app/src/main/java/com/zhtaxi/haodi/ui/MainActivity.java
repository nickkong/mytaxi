package com.zhtaxi.haodi.ui;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.nickkong.commonlibrary.service.LocationService;
import com.nickkong.commonlibrary.ui.activity.BaseActivity;
import com.nickkong.commonlibrary.ui.listener.OnDialogClickListener;
import com.nickkong.commonlibrary.util.HttpUtil;
import com.nickkong.commonlibrary.util.Tools;
import com.umeng.analytics.MobclickAgent;
import com.zhtaxi.haodi.HaodiApplication;
import com.zhtaxi.haodi.R;
import com.zhtaxi.haodi.ui.activity.LoginActivity;
import com.zhtaxi.haodi.ui.activity.MeActivity;
import com.zhtaxi.haodi.ui.activity.MessageActivity;
import com.zhtaxi.haodi.ui.fragment.HuishouFragment;
import com.zhtaxi.haodi.ui.fragment.YuecheFragment;
import com.zhtaxi.haodi.ui.listener.OnHuishouBtnClickListener;
import com.zhtaxi.haodi.ui.listener.OnYuecheBtnClickListener;
import com.zhtaxi.haodi.util.Constant;
import com.zhtaxi.haodi.util.RequestAddress;
import com.zhtaxi.haodi.util.UpdateManager;
import com.zhtaxi.haodi.widget.CustomViewPager;
import com.zhtaxi.haodi.widget.zxing.activity.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 主界面，默认显示挥手叫车
 * Created by NickKong on 16/7/2.
 */
public class MainActivity extends BaseActivity implements OnClickListener,
                    OnYuecheBtnClickListener,OnHuishouBtnClickListener {

    private String TAG = getClass().getSimpleName();

    private static final int APPEAR_DELAY = 2000;
    private static final int DISAPPEAR_DELAY = 2500;
    private static final int UPLOADGPS_PERIOD = 5000;
    private static final int GETNEARBYUSERS_PERIOD = 10000;

    private static final int SUCCESSCODE_UPLOADGPS = 1;
    private static final int SUCCESSCODE_QUERYNEARBYUSERS = 2;
    private static final int HANDLER_UPLOADGPS = 3;
    private static final int SUCCESSCODE_HUISHOU = 4;
    private static final int SUCCESSCODE_CANCEL = 5;
    private static final int HANDLER_GETNEARBYUSERS = 6;

    private Button btn_yueche,btn_huishou;
    private long exitTime = 0;
    private int screenWidth;
    private LocationService locationService;
    private CustomViewPager vp_control;
    private List<Fragment> pages;
    private HuishouFragment tab1;
    private YuecheFragment tab2;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private BDLocation mylocation;
    private boolean isFirstLoc = true;

    private StringBuffer sb = new StringBuffer();
    private Timer timer;
    private TimerTask task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        setContentView(R.layout.activity_main2);

        initView();

        initControl();

        initMap();

//        initWelcomePage();

        checkUpdate();

        MobclickAgent.enableEncrypt(true);

    }

    /**
     * 初始化控件
     */
    @Override
    public void initView(){

        Button requestLocButton = (Button) findViewById(R.id.btn_getlocation);
        Button btn_message = (Button) findViewById(R.id.btn_message);
        btn_message.setOnClickListener(this);
        Button btn_me = (Button) findViewById(R.id.btn_me);
        btn_me.setOnClickListener(this);
        Button btn_more = (Button) findViewById(R.id.btn_more);
        btn_more.setOnClickListener(this);
        btn_yueche = (Button) findViewById(R.id.btn_yueche);
        btn_yueche.setOnClickListener(this);
        btn_huishou = (Button) findViewById(R.id.btn_huishou);
        btn_huishou.setOnClickListener(this);
    }

    /**
     * 初始化地图
     */
    private void initMap(){

        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.showZoomControls(false);
        mMapView.showScaleControl(false);
        mMapView.setLogoPosition(LogoPosition.logoPostionleftTop);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.mipmap.car_bearing);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                LocationMode.NORMAL, true, null));
        //佛山 23.031033,113.131019
        //珠海 22.256915,113.562447
        LatLng ll = new LatLng(22.256915,113.562447);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(15.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        mBaiduMap.setOnMarkerClickListener(listener);
    }

    /**
     * 检查更新
     */
    private void checkUpdate(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    //wifi状态检查更新
                    if(Tools.isWifiConnected(MainActivity.this)){
                        UpdateManager mUpdateManager = new UpdateManager(MainActivity.this);
                        mUpdateManager.sendUpdateRequest();
                    }
                } catch (Exception e) {
                }
            }
        }, DISAPPEAR_DELAY);
    }

    /**
     * 初始化欢迎页
     */
    private void initWelcomePage(){
        final ImageView iv_welcome;
        iv_welcome = (ImageView) findViewById(R.id.iv_welcome);
        iv_welcome.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimationSet animationSet = new AnimationSet(true);
                AlphaAnimation alphaAnimation = new AlphaAnimation(1,0);
                alphaAnimation.setDuration(DISAPPEAR_DELAY - APPEAR_DELAY);
                animationSet.addAnimation(alphaAnimation);
                iv_welcome.setAnimation(animationSet);
            }
        }, APPEAR_DELAY);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                iv_welcome.setVisibility(View.GONE);

                try {
                    //wifi状态检查更新
                    if(Tools.isWifiConnected(MainActivity.this)){
                        UpdateManager mUpdateManager = new UpdateManager(MainActivity.this);
                        mUpdateManager.sendUpdateRequest();
                    }
                } catch (Exception e) {
                }
            }
        }, DISAPPEAR_DELAY);
    }

    /**
     * 初始化底部操作栏viewpager
     */
    private void initControl(){
        vp_control = (CustomViewPager) findViewById(R.id.vp_control);
        vp_control.setScanScroll(false);
        pages = new ArrayList<>();

        tab1 = new HuishouFragment();
        tab2 = new YuecheFragment();

        pages.add(tab1);
        pages.add(tab2);

        FragmentPagerAdapter controlAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                return super.instantiateItem(container, position);
            }

            @Override
            public int getCount() {
                return pages.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return pages.get(arg0);
            }
        };
        vp_control.setAdapter(controlAdapter);
    }

    @Override
    public void onClick(View v) {

        //不需要校验登录也能操作的功能
        if(v.getId()==R.id.btn_yueche || v.getId()==R.id.btn_huishou){
            switch (v.getId()){
                case R.id.btn_yueche:
                    resetView();
                    btn_yueche.setTextColor(getResources().getColor(R.color.MAIN));
                    vp_control.setCurrentItem(1);
                    MapStatus.Builder builder_yueche = new MapStatus.Builder();
                    if(mylocation!=null){
                        LatLng point = new LatLng(mylocation.getLatitude(),mylocation.getLongitude());
                        builder_yueche.target(point).zoom(17.9f);
                    }else {
                        builder_yueche.zoom(17.9f);
                    }
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder_yueche.build()));
                    mBaiduMap.clear();
                    addMarker(mylocation);
                    break;
                case R.id.btn_huishou:
                    resetView();
                    btn_huishou.setTextColor(getResources().getColor(R.color.MAIN));
                    vp_control.setCurrentItem(0);
                    MapStatus.Builder builder_huishou = new MapStatus.Builder();
                    if(mylocation!=null){
                        LatLng point = new LatLng(mylocation.getLatitude(),mylocation.getLongitude());
                        builder_huishou.target(point).zoom(18.1f);
                    }else {
                        builder_huishou.zoom(18.1f);
                    }
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder_huishou.build()));
                    mBaiduMap.clear();
                    addMarker(mylocation);
                    break;
            }
        }
        //需要校验登录也能操作的功能
        else {
            //未登录，跳转注册/登录页面
            if(needLogin()){
                startActivityByFade(new Intent(this, LoginActivity.class),false);
            }else {
                switch (v.getId()){
                    //进入消息中心
                    case R.id.btn_message:
                        startActivity(new Intent(this, MessageActivity.class),false);
                        break;
                    //进入我的
                    case R.id.btn_me:
                        startActivity(new Intent(this, MeActivity.class),false);
                        break;
                    //
                    case R.id.btn_more:
//                    showPopupWindow();
                        startActivity(new Intent(MainActivity.this, CaptureActivity.class),false);
//                        getNearByUsers();
                        break;
                }
            }
        }
    }

    /**
     * 恢复按钮状态
     */
    private void resetView(){
        btn_yueche.setTextColor(getResources().getColor(R.color.white));
        btn_huishou.setTextColor(getResources().getColor(R.color.white));
    }

    /**
     * 定时执行上传gps
     */
    private void doUploadGps(){

        timer = new Timer(true);

        task = new TimerTask() {

            public void run() {
                mHandler.sendEmptyMessage(HANDLER_UPLOADGPS);
            }
        };

        timer.schedule(task, UPLOADGPS_PERIOD, UPLOADGPS_PERIOD);

    }

    /**
     * 定时执行获取附近车和人
     */
    private void doGetNearByUsers(){

        timer = new Timer(true);

        task = new TimerTask() {

            public void run() {
                mHandler.sendEmptyMessage(HANDLER_GETNEARBYUSERS);
            }
        };

        timer.schedule(task, 0, UPLOADGPS_PERIOD);

    }

    /**
     * 获取附近车辆和人位置信息
     */
    private void getNearByUsers(){
        Log.d(TAG,"mylocation==="+mylocation);
        if(mylocation!=null){
            Map<String, Object> params = new HashMap();
            params.put("lat", mylocation.getLatitude()+"");
            params.put("lng", mylocation.getLongitude()+"");
//        params.put("distanceLessThan", "5");
            HttpUtil.doGet(TAG,this,mHandler, Constant.HTTPUTIL_FAILURECODE,SUCCESSCODE_QUERYNEARBYUSERS,
                    RequestAddress.queryNearByUsers,params);
        }

    }

    /**
     * 上传gps
     * userId,licensePlate(车牌号码)，isTrip(是否行程中)，orderNo(订单号)，mapType(地图类型)，locations
     * isTrip为0时不用传orderNo
     * locations=纬度1,经度1,时间1long型;纬度2,经度2,时间2long型;纬度3,经度3,时间3long型;纬度4,经度4,时间4long型;
     */
    private void uploadGps(){
        if(sb.toString().length()>0){
            Map params = generateRequestMap();
//        params.put("licensePlate", "粤Y99999");
            params.put("isTrip", "0");
//        params.put("orderNo", "0");
            params.put("mapType", "0");
            params.put("locations", sb.toString());
            HttpUtil.doGet(TAG,this,mHandler, Constant.HTTPUTIL_FAILURECODE,SUCCESSCODE_UPLOADGPS,
                    RequestAddress.uploadGps,params);
        }
    }

    @Override
    public void change2Yueche() {
        vp_control.setCurrentItem(1);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(17.9f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        mBaiduMap.clear();
        addMarker(mylocation);
    }

    @Override
    public void doHuishou() {
        showLoadingDialog("挥手中...",1);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                huishou();
//            }
//        }, 2000);
        huishou();
    }

    @Override
    public void cancelHuishou() {
        cancel();
    }

    @Override
    public void change2Huishou() {
        vp_control.setCurrentItem(0);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(18.1f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        mBaiduMap.clear();
        addMarker(mylocation);
    }

    /**
     * 触发挥手叫车
     */
    private void huishou(){

        Map params = generateRequestMap();
//        params.put("assignName", "0");
        params.put("assignLat", mylocation.getLatitude()+"");
        params.put("assignLng", mylocation.getLongitude()+"");
        params.put("mapType", "0");
        HttpUtil.doGet(TAG,MainActivity.this,mHandler,Constant.HTTPUTIL_FAILURECODE,SUCCESSCODE_HUISHOU,
                RequestAddress.startWave,params);
    }

    /**
     * 取消挥手叫车
     */
    private void cancel(){

        Map params = generateRequestMap();
        HttpUtil.doGet(TAG,MainActivity.this,mHandler,Constant.HTTPUTIL_FAILURECODE,SUCCESSCODE_CANCEL,
                RequestAddress.stopWave,params);
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mylocation = location;

            if(tab1!=null && tab2!=null){
                if(tab1.address_start!=null && tab2.address_start!=null){
                    tab1.address_start.setText(location.getLocationDescribe());
                    tab2.address_start.setText(location.getLocationDescribe());
                }
            }

            double lat = location.getLatitude();
            double lng = location.getLongitude();

            //经度和纬度都不是返回4.9E-324才记录
            if(!Constant.LOCATION_ERROR.equals(lat+"") && !Constant.LOCATION_ERROR.equals(lng+"")){
                sb.append(lat+","+lng+","+System.currentTimeMillis()+";");
            }

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(location.getDerect()).latitude(lat)
                    .longitude(lng).build();

            mBaiduMap.setMyLocationData(locData);

            if (isFirstLoc) {

                isFirstLoc = false;
                LatLng ll = new LatLng(lat,lng);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

                addMarker(location);

            }
        }
    }

    /**
     * 模拟添加附近车辆
     */
    private void addMarker(BDLocation location){

        Random ra =new Random();
        //定义Maker坐标点
        LatLng point = new LatLng(location.getLatitude()+ra.nextDouble()*0.001, location.getLongitude()+ra.nextDouble()*0.001);
        LatLng point1 = new LatLng(location.getLatitude()+ra.nextDouble()*0.001, location.getLongitude()-ra.nextDouble()*0.001);
        LatLng point2 = new LatLng(location.getLatitude()-ra.nextDouble()*0.001, location.getLongitude()+ra.nextDouble()*0.001);
        LatLng point3 = new LatLng(location.getLatitude()-ra.nextDouble()*0.001, location.getLongitude()+ra.nextDouble()*0.001);
        LatLng point4 = new LatLng(location.getLatitude()+ra.nextDouble()*0.001, location.getLongitude()-ra.nextDouble()*0.001);
        LatLng point5 = new LatLng(location.getLatitude()+ra.nextDouble()*0.001, location.getLongitude()-ra.nextDouble()*0.001);
        LatLng point6 = new LatLng(location.getLatitude()+ra.nextDouble()*0.001, location.getLongitude()-ra.nextDouble()*0.001);

        int num = (int)(1+Math.random()*(10-1+1));
        int fnum = num*10;

        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.car_bearing);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions makeroption = new MarkerOptions()
                .position(point).rotate(-fnum)
                .icon(bitmap).title("0");
        OverlayOptions makeroption1 = new MarkerOptions()
                .position(point1).rotate(fnum)
                .icon(bitmap).title("1");
        OverlayOptions makeroption2 = new MarkerOptions()
                .position(point2).rotate(-fnum)
                .icon(bitmap).title("2");
        OverlayOptions makeroption3 = new MarkerOptions()
                .position(point3).rotate(fnum)
                .icon(bitmap).title("3");
        OverlayOptions makeroption4 = new MarkerOptions()
                .position(point4).rotate(-fnum)
                .icon(bitmap).title("4");
        OverlayOptions makeroption5 = new MarkerOptions()
                .position(point5).rotate(fnum)
                .icon(bitmap).title("5");
        OverlayOptions makeroption6 = new MarkerOptions()
                .position(point6).rotate(fnum)
                .icon(bitmap).title("6");
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(makeroption);
        mBaiduMap.addOverlay(makeroption1);
        mBaiduMap.addOverlay(makeroption2);
        mBaiduMap.addOverlay(makeroption3);
        mBaiduMap.addOverlay(makeroption4);
        mBaiduMap.addOverlay(makeroption5);
        mBaiduMap.addOverlay(makeroption6);

    }

    /**
     * 点击marker显示信息框
     */
    private void showInfoWindow(LatLng pt){
        View view = getLayoutInflater().inflate(R.layout.infowindow,null);
//        //创建InfoWindow展示的view
//        Button button = new Button(getApplicationContext());
//        button.setBackgroundResource(R.color.TEXT_BG);
//        button.setText("弹出框");
        //定义用于显示该InfoWindow的坐标点
//        LatLng pt = new LatLng(39.86923, 116.397428);
        //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
        InfoWindow mInfoWindow = new InfoWindow(view, pt, -47);
        //显示InfoWindow
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            String message = (String) msg.obj;
            switch (msg.what) {
                case Constant.HTTPUTIL_FAILURECODE:

                    break;
                //上传gps
                case SUCCESSCODE_UPLOADGPS:
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        String result = jsonObject.getString("result");
                        //注册/登录成功，返回上一页
                        if (Constant.RECODE_SUCCESS.equals(result)) {

                        }
                        else if (Constant.RECODE_FAILED.equals(result)) {
                            String errMsgs = jsonObject.getString("errMsgs");

                        }
                        else if (Constant.RECODE_FAILED_SESSION_WRONG.equals(result)) {
                            reLogin();
                            showTipsDialog("登录信息失效，请重新登录",1,dialogClickListener);
                            //取消自动上传位置信息
                            task.cancel();
                            task = null;
                            timer.cancel();
                            timer = null;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                //获取附近车辆
                case SUCCESSCODE_QUERYNEARBYUSERS:
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        String result = jsonObject.getString("result");
//                        Tools.showToast(MainActivity.this,message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                //定时上传GPS
                case HANDLER_UPLOADGPS:
                    uploadGps();
                    sb = new StringBuffer();
                    break;
                //定时获取附近人和车
                case HANDLER_GETNEARBYUSERS:
                    getNearByUsers();
                    break;
                //触发挥手叫车
                case SUCCESSCODE_HUISHOU:
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        String result = jsonObject.getString("result");
                        disLoadingDialog();
                        //挥手成功
                        if (Constant.RECODE_SUCCESS.equals(result)) {
                            String noticeCarNum = jsonObject.getString("noticeCarNum");

                            if("0".equals(noticeCarNum)){
                                showTipsDialog("附近暂时没有司机",0,null);
                            }else {
                                showTipsDialog("已通知附近"+noticeCarNum+"位司机",0,null);
                            }
                        }
                        else if (Constant.RECODE_FAILED.equals(result)) {
                            String errMsgs = jsonObject.getString("errMsgs");

                        }
                        else if (Constant.RECODE_FAILED_SESSION_WRONG.equals(result)) {
                            reLogin();
                            showTipsDialog("登录信息失效，请重新登录",1,dialogClickListener);
                            //取消自动上传位置信息
                            if(task!=null){
                                task.cancel();
                                task = null;
                            }
                            if(timer!=null){
                                timer.cancel();
                                timer = null;
                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                //取消挥手叫车
                case SUCCESSCODE_CANCEL:
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        String result = jsonObject.getString("result");
                        disLoadingDialog();
                        //挥手成功
                        if (Constant.RECODE_SUCCESS.equals(result)) {


                        }
                        else if (Constant.RECODE_FAILED.equals(result)) {
                            String errMsgs = jsonObject.getString("errMsgs");

                        }
                        else if (Constant.RECODE_FAILED_SESSION_WRONG.equals(result)) {
                            reLogin();
                            showTipsDialog("登录信息失效，请重新登录",1,dialogClickListener);
                            //取消自动上传位置信息
                            task.cancel();
                            task = null;
                            timer.cancel();
                            timer = null;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    /**
     * 点击车辆事件监听
     */
    BaiduMap.OnMarkerClickListener listener = new BaiduMap.OnMarkerClickListener() {

        public boolean onMarkerClick(Marker marker){

            showInfoWindow(marker.getPosition());

            return true;
        }
    };

    private PopupWindow other_popupWindow;
    private LinearLayout popupWindowLayout;
    private ListView popupwindow_listview;
    private List<Map<String, Object>> popupWindowList = new ArrayList<>();
    private SimpleAdapter popupWindowAdapter;

    /**
     * 显示消息、扫一扫
     */
    private void showPopupWindow() {

        if (popupWindowLayout == null || popupwindow_listview == null) {

            popupWindowLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.popupwindow_view, null);
            popupWindowLayout.setHorizontalGravity(Gravity.CENTER);

            popupwindow_listview = (ListView) popupWindowLayout.findViewById(R.id.popupwindow_listview);
            popupwindow_listview.setClickable(true);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) popupwindow_listview.getLayoutParams();
            layoutParams.width = screenWidth / 3 - getResources().getDimensionPixelSize(R.dimen.line) * 2;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.CENTER;
            popupwindow_listview.setLayoutParams(layoutParams);

            popupwindow_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    other_popupWindow.dismiss();

                    switch ((int) ((Map<String, Object>) parent.getAdapter().getItem(position)).get("tag")) {
                        case 1:
                            Intent openCameraIntent = new Intent(MainActivity.this, CaptureActivity.class);
                            startActivity(openCameraIntent);
                            break;
                        case 2:
                            findViewById(R.id.btn_message).performClick();
                            break;

                    }

                }
            });

            popupWindowAdapter = new SimpleAdapter(this, popupWindowList, R.layout.popupwindow_item_view,
                    new String[]{"img", "txt"}, new int[]{R.id.img, R.id.txt});
            popupwindow_listview.setAdapter(popupWindowAdapter);

            other_popupWindow = new PopupWindow(popupWindowLayout,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
            other_popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources()));
            other_popupWindow.setFocusable(true);
            other_popupWindow.setTouchable(true);
            other_popupWindow.setOutsideTouchable(true);

        }

        popupWindowList.clear();

        Map<String, Object> item = new HashMap<>();
        item.put("img", R.mipmap.start);
        item.put("txt", "扫一扫");
        item.put("tag", 1);
        popupWindowList.add(item);

        item = new HashMap<>();
        item.put("img", R.mipmap.end);
        item.put("txt", "消息");
        item.put("tag", 2);
        popupWindowList.add(item);

        popupWindowAdapter.notifyDataSetChanged();

        other_popupWindow.showAsDropDown(findViewById(R.id.btn_more), screenWidth / 3, 0);
    }

    /**
     * 按钮事件监听
     */
    private OnDialogClickListener dialogClickListener = new OnDialogClickListener() {
        @Override
        public void doConfirm() {
            //未登录，跳转注册/登录页面
            if(needLogin()){
                startActivityByFade(new Intent(MainActivity.this, LoginActivity.class),false);
            }
        }

        @Override
        public void doConfirm(int type) {

        }
    };

    /**
     * 按两次手机返回键退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MobclickAgent.onKillProcess(this);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        Log.d(TAG,"===onStart===");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG,"===onStop===");
        super.onStop();
    }

    @Override
    public void onPause() {
        Log.d(TAG,"===onPause===");
        mMapView.onPause();

        locationService.unregisterListener(myListener);
        locationService.stop();

        //取消自动上传位置信息
        if(task!=null){
            task.cancel();
            task = null;
        }
        if(timer!=null){
            timer.cancel();
            timer = null;
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(TAG,"===onResume===");
        mMapView.onResume();

        locationService = ((HaodiApplication)getApplication()).locationService;
        locationService.registerListener(myListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();

        if(!needLogin()){
            sb = new StringBuffer();
            doUploadGps();
        }
//        if(!needLogin()){
//            doGetNearByUsers();
//        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"===onDestroy===");
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

}
