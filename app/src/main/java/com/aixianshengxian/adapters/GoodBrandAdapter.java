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
import com.aixianshengxian.module.GoodsItem;
import com.aixianshengxian.util.PurchaseBillUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/11/13.
 */

public class GoodBrandAdapter extends RecyclerView.Adapter<GoodBrandAdapter.ViewHolder> {
    private List<GoodsItem> mGoodsItem;
    private Map<String,GoodsItem> mCheckedGoodsItem;
    private boolean[] mDataSelectList;//复选框是否选中
    private OnItemClickListener mOnItemClickListener;
    private Context context;
    private LayoutInflater inflater = null;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_product_name;
        TextView tv_unit;
        CheckBox cbtn_choose;
        View myView;

        public ViewHolder(View view){//自定义内部类，继承自RecyclerView.ViewHolder
            super(view);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            tv_unit = (TextView) view.findViewById(R.id.tv_unit);
            cbtn_choose = (CheckBox) view.findViewById(R.id.cbtn_choose);
            myView=view;//定义一个包括图片和文字的布局
        }
    }

    public Map<String,GoodsItem> getmCheckedGoodsItem() {
        return mCheckedGoodsItem;
    }

    public GoodBrandAdapter(List<GoodsItem> MGoodsItem,Map<String,GoodsItem> MCheckedGoodsItem,Context context){
        //this.mData = MData;
        this.mGoodsItem = MGoodsItem;
        this.mCheckedGoodsItem = MCheckedGoodsItem;
        this.context = context;
        inflater = LayoutInflater.from(context);
        initData();
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener{
        void onItemClick(GoodsItem goodsitem);
        //void onCheckBoxClick(int position,Boolean isChecked);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(GoodBrandAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void initData() {
        if (mGoodsItem != null && mGoodsItem.size() > 0) {
            mDataSelectList = new boolean[mGoodsItem.size()];
        }
        for (GoodsItem goodsItem : mGoodsItem) {
            if (mCheckedGoodsItem.get(goodsItem.getGoods().getUuid()) != null) {
                goodsItem.setSelector(true);
            }
        }
    }



    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_search_brand, parent, false);//传入布局

        final ViewHolder holder = new ViewHolder(view);

        if (mOnItemClickListener != null) {
            //整体监听
            holder.myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onItemClick(mGoodsItem.get(position));
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
        if(mGoodsItem != null &&mGoodsItem.size()>0){
            final GoodsItem goodsitem= mGoodsItem.get(position);
            String uuid = goodsitem.getGoods().getCode();
            String name = goodsitem.getGoods().getName();
            String spec =  goodsitem.getGoods().getSpec()==null?null:goodsitem.getGoods().getSpec();

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

            holder.tv_unit.setText(goodsitem.getGoods().getSdUnit().getName());//单位

            holder.itemView.setTag(position);
            holder.cbtn_choose.setTag(position);

            if(goodsitem.getSelector()){
                holder.cbtn_choose.setChecked(true);
            }else {
                holder.cbtn_choose.setChecked(false);
            }

            holder.cbtn_choose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if((Integer)holder.cbtn_choose.getTag() == position){
                        if(holder.cbtn_choose.isChecked()){
                            goodsitem.setSelector(true);
                            mCheckedGoodsItem.put(goodsitem.getGoods().getUuid(),goodsitem);
                        }else {
                            goodsitem.setSelector(false);
                            mCheckedGoodsItem.remove(goodsitem.getGoods().getUuid());
                        }

                    }else {
                        android.util.Log.d("goodsList", "position: "+ position);
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((Integer)holder.cbtn_choose.getTag() == position){
                        goodsitem.setSelector(!goodsitem.getSelector());
                        holder.cbtn_choose.setChecked(goodsitem.getSelector());
                        notifyItemChanged(position);
                        if(holder.cbtn_choose.isChecked()){
                            goodsitem.setSelector(true);
                            mCheckedGoodsItem.put(goodsitem.getGoods().getUuid(),goodsitem);
                        }else {
                            goodsitem.setSelector(false);
                            mCheckedGoodsItem.remove(goodsitem.getGoods().getUuid());
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
        return mGoodsItem == null ? 0 : mGoodsItem.size();
    }

    //添加数据
    public void addItem(List<GoodsItem> newDatas) {
        mGoodsItem.clear();
        mGoodsItem.addAll(newDatas);
    }

    public void addMoreItem(List<GoodsItem> newDatas) {
        mGoodsItem.addAll(newDatas);
    }

    public List<GoodsItem> getData() {
        return mGoodsItem;
    }
}
