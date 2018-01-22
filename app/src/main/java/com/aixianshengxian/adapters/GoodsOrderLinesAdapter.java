package com.aixianshengxian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.xmzynt.storm.service.sale.GoodsOrderLine;
import com.xmzynt.storm.util.BigDecimalUtil;
import com.xmzynt.storm.util.StringUtil;

import java.util.List;

/**
 * Created by cwj on 2017/6/7.
 */

public class GoodsOrderLinesAdapter extends RecyclerView.Adapter<GoodsOrderLinesAdapter.ViewHolder>{

    private List<GoodsOrderLine> lines;
    private LayoutInflater mInflayter;

    public GoodsOrderLinesAdapter(List<GoodsOrderLine> lines, Context context) {
        this.mInflayter=LayoutInflater.from(context);
        this.lines = lines;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflayter.inflate(R.layout.item_goods_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(lines != null && lines.size()>0){
            GoodsOrderLine goodsOrderLine = lines.get(position);

            holder.tv_goods_no.setText(""+goodsOrderLine.getLineNo());//序号
            holder.tv_goods_amount.setText(""+ BigDecimalUtil.convertToScale(goodsOrderLine.getGoodsQty().multiply(lines.get(position).getOrderPrice()), 2));//金额
            holder.tv_goods_count.setText(StringUtil.toString(goodsOrderLine.getGoodsQty()));//数量
            holder.tv_goods_unit_price.setText(StringUtil.toString(goodsOrderLine.getOrderPrice()));//单价
            holder.tv_goods_unit.setText(""+goodsOrderLine.getGoodsUnit().getName());//单位
            holder.tv_goods_name.setText(""+goodsOrderLine.getGoods().getName());//名称
            holder.itemView.setTag(position);
        }else {
            holder.itemView.setTag(position);
        }

    }

    @Override
    public int getItemCount() {
        if(lines != null && lines.size() >0){
            return lines.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_goods_no;
        TextView tv_goods_name;
        TextView tv_goods_unit ;
        TextView tv_goods_count ;
        TextView tv_goods_unit_price;
        TextView tv_goods_amount;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_goods_amount = (TextView) itemView.findViewById(R.id.tv_goods_amount);
            tv_goods_no = (TextView) itemView.findViewById(R.id.tv_goods_no);
            tv_goods_name = (TextView) itemView.findViewById(R.id.tv_goods_name);
            tv_goods_unit = (TextView) itemView.findViewById(R.id.tv_goods_unit);
            tv_goods_count = (TextView) itemView.findViewById(R.id.tv_goods_count);
            tv_goods_unit_price = (TextView) itemView.findViewById(R.id.tv_goods_unit_price);

        }
    }
}
