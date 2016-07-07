package com.zhtaxi.haodi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * 基类
 * Created by NickKong on 16/7/7.
 */
public class BaseFragment extends Fragment{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        activityAnimation();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        activityAnimation();
    }

    /**
     * 设置页面跳转动画
     */
    private void activityAnimation(){
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
