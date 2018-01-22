package com.aixianshengxian.adapters;

import android.content.Context;
import android.media.Image;
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

import com.aixianshengxian.activity.machine.StockInActivity;
import com.aixianshengxian.activity.purchase.AddPurchaseActivity;
import com.aixianshengxian.module.ProductStockIn;
import com.aixianshengxian.R;

import java.util.List;

/**
 * Created by Administrator on 2017/10/25.
 */

public class ProductStockInAdapter extends RecyclerView.Adapter<ProductStockInAdapter.ViewHolder> {
    private List<ProductStockIn> mData;
    private OnItemClickListener mOnItemClickListener;
    private Context context;
    private LayoutInflater inflater = null;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_product_name;
        EditText edt_stock_in_num;
        TextView tv_unit;
        EditText edt_stock_in_price;
        EditText edt_stay_day;
        ImageView iv_delete;
        View myView;

        public ViewHolder(View view) {//自定义内部类，继承自RecyclerView.ViewHolder
            super(view);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            edt_stock_in_num = (EditText) view.findViewById(R.id.edt_stock_in_num);
            tv_unit = (TextView) view.findViewById(R.id.tv_unit);
            edt_stock_in_price = (EditText) view.findViewById(R.id.edt_stock_in_price);
            edt_stay_day = (EditText) view.findViewById(R.id.edt_stay_day);
            iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
            myView=view;//定义一个包括图片和文字的布局
        }
    }

    public ProductStockInAdapter(List<ProductStockIn> MData, Context context) {
        this.mData = MData;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener{
        void onDeleteClick(int position);
        void onItemClick(ProductStockIn productstockin,int position);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(ProductStockInAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public ProductStockInAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_add_stock_in,parent,false);//传入布局
        final ProductStockInAdapter.ViewHolder holder = new ProductStockInAdapter.ViewHolder(view);

        if (mOnItemClickListener!=null) {
            //删除按钮监听
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onDeleteClick(position);
                }
            });

            holder.myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onItemClick(mData.get(position),position);
                }
            });
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(ProductStockInAdapter.ViewHolder holder, int position) {
        final ProductStockIn productstockin = mData.get(position);
        holder.tv_product_name.setText(productstockin.getProductName());//商品名称

        //入库数
        //这个很重要，先移开TextWatcher的监听器
        if (holder.edt_stock_in_num.getTag() instanceof android.text.TextWatcher) {
            holder.edt_stock_in_num.removeTextChangedListener((TextWatcher) holder.edt_stock_in_num.getTag());
        }
        holder.edt_stock_in_num.setText(String.valueOf(productstockin.getStockInNum()));
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
                    productstockin.setStockInNum(0);
                    Toast.makeText(StockInActivity.mactivity,"入库数不能为空！",Toast.LENGTH_SHORT).show();
                } else {
                    productstockin.setStockInNum(Double.valueOf(s.toString()));
                }
            }
        };
        holder.edt_stock_in_num.addTextChangedListener(watcher1);
        holder.edt_stock_in_num.setTag(watcher1);

        //入库单价
        //这个很重要，先移开TextWatcher的监听器
        if (holder.edt_stock_in_price.getTag() instanceof android.text.TextWatcher) {
            holder.edt_stock_in_price.removeTextChangedListener((TextWatcher) holder.edt_stock_in_price.getTag());
        }
        holder.edt_stock_in_price.setText(String.valueOf(productstockin.getUnitPrice()));
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
                    productstockin.setUnitPrice(0);
                    Toast.makeText(StockInActivity.mactivity,"入库数不能为空！",Toast.LENGTH_SHORT).show();
                } else {
                    productstockin.setUnitPrice(Double.valueOf(s.toString()));
                }
            }
        };
        holder.edt_stock_in_price.addTextChangedListener(watcher1);
        holder.edt_stock_in_price.setTag(watcher2);

        holder.tv_unit.setText(productstockin.getUnit());//单位

        //保质期
        //这个很重要，先移开TextWatcher的监听器
        if (holder.edt_stay_day.getTag() instanceof android.text.TextWatcher) {
            holder.edt_stay_day.removeTextChangedListener((TextWatcher) holder.edt_stay_day.getTag());
        }
        holder.edt_stay_day.setText(String.valueOf(productstockin.getStayDay()));
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
                    productstockin.setStayDay(0);
                    Toast.makeText(StockInActivity.mactivity,"入库数不能为空！",Toast.LENGTH_SHORT).show();
                } else {
                    productstockin.setStayDay(Double.valueOf(s.toString()));
                }
            }
        };
        holder.edt_stay_day.addTextChangedListener(watcher1);
        holder.edt_stay_day.setTag(watcher3);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    //添加数据
    public void addItem(List<ProductStockIn> newDatas) {

        newDatas.addAll(mData);
        mData.removeAll(mData);
        mData.addAll(newDatas);
        notifyDataSetChanged();
    }

    public void addMoreItem(List<ProductStockIn> newDatas) {
        mData.addAll(newDatas);
        notifyDataSetChanged();
    }

}
