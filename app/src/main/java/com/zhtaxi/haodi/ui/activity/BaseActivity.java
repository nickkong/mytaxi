package com.zhtaxi.haodi.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.KeyEvent;

import com.umeng.analytics.MobclickAgent;
import com.zhtaxi.haodi.R;
import com.zhtaxi.haodi.util.Constant;
import com.zhtaxi.haodi.widget.LoadingDialog;

/**
 * Activity基类
 * Created by NickKong on 16/7/7.
 */
public abstract class BaseActivity extends FragmentActivity{

    public SharedPreferences.Editor editor_user;
    public SharedPreferences sp_user;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp_user = getSharedPreferences(Constant.SP_KEY, Activity.MODE_PRIVATE);
        editor_user = sp_user.edit();
    }

    /**
     * 自定义跳转页面方法
     * 主要是统一切换动画、增加是否关闭当前页面
     */
    protected void startActivity(Intent intent, boolean flag) {
        super.startActivity(intent);
        startActivityAnimation();
        if (flag) finish();
    }

    /**
     * 重写跳转页面方法
     * 淡出淡入动画
     */
    public void startActivityByFade(Intent intent){
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
     * 初始化控件
     */
    protected abstract void initView();

    /**
     * 带返回动画的关闭页面
     */
    protected void doFinish(){
        finish();
        returnActivityAnimation();
    }

    /**
     * 带返回动画的关闭页面,淡入淡出动画
     */
    protected void doFinishByFade(){
        finish();
        returnActivityAnimationByFade();
    }

    /**
     * 带返回动画的返回键关闭页面
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            doFinish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置页面跳转动画
     */
    private void startActivityAnimation(){
        overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
    }

    /**
     * 设置页面跳转动画，淡出淡入
     */
    private void startActivityAnimationByFade(){
        overridePendingTransition(R.anim.abc_fade_in, android.R.anim.fade_out);
    }

    /**
     * 设置页面返回跳转动画
     */
    private void returnActivityAnimation(){
        overridePendingTransition(0, android.R.anim.slide_out_right);
    }

    /**
     * 设置页面返回跳转动画，淡入淡出
     */
    private void returnActivityAnimationByFade(){
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * 根据userId判断是否需要登录
     */
    protected boolean needLogin(){

        return "".equals(sp_user.getString("userId", ""));
    }

    /**
     * 显示进度提示框
     */
    public void showLoadingDialog(String content, int type){

        if(loadingDialog == null){
            loadingDialog = new LoadingDialog(this,content,type);
            loadingDialog.getWindow().setGravity(Gravity.CENTER);
            loadingDialog.setCancelable(false);
        }
        loadingDialog.show();
    }

    /**
     * 隐藏进度提示框
     */
    public void disLoadingDialog(){

        if(loadingDialog!=null){
            loadingDialog.dismiss();
            loadingDialog = null;
        }
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
