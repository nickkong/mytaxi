package com.zhtaxi.haodi.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

    @Override
    public void initView() {
        Button btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
