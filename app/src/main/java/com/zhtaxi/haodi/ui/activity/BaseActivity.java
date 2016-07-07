package com.zhtaxi.haodi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.umeng.analytics.MobclickAgent;

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
        activityAnimation();
        if (flag) finish();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        activityAnimation();
    }

    protected abstract void initView();

    /**
     * 带返回动画的关闭页面
     */
    protected void doFinish(){
        finish();
        activityAnimation();
    }

    /**
     * 带返回动画的返回键关闭页面
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            activityAnimation();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置页面跳转动画
     */
    private void activityAnimation(){
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * 友盟页面统计
     */
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    /**
     * 友盟页面统计
     */
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
