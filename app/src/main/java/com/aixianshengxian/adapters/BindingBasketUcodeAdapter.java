package com.aixianshengxian.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.aixianshengxian.module.UcodeMessage;

import java.util.List;

/**
 * Created by Administrator on 2017/9/28.
 */

public class BindingBasketUcodeAdapter extends RecyclerView.Adapter<BindingBasketUcodeAdapter.ViewHolder>{
    private List<String> mData;
    private OnItemClickListener mOnItemClickListener;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_ucode,tv_operate_time;
        ImageView iv_delete;
        View myView;

        public ViewHolder(View view){//自定义内部类，继承自RecyclerView.ViewHolder
            super(view);//RecyclerView子项的最外层布局，方便获取下列id
            tv_ucode = (TextView)view.findViewById(R.id.tv_ucode);
            tv_operate_time = (TextView)view.findViewById(R.id.tv_operate_time);
            tv_operate_time.setVisibility(View.GONE);
            iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
            iv_delete.setVisibility(View.VISIBLE);
            myView=view;//定义一个包括图片和文字的布局
        }
    }
    public BindingBasketUcodeAdapter(List<String> MData){
        this.mData = MData;
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener{
        void onDeleteClick(int position);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public BindingBasketUcodeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_ucode,parent,false);//传入布局

        final BindingBasketUcodeAdapter.ViewHolder holder = new ViewHolder(view);

        if (mOnItemClickListener!=null) {
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onDeleteClick(position);
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(BindingBasketUcodeAdapter.ViewHolder holder, int position) {
        String ucodeMessage = mData.get(position);
        holder.tv_ucode.setText(ucodeMessage);
        //holder.tv_operate_time.setText(ucodeMessage.getOperateTime());
    }

    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }
}
