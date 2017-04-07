package com.geek.springdemo.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.geek.springdemo.R;
import com.geek.springdemo.http.HttpImageUtil;
import com.geek.springdemo.model.AccountsModel;

import java.util.List;

/**
 * Created by cmm on 2017/3/17.
 */

public class MainContentAdapter extends BaseAdapter{
    private Context mContext;
    private List<AccountsModel> mList;
    private Holder holder;
    private OnCallBack mCallBack;

    public MainContentAdapter(Context context,List<AccountsModel> list,OnCallBack callBack){
        this.mContext = context;
        this.mList = list;
        this.mCallBack = callBack;
    }

    public interface OnCallBack{
        void onLooKDes(int pos);
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.mian_content_item,null);
            holder = new Holder();
            holder.type = (TextView) convertView.findViewById(R.id.type);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.money = (TextView) convertView.findViewById(R.id.money);
            holder.kind = (TextView) convertView.findViewById(R.id.kind);
            holder.note = (TextView) convertView.findViewById(R.id.note);
            holder.isShow = (TextView) convertView.findViewById(R.id.isShow);
            holder.look = (TextView) convertView.findViewById(R.id.des);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        AccountsModel model = mList.get(position);

        if (model.getType().equals("0")){
            holder.type.setText("收入");
            holder.type.setTextColor(Color.parseColor("#23c975"));
        }else{
            holder.type.setText("支出");
            holder.type.setTextColor(Color.parseColor("#D25544"));
        }

        holder.time.setText(model.getTime());
        HttpImageUtil.loadRoundImage(holder.img,model.getImage());
        holder.money.setText(Html.fromHtml("金额："+"<font color='#D25544'>"+model.getMoney()+"</font>"));
        holder.kind.setText("类型："+model.getKind());
        holder.note.setText("描述："+model.getNote());

        if (position==mList.size()-1){
            holder.isShow.setVisibility(View.VISIBLE);
        }else{
            holder.isShow.setVisibility(View.GONE);
        }

        if (model.getId().equals("")){
            holder.look.setVisibility(View.GONE);
        }else{
            holder.look.setVisibility(View.VISIBLE);
        }

        holder.look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack!=null){
                    mCallBack.onLooKDes(position);
                }
            }
        });

        return convertView;
    }

    private class Holder{
        TextView type,time;
        ImageView img;
        TextView money,kind,note,look;
        TextView isShow;
    }
}
