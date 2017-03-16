package com.geek.springdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.geek.springdemo.R;

import java.util.Arrays;
import java.util.List;

/**
 * 左侧适配器
 * Created by cmm on 2017/3/16.
 */

public class MainLeftAdapter extends BaseAdapter{
    private Context mContext;
    private String[] values = {
            "历史查询","统计分析","设置"
    };
    private int[] keys = {
            R.drawable.qz_39,R.drawable.qz_50,R.drawable.qz_59
    };
    private Holder holder;

    public MainLeftAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return values.length;
    }

    @Override
    public Object getItem(int position) {
        return values[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.main_left_item,null);
            holder = new Holder();
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        holder.img.setImageResource(keys[position]);
        holder.name.setText(values[position]);
        return convertView;
    }

    private class Holder{
        ImageView img;
        TextView name;
    }
}
