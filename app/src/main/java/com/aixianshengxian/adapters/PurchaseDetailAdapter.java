package com.aixianshengxian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.aixianshengxian.util.PurchaseBillUtil;
import com.xmzynt.storm.service.purchase.bill.PurchaseBill;
import com.xmzynt.storm.service.purchase.bill.PurchaseBillLine;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/19.
 */

public class PurchaseDetailAdapter extends RecyclerView.Adapter<PurchaseDetailAdapter.ViewHolder> {
    private List<PurchaseBillLine> mData;
    private PurchaseBill mPurchaseBill;
    private OnItemClickListener mOnItemClickListener;
    private Context context;
    private LayoutInflater inflater = null;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_product_name;
        TextView tv_unit_price;
        TextView tv_unit;
        TextView tv_purchase_num;
        TextView tv_place;
        TextView tv_customer;
        TextView tv_remark;
        TextView tv_purchase_price;
        TextView tv_detail_state;
        ImageView iv_printer;
        View myView;

        public ViewHolder(View view) {//自定义内部类，继承自RecyclerView.ViewHolder
            super(view);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            tv_unit_price = (TextView) view.findViewById(R.id.tv_unit_price);
            tv_unit = (TextView) view.findViewById(R.id.tv_unit);
            tv_purchase_num = (TextView) view.findViewById(R.id.tv_purchase_num);
            tv_place = (TextView) view.findViewById(R.id.tv_place);
            tv_customer = (TextView) view.findViewById(R.id.tv_customer);
            tv_remark = (TextView) view.findViewById(R.id.tv_remark);
            tv_purchase_price = (TextView) view.findViewById(R.id.tv_purchase_price);
            tv_detail_state = (TextView) view.findViewById(R.id.tv_detail_state);
            iv_printer = (ImageView) view.findViewById(R.id.iv_printer);
            myView = view;//定义一个包括图片和文字的布局
        }
    }

    public PurchaseDetailAdapter(List<PurchaseBillLine> MData, Context context, PurchaseBill purchaseBill) {
        this.mData = MData;
        this.context = context;
        this.mPurchaseBill = purchaseBill;
        inflater = LayoutInflater.from(context);
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener {
        void onPinterClick(PurchaseBillLine purchasedetail);
        void onItemClick(PurchaseBillLine purchasedetail);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(PurchaseDetailAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public PurchaseDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_purchase_detail, parent, false);//传入布局
        final PurchaseDetailAdapter.ViewHolder holder = new ViewHolder(view);

        if (mOnItemClickListener != null) {
            //整体监听
            if (mPurchaseBill.getStatus().getCaption().equals("待审核") || mPurchaseBill.getStatus().getCaption().equals("已审核")) {
                holder.tv_detail_state.setVisibility(View.GONE);
                holder.iv_printer.setVisibility(View.VISIBLE);
                holder.iv_printer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getAdapterPosition();//获取点击位置
                        mOnItemClickListener.onPinterClick(mData.get(position));
                    }
                });
            } else {
                holder.tv_detail_state.setVisibility(View.VISIBLE);
                holder.tv_detail_state.setText(mPurchaseBill.getStatus().getCaption());
                holder.iv_printer.setVisibility(View.GONE);
            }
            if (mPurchaseBill.getStatus().getCaption().equals("已入库") || mPurchaseBill.getStatus().getCaption().equals("已审核")) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getAdapterPosition();//获取点击位置
                        mOnItemClickListener.onItemClick(mData.get(position));
                    }
                });
            }
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(PurchaseDetailAdapter.ViewHolder holder, int position) {
        final PurchaseBillLine purchasedetail = mData.get(position);
        if (purchasedetail != null) {
            String uuid = purchasedetail.getGoods().getCode();
            String name = purchasedetail.getGoods().getName();
            String spec =  purchasedetail.getGoodsSpec()==null?null:purchasedetail.getGoodsSpec();

            if (spec != null) {
                Map<String, Object> valueMap = PurchaseBillUtil.getMap(spec);
                if (spec.equals("{}")) {
                    holder.tv_product_name.setText(uuid + "  " + name);//商品名称
                } else {
                    holder.tv_product_name.setText(uuid + "  " + name + "  " + valueMap.keySet() + valueMap.values());//商品名称
                }
            } else {
                holder.tv_product_name.setText(uuid + "  " + name);//商品名称
            }

            holder.tv_unit_price.setText(String.valueOf(purchasedetail.getPrice()));
            holder.tv_unit.setText(purchasedetail.getGoodsUnit().getName());
            holder.tv_purchase_num.setText(String.valueOf(purchasedetail.getPurchaseQty()));
            holder.tv_place.setText(purchasedetail.getOrigin());
            String customeName=purchasedetail.getCustomer()==null ?"":purchasedetail.getCustomer().getName();
            if(purchasedetail.getCustomerDept()!=null){
                customeName+= "【"+purchasedetail.getCustomerDept().getName()+"】";
            }
            holder.tv_customer.setText(customeName);

            holder.tv_remark.setText(purchasedetail.getRemark());
            holder.tv_purchase_price.setText(String.valueOf(purchasedetail.getSubtotal()) + " 元");

            /*if (purchasedetail.getStatus().getCaption().equals("已入库") || purchasedetail.getStatus().getCaption().equals("已完成")) {
                holder.tv_detail_state.setVisibility(View.VISIBLE);
                holder.tv_detail_state.setText(purchasedetail.getStatus().getCaption());
                holder.iv_printer.setVisibility(View.GONE);
            } else {
                holder.tv_detail_state.setVisibility(View.GONE);
                holder.iv_printer.setVisibility(View.VISIBLE);
            }*/
        }


    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    //添加数据
    public void addItem(List<PurchaseBillLine> newDatas) {

        newDatas.addAll(mData);
        mData.removeAll(mData);
        mData.addAll(newDatas);
        notifyDataSetChanged();
    }

    public void addMoreItem(List<PurchaseBillLine> newDatas) {
        mData.addAll(newDatas);
        notifyDataSetChanged();
    }
}