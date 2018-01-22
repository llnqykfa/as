package com.aixianshengxian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aixianshengxian.R;
import com.aixianshengxian.activity.purchase.AddPurchaseActivity;
import com.aixianshengxian.module.AddPurchaseDetail;
import com.xmzynt.storm.service.purchase.bill.PurchaseBillLine;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/10/20.
 */

public class AddPurchaseDetailAdapter extends RecyclerView.Adapter<AddPurchaseDetailAdapter.ViewHolder> {
    private List<PurchaseBillLine> mData;
    private OnItemClickListener mOnItemClickListener;
    private Context context;
    private LayoutInflater inflater = null;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_product_name;
        EditText edt_unit_price;
        TextView tv_unit;
        EditText edt_purchase_num;
        EditText edt_place;
        EditText edt_remark;
        TextView edt_purchase_price;
        ImageView iv_delete;
        View myView;
        public ViewHolder(View view){//自定义内部类，继承自RecyclerView.ViewHolder
            super(view);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            edt_unit_price = (EditText) view.findViewById(R.id.edt_unit_price);
            tv_unit = (TextView) view.findViewById(R.id.tv_unit);
            edt_purchase_num = (EditText) view.findViewById(R.id.edt_purchase_num);
            edt_place = (EditText) view.findViewById(R.id.edt_place);
            edt_remark = (EditText) view.findViewById(R.id.edt_remark);
            edt_purchase_price = (TextView) view.findViewById(R.id.edt_purchase_price);
            iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
            myView=view;//定义一个包括图片和文字的布局
        }
    }

    public AddPurchaseDetailAdapter(List<PurchaseBillLine> MData, Context context) {
        this.mData = MData;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener{
        void onDeleteClick(int position);
        void onUnitClick(int position,View v);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(AddPurchaseDetailAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public AddPurchaseDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_add_purchase,parent,false);//传入布局
        final AddPurchaseDetailAdapter.ViewHolder holder = new ViewHolder(view);

        if (mOnItemClickListener!=null) {
            //删除监听
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onDeleteClick(position);
                }
            });

            //单位监听
            holder.tv_unit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onUnitClick(position,v);
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PurchaseBillLine addpurchasedetail = mData.get(position);
        holder.tv_product_name.setText(addpurchasedetail.getGoods().getName());

        //单价
        //holder.edt_unit_price.setText(String.valueOf(addpurchasedetail.getUniPrice()));
        //这个很重要，先移开TextWatcher的监听器
        if (holder.edt_unit_price.getTag() instanceof android.text.TextWatcher) {
            holder.edt_unit_price.removeTextChangedListener((TextWatcher) holder.edt_unit_price.getTag());
        }
        holder.edt_unit_price.setText(String.valueOf(addpurchasedetail.getPrice()));
        TextWatcher watcher1 = new TextWatcher() {

            private ViewHolder mHolder;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    addpurchasedetail.setPrice(BigDecimal.ZERO);
                    Toast.makeText(AddPurchaseActivity.mactivity,"单价不能为空！",Toast.LENGTH_SHORT).show();
                    refreshPurchasePrice(addpurchasedetail,holder);
                } else {
                    addpurchasedetail.setPrice(BigDecimal.valueOf(Double.valueOf(s.toString())));
                    refreshPurchasePrice(addpurchasedetail,holder);
                }
            }
        };
        holder.edt_unit_price.addTextChangedListener(watcher1);
        holder.edt_unit_price.setTag(watcher1);

        holder.tv_unit.setText(addpurchasedetail.getGoodsUnit().getName());//单位

        //采购数
        //这个很重要，先移开TextWatcher的监听器
        if (holder.edt_purchase_num.getTag() instanceof android.text.TextWatcher) {
            holder.edt_purchase_num.removeTextChangedListener((TextWatcher) holder.edt_purchase_num.getTag());
        }
        holder.edt_purchase_num.setText(String.valueOf(addpurchasedetail.getPurchaseQty()));//采购数变化监听
        TextWatcher watcher2 = new TextWatcher() {

            private ViewHolder mHolder;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    addpurchasedetail.setPurchaseQty(BigDecimal.ZERO);
                    Toast.makeText(AddPurchaseActivity.mactivity,"采购数不能为空！",Toast.LENGTH_SHORT).show();
                    refreshPurchasePrice(addpurchasedetail,holder);
                } else {
                    addpurchasedetail.setPurchaseQty(BigDecimal.valueOf(Double.valueOf(s.toString())));
                    refreshPurchasePrice(addpurchasedetail,holder);
                }
            }
        };
        holder.edt_purchase_num.addTextChangedListener(watcher2);
        holder.edt_purchase_num.setTag(watcher2);

        //出产地
        //holder.edt_place.setText(addpurchasedetail.getPlace());
        //这个很重要，先移开TextWatcher的监听器
        if (holder.edt_place.getTag() instanceof android.text.TextWatcher) {
            holder.edt_place.removeTextChangedListener((TextWatcher) holder.edt_place.getTag());
        }
        holder.edt_place.setText(addpurchasedetail.getOrigin());
        TextWatcher watcher3 = new TextWatcher() {

            private ViewHolder mHolder;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    addpurchasedetail.setOrigin("");
                    Toast.makeText(AddPurchaseActivity.mactivity,"出产地不能为空！",Toast.LENGTH_SHORT).show();
                } else {
                    addpurchasedetail.setOrigin(s.toString());
                }
            }
        };
        holder.edt_place.addTextChangedListener(watcher3);
        holder.edt_place.setTag(watcher3);

        holder.edt_remark.setText(addpurchasedetail.getRemark());

        //小计
        holder.edt_purchase_price.setText(String.valueOf(addpurchasedetail.getSubtotal()));
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

    public List<PurchaseBillLine> getData() {
        return mData;
    }

    //实时更新小计
    public void refreshPurchasePrice(PurchaseBillLine addpurchasedetail,ViewHolder holder) {
        BigDecimal purchasePrice = addpurchasedetail.getPurchaseQty().multiply(addpurchasedetail.getPrice());
        addpurchasedetail.setSubtotal(purchasePrice);
        holder.edt_purchase_price.setText(String.valueOf(addpurchasedetail.getSubtotal()));
    }

}
