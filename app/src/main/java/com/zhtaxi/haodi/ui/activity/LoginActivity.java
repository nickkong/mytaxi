package com.zhtaxi.haodi.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zhtaxi.haodi.R;
import com.zhtaxi.haodi.util.Constant;
import com.zhtaxi.haodi.util.HttpUtil;
import com.zhtaxi.haodi.util.RequestAddress;
import com.zhtaxi.haodi.util.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 登录页面，首次验证为注册，再次验证为登录
 * Created by NickKong on 16/7/13.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private String TAG = getClass().getSimpleName();

    private static final int SUCCESSCODE_LOGIN = 1;
    private static final int SUCCESSCODE_CERTCODE = 2;
    private static final int CERTCODE_UI = 3;

    private Button btn_getCertCode;

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

        View ll_back = findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        btn_getCertCode = (Button) findViewById(R.id.btn_getCertCode);
        btn_getCertCode.setOnClickListener(this);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_certCode = (EditText) findViewById(R.id.et_certCode);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            //返回
            case R.id.ll_back:
                doFinish();
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
        Map params = new HashMap();
        params.put("mobilePhone", et_phone.getText().toString());
        params.put("certCode", et_certCode.getText().toString());
        HttpUtil.doGet(TAG,this,mHandler,Constant.HTTPUTIL_FAILURECODE,SUCCESSCODE_LOGIN,
                RequestAddress.login,params);
    }

    /**
     * 获取验证码
     */
    private void getCertCode(){
        //验证码输入框获得焦点
        et_certCode.setFocusable(true);
        et_certCode.setFocusableInTouchMode(true);
        et_certCode.requestFocus();
        //定时60秒
        time = Constant.CERTCODE_TIME;
        timer = new Timer(true);
        Map params = new HashMap();
        params.put("mobilePhone", et_phone.getText().toString());
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
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        String result = jsonObject.getString("result");
                        if (Constant.RECODE_SUCCESS.equals(result)) {
//                            JSONArray datalist = jsonObject.getJSONArray("datalist");
//                            Gson gson = new Gson();
//                            Type listType = new TypeToken<List<GoodsPropertysData>>() {
//                            }.getType();
//                            Constant.propertys = gson.fromJson(datalist.toString(), listType);
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
                            task = new TimerTask() {
                                public void run() {
                                    mHandler.sendEmptyMessage(CERTCODE_UI);
                                }
                            };
                            timer.schedule(task, 0, 1000);
                        }
                        else if (Constant.RECODE_FAILED.equals(result)) {
                            String errMsgs = jsonObject.getString("errMsgs");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case CERTCODE_UI:
                    time--;
                    btn_getCertCode.setEnabled(false);
//                        btn_getCertCode.setTextColor(getResources().getColor(R.color.TEXT_SUB));
//                        btn_getCertCode.setBackgroundResource(R.drawable.button_bg_border_main_pressed);
                    btn_getCertCode.setText(time+"秒");
                    if (time == 0) {
                        task.cancel();
                        task = null;
                        timer.cancel();
                        timer = null;
                        btn_getCertCode.setText("获取");
                        btn_getCertCode.setEnabled(true);
//                            btn_getCertCode.setTextColor(getResources().getColor(R.color.MAIN));
//                            btn_getCertCode.setBackgroundResource(R.drawable.button_bg_border_main_normal);
                    }
                    break;
            }
        }
    };
}
