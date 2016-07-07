package com.zhtaxi.haodi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
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
import com.umeng.analytics.MobclickAgent;
import com.zhtaxi.haodi.HaodiApplication;
import com.zhtaxi.haodi.R;
import com.zhtaxi.haodi.service.LocationService;
import com.zhtaxi.haodi.ui.activity.BaseActivity;
import com.zhtaxi.haodi.ui.activity.MeActivity;
import com.zhtaxi.haodi.ui.activity.MessageActivity;
import com.zhtaxi.haodi.ui.fragment.HuishouFragment;
import com.zhtaxi.haodi.ui.fragment.YuecheFragment;
import com.zhtaxi.haodi.ui.listener.OnHuishouBtnClickListener;
import com.zhtaxi.haodi.ui.listener.OnYuecheBtnClickListener;
import com.zhtaxi.haodi.util.Tools;
import com.zhtaxi.haodi.util.UpdateManager;
import com.zhtaxi.haodi.widget.CustomViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 主界面，默认显示挥手叫车
 * Created by NickKong on 16/7/2.
 */
public class MainActivity extends BaseActivity implements OnClickListener,
                    OnYuecheBtnClickListener,OnHuishouBtnClickListener {

    private String TAG = getClass().getSimpleName();

    private static final int APPEAR_DELAY = 2000;
    private static final int DISAPPEAR_DELAY = 2500;

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
     *地图初始化
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
        LatLng ll = new LatLng(22.275715,113.534735);
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
        switch (v.getId()){
            case R.id.btn_message:
                startActivity(new Intent(this, MessageActivity.class),false);
                break;
            case R.id.btn_me:
                startActivity(new Intent(this, MeActivity.class),false);
                break;
        }
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
                tab1.address_start.setText(location.getLocationDescribe());
                tab2.address_start.setText(location.getLocationDescribe());
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
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        locationService = ((HaodiApplication)getApplication()).locationService;
        locationService.registerListener(myListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();
        super.onStart();
    }

    @Override
    protected void onStop() {
        locationService.unregisterListener(myListener);
        locationService.stop();
        super.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

}
