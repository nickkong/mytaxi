package com.zhtaxi.haodi.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.zhtaxi.haodi.R;
import com.zhtaxi.haodi.ui.listener.OnTimePickerSubmitListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 自定义时间选择器
 * Created by NickKong on 16/7/7.
 */
public class CustomTimePicker extends Dialog implements View.OnClickListener{

    private String TAG = getClass().getSimpleName();

    private String hour,minute;
    private WheelView wv_date, wv_hour, wv_minute;
    private OnTimePickerSubmitListener timePickerSubmitListener;

    private static final String[] DATE = new String[]{"今天","明天"};
    private static final String[] HOUR = new String[]{"0点","1点","2点","3点","4点","5点","6点","7点",
                                            "8点","9点","10点","11点","12点","13点","14点","15点","16点",
                                            "17点","18点","19点","20点","21点","22点","23点"};
    private static final String[] MINUTE = new String[]{"00分","10分","20分","30分","40分","50分"};

    private List<String> date_list,hour_list,minute_list;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init();
    }

    /**
     * 构造函数
     */
    public CustomTimePicker(Context context, OnTimePickerSubmitListener timePickerSubmitListener) {
        super(context,R.style.dialog_full);

        this.context = context;
        date_list = new ArrayList<>();
        hour_list = new ArrayList<>();
        minute_list = new ArrayList<>();

        SimpleDateFormat sdf_hour = new SimpleDateFormat("HH");//设置日期格式
        hour = sdf_hour.format(new Date());
        int int_hour = Integer.parseInt(hour);
        SimpleDateFormat sdf_minute = new SimpleDateFormat("mm");//设置日期格式
        minute = sdf_minute.format(new Date());
        int int_minute = Integer.parseInt(minute);

        //23:31后 预约时间只能是明天0:00后，需特殊处理
        if(int_hour==23 && int_minute>30){
            date_list.clear();
            date_list.add("明天");
            hour_list.clear();
            for(int i=0;i<24;i++){
                hour_list.add(i+"点");
            }
            if(int_minute<=40 && int_minute>30){
                minute_list.clear();
                for(int i=0;i<6;i++){
                    minute_list.add(i+"0分");
                }
            }else if(int_minute<=50 && int_minute>40){
                minute_list.clear();
                for(int i=1;i<6;i++){
                    minute_list.add(i+"0分");
                }
            }else if(int_minute>50){
                minute_list.clear();
                for(int i=2;i<6;i++){
                    minute_list.add(i+"0分");
                }
            }
        }
        //23:30以及之前
        else {
            date_list.clear();
            date_list.add("今天");
            date_list.add("明天");
            //根据手机当前时间构造可预约的小时
            if(int_minute<=30){
                hour_list.clear();
                for(int i=0;i<24-int_hour;i++){
                    hour_list.add(int_hour+i+"点");
                }
            }else {
                hour_list.clear();
                for(int i=0;i<23-int_hour;i++){
                    hour_list.add(int_hour+1+i+"点");
                }
            }

            //只能预约当前时间的20分钟后
            int future_minute = int_minute+20;
            //根据手机当前时间构造可预约的分钟
            if(future_minute<=60 && future_minute>50){
                minute_list.clear();
                for(int i=0;i<6;i++){
                    minute_list.add(i+"0分");
                }
            }else if(future_minute<=50 && future_minute>40){
                minute_list.clear();
                for(int i=5;i<6;i++){
                    minute_list.add(i+"0分");
                }
            }else if(future_minute<=40 && future_minute>30){
                minute_list.clear();
                for(int i=4;i<6;i++){
                    minute_list.add(i+"0分");
                }
            }else if(future_minute<=30 && future_minute>20){
                minute_list.clear();
                for(int i=3;i<6;i++){
                    minute_list.add(i+"0分");
                }
            }else if(future_minute<=70 && future_minute>60){
                minute_list.clear();
                for(int i=1;i<6;i++){
                    minute_list.add(i+"0分");
                }
            }else if((future_minute<80 && future_minute>70) || future_minute==20){
                minute_list.clear();
                for(int i=2;i<6;i++){
                    minute_list.add(i+"0分");
                }
            }
        }

        this.timePickerSubmitListener = timePickerSubmitListener;

    }

    /**
     * 初始化控件
     */
    private void init() {

        LayoutInflater inflater = LayoutInflater.from(context);
        final LinearLayout dialog = (LinearLayout) inflater.inflate(R.layout.dialog_timepicker, null);

        View ll_cancel = dialog.findViewById(R.id.ll_cancel);
        ll_cancel.setOnClickListener(this);
        View ll_submit = dialog.findViewById(R.id.ll_submit);
        ll_submit.setOnClickListener(this);

        wv_date = (WheelView) dialog.findViewById(R.id.date);
        wv_hour = (WheelView) dialog.findViewById(R.id.hour);
        wv_minute = (WheelView) dialog.findViewById(R.id.minute);

        wv_date.setOffset(1);
        wv_date.setItems(date_list);
        wv_date.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                if(date_list.size()!=1){
                    //今天
                    if(selectedIndex==1){
                        wv_hour.setItems(hour_list);
                        wv_minute.setItems(minute_list);
                    }
                    //明天
                    if(selectedIndex==2){
                        wv_hour.setItems(Arrays.asList(HOUR));
                        wv_minute.setItems(Arrays.asList(MINUTE));
                    }
                }
            }
        });
        wv_hour.setOffset(1);
        wv_hour.setItems(hour_list);
        wv_hour.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d(TAG, "selectedIndex: " + selectedIndex + ", item: " + item);

                if(hour_list.size()!=1){
                    if(selectedIndex==1){
                        wv_minute.setItems(minute_list);
                    }else {
                        wv_minute.setItems(Arrays.asList(MINUTE));
                    }
                }
            }
        });
        wv_minute.setOffset(1);
        wv_minute.setItems(minute_list);
        wv_minute.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d(TAG, "selectedIndex: " + selectedIndex + ", item: " + item);
            }
        });
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(dialog, params);
        getWindow().getAttributes().width = ViewGroup.LayoutParams.MATCH_PARENT;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //取消预约时间
            case R.id.ll_cancel:
                dismiss();
                break;
            //确定
            case R.id.ll_submit:
                String hour = wv_hour.getSeletedItem().replace("点","");
                String minute = wv_minute.getSeletedItem().replace("分","");
                timePickerSubmitListener.doSubmit(wv_date.getSeletedItem()+" "+hour+":"+minute);
                dismiss();
                break;
        }
    }

}
