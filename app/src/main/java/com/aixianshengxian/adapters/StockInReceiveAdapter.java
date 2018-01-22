package com.aixianshengxian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aixianshengxian.R;
import com.aixianshengxian.activity.machine.StockInActivity;
import com.aixianshengxian.module.ProductReceive;
import com.aixianshengxian.module.StockOutRecordConsume;
import com.aixianshengxian.util.DatesUtils;
import com.xmzynt.storm.service.goods.Goods;
import com.xmzynt.storm.service.wms.MaterialsTree;
import com.xmzynt.storm.service.wms.stockout.StockOutRecord;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/10/27.
 */

public class StockInReceiveAdapter extends RecyclerView.Adapter<StockInReceiveAdapter.ViewHolder> {
    private List<StockOutRecordConsume> mStockOutRecord;
    private OnItemClickListener mOnItemClickListener;
    private Context context;
    private LayoutInflater inflater = null;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_product_name;
        TextView tv_unit_price;
        TextView tv_unit;
        TextView tv_receive_num;
        TextView tv_consume_num;
        EditText edt_present_consume;
        TextView tv_receive_time;
        ImageView iv_delete;
        View myView;

        public ViewHolder(View view) {//自定义内部类，继承自RecyclerView.ViewHolder
            super(view);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            tv_unit_price = (TextView) view.findViewById(R.id.tv_unit_price);
            tv_unit = (TextView) view.findViewById(R.id.tv_unit);
            tv_receive_num = (TextView) view.findViewById(R.id.tv_receive_num);
            tv_consume_num = (TextView) view.findViewById(R.id.tv_consume_num);
            edt_present_consume = (EditText) view.findViewById(R.id.edt_present_consume);
            tv_receive_time = (TextView) view.findViewById(R.id.tv_receive_time);
            iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
            myView=view;//定义一个包括图片和文字的布局
        }
    }

    public StockInReceiveAdapter(List<StockOutRecordConsume> mStockOutRecord, Context context) {
        this.mStockOutRecord = mStockOutRecord;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener{
        void onDeleteClick(int position);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(StockInReceiveAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public StockInReceiveAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_machine_receive,parent,false);//传入布局
        final StockInReceiveAdapter.ViewHolder holder = new StockInReceiveAdapter.ViewHolder(view);

        //删除按钮监听
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();//获取点击位置
                mOnItemClickListener.onDeleteClick(position);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(StockInReceiveAdapter.ViewHolder holder, int position) {
        final StockOutRecordConsume stockInReceive = mStockOutRecord.get(position);
        holder.tv_product_name.setText(stockInReceive.getStockOutRecord().getGoods().getName());//商品名称
        holder.tv_unit.setText(stockInReceive.getStockOutRecord().getGoodsUnit().getName());//单位
        holder.tv_receive_num.setText(String.valueOf(stockInReceive.getStockOutRecord().getQuantity()));//领用量
        BigDecimal consumeNum = stockInReceive.getStockOutRecord().getQuantity().subtract(stockInReceive.getStockOutRecord().getRemainderQty());//消耗量
        holder.tv_consume_num.setText(String.valueOf(consumeNum));//消耗量

        //这个很重要，先移开TextWatcher的监听器
        if (holder.edt_present_consume.getTag() instanceof TextWatcher) {
            holder.edt_present_consume.removeTextChangedListener((TextWatcher) holder.edt_present_consume.getTag());
        }
        holder.edt_present_consume.setText(String.valueOf(0));//本次消耗数变化监听
        TextWatcher watcher = new TextWatcher() {

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
                    stockInReceive.setConsume(BigDecimal.valueOf(0));
                    Toast.makeText(StockInActivity.mactivity,"消耗数量不能为空",Toast.LENGTH_SHORT).show();
                } else {
                    stockInReceive.setConsume(BigDecimal.valueOf(Double.parseDouble(s.toString())));
                }
            }
        };
        holder.edt_present_consume.addTextChangedListener(watcher);
        holder.edt_present_consume.setTag(watcher);

        Date receivetime = stockInReceive.getStockOutRecord().getDeliveryTime();//先获得String类型
        holder.tv_receive_time.setText(String.valueOf(DatesUtils.dateToStrLong(receivetime)));
    }

    @Override
    public int getItemCount() {
        return mStockOutRecord == null ? 0 : mStockOutRecord.size();
    }

    //添加数据
    public void addItem(List<StockOutRecordConsume> newDatas) {

        newDatas.addAll(mStockOutRecord);
        mStockOutRecord.removeAll(mStockOutRecord);
        mStockOutRecord.addAll(newDatas);
        notifyDataSetChanged();
    }

    public void addMoreItem(List<StockOutRecordConsume> newDatas) {
        mStockOutRecord.addAll(newDatas);
        notifyDataSetChanged();
    }
}
