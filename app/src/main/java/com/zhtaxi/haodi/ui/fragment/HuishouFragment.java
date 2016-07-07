package com.zhtaxi.haodi.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zhtaxi.haodi.R;
import com.zhtaxi.haodi.ui.listener.OnYuecheBtnClickListener;

/**
 * 挥手叫车
 * Created by NickKong on 16/7/2.
 */
public class HuishouFragment extends BaseFragment implements View.OnClickListener{

    public TextView address_start;
    private OnYuecheBtnClickListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_huishou, container, false);

        Button btn_change_yueche = (Button) view.findViewById(R.id.btn_change_yueche);
        btn_change_yueche.setOnClickListener(this);
        Button btn_huishou = (Button) view.findViewById(R.id.btn_huishou);
        btn_huishou.setOnClickListener(this);
        address_start = (TextView) view.findViewById(R.id.address_start);
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_change_yueche:
                listener.doYueche();
                break;
            case R.id.btn_huishou:
                doVibrator();
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
