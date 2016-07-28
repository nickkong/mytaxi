package com.zhtaxi.haodi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nickkong.commonlibrary.ui.activity.BaseActivity;
import com.zhtaxi.haodi.R;

/**
 * 您要去哪儿，目的地搜索
 * Created by NickKong on 16/7/6.
 */
public class DestinationActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_destination);

        initView();
    }

    /**
     * 初始化控件
     */
    @Override
    public void initView() {
        Button btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        View ll_home = findViewById(R.id.ll_home);
        ll_home.setOnClickListener(this);
        View ll_company = findViewById(R.id.ll_company);
        ll_company.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            //返回
            case R.id.btn_back:
                doFinish();
                break;
            case R.id.ll_home:
                //未登录，跳转注册/登录页面
                if(needLogin()){
                    startActivityByFade(new Intent(this, LoginActivity.class));
                }else {

                }
                break;
            case R.id.ll_company:
                //未登录，跳转注册/登录页面
                if(needLogin()){
                    startActivityByFade(new Intent(this, LoginActivity.class));
                }else {

                }
                break;
        }

    }
}
