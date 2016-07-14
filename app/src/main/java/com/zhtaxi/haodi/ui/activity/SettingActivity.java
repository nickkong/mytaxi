package com.zhtaxi.haodi.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.zhtaxi.haodi.R;
import com.zhtaxi.haodi.util.Constant;
import com.zhtaxi.haodi.util.HttpUtil;
import com.zhtaxi.haodi.util.RequestAddress;

/**
 * 设置，包含退出
 * Created by NickKong on 16/7/9.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener{

    private String TAG = getClass().getSimpleName();

    private static final int SUCCESSCODE_LOGOUT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        initView();
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initView() {
        View ll_back = findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        View rl_logout = findViewById(R.id.rl_logout);
        rl_logout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            //返回
            case R.id.ll_back:
                doFinish();
                break;
            //退出
            case R.id.rl_logout:
                logout();
                break;
        }
    }

    /**
     * 退出
     */
    private void logout() {
        HttpUtil.doGet(TAG,this,mHandler, Constant.HTTPUTIL_FAILURECODE,SUCCESSCODE_LOGOUT,
                RequestAddress.logout,null);
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            String message = (String) msg.obj;
            switch (msg.what) {
                case Constant.HTTPUTIL_FAILURECODE:

                    break;
                //退出
                case SUCCESSCODE_LOGOUT:

                    editor_user.clear();
                    editor_user.commit();
                    setResult(RESULT_OK);
                    doFinishByFade();
//                    try {
//                        JSONObject jsonObject = new JSONObject(message);
//                        String result = jsonObject.getString("result");
//                        //注册/登录成功，返回上一页
//                        if (Constant.RECODE_SUCCESS.equals(result)) {
//                            String userId = jsonObject.getString("userId");
//                            editor_user.putString("userId",userId);
//                            editor_user.commit();
//                            doFinishByFade();
//                        }
//                        else if (Constant.RECODE_FAILED.equals(result)) {
//                            String errMsgs = jsonObject.getString("errMsgs");
//                            Tools.showToast(LoginActivity.this,errMsgs);
//
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    break;
            }
        }
    };
}
