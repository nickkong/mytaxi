package com.zhtaxi.haodi.ui.activity;

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                doFinish();
                break;
        }
    }

}
