package com.zhtaxi.haodi.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.zhtaxi.haodi.R;
import com.zhtaxi.haodi.util.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Fragment基类
 * Created by NickKong on 16/7/7.
 */
public class BaseFragment extends Fragment{

    public SharedPreferences.Editor editor_user;
    public SharedPreferences sp_user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp_user = getActivity().getSharedPreferences(Constant.SP_KEY, Activity.MODE_PRIVATE);
        editor_user = sp_user.edit();
    }

    /**
     * 重写跳转页面方法
     * 主要是统一切换动画
     */
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        startActivityAnimation();
    }

    /**
     * 重写跳转页面方法
     * 淡出淡入动画
     */
    protected void startActivityByFade(Intent intent){
        super.startActivity(intent);
        startActivityAnimationByFade();
    }

    /**
     * 重写带返回参数的跳转页面方法
     * 主要是统一切换动画
     */
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        startActivityAnimation();
    }

    /**
     * 设置页面跳转动画
     */
    private void startActivityAnimation(){
        getActivity().overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
    }

    /**
     * 设置页面跳转动画，淡出淡入
     */
    private void startActivityAnimationByFade(){
        getActivity().overridePendingTransition(R.anim.abc_fade_in, android.R.anim.fade_out);
    }

    /**
     * 根据userId判断是否需要登录
     */
    protected boolean needLogin(){

        return "".equals(sp_user.getString("userId", ""));
    }

    /**
     * 封装调用登录后接口的固定参数
     */
    protected Map<String, Object> generateRequestMap() {

        Map<String, Object> map = new HashMap();
        map.put("userId", sp_user.getString("userId",""));
        map.put("haode_session_id", sp_user.getString("sessionId",""));

        return map;
    }
}
