package com.zhtaxi.haodi.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nickkong.commonlibrary.ui.fragment.BaseFragment;
import com.zhtaxi.haodi.R;
import com.zhtaxi.haodi.ui.activity.LoginActivity;
import com.zhtaxi.haodi.ui.listener.OnYuecheBtnClickListener;

/**
 * 挥手叫车
 * Created by NickKong on 16/7/2.
 */
public class HuishouFragment extends BaseFragment implements View.OnClickListener{

    public TextView address_start;
    private OnYuecheBtnClickListener listener;
    private Button btn_cancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //初始化控件
        View view = inflater.inflate(R.layout.tab_huishou2, container, false);
        Button btn_change_yueche = (Button) view.findViewById(R.id.btn_change_yueche);
        btn_change_yueche.setOnClickListener(this);
        Button btn_huishou = (Button) view.findViewById(R.id.btn_huishou);
        btn_huishou.setOnClickListener(this);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        address_start = (TextView) view.findViewById(R.id.address_start);
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            //切换约车页面
            case R.id.btn_change_yueche:
                listener.change2Yueche();
                break;
            //挥手叫车
            case R.id.btn_huishou:
                //未登录，跳转注册/登录页面
                if (needLogin()){
                    startActivityByFade(new Intent(getActivity(), LoginActivity.class));
                }
                //已登录
                else {
                    doVibrator();
                    listener.doHuishou();
                    btn_cancel.setVisibility(View.VISIBLE);
                }
                break;
            //取消挥手
            case R.id.btn_cancel:
                listener.cancelHuishou();
                btn_cancel.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 开启振动
     */
    private void doVibrator(){
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        long [] pattern = {0,20,0,0};
        vibrator.vibrate(pattern,-1);
    }

    /**
     * 绑定监听，告知父activity切换成约车状态
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnYuecheBtnClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnYuecheBtnClickListener");
        }
    }
}
