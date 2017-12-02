package com.aixianshengxian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.aixianshengxian.util.DatesUtils;
import com.xmzynt.storm.service.purchase.bill.PurchaseBill;

import java.util.List;
import java.util.Date;

/**
 * Created by Administrator on 2017/10/13.
 */

public class PurchaseRefreshAdapter extends RecyclerView.Adapter<PurchaseRefreshAdapter.ViewHolder> {
    private List<PurchaseBill> mData;
    private OnItemClickListener mOnItemClickListener;
    private Context context;
    private LayoutInflater inflater = null;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_provider;
        TextView tv_purchase_ucode;
        TextView tv_purchase_time;
        TextView tv_purchase_state;
        View myView;

        public ViewHolder(View view){//自定义内部类，继承自RecyclerView.ViewHolder
            super(view);
            tv_provider = (TextView) view.findViewById(R.id.tv_provider);
            tv_purchase_ucode = (TextView) view.findViewById(R.id.tv_purchase_ucode);
            tv_purchase_time = (TextView) view.findViewById(R.id.tv_purchase_time);
            tv_purchase_state = (TextView) view.findViewById(R.id.tv_purchase_state);
            myView=view;//定义一个包括图片和文字的布局
        }
    }

    public PurchaseRefreshAdapter(List<PurchaseBill> MData,Context context) {
        this.mData = MData;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener{
        void onItemClick(PurchaseBill purchaseBill);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(PurchaseRefreshAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public PurchaseRefreshAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        final View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_purchase,parent,false);//传入布局
        final PurchaseRefreshAdapter.ViewHolder holder = new ViewHolder(view);

        if (mOnItemClickListener!=null) {
            //整体监听
            holder.myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onItemClick(mData.get(position));
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(PurchaseRefreshAdapter.ViewHolder holder, int position) {
        final PurchaseBill purchaseBill = mData.get(position);
        holder.tv_provider.setText(purchaseBill.getSupplier() == null?"":purchaseBill.getSupplier().getName());
        holder.tv_purchase_ucode.setText(purchaseBill.getBillNumber());
        Date purchaseTime = purchaseBill.getDeliveryTime();
        holder.tv_purchase_time.setText(String.valueOf(DatesUtils.dateToStr(purchaseTime)));
        holder.tv_purchase_state.setText(purchaseBill.getStatus().getCaption());
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    //添加数据
    public void addItem(List<PurchaseBill> newDatas) {

//        newDatas.addAll(mData);
//        mData.removeAll(mData);
        mData.clear();
        mData.addAll(newDatas);
//        notifyDataSetChanged();
    }

    public void addMoreItem(List<PurchaseBill> newDatas) {
        mData.addAll(newDatas);
//        notifyDataSetChanged();
    }

    public List<PurchaseBill> getData() {
        return mData;
    }
}
