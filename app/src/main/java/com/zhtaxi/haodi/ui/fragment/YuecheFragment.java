package com.zhtaxi.haodi.ui.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nickkong.commonlibrary.ui.fragment.BaseFragment;
import com.zhtaxi.haodi.R;
import com.zhtaxi.haodi.ui.activity.DestinationActivity;
import com.zhtaxi.haodi.ui.activity.LoginActivity;
import com.zhtaxi.haodi.ui.listener.OnHuishouBtnClickListener;
import com.zhtaxi.haodi.ui.listener.OnTimePickerSubmitListener;
import com.zhtaxi.haodi.widget.CustomTimePicker;

/**
 * 约车
 * Created by NickKong on 16/7/2.
 */
public class YuecheFragment extends BaseFragment implements View.OnClickListener{

    private String TAG = getClass().getSimpleName();

    private static final int REQUESTCODE_DESTINATION = 1;

    public TextView address_start,tv_yueche_time,address_end;
    private TextView tv_yueche_now,tv_yueche_future;
    private View ll_time,ll_move,ll_change_start,ll_yueche_now,ll_yueche_future,line;
    private OnHuishouBtnClickListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //初始化控件
        View view = inflater.inflate(R.layout.tab_yueche2, container, false);

        tv_yueche_now = (TextView) view.findViewById(R.id.tv_yueche_now);
        tv_yueche_future = (TextView) view.findViewById(R.id.tv_yueche_future);
        Button btn_change_huishou = (Button) view.findViewById(R.id.btn_change_huishou);
        btn_change_huishou.setOnClickListener(this);
        Button btn_yueche = (Button) view.findViewById(R.id.btn_yueche);
        btn_yueche.setOnClickListener(this);
        address_start = (TextView) view.findViewById(R.id.address_start);
        tv_yueche_time = (TextView) view.findViewById(R.id.tv_yueche_time);
        address_end = (TextView) view.findViewById(R.id.address_end);
        ll_time = view.findViewById(R.id.ll_time);
        ll_time.setOnClickListener(this);
        ll_time.setClickable(false);
        ll_move = view.findViewById(R.id.ll_move);
        line = view.findViewById(R.id.line);
        line.setVisibility(View.GONE);
        View ll_change_end = view.findViewById(R.id.ll_change_end);
        ll_change_end.setOnClickListener(this);
        ll_change_start = view.findViewById(R.id.ll_change_start);
        ll_yueche_now = view.findViewById(R.id.ll_yueche_now);
        ll_yueche_now.setOnClickListener(this);
        ll_yueche_future = view.findViewById(R.id.ll_yueche_future);
        ll_yueche_future.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            //约车-现在
            case R.id.ll_yueche_now:
                //选择“现在”标签后样式变化
                tv_yueche_now.setTextColor(getResources().getColor(R.color.TEXT_MAIN));
                tv_yueche_future.setTextColor(getResources().getColor(R.color.TEXT_HINT));
                ll_yueche_now.setBackgroundResource(R.drawable.border_lefttopradius_enable);
                ll_yueche_future.setBackgroundResource(R.drawable.border_righttopradius_disable);

                //切换选择时间的动画，向下隐藏
                ObjectAnimator downanimator = new ObjectAnimator().ofFloat(ll_move, "translationY", 0);
                downanimator.start();
                //动画监听
                downanimator.addListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator arg0) {
                    }
                    @Override
                    public void onAnimationRepeat(Animator arg0) {
                    }
                    @Override
                    public void onAnimationEnd(Animator arg0) {
                        ll_change_start.setBackgroundResource(R.drawable.border_notopbottom_rightradius);
                        ll_time.setClickable(false);
                        line.setVisibility(View.GONE);
                    }
                    @Override
                    public void onAnimationCancel(Animator arg0) {
                    }
                });
                break;
            //约车-预约
            case R.id.ll_yueche_future:
                //选择“预约”标签后样式变化
                tv_yueche_now.setTextColor(getResources().getColor(R.color.TEXT_HINT));
                tv_yueche_future.setTextColor(getResources().getColor(R.color.TEXT_MAIN));
                ll_change_start.setBackgroundResource(R.drawable.border_notopbottom_noradius);
                ll_yueche_now.setBackgroundResource(R.drawable.border_lefttopradius_disable);
                ll_yueche_future.setBackgroundResource(R.drawable.border_righttopradius_enable);

                //切换选择时间的动画，向上显示
                ObjectAnimator upanimator = new ObjectAnimator().ofFloat(ll_move, "translationY", -ll_time.getHeight());
                upanimator.start();
                //动画监听
                upanimator.addListener(new Animator.AnimatorListener() {

                  @Override
                  public void onAnimationStart(Animator arg0) {
                  }
                  @Override
                  public void onAnimationRepeat(Animator arg0) {
                  }
                  @Override
                  public void onAnimationEnd(Animator arg0) {
                      ll_time.setClickable(true);
                      line.setVisibility(View.VISIBLE);
                  }
                  @Override
                  public void onAnimationCancel(Animator arg0) {
                  }
                });
                break;
            //切换挥手叫车
            case R.id.btn_change_huishou:
                listener.change2Huishou();
                break;
            //选择预约时间
            case R.id.ll_time:
                showTimepicker();
                break;
            //选择目的地
            case R.id.ll_change_end:
                startActivityForResult(new Intent(getActivity(), DestinationActivity.class),REQUESTCODE_DESTINATION);
                break;
            //呼叫出租车
            case R.id.btn_yueche:
                //未登录，跳转注册/登录页面
                if (needLogin()){
                    startActivityByFade(new Intent(getActivity(), LoginActivity.class));
                }
                //已登录
                else {
                    doVibrator();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUESTCODE_DESTINATION && resultCode == Activity.RESULT_OK){
            String name = data.getStringExtra("name");
            if(name!=null){
                address_end.setText(name);
                address_end.setTextColor(getResources().getColor(R.color.TEXT_FOUR));
            }
        }
    }

    /**
     * 选择预约时间dialog
     */
    private void showTimepicker() {

        CustomTimePicker customTimePicker = new CustomTimePicker(getActivity(),timePickerSubmitListener);
        customTimePicker.getWindow().setGravity(Gravity.BOTTOM);
        customTimePicker.setCanceledOnTouchOutside(false);
        customTimePicker.getWindow().setWindowAnimations(R.style.DIALOG);
        customTimePicker.show();
    }

    /**
     * 选择预约时间回调监听，设置显示比如“今天15:00”
     */
    private OnTimePickerSubmitListener timePickerSubmitListener = new OnTimePickerSubmitListener() {
        @Override
        public void doSubmit(String content) {
            tv_yueche_time.setText(content);
        }
    };

    /**
     * 开启振动
     */
    private void doVibrator(){
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        long [] pattern = {0,20,0,0};
        vibrator.vibrate(pattern,-1);
    }

    /**
     * 绑定监听，告知父activity切换成挥手叫车状态
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnHuishouBtnClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHuishouBtnClickListener");
        }
    }
}
