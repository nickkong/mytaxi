package com.zhtaxi.haodi.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.zhtaxi.haodi.R;

/**
 * 消息中心
 * Created by NickKong on 16/7/2.
 */
public class MessageActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message);

        initView();
    }

    /**
     * 初始化控件
     */
    @Override
    public void initView() {
        View ll_back = findViewById(R.id.ll_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFinish();
            }
        });
    }
}
