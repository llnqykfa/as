package com.aixianshengxian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.aixianshengxian.util.DatesUtils;
import com.aixianshengxian.util.PurchaseBillUtil;
import com.xmzynt.storm.service.purchase.bill.PurchaseBill;
import com.xmzynt.storm.service.wms.stock.Stock;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/13.
 */

public class StockRecordAdapter extends RecyclerView.Adapter<StockRecordAdapter.ViewHolder> {
    private List<Stock> mStock;
    private OnItemClickListener mOnItemClickListener;
    private Context context;
    private LayoutInflater inflater = null;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_product_name;
        TextView tv_stock_num;
        TextView tv_unit;
        TextView tv_associated;
        View myView;

        public ViewHolder(View view){//自定义内部类，继承自RecyclerView.ViewHolder
            super(view);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            tv_stock_num = (TextView) view.findViewById(R.id.tv_stock_num);
            tv_unit = (TextView) view.findViewById(R.id.tv_unit);
            //tv_associated = (TextView) view.findViewById(R.id.tv_associated);
            myView=view;//定义一个包括图片和文字的布局
        }
    }

    public StockRecordAdapter(List<Stock> MData,Context context) {
        this.mStock = MData;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener{
        void onItemClick(Stock stock);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(StockRecordAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public StockRecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        final View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_search_stock_record,parent,false);//传入布局
        final StockRecordAdapter.ViewHolder holder = new ViewHolder(view);

        if (mOnItemClickListener!=null) {
            //整体监听
            holder.myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onItemClick(mStock.get(position));
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Stock stock = mStock.get(position);
        String uuid = stock.getGoods().getCode();
        String name = stock.getGoods().getName();
        String spec =  stock.getGoodsSpec()==null?null:stock.getGoodsSpec();

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
        holder.tv_stock_num.setText(String.valueOf(stock.getAmount()));//库存数
        holder.tv_unit.setText(stock.getGoodsUnit().getName());//单位

//        if (stock.getBasketCodes() != null) {
//            List<String> associatedBasket = stock.getBasketCodes();
//            showAssociatedBasket(associatedBasket,holder);
//        } else {
//            holder.tv_associated.setText("没有关联周转箱");
//        }

    }

    //关联周转箱
    private void showAssociatedBasket(List<String> associatedBasket, ViewHolder holder) {
        String basketStr = new String();
        for (int i = 0;i < associatedBasket.size();i ++) {
            if (i == associatedBasket.size() - 1) {
                basketStr = basketStr + associatedBasket.get(i);
            } else {
                basketStr = basketStr + associatedBasket.get(i) + ",";
            }
        }
        holder.tv_associated.setText(basketStr);
    }

    @Override
    public int getItemCount() {
        return mStock == null ? 0 : mStock.size();
    }

    //添加数据
    public void addItem(List<Stock> newDatas) {

//        newDatas.addAll(mData);
//        mData.removeAll(mData);
        mStock.clear();
        mStock.addAll(newDatas);
//        notifyDataSetChanged();
    }

    public void addMoreItem(List<Stock> newDatas) {
        mStock.addAll(newDatas);
//        notifyDataSetChanged();
    }

    public List<Stock> getData() {
        return mStock;
    }
}
