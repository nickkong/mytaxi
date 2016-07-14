package com.zhtaxi.haodi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zhtaxi.haodi.R;

/**
 * 我的
 * Created by NickKong on 16/7/2.
 */
public class MeActivity extends BaseActivity implements View.OnClickListener{

    private String TAG = getClass().getSimpleName();

    private static final int REQUESTCODE_SETTING = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_me);

        initView();
    }

    /**
     * 初始化控件
     */
    @Override
    public void initView() {
        View ll_back = findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        View rl_topersonalinfo = findViewById(R.id.rl_topersonalinfo);
        rl_topersonalinfo.setOnClickListener(this);
        View rl_tosetting = findViewById(R.id.rl_tosetting);
        rl_tosetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //返回
            case R.id.ll_back:
                doFinish();
                break;
            //跳转个人资料页面
            case R.id.rl_topersonalinfo:
                startActivity(new Intent(this,PersonalInfoActivity.class),false);
                break;
            //跳转设置页面
            case R.id.rl_tosetting:
                startActivityForResult(new Intent(this,SettingActivity.class),REQUESTCODE_SETTING);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //退出后关闭“我的”页面
        if(requestCode == REQUESTCODE_SETTING && resultCode == RESULT_OK){
            doFinishByFade();
        }
    }
}
