package com.geek.springdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.geek.springdemo.R;
import com.geek.springdemo.model.AccountsModel;

import java.util.List;

/**
 * Created by Administrator on 2017/4/14.
 */

public class HistoryDetailListAdapter extends BaseAdapter{
    private Context mContext;
    private List<AccountsModel.DataBean> mList;
    private Holder holder;
    private OnCallBackLook mCallBack;

    public HistoryDetailListAdapter(Context context,List<AccountsModel.DataBean> list,OnCallBackLook callBack){
        this.mContext = context;
        this.mList = list;
        this.mCallBack = callBack;
    }

    public interface OnCallBackLook{
        void onLookDetail(int pos);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.history_detail_list_item,null);
            holder = new Holder();
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.look = (TextView) convertView.findViewById(R.id.look);
            holder.kind = (TextView) convertView.findViewById(R.id.kind);
            holder.money = (TextView) convertView.findViewById(R.id.money);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        AccountsModel.DataBean model = mList.get(position);
        holder.kind.setText(model.getKind());
        holder.money.setText(model.getMoney());
        holder.time.setText(model.getTime().replace(" ","\n"));
        holder.look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack!=null){
                    mCallBack.onLookDetail(position);
                }
            }
        });

        return convertView;
    }

    private class Holder{
        TextView time,kind,money;
        TextView look;
    }
}
