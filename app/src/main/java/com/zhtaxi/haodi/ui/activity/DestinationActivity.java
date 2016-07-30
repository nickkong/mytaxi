package com.zhtaxi.haodi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nickkong.commonlibrary.ui.activity.BaseActivity;
import com.nickkong.commonlibrary.util.HttpUtil;
import com.nickkong.commonlibrary.widget.pulltoRefreshAndLoad.PullableListView;
import com.zhtaxi.haodi.R;
import com.zhtaxi.haodi.adapter.PoiListAdapter;
import com.zhtaxi.haodi.domain.PoiData;
import com.zhtaxi.haodi.util.Constant;
import com.zhtaxi.haodi.util.RequestAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 您要去哪儿，目的地搜索
 * Created by NickKong on 16/7/6.
 */
public class DestinationActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    private String TAG = getClass().getSimpleName();

    private static final int SUCCESSCODE_GETPLACE = 1;

    private PullableListView listView;
    private PoiListAdapter adapter;
    private List<PoiData> arrays;
    private List<PoiData> hotpoi_arrays;
    private View ll_commonaddress;
    private View headview;
    private View ll_searching;

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

        //初始化默认poi
        hotpoi_arrays = new ArrayList<>();
        initDefaultPoiData();

        Button btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        View ll_home = findViewById(R.id.ll_home);
        ll_home.setOnClickListener(this);
        View ll_company = findViewById(R.id.ll_company);
        ll_company.setOnClickListener(this);
        ll_searching = findViewById(R.id.ll_searching);

        //初始化列表控件
        listView = (PullableListView) findViewById(R.id.content_view);
        listView.setCanPullDown(false);
        listView.setCanPullUp(false);
        listView.setOnItemClickListener(this);

        //首次进入获取默认的poi地址
        getDefaultPoi();

        ll_commonaddress = findViewById(R.id.ll_commonaddress);

        headview = getLayoutInflater().inflate(
                R.layout.tips_nosearchresult, null);

        EditText et_addresskeyword = (EditText) findViewById(R.id.et_addresskeyword);

        //监听搜索输入框文字变化
        et_addresskeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //输入框没有文字时
                if(s.toString().length()==0){
                    //显示默认的poi地址
                    getDefaultPoi();
                    //显示预设的家、公司
                    ll_commonaddress.setVisibility(View.VISIBLE);
                }
                //输入框有文字时
                else {
                    //显示正在搜索
                    ll_searching.setVisibility(View.VISIBLE);
                    //隐藏地址列表
                    listView.setVisibility(View.GONE);
                    //获取poi地址
                    getPlace(s.toString());
                    //隐藏预设的家、公司
                    ll_commonaddress.setVisibility(View.GONE);
                }
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

    /**
     * 利用百度地图api，获取城市poi信息
     */
    private void getPlace(String keyword){

        Map<String, Object> params = new HashMap();
        params.put("query", keyword);
        params.put("region", "140"); //140 珠海，138 佛山
        params.put("output", "json");
        params.put("city_limit", "true");
        params.put("ak", "8wMQn0vRDF7GGrQvI1HXsLov0eWUoYFR");
        HttpUtil.doGet(TAG,this,mHandler, Constant.HTTPUTIL_FAILURECODE,SUCCESSCODE_GETPLACE,
                RequestAddress.search,params);

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            String message = (String) msg.obj;
            switch (msg.what) {
                //获取地址失败、网络原因等
                case Constant.HTTPUTIL_FAILURECODE:
                    getNoResult();
                    break;
                //根据关键字搜索相关poi
                case SUCCESSCODE_GETPLACE:
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        String status = jsonObject.getString("status");
                        ll_searching.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        //成功
                        if ("0".equals(status)) {
                            JSONArray results = jsonObject.getJSONArray("results");
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<PoiData>>() {}.getType();
                            arrays = gson.fromJson(results.toString(), listType);

                            if (arrays != null && arrays.size() > 0) {
                                listView.removeHeaderView(headview);
                                listView.setDividerHeight(2);
                            }else {
                                listView.removeHeaderView(headview);
                                listView.addHeaderView(headview);
                                listView.setDividerHeight(0);
                            }

                            adapter = new PoiListAdapter(DestinationActivity.this, arrays);
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            getNoResult();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    String[] names = {"九洲港码头","夏湾市场","摩尔广场","九洲港口岸","珠海拱北口岸","扬名广场","北京理工大学珠海学院"
            ,"珠海机场","北京师范大学珠海分校","华发商都","香洲总站","唐家市场","中海富华里","拱北口岸"};
    String[] addresses = {"香洲区情侣南路","香洲区夏湾路226号附近","拱北","香洲区情侣南路","香洲区拱北侨光路3号"
            ,"香洲区凤凰南路1088","香洲区金凤路6号","珠海机场","北京师范大学珠海分校","香洲区珠海大道8号华发商都2楼C2021馆"
            ,"993路","香洲区唐中路","广东省珠海九洲大道与白石路交汇处","拱北口岸"};

    /**
     * 填充默认地址数据
     */
    private void initDefaultPoiData(){

        for(int i=0;i<names.length;i++){
            PoiData data = new PoiData();
            data.setName(names[i]);
            data.setAddress(addresses[i]);
            hotpoi_arrays.add(data);
        }
    }

    /**
     * 显示默认地址
     */
    private void getDefaultPoi(){
        arrays = hotpoi_arrays;

        if (arrays != null && arrays.size() > 0) {
            listView.removeHeaderView(headview);
            listView.setDividerHeight(2);
        }else {
            listView.removeHeaderView(headview);
            listView.addHeaderView(headview);
            listView.setDividerHeight(0);
        }

        adapter = new PoiListAdapter(DestinationActivity.this, arrays);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * 搜索无结果
     * 数据异常、注册百度参数异常等，界面显示效果
     */
    private void getNoResult(){
        //隐藏正在搜索
        ll_searching.setVisibility(View.GONE);
        //显示搜索无结果
        listView.setVisibility(View.VISIBLE);
        listView.removeHeaderView(headview);
        listView.addHeaderView(headview);
        listView.setDividerHeight(0);
        //清空搜索结果历史，提示搜索无结果
        adapter = new PoiListAdapter(DestinationActivity.this, new ArrayList<PoiData>());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * 选择地址后，回传poi名称、地址、经纬度
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = getIntent();
        intent.putExtra("name",arrays.get(position).getName());
        setResult(RESULT_OK,intent);
        doFinish();
    }
}
