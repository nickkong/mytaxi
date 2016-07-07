package com.zhtaxi.haodi.ui.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zhtaxi.haodi.R;
import com.zhtaxi.haodi.ui.activity.DestinationActivity;
import com.zhtaxi.haodi.ui.listener.OnHuishouBtnClickListener;
import com.zhtaxi.haodi.ui.listener.OnTimePickerSubmitListener;
import com.zhtaxi.haodi.widget.CustomTimePicker;

/**
 * 约车
 * Created by NickKong on 16/7/2.
 */
public class YuecheFragment extends Fragment implements View.OnClickListener{

    private String TAG = getClass().getSimpleName();

    public TextView address_start,tv_yueche_time;
    private TextView tv_yueche_now,tv_yueche_future;
    private View ll_time,ll_move,ll_change_start,ll_change_end,ll_yueche_now,ll_yueche_future,line;
    private OnHuishouBtnClickListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_yueche, container, false);

        tv_yueche_now = (TextView) view.findViewById(R.id.tv_yueche_now);
        tv_yueche_future = (TextView) view.findViewById(R.id.tv_yueche_future);
        Button btn_change_huishou = (Button) view.findViewById(R.id.btn_change_huishou);
        btn_change_huishou.setOnClickListener(this);
        address_start = (TextView) view.findViewById(R.id.address_start);
        tv_yueche_time = (TextView) view.findViewById(R.id.tv_yueche_time);
        ll_time = view.findViewById(R.id.ll_time);
        ll_time.setOnClickListener(this);
        ll_time.setClickable(false);
        ll_move = view.findViewById(R.id.ll_move);
        line = view.findViewById(R.id.line);
        line.setVisibility(View.GONE);
        ll_change_end = view.findViewById(R.id.ll_change_end);
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
            case R.id.ll_yueche_now:
                tv_yueche_now.setTextColor(getResources().getColor(R.color.TEXT_MAIN));
                tv_yueche_future.setTextColor(getResources().getColor(R.color.TEXT_HINT));
                ll_yueche_now.setBackgroundResource(R.drawable.border_lefttopradius_enable);
                ll_yueche_future.setBackgroundResource(R.drawable.border_righttopradius_disable);
                ObjectAnimator downanimator = new ObjectAnimator().ofFloat(ll_move, "translationY", 0);
                downanimator.start();
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
            case R.id.ll_yueche_future:
                tv_yueche_now.setTextColor(getResources().getColor(R.color.TEXT_HINT));
                tv_yueche_future.setTextColor(getResources().getColor(R.color.TEXT_MAIN));
                ll_change_start.setBackgroundResource(R.drawable.border_notopbottom_noradius);
                ll_yueche_now.setBackgroundResource(R.drawable.border_lefttopradius_disable);
                ll_yueche_future.setBackgroundResource(R.drawable.border_righttopradius_enable);
                ObjectAnimator upanimator = new ObjectAnimator().ofFloat(ll_move, "translationY", -ll_time.getHeight());
                upanimator.start();
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
            case R.id.btn_change_huishou:
                listener.doHuishou();
                break;
            case R.id.ll_time:
                showTimepicker();
                break;
            case R.id.ll_change_end:
                startActivity(new Intent(getActivity(), DestinationActivity.class));
                break;
        }
    }

    private void showTimepicker() {

        CustomTimePicker customTimePicker = new CustomTimePicker(getActivity(),timePickerSubmitListener);
        customTimePicker.getWindow().setGravity(Gravity.BOTTOM);
        customTimePicker.setCanceledOnTouchOutside(false);
        customTimePicker.getWindow().setWindowAnimations(R.style.DIALOG);  //添加动画
        customTimePicker.show();
    }

    private OnTimePickerSubmitListener timePickerSubmitListener = new OnTimePickerSubmitListener() {
        @Override
        public void doSubmit(String content) {
            tv_yueche_time.setText(content);
        }
    };


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
