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
import com.aixianshengxian.util.DatesUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/27.
 */

public class StockOutRecordItemAdapter extends RecyclerView.Adapter<StockOutRecordItemAdapter.ViewHolder> {
    private List<StockOutRecordItem> mStockOutRecordItem;
    private Map<String,StockOutRecordItem> mCheckedStockOutRecordItem;
    private boolean[] mDataSelectList;//复选框是否选中
    private OnItemClickListener mOnItemClickListener;
    private Context context;
    private LayoutInflater inflater = null;

    class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbtn_choose;
        TextView tv_product_name;
        TextView tv_unit_price;
        TextView tv_unit;
        TextView tv_receive_num;
        TextView tv_consume_num;
        TextView tv_receive_time;
        View myView;

        public ViewHolder(View view) {//自定义内部类，继承自RecyclerView.ViewHolder
            super(view);
            cbtn_choose = (CheckBox) view.findViewById(R.id.cbtn_choose);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            tv_unit_price = (TextView) view.findViewById(R.id.tv_unit_price);
            tv_unit = (TextView) view.findViewById(R.id.tv_unit1);
            tv_receive_num = (TextView) view.findViewById(R.id.tv_receive_num);
            tv_consume_num = (TextView) view.findViewById(R.id.tv_consume_num);
            tv_receive_time = (TextView) view.findViewById(R.id.tv_receive_time);
            myView=view;//定义一个包括图片和文字的布局
        }
    }

    public StockOutRecordItemAdapter(List<StockOutRecordItem> MData, Map<String,StockOutRecordItem> CheckedStockOutRecordItem, Context context) {
        this.mStockOutRecordItem = MData;
        this.mCheckedStockOutRecordItem = CheckedStockOutRecordItem;
        this.context = context;
        inflater = LayoutInflater.from(context);
        initData();
    }

    public Map<String,StockOutRecordItem> getmCheckedStockOutRecordItem() {
        return mCheckedStockOutRecordItem;
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener{
        void onItemClick(StockOutRecordItem stockOutRecordItem);
        //void onCheckBoxClick(int position,Boolean isChecked);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(StockOutRecordItemAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void initData() {
        if (mStockOutRecordItem != null && mStockOutRecordItem.size() > 0) {
            mDataSelectList = new boolean[mStockOutRecordItem.size()];
        }
        for (StockOutRecordItem stockOutRecordItem : mStockOutRecordItem) {
            if (mCheckedStockOutRecordItem.get(stockOutRecordItem.getStockOutRecord().getUuid()) != null) {
                stockOutRecordItem.setSelector(true);
            }
        }
    }

    @Override
    public StockOutRecordItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_select_receive,parent,false);//传入布局
        final StockOutRecordItemAdapter.ViewHolder holder = new StockOutRecordItemAdapter.ViewHolder(view);

        if (mOnItemClickListener!=null) {
            //整体监听
            holder.myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onItemClick(mStockOutRecordItem.get(position));
                }
            });
            //复选框按钮监听
            /*holder.cbtn_choose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onCheckBoxClick(position,isChecked);
                }
            });*/
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        if(mStockOutRecordItem != null && mStockOutRecordItem.size()>0){
            final StockOutRecordItem stockOutRecord = mStockOutRecordItem.get(position);
            holder.tv_product_name.setText(stockOutRecord.getStockOutRecord().getGoods().getName());//商品名称
            holder.tv_unit.setText(stockOutRecord.getStockOutRecord().getGoodsUnit().getName());//单位
            holder.tv_receive_num.setText(String.valueOf(stockOutRecord.getStockOutRecord().getQuantity()));//领用量

            BigDecimal consume = stockOutRecord.getStockOutRecord().getQuantity().subtract(stockOutRecord.getStockOutRecord().getRemainderQty());
            holder.tv_consume_num.setText(String.valueOf(consume));//消耗量
            if(stockOutRecord.getStockOutRecord().getDeliveryTime()!=null){
                holder.tv_receive_time.setText(DatesUtils.dateToStr(stockOutRecord.getStockOutRecord().getDeliveryTime()));
            }else{
                holder.tv_receive_time.setText("");
            }

            holder.itemView.setTag(position);
            holder.cbtn_choose.setTag(position);

            if(stockOutRecord.getSelector()){
                holder.cbtn_choose.setChecked(true);
            }else {
                holder.cbtn_choose.setChecked(false);
            }

            holder.cbtn_choose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if((Integer)holder.cbtn_choose.getTag() == position){
                        boolean ischeck = holder.cbtn_choose.isChecked();
                        if(holder.cbtn_choose.isChecked()){
                            stockOutRecord.setSelector(true);
                            mCheckedStockOutRecordItem.put(stockOutRecord.getStockOutRecord().getUuid(),stockOutRecord);
                        }else {
                            stockOutRecord.setSelector(false);
                            mCheckedStockOutRecordItem.remove(stockOutRecord.getStockOutRecord().getUuid());
                        }

                    }else {
                        android.util.Log.d("stockOutRecordList", "position: "+ position);
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((Integer)holder.cbtn_choose.getTag() == position){
                        stockOutRecord.setSelector(!stockOutRecord.getSelector());
                        holder.cbtn_choose.setChecked(stockOutRecord.getSelector());
                        notifyItemChanged(position);
                        if(holder.cbtn_choose.isChecked()){
                            stockOutRecord.setSelector(true);
                            mCheckedStockOutRecordItem.put(stockOutRecord.getStockOutRecord().getUuid(),stockOutRecord);
                        }else {
                            stockOutRecord.setSelector(false);
                            mCheckedStockOutRecordItem.remove(stockOutRecord.getStockOutRecord().getUuid());
                        }

                    }else {
                        android.util.Log.d("stockOutRecordList", "position: "+ position);
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

    //添加数据
    public void addItem(List<StockOutRecordItem> newDatas) {
        mStockOutRecordItem.clear();
        mStockOutRecordItem.addAll(newDatas);
    }

    public void addMoreItem(List<StockOutRecordItem> newDatas) {
        mStockOutRecordItem.addAll(newDatas);
    }

    public List<StockOutRecordItem> getData() {
        return mStockOutRecordItem;
    }
}
