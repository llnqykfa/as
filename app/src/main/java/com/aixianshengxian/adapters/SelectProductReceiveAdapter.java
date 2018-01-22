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
import com.aixianshengxian.util.DatesUtils;
import com.xmzynt.storm.service.wms.stockin.StockInRecord;

import java.util.List;

/**
 * Created by Administrator on 2017/10/27.
 */

public class SelectProductReceiveAdapter extends RecyclerView.Adapter<SelectProductReceiveAdapter.ViewHolder> {
    private List<StockInRecord> mData;
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

    public SelectProductReceiveAdapter(List<StockInRecord> MData, Context context) {
        this.mData = MData;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener{
        void onCheckBoxClick(int position,Boolean isChecked);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(SelectProductReceiveAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public SelectProductReceiveAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_machine_receive,parent,false);//传入布局
        final SelectProductReceiveAdapter.ViewHolder holder = new SelectProductReceiveAdapter.ViewHolder(view);

        if (mOnItemClickListener!=null) {
            //复选框按钮监听
            holder.cbtn_choose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onCheckBoxClick(position,isChecked);
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(SelectProductReceiveAdapter.ViewHolder holder, int position) {
        final StockInRecord stockInRecord = mData.get(position);
        holder.tv_product_name.setText(stockInRecord.getGoods().getName());//商品名称
        holder.tv_unit.setText(stockInRecord.getGoodsUnit().getName());//单位
        holder.tv_receive_num.setText(String.valueOf(stockInRecord.getQuantity()));//领用量

        holder.tv_consume_num.setText(String.valueOf(stockInRecord.getDeductQty()));//消耗量
        if(stockInRecord.getDeliveryTime()!=null){
            holder.tv_receive_time.setText(DatesUtils.dateToStr(stockInRecord.getDeliveryTime()));
        }else{
            holder.tv_receive_time.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    //添加数据
    public void addItem(List<StockInRecord> newDatas) {

        mData.clear();
        mData.addAll(newDatas);
        notifyDataSetChanged();
    }

    public void addMoreItem(List<StockInRecord> newDatas) {
        mData.addAll(newDatas);
        notifyDataSetChanged();
    }
}
