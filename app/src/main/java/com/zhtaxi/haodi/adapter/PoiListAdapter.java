package com.zhtaxi.haodi.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhtaxi.haodi.R;
import com.zhtaxi.haodi.domain.PoiData;

import java.util.List;

/**
 * poi地址列表adapter
 * Created by AeiouKong on 16/7/29.
 */
public class PoiListAdapter extends BaseAdapter {

    private String TAG = getClass().getSimpleName();

    private List<PoiData> arrays;
    private LayoutInflater inflater;

    public PoiListAdapter(Activity act, List<PoiData> arrays) {
        this.arrays = arrays;
        this.inflater = act.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return arrays == null ? 0 : arrays.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        GoodsHolder mholder;

        String name = arrays.get(position).getName();
        String address = arrays.get(position).getAddress();

        if (convertView == null) {
            mholder = new GoodsHolder();

            convertView = inflater.inflate(R.layout.item_poi,
                    null);
            mholder.tv_name = (TextView) convertView
                    .findViewById(R.id.tv_name);
            mholder.tv_address = (TextView) convertView
                    .findViewById(R.id.tv_address);

            convertView.setTag(mholder);

        } else {
            mholder = (GoodsHolder) convertView.getTag();
        }

        mholder.tv_name.setText(name);
        mholder.tv_address.setText(address);
        return convertView;
    }

    static class GoodsHolder {
        TextView tv_name;
        TextView tv_address;
    }

}
