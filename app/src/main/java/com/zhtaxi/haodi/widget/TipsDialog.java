package com.zhtaxi.haodi.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhtaxi.haodi.R;
import com.zhtaxi.haodi.ui.listener.OnDialogClickListener;

/**
 * 提示对话框
 * Created by NickKong on 16/7/14.
 */
public class TipsDialog extends Dialog implements View.OnClickListener{

    private String TAG = getClass().getSimpleName();
    private Context context;
    private String content,dialog_left,dialog_right;
    private OnDialogClickListener listener;
    private int dialogType;
    private int doConfirm = 1;


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_left:
                if(doConfirm==0){
                    dismiss();
                    listener.doConfirm();
                }else if(doConfirm==1){
                    dismiss();
                }
                break;
            case R.id.dialog_right:
                if(doConfirm==0){
                    dismiss();
                }else if(doConfirm==1){
                    dismiss();
                    listener.doConfirm();
                }
                break;
            case R.id.btn_confirm:
                dismiss();
                listener.doConfirm();
                break;
        }

    }

    //dialogType 0 无按钮对话框1秒消失  1 一个确认按钮  2 是否按钮
    public TipsDialog(Context context, String content,
                      OnDialogClickListener listener, int dialogType) {

        super(context,R.style.dialog_tips);

        this.context = context;
        this.content = content;
        this.dialog_left = "取消";
        this.dialog_right = "确定";
        this.listener = listener;
        this.dialogType = dialogType;
    }

    //dialogType 0 无按钮对话框1秒消失  1 一个确认按钮  2 是否按钮
    //doConfirm 左右两个按键  0 左键确定 1 右键确定
    public TipsDialog(Context context, String content, String dialog_left, String dialog_right,
                      OnDialogClickListener listener, int dialogType, int doConfirm) {

        super(context,R.style.dialog_tips);

        this.context = context;
        this.content = content;
        this.dialog_left = dialog_left;
        this.dialog_right = dialog_right;
        this.listener = listener;
        this.dialogType = dialogType;
        this.doConfirm = doConfirm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init();
    }

    private void init() {

        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout dialog = (LinearLayout) inflater.inflate(R.layout.dialog_tips, null);

        TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
        tv_content.setText(content);
        Button dialog_left = (Button) dialog.findViewById(R.id.dialog_left);
        dialog_left.setText(this.dialog_left);
        dialog_left.setOnClickListener(this);
        Button dialog_right = (Button) dialog.findViewById(R.id.dialog_right);
        dialog_right.setText(this.dialog_right);
        dialog_right.setOnClickListener(this);
        Button btn_confirm = (Button) dialog.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        View ll_dialog = dialog.findViewById(R.id.ll_dialog);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(dialog, params);
        getWindow().getAttributes().width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8);

        if(dialogType == 0){
            ll_dialog.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            }, 2000);
        }else if(dialogType == 1){
            ll_dialog.setVisibility(View.GONE);
            btn_confirm.setVisibility(View.VISIBLE);
        }
    }
}
