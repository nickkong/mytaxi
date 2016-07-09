package com.zhtaxi.haodi.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zhtaxi.haodi.R;

/**
 * 设置
 * Created by NickKong on 16/7/9.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        initView();
    }

    @Override
    protected void initView() {

        View ll_back = findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.ll_back:
                doFinish();
                break;
        }
    }
}
