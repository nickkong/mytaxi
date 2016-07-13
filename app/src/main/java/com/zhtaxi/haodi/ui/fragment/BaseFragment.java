package com.zhtaxi.haodi.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.zhtaxi.haodi.R;
import com.zhtaxi.haodi.util.Constant;

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
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        activityAnimation();
    }

    /**
     * 重写带返回参数的跳转页面方法
     * 主要是统一切换动画
     */
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        activityAnimation();
    }

    /**
     * 设置页面跳转动画
     */
    private void activityAnimation(){
        getActivity().overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
    }
}
