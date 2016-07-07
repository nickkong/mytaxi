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


public class CustomTimePicker extends Dialog implements View.OnClickListener{

    private String TAG = getClass().getSimpleName();

    private String hour,minute;
    private WheelView wva1,wva2,wva3;
    private View ll_cancel,ll_submit;
    private OnTimePickerSubmitListener timePickerSubmitListener;

    private static final String[] DATE = new String[]{"今天","明天"};
    private static final String[] HOUR = new String[]{"0点","1点","2点","3点","4点","5点","6点","7点",
                                            "8点","9点","10点","11点","12点","13点","14点","15点","16点",
                                            "17点","18点","19点","20点","21点","22点","23点"};
    private static final String[] MINUTE = new String[]{"00分","10分","20分","30分","40分","50分"};

    private List<String> hour_list,minute_list;

    private Context context;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_cancel:
                dismiss();
                break;
            case R.id.ll_submit:
                String hour = wva2.getSeletedItem().replace("点","");
                String minute = wva3.getSeletedItem().replace("分","");
                timePickerSubmitListener.doSubmit(wva1.getSeletedItem()+" "+hour+":"+minute);
                dismiss();
                break;
        }

    }

    public CustomTimePicker(Context context, OnTimePickerSubmitListener timePickerSubmitListener) {
        super(context,R.style.dialog_full);

        this.context = context;
        hour_list = new ArrayList<>();
        minute_list = new ArrayList<>();

        SimpleDateFormat sdf_hour = new SimpleDateFormat("HH");//设置日期格式
        hour = sdf_hour.format(new Date());
        int int_hour = Integer.parseInt(hour);
        SimpleDateFormat sdf_minute = new SimpleDateFormat("MM");//设置日期格式
        minute = sdf_minute.format(new Date());
        int int_minute = Integer.parseInt(minute);
        for(int i=0;i<24-int_hour;i++){
            hour_list.add(int_hour+i+"点");
        }
        this.timePickerSubmitListener = timePickerSubmitListener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init();
    }

    private void init() {

        LayoutInflater inflater = LayoutInflater.from(context);
        final LinearLayout dialog = (LinearLayout) inflater.inflate(R.layout.dialog_timepicker, null);

        ll_cancel = dialog.findViewById(R.id.ll_cancel);
        ll_cancel.setOnClickListener(this);
        ll_submit = dialog.findViewById(R.id.ll_submit);
        ll_submit.setOnClickListener(this);

        wva1 = (WheelView) dialog.findViewById(R.id.main1);
        wva2 = (WheelView) dialog.findViewById(R.id.main2);
        wva3 = (WheelView) dialog.findViewById(R.id.main3);

        wva1.setOffset(1);
        wva1.setItems(Arrays.asList(DATE));
        wva1.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d(TAG, "selectedIndex: " + selectedIndex + ", item: " + item);
                if(selectedIndex==1){
                    wva2.setItems(hour_list);
                }
                if(selectedIndex==2){
                    wva2.setItems(Arrays.asList(HOUR));
                }
            }
        });
        wva2.setOffset(1);
        wva2.setItems(hour_list);
        wva2.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d(TAG, "selectedIndex: " + selectedIndex + ", item: " + item);
            }
        });
        wva3.setOffset(1);
        wva3.setItems(Arrays.asList(MINUTE));
        wva3.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d(TAG, "selectedIndex: " + selectedIndex + ", item: " + item);
            }
        });
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(dialog, params);
        getWindow().getAttributes().width = ViewGroup.LayoutParams.MATCH_PARENT;


    }

}
