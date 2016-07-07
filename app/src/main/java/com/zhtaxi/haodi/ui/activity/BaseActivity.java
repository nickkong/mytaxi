package com.zhtaxi.haodi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.umeng.analytics.MobclickAgent;
import com.zhtaxi.haodi.R;

/**
 * 基类
 * Created by NickKong on 16/7/7.
 */
public abstract class BaseActivity extends FragmentActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected void startActivity(Intent intent, boolean flag) {
        startActivity(intent);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
        if (flag) finish();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
    }

    protected abstract void initView();


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
