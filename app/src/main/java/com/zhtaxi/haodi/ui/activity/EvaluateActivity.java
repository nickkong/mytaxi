package com.zhtaxi.haodi.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.nickkong.commonlibrary.ui.activity.BaseActivity;
import com.zhtaxi.haodi.R;

/**
 * 评价司机页面
 * Created by NickKong on 16/8/26.
 */
public class EvaluateActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_evaluate);

        initView();
    }

    @Override
    protected void initView() {

        ImageView iv_evaluate_good = (ImageView) findViewById(R.id.iv_evaluate_good);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            //好评
            case R.id.iv_evaluate_good:
                doFinishByFade();
                break;
            //差评
            case R.id.iv_evaluate_bad:
                doFinishByFade();
                break;
        }
    }
}
