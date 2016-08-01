package com.zhtaxi.haodi.ui.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nickkong.commonlibrary.ui.activity.BaseActivity;
import com.nickkong.commonlibrary.util.HttpUtil;
import com.nickkong.commonlibrary.util.Tools;
import com.zhtaxi.haodi.R;
import com.zhtaxi.haodi.util.Constant;
import com.zhtaxi.haodi.util.PublicResource;
import com.zhtaxi.haodi.util.RequestAddress;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

/**
 * 登录页面，首次验证为注册，再次验证为登录
 * Created by NickKong on 16/7/13.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private String TAG = getClass().getSimpleName();

    private static final int SUCCESSCODE_LOGIN = 1;
    private static final int SUCCESSCODE_CERTCODE = 2;
    private static final int CERTCODE_UI = 3;

    private Editor editor;

    private Button btn_getCertCode;

    private String phone;

    private int time = Constant.CERTCODE_TIME;
    private Timer timer;
    private TimerTask task;

    private EditText et_phone,et_certCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initView();
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initView() {

        //首次注册推送服务后，getRegistrationID才不为空
        if(!"".equals(JPushInterface.getRegistrationID(this))){
            PublicResource.REGISTRATION_ID = JPushInterface.getRegistrationID(this);
        }

        SharedPreferences sp = getSharedPreferences(getString(R.string.app_name), Activity.MODE_PRIVATE);
        editor = sp.edit();

        View ll_back = findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        btn_getCertCode = (Button) findViewById(R.id.btn_getCertCode);
        btn_getCertCode.setOnClickListener(this);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_certCode = (EditText) findViewById(R.id.et_certCode);

        //回显登录过的手机号码
        String sp_phone = sp.getString(Constant.SP_PHONE_KEY, "");
        if (!"".equals(sp_phone.trim())) {
            et_phone.setText(sp_phone);
            et_certCode.requestFocus();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            //返回
            case R.id.ll_back:
                doFinishByFade();
                break;
            //登录
            case R.id.btn_login:
                if(et_phone.getText().toString().trim().matches(getString(R.string.mobile_matches))&&
                        et_certCode.getText().toString().trim().length()>0){
                    login();
                }else if(et_certCode.getText().toString().trim().length()==0){
                    Tools.showToast(this,getString(R.string.certcode_tips));
                }else if(!et_phone.getText().toString().trim().matches(getString(R.string.mobile_matches))){
                    Tools.showToast(this,getString(R.string.matchphone_tips));
                }
                break;
            //获取短信验证码
            case R.id.btn_getCertCode:
                if(et_phone.getText().toString().trim().matches(getString(R.string.mobile_matches))){
                    getCertCode();
                }else {
                    Tools.showToast(this,getString(R.string.matchphone_tips));
                }
                break;
        }
    }

    /**
     * 登录
     */
    private void login(){

        showLoadingDialog("登录中...",1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                phone = et_phone.getText().toString().trim();
                Map params = new HashMap();
                params.put("userType", "0");//0:乘客,1:司机
                params.put("mobilePhone", phone);
                params.put("certCode", et_certCode.getText().toString().trim());
                params.put("deviceId", PublicResource.REGISTRATION_ID);
                HttpUtil.doGet(TAG,LoginActivity.this,mHandler,Constant.HTTPUTIL_FAILURECODE,SUCCESSCODE_LOGIN,
                        RequestAddress.login,params);

            }
        }, 2000);

    }

    /**
     * 获取验证码
     */
    private void getCertCode(){

        showLoadingDialog("验证码已发送",2);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                disLoadingDialog();
            }
        }, 2000);

        //验证码输入框获得焦点
        et_certCode.requestFocus();
        //定时60秒
        time = Constant.CERTCODE_TIME;
        timer = new Timer(true);

        task = new TimerTask() {
            public void run() {
                mHandler.sendEmptyMessage(CERTCODE_UI);
            }
        };
        timer.schedule(task, 0, 1000);

        phone = et_phone.getText().toString().trim();
        Map params = new HashMap();
        params.put("mobilePhone", phone);
        HttpUtil.doGet(TAG,this,mHandler,Constant.HTTPUTIL_FAILURECODE,SUCCESSCODE_CERTCODE,
                RequestAddress.getCertCode,params);
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            String message = (String) msg.obj;
            switch (msg.what) {
                case Constant.HTTPUTIL_FAILURECODE:

                    break;
                //登录
                case SUCCESSCODE_LOGIN:

                    disLoadingDialog();

                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        String result = jsonObject.getString("result");
                        //注册/登录成功，返回上一页
                        if (Constant.RECODE_SUCCESS.equals(result)) {

                            String userId = jsonObject.getString("userId");
                            String sessionId = jsonObject.getString("sessionId");
                            editor_user.putString("userId",userId);
                            editor_user.putString("sessionId",sessionId);
                            editor_user.commit();

                            //保存手机号码，方便下次登录时可以回显
                            editor.putString(Constant.SP_PHONE_KEY, phone);
                            editor.commit();

                            doFinishByFade();
                        }
                        else if (Constant.RECODE_FAILED.equals(result)) {
                            String errMsgs = jsonObject.getString("errMsgs");
                            Tools.showToast(LoginActivity.this,errMsgs);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                //获取验证码
                case SUCCESSCODE_CERTCODE:
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        String result = jsonObject.getString("result");
                        if (Constant.RECODE_SUCCESS.equals(result)) {

                        }
                        else if (Constant.RECODE_FAILED.equals(result)) {
                            String errMsgs = jsonObject.getString("errMsgs");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                //更新倒数
                case CERTCODE_UI:
                    time--;
                    btn_getCertCode.setEnabled(false);
                    btn_getCertCode.setBackgroundResource(R.drawable.border_radius_mainsub_disable);
                    btn_getCertCode.setText(time+"秒");
                    if (time == 0) {
                        task.cancel();
                        task = null;
                        timer.cancel();
                        timer = null;
                        btn_getCertCode.setText("获取");
                        btn_getCertCode.setEnabled(true);
                        btn_getCertCode.setBackgroundResource(R.drawable.border_radius_mainsub_enable);
                    }
                    break;
            }
        }
    };

    /**
     * 带返回动画的返回键关闭页面
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            doFinishByFade();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
