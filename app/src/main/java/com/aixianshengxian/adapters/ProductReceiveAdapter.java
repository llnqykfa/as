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
import com.aixianshengxian.activity.machine.StockInActivity;
import com.aixianshengxian.module.ProductReceive;
import com.aixianshengxian.util.DatesUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/10/27.
 */

public class ProductReceiveAdapter extends RecyclerView.Adapter<ProductReceiveAdapter.ViewHolder> {
    private List<ProductReceive> mData;
    private OnItemClickListener mOnItemClickListener;
    private Context context;
    private LayoutInflater inflater = null;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_product_name;
        TextView tv_unit_price;
        TextView tv_unit1;
        ImageView iv_delete;
        TextView tv_receive_num;
        TextView tv_consume_num;
        EditText edt_present_consume;
        TextView tv_unit2;
        TextView tv_receive_time;
        View myView;

        public ViewHolder(View view) {//自定义内部类，继承自RecyclerView.ViewHolder
            super(view);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            tv_unit_price = (TextView) view.findViewById(R.id.tv_unit_price);
            tv_unit1 = (TextView) view.findViewById(R.id.tv_unit1);
            iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
            tv_receive_num = (TextView) view.findViewById(R.id.tv_receive_num);
            tv_consume_num = (TextView) view.findViewById(R.id.tv_consume_num);
            edt_present_consume = (EditText) view.findViewById(R.id.edt_present_consume);
            tv_unit2 = (TextView) view.findViewById(R.id.tv_unit2);
            tv_receive_time = (TextView) view.findViewById(R.id.tv_receive_time);
            myView=view;//定义一个包括图片和文字的布局
        }
    }

    public ProductReceiveAdapter(List<ProductReceive> MData, Context context) {
        this.mData = MData;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener{
        void onDeleteClick(int position);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(ProductReceiveAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public ProductReceiveAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_machine_receive,parent,false);//传入布局
        final ProductReceiveAdapter.ViewHolder holder = new ProductReceiveAdapter.ViewHolder(view);

        if (mOnItemClickListener!=null) {
            //删除按钮监听
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
    public void onBindViewHolder(ProductReceiveAdapter.ViewHolder holder, int position) {
        final ProductReceive productreceive = mData.get(position);
        holder.tv_product_name.setText(productreceive.getProductName());//商品名称
        holder.tv_unit1.setText(productreceive.getUnit());//单位
        holder.tv_receive_num.setText(String.valueOf(productreceive.getReceiveNum()));//领用量
        holder.tv_consume_num.setText(String.valueOf(productreceive.getConsumeNum()));//消耗量

        //此次消耗量
        //这个很重要，先移开TextWatcher的监听器
        if (holder.edt_present_consume.getTag() instanceof android.text.TextWatcher) {
            holder.edt_present_consume.removeTextChangedListener((TextWatcher) holder.edt_present_consume.getTag());
        }
        holder.edt_present_consume.setText(String.valueOf(productreceive.getPresentConsume()));
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
                    productreceive.setPresentConsume(0);
                    Toast.makeText(StockInActivity.mactivity,"消耗数不能为空！",Toast.LENGTH_SHORT).show();
                } else {
                    productreceive.setPresentConsume(Double.valueOf(s.toString()));
                }
            }
        };
        holder.edt_present_consume.addTextChangedListener(watcher1);
        holder.edt_present_consume.setTag(watcher1);

        holder.tv_unit2.setText(productreceive.getUnit());//单位
        String receivetime = productreceive.getReceiveTime();//先获得String类型
        holder.tv_receive_time.setText(String.valueOf(DatesUtils.getStringHouAndMin(receivetime)));
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    //添加数据
    public void addItem(List<ProductReceive> newDatas) {

        newDatas.addAll(mData);
        mData.removeAll(mData);
        mData.addAll(newDatas);
        notifyDataSetChanged();
    }

    public void addMoreItem(List<ProductReceive> newDatas) {
        mData.addAll(newDatas);
        notifyDataSetChanged();
    }
}
