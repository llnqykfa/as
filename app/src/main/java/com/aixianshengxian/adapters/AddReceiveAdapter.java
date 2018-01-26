package com.aixianshengxian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.aixianshengxian.module.StockOutRecordItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/13.
 */

public class AddReceiveAdapter extends RecyclerView.Adapter<AddReceiveAdapter.ViewHolder> {
    private List<StockOutRecordItem> mStockOutRecordItem;
    private Map<String,StockOutRecordItem> mCheckedOutItem;
    private boolean[] mDataSelectList;//复选框是否选中
    private OnItemClickListener mOnItemClickListener;
    private Context context;
    private LayoutInflater inflater = null;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_product_name;
        TextView tv_price;
        TextView tv_unit;
        TextView tv_receive_num;
        TextView tv_consume_num;
        TextView tv_receive_time;
        CheckBox cbtn_choose;
        View myView;

        public ViewHolder(View view){//自定义内部类，继承自RecyclerView.ViewHolder
            super(view);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            tv_price = (TextView) view.findViewById(R.id.tv_unit_price);
            tv_unit = (TextView) view.findViewById(R.id.tv_unit);
            tv_receive_num = (TextView) view.findViewById(R.id.tv_receive_num);
            tv_consume_num = (TextView) view.findViewById(R.id.tv_consume_num);
            tv_receive_time = (TextView) view.findViewById(R.id.tv_receive_time);
            cbtn_choose = (CheckBox) view.findViewById(R.id.cbtn_choose);
            myView=view;//定义一个包括图片和文字的布局
        }
    }

    public Map<String,StockOutRecordItem> getCheckedOutItem() {
        return mCheckedOutItem;
    }

    public AddReceiveAdapter(List<StockOutRecordItem> StockOutRecordItem, Map<String,StockOutRecordItem> CheckedOutItem, Context context){
        this.mStockOutRecordItem = StockOutRecordItem;
        this.mCheckedOutItem = CheckedOutItem;
        this.context = context;
        inflater = LayoutInflater.from(context);
        initData();
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener{
        void onItemClick(StockOutRecordItem stockOutRecordItem);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void initData() {
        if (mStockOutRecordItem != null && mStockOutRecordItem.size() > 0) {
            mDataSelectList = new boolean[mStockOutRecordItem.size()];
        }
        for (StockOutRecordItem stockOutRecordItem : mStockOutRecordItem) {
            if (mCheckedOutItem.get(stockOutRecordItem.getStockOutRecord().getGoods().getUuid()) != null) {
                stockOutRecordItem.setSelector(true);
            }
        }
    }



    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_select_receive, parent, false);//传入布局

        final ViewHolder holder = new ViewHolder(view);

        if (mOnItemClickListener != null) {
            //整体监听
            holder.myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onItemClick(mStockOutRecordItem.get(position));
                }
            });

            /*//复选框按钮监听
            holder.cbtn_choose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onCheckBoxClick(position,isChecked);
                }
            });*/
        }
        return holder;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(mStockOutRecordItem != null && mStockOutRecordItem.size()>0){
            final StockOutRecordItem stockOutRecordItem= mStockOutRecordItem.get(position);
            holder.tv_product_name.setText(stockOutRecordItem.getStockOutRecord().getGoods().getName());//商品名称
            //holder.tv_price.setText(String.valueOf(stockOutRecordItem.getStockOutRecord().getPrice()));//单价
            holder.tv_unit.setText(stockOutRecordItem.getStockOutRecord().getGoodsUnit().getName());//单位
            holder.tv_receive_num.setText(String.valueOf(stockOutRecordItem.getStockOutRecord().getQuantity()));//领用量
            BigDecimal consumeNum = stockOutRecordItem.getStockOutRecord().getQuantity().subtract(stockOutRecordItem.getStockOutRecord().getRemainderQty());//消耗量
            holder.tv_consume_num.setText(String.valueOf(consumeNum));//消耗量

            holder.cbtn_choose.setTag(position);

            if(stockOutRecordItem.getSelector()){
                holder.cbtn_choose.setChecked(true);
            }else {
                holder.cbtn_choose.setChecked(false);
            }

            holder.cbtn_choose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if((Integer)holder.cbtn_choose.getTag() == position){
                        //boolean ischeck = holder.cbtn_choose.isChecked();
                        if(holder.cbtn_choose.isChecked()){
                            stockOutRecordItem.setSelector(true);
                            mCheckedOutItem.put(stockOutRecordItem.getStockOutRecord().getGoods().getUuid(),stockOutRecordItem);
                        }else {
                            stockOutRecordItem.setSelector(false);
                            mCheckedOutItem.remove(stockOutRecordItem.getStockOutRecord().getGoods().getUuid());
                        }

                    }else {
                        android.util.Log.d("goodsList", "position: "+ position);
                    }
                }
            });
        } else {

        }
    }

    @Override
    public int getItemCount() {
        return mStockOutRecordItem == null ? 0 : mStockOutRecordItem.size();
    }
}
