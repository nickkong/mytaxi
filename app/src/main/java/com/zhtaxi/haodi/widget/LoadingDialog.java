package com.zhtaxi.haodi.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhtaxi.haodi.R;


/**
 * 加载页面时候的进度对话框/操作成功提示对话框
 * Created by NickKong on 16/7/14.
 */
public class LoadingDialog extends Dialog {

    private String TAG = getClass().getSimpleName();

    private Context context;
    private ImageView iv_load;
    private String content;
    private int type; //1、loading, 2、确认

    public LoadingDialog(Context context,String content,int type) {

        super(context, R.style.dialog_loading);

        this.context = context;
        this.content = content;
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init();
    }

    private void init() {

        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout dialog = (LinearLayout) inflater.inflate(R.layout.dialog_loading, null);

        iv_load = (ImageView) dialog.findViewById(R.id.iv_load);
        ImageView iv_ok = (ImageView) dialog.findViewById(R.id.iv_ok);
        TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);

        tv_content.setText(content);

        //显示进度条
        if(type == 1){
            iv_load.setVisibility(View.VISIBLE);
            RotateAnimation rotateAnimation = new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF,
                    0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            rotateAnimation.setDuration(1000);
            rotateAnimation.setRepeatCount(10000);//设置重复次数
            rotateAnimation.setInterpolator(new LinearInterpolator());//不停顿
            iv_load.startAnimation(rotateAnimation);
        }
        //显示操作成功确认图标
        else if(type == 2){
            iv_ok.setVisibility(View.VISIBLE);
        }

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(dialog, params);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(type == 1){
            iv_load.clearAnimation();
        }
    }
}
