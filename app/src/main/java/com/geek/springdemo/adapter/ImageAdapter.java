package com.geek.springdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.geek.springdemo.R;
import com.geek.springdemo.activity.ImageActivity;
import com.geek.springdemo.http.HttpImageUtil;

import java.util.List;

/**
 * 图片显示 适配器
 * Created by  on 2017/3/21.
 */

public class ImageAdapter extends BaseAdapter{
    private Context mContext;
    private List<String> mList;
    private Holder holder;

    public ImageAdapter(Context context,List<String> list){
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.image_item,null);
            holder = new Holder();
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        HttpImageUtil.loadImage(holder.img,mList.get(position));
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mList.get(position).equals("")){
                    Intent intent=new Intent(mContext, ImageActivity.class);
                    intent.putExtra("path",mList.get(position));
                    mContext.startActivity(intent);
                }
            }
        });

        return convertView;
    }

    private class Holder{
        ImageView img;
    }
}
