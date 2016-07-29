package com.zhtaxi.haodi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nickkong.commonlibrary.ui.activity.BaseActivity;
import com.nickkong.commonlibrary.util.HttpUtil;
import com.nickkong.commonlibrary.widget.pulltoRefreshAndLoad.PullToRefreshLayout;
import com.zhtaxi.haodi.R;
import com.zhtaxi.haodi.adapter.PoiListAdapter;
import com.zhtaxi.haodi.domain.PoiData;
import com.zhtaxi.haodi.util.Constant;
import com.zhtaxi.haodi.util.RequestAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 您要去哪儿，目的地搜索
 * Created by NickKong on 16/7/6.
 */
public class DestinationActivity extends BaseActivity implements View.OnClickListener{

    private String TAG = getClass().getSimpleName();

    private static final int SUCCESSCODE_GETPLACE = 1;

    private ListView listView;
    private PoiListAdapter adapter;
    private PullToRefreshLayout ptrl;
    private List<PoiData> arrays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_destination);

        initView();
    }

    /**
     * 初始化控件
     */
    @Override
    public void initView() {
        Button btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        View ll_home = findViewById(R.id.ll_home);
        ll_home.setOnClickListener(this);
        View ll_company = findViewById(R.id.ll_company);
        ll_company.setOnClickListener(this);
        ptrl = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
//        ptrl.setOnRefreshListener(new MyListener());
        listView = (ListView) findViewById(R.id.content_view);
        EditText et_addresskeyword = (EditText) findViewById(R.id.et_addresskeyword);
        et_addresskeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG,"s.toString()=="+s.toString());
                getPlace(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            //返回
            case R.id.btn_back:
                doFinish();
                break;
            case R.id.ll_home:
                //未登录，跳转注册/登录页面
                if(needLogin()){
                    startActivityByFade(new Intent(this, LoginActivity.class));
                }else {

                }
                break;
            case R.id.ll_company:
                //未登录，跳转注册/登录页面
                if(needLogin()){
                    startActivityByFade(new Intent(this, LoginActivity.class));
                }else {

                }
                break;
        }
    }

    private void getPlace(String keyword){

        Map<String, Object> params = new HashMap();
        params.put("query", keyword);
        params.put("region", "138"); //140 珠海，138 佛山
        params.put("output", "json");
        params.put("city_limit", "true");
        params.put("ak", "8wMQn0vRDF7GGrQvI1HXsLov0eWUoYFR");
        HttpUtil.doGet(TAG,this,mHandler, Constant.HTTPUTIL_FAILURECODE,SUCCESSCODE_GETPLACE,
                RequestAddress.search,params);

        //http://api.map.baidu.com/place/v2/suggestion?query=天安门&region=131&output=json&ak={您的密钥}
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            String message = (String) msg.obj;
            switch (msg.what) {
                case Constant.HTTPUTIL_FAILURECODE:

                    break;
                //根据关键字搜索相关poi
                case SUCCESSCODE_GETPLACE:
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        String status = jsonObject.getString("status");
                        //成功
                        if ("0".equals(status)) {
                            JSONArray results = jsonObject.getJSONArray("results");
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<PoiData>>() {}.getType();
                            Log.d(TAG,"results.toString()==="+results.toString());
                            arrays = gson.fromJson(results.toString(), listType);
                            adapter = new PoiListAdapter(DestinationActivity.this, arrays);
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                        else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
}
