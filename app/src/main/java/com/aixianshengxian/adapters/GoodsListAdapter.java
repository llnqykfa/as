package com.aixianshengxian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.aixianshengxian.entity.SalesCatalogLineItem;
import com.xmzynt.storm.util.StringUtil;

import java.util.List;

/**
 * Created by cwj on 2017/6/9.
 */

public class GoodsListAdapter extends RecyclerView.Adapter<GoodsListAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<SalesCatalogLineItem> mDataList;
    private List<SalesCatalogLineItem> mALL;
    private boolean[] mDataSelectList;//复选框是否选中
   // private List<Boolean> mDataSelectList =new ArrayList<>();

    public interface OnItemClickLitener
    {
        void onItemClick( SalesCatalogLineItem salesCatalogLineItem);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public List<SalesCatalogLineItem> getmALL() {
        return mALL;
    }

    public GoodsListAdapter(List<SalesCatalogLineItem> mDataList, Context context, List<SalesCatalogLineItem> mALL) {
        mInflater = LayoutInflater.from(context);
        this.mDataList = mDataList;
        this.mALL = mALL;
       // this.mDataSelectList = mDataSelectList;
    }

    public void initData() {
        if (mDataList != null && mDataList.size() > 0) {
          mDataSelectList = new boolean[mDataList.size()];
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_goods_select,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(mDataList != null &&mDataList.size()>0){
            final SalesCatalogLineItem salesCatalogLineItem = mDataList.get(position);
            holder.tv_goods_no.setText(salesCatalogLineItem.getSalesCatalogLine().getGoods().getCode());//商品编码
            holder.tv_goods_name.setText(salesCatalogLineItem.getSalesCatalogLine().getGoods().getName());//商品名称
            holder.tv_unit_price.setText("￥"+ StringUtil.toString(salesCatalogLineItem.getSalesCatalogLine().getOrderPrice())+"/"+salesCatalogLineItem.getSalesCatalogLine().getGoodsUnit().getName());
//
//            if (mDataSelectList != null && position < mDataSelectList.length) {
//                holder.cbSelect.setChecked(mDataSelectList[position]);
//            }else {
//                holder.cbSelect.setChecked(false);
//            }

            // 点击CheckBox无效果
           // holder.cbSelect.setClickable(false);

            holder.itemView.setTag(position);
            holder.cbSelect.setTag(position);

            if(salesCatalogLineItem.getSelector()){
                holder.cbSelect.setChecked(true);
            }else {
                holder.cbSelect.setChecked(false);
            }

            holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if((Integer)holder.cbSelect.getTag() == position){
                        boolean ischeck = holder.cbSelect.isChecked();
                        if(holder.cbSelect.isChecked()){
                            salesCatalogLineItem .setSelector(true);
                        }else {
                            salesCatalogLineItem.setSelector(false);
                        }
                        for(SalesCatalogLineItem salesCatalogLineItem1 : mALL){
                            if(salesCatalogLineItem1.getSalesCatalogLine().getGoods().getCode().equals(salesCatalogLineItem.getSalesCatalogLine().getGoods().getCode())){
                                salesCatalogLineItem1.setSelector(salesCatalogLineItem.getSelector());//更新所有数据集
                            }
                        }
                    }else {
                        android.util.Log.d("goodsList", "postion: "+ position);
                    }
                }
            });

            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((Integer)holder.itemView.getTag() == position){
                        salesCatalogLineItem.setSelector(!salesCatalogLineItem.getSelector());
                        for(SalesCatalogLineItem salesCatalogLineItem1 : mALL){
                            if(salesCatalogLineItem1.getSalesCatalogLine().getGoods().getCode().equals(salesCatalogLineItem.getSalesCatalogLine().getGoods().getCode())){
                                salesCatalogLineItem1.setSelector(salesCatalogLineItem.getSelector());//更新所有数据集
                            }
                        }
                        notifyItemChanged(position);
                    }else {
                        android.util.Log.d("goodsList", "postion: "+ position);
                    }
                }
            });

//            }

        }else {
            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        if(mDataList != null &&mDataList.size()>0){
            return mDataList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView tv_goods_no ;
        TextView tv_goods_name;
        TextView tv_unit_price;
//        ImageView image_select;
        LinearLayout line_goods;
        CheckBox cbSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_goods_name = (TextView) itemView.findViewById(R.id.tv_goods_name);
            tv_goods_no = (TextView) itemView.findViewById(R.id.tv_goods_no);
            tv_unit_price = (TextView) itemView.findViewById(R.id.tv_unit_price);
//            image_select = (ImageView) itemView.findViewById(R.id.image_select);
            line_goods = (LinearLayout) itemView.findViewById(R.id.line_goods);
            cbSelect = (CheckBox) itemView.findViewById(R.id.cb_select);
        }
    }
    public void addMoreItem(List<SalesCatalogLineItem> newDatas) {
        mDataList.addAll(newDatas);
        notifyDataSetChanged();
    }
}
