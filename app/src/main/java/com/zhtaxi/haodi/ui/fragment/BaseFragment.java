package com.zhtaxi.haodi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.zhtaxi.haodi.R;

/**
 * Fragment基类
 * Created by NickKong on 16/7/7.
 */
public class BaseFragment extends Fragment{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
