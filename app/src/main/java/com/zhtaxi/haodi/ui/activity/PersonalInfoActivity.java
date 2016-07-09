package com.zhtaxi.haodi.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.zhtaxi.haodi.R;

/**
 * 个人资料
 * Created by NickKong on 16/7/9.
 */
public class PersonalInfoActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_personalinfo);

        initView();

    }

    /**
     * 初始化控件
     */
    @Override
    protected void initView() {

        View ll_back = findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            //返回
            case R.id.ll_back:
                doFinish();
                break;
        }

    }
}
