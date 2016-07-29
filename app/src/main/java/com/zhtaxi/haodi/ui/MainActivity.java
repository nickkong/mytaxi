package com.zhtaxi.haodi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
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

    private static final int SUCCESSCODE_UPLOADGPS = 1;
    private static final int SUCCESSCODE_QUERYNEARBYUSERS = 2;
    private static final int HANDLER_UPLOADGPS = 3;

    private long exitTime = 0;
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

        setContentView(R.layout.activity_main);

        initView();

        initControl();

        initMap();

        initWelcomePage();

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

        //未登录，跳转注册/登录页面
        if(needLogin()){
            startActivityByFade(new Intent(this, LoginActivity.class));
        }else {
            switch (v.getId()){
                //进入消息中心
                case R.id.btn_message:
                    startActivity(new Intent(this, MessageActivity.class),false);
//                    getNearByUsers();
                    break;
                //进入我的
                case R.id.btn_me:
                    startActivity(new Intent(this, MeActivity.class),false);
//                    getPlace();
                    break;
            }
        }
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

    private void getNearByUsers(){

//        Map params = generateRequestMap();
        Map<String, Object> params = new HashMap();
//        params.put("userId", sp_user.getString("userId",""));
        params.put("lat", "23.658819");
        params.put("lng", "116.607008");
//        params.put("distanceLessThan", "5");
        HttpUtil.doGet(TAG,this,mHandler, Constant.HTTPUTIL_FAILURECODE,SUCCESSCODE_QUERYNEARBYUSERS,
                RequestAddress.queryNearByUsers,params);

    }

    /**
     * 上传gps
     * userId,licensePlate(车牌号码)，isTrip(是否行程中)，orderNo(订单号)，mapType(地图类型)，locations
     * isTrip为0时不用传orderNo
     * locations=纬度1,经度1,时间1long型;纬度2,经度2,时间2long型;纬度3,经度3,时间3long型;纬度4,经度4,时间4long型;
     */
    private void uploadGps(){
//        Log.d(TAG,"sb.toString()==="+sb.toString());
        Map params = generateRequestMap();
        params.put("licensePlate", "粤Y99999");
        params.put("isTrip", "0");
//        params.put("orderNo", "0");
        params.put("mapType", "0");
        params.put("locations", sb.toString());
        HttpUtil.doGet(TAG,this,mHandler, Constant.HTTPUTIL_FAILURECODE,SUCCESSCODE_UPLOADGPS,
                RequestAddress.uploadGps,params);
    }

    @Override
    public void doYueche() {
        vp_control.setCurrentItem(1);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(17.9f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        mBaiduMap.clear();
        addMarker(mylocation);
    }

    @Override
    public void doHuishou() {
        vp_control.setCurrentItem(0);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(18.1f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        mBaiduMap.clear();
        addMarker(mylocation);
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

            //经度和纬度都不是返回4.9E-324才记录
            if(!Constant.LOCATION_ERROR.equals(location.getLatitude())&&
                    !Constant.LOCATION_ERROR.equals(location.getLongitude())){
                sb.append(location.getLatitude()+","+location.getLongitude()+","+System.currentTimeMillis()+";");
            }

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(location.getDerect()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            mBaiduMap.setMyLocationData(locData);

            if (isFirstLoc) {

                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
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
                .icon(bitmap);
        OverlayOptions makeroption1 = new MarkerOptions()
                .position(point1).rotate(fnum)
                .icon(bitmap);
        OverlayOptions makeroption2 = new MarkerOptions()
                .position(point2).rotate(-fnum)
                .icon(bitmap);
        OverlayOptions makeroption3 = new MarkerOptions()
                .position(point3).rotate(fnum)
                .icon(bitmap);
        OverlayOptions makeroption4 = new MarkerOptions()
                .position(point4).rotate(-fnum)
                .icon(bitmap);
        OverlayOptions makeroption5 = new MarkerOptions()
                .position(point5).rotate(fnum)
                .icon(bitmap);
        OverlayOptions makeroption6 = new MarkerOptions()
                .position(point6).rotate(fnum)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(makeroption);
        mBaiduMap.addOverlay(makeroption1);
        mBaiduMap.addOverlay(makeroption2);
        mBaiduMap.addOverlay(makeroption3);
        mBaiduMap.addOverlay(makeroption4);
        mBaiduMap.addOverlay(makeroption5);
        mBaiduMap.addOverlay(makeroption6);
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

                    break;
                //定时上传GPS
                case HANDLER_UPLOADGPS:
                    uploadGps();
                    sb = new StringBuffer();
                    break;
            }
        }
    };

    /**
     * 按钮事件监听
     */
    private OnDialogClickListener dialogClickListener = new OnDialogClickListener() {
        @Override
        public void doConfirm() {
            //未登录，跳转注册/登录页面
            if(needLogin()){
                startActivityByFade(new Intent(MainActivity.this, LoginActivity.class));
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
