package com.aixianshengxian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.xmzynt.storm.service.goods.Goods;

import java.util.List;

/**
 * Created by cwj on 2017/6/9.
 */

public class TempGoodsListAdapter extends RecyclerView.Adapter<TempGoodsListAdapter.ViewHolder>{

    LayoutInflater mInflater;
    private List<Goods> mDataList ;

    public interface OnItemClickLitener
    {
        void onItemClick( Goods goods);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
    public TempGoodsListAdapter(List<Goods> mDataList, Context context) {
        mInflater = LayoutInflater.from(context);
        this.mDataList = mDataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_temp_goods_list,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        if(mDataList != null &&mDataList.size()>0){
            final Goods goods = mDataList.get(position);
            holder.tv_goods_name.setText(goods.getName());
            holder.tv_goods_no.setText(goods.getCode());
            holder.line_goods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(goods);
                }
            });
           // holder.tv_unit.setText(goods.ge);
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
        LinearLayout line_goods;
        TextView tv_goods_no;
        TextView tv_goods_name;
        TextView tv_unit;
        public ViewHolder(View itemView) {
            super(itemView);
            line_goods = (LinearLayout) itemView.findViewById(R.id.line_goods);
            tv_goods_no = (TextView) itemView.findViewById(R.id.tv_goods_no);
            tv_goods_name = (TextView) itemView.findViewById(R.id.tv_goods_name);
            tv_unit = (TextView) itemView.findViewById(R.id.tv_unit);
        }
    }
}
