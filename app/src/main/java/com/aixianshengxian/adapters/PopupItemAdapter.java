package com.aixianshengxian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aixianshengxian.R;

import java.util.List;

/**
 * Created by cwj on 2017/6/5.
 */

public class PopupItemAdapter extends RecyclerView.Adapter<PopupItemAdapter.PopupItemViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<String > mDataList;
    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public PopupItemAdapter(List<String> mDataList, Context context) {
       mLayoutInflater= LayoutInflater.from(context);
        this.mDataList = mDataList;
    }

    @Override
    public PopupItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PopupItemViewHolder holder =new PopupItemViewHolder( mLayoutInflater.inflate(R.layout.popup_ordermanager,null,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(PopupItemViewHolder holder, final int position) {
            if(mDataList != null && mDataList.size()>0 ){
                if(!TextUtils.equals("",mDataList.get(position))){
                    holder.tv_manager_way.setText(mDataList.get(position));
                }else {
                    holder.tv_manager_way.setText("");
                }
            }else {
                holder.tv_manager_way.setText("");
            }
        if(mOnItemClickLitener != null ){
           holder.tv_manager_way.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   mOnItemClickLitener.onItemClick(v,position);
               }
           });
        }
    }

    @Override
    public int getItemCount() {
        if(mDataList !=null && mDataList.size()>0){
            return mDataList.size();
        }
        return 0;
    }

    class PopupItemViewHolder extends RecyclerView.ViewHolder{
        TextView tv_manager_way;
        public PopupItemViewHolder(View itemView) {
            super(itemView);
            tv_manager_way = (TextView) itemView.findViewById(R.id.tv_manager_way);
        }

    }
}
