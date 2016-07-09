package com.zhtaxi.haodi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zhtaxi.haodi.R;

/**
 * 我的
 * Created by NickKong on 16/7/2.
 */
public class MeActivity extends BaseActivity implements View.OnClickListener{

    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_me);

        initView();
    }

    @Override
    public void initView() {
        Button btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        View rl_topersonalinfo = findViewById(R.id.rl_topersonalinfo);
        rl_topersonalinfo.setOnClickListener(this);
        View rl_tosetting = findViewById(R.id.rl_tosetting);
        rl_tosetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                doFinish();
                break;
            case R.id.rl_topersonalinfo:
                startActivity(new Intent(this,PersonalInfoActivity.class),false);
                break;
            case R.id.rl_tosetting:
                startActivity(new Intent(this,SettingActivity.class),false);
                break;
        }
    }

}
