package com.aixianshengxian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.xmzynt.storm.service.sale.GoodsOrderLine;
import com.xmzynt.storm.service.sale.StockOutLine;
import com.xmzynt.storm.util.BigDecimalUtil;
import com.xmzynt.storm.util.StringUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by cwj on 2017/6/7.
 */

public class StockoutListAdapter extends RecyclerView.Adapter<StockoutListAdapter.ViewHolder> {
     private    LayoutInflater mInflater;
      private   List<GoodsOrderLine> mDataList;
    private  List<StockOutLine> mStockoutLines ;


    public List<GoodsOrderLine> getmDataList() {
        return mDataList;
    }

    public StockoutListAdapter(List<GoodsOrderLine> mDataList,List<StockOutLine> mStockoutLines, Context context) {
        this.mDataList = mDataList;
        this.mStockoutLines = mStockoutLines ;
        mInflater = LayoutInflater.from(context);
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_goods_stock_out,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder( final ViewHolder holder, final int position) {
        holder.itemView.setTag(position);

        if(mDataList != null && mDataList.size()>0
                && mStockoutLines != null && mStockoutLines.size()>0
                && mStockoutLines.size()== mDataList.size()){
          final   GoodsOrderLine goodsOrderLine = mDataList.get(position);
            final  StockOutLine stockOutLine = mStockoutLines.get(position);
            holder.tv_goods_name.setText(goodsOrderLine.getGoods().getName());
            holder.tv_order_quantity.setText(StringUtil.toString(goodsOrderLine.getGoodsQty())+goodsOrderLine.getGoodsUnit().getName());
            holder.tv_has_stock_out_qtty.setText(StringUtil.toString(goodsOrderLine.getHasStockOutQty()));
          /***TODO:(防止数据错乱的方法一)***/
            //首先删除绑定监听
            holder.edit_stock_out_quantity.removeTextChangedListener((TextWatcher) (holder.edit_stock_out_quantity.getTag()));
            //删除绑定监听后设值
            if (stockOutLine.getStockOutQty().compareTo(BigDecimal.ZERO) != 0 ) {
                holder.edit_stock_out_quantity.setText(StringUtil.toString( stockOutLine.getStockOutQty()));
            }else{
                holder.edit_stock_out_quantity.setText("");
            }
            //设置监听
            TextWatcher textWatcher1 = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    //设定Bean值
                    String value = s.toString();
                    if (mStockoutLines != null && value != null) {
                        Log.d("test", "s=" + value);
                        BigDecimal quantity;
                        if (value.length() == 0) {
                            quantity = new BigDecimal(0);
                        } else {
                            quantity = new BigDecimal(value);
                        }

                        quantity = BigDecimalUtil.convertToScale(quantity, 2);
                        mStockoutLines.get(position).setStockOutQty(quantity);
//                        notifyDataSetChanged();
                    }else {
                        Log.d("stockoutList", "afterTextChanged: "+"123");
                    }

                }
            };

            //绑定监听
            holder.edit_stock_out_quantity.addTextChangedListener(textWatcher1);
            //监听设定为TAG
            holder.edit_stock_out_quantity.setTag(textWatcher1);
            holder.edit_stock_out_quantity.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){//获得焦点时清除当前值
                       // holder.edit_stock_out_quantity.setText("");
                    }else {
                        if (stockOutLine.getStockOutQty().compareTo(BigDecimal.ZERO) != 0 ) {
                            holder.edit_stock_out_quantity.setText(StringUtil.toString( stockOutLine.getStockOutQty()));
                        }else{
                            holder.edit_stock_out_quantity.setText("");
                        }
                    }
                }
            });
/***TODO:(防止数据错乱的方法二)***/
        //设定控件的值 ，从bean中获得
        if(goodsOrderLine.getRemark()!= null && !TextUtils.isEmpty(goodsOrderLine.getRemark())){
            holder.edit_remark.setText(goodsOrderLine.getRemark());

        }else {
            holder.edit_remark.setText("");
        }
            //设定postion 为Tag
            holder.edit_remark.setTag(position);
            holder.edit_remark.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){//获得焦点时清除当前值
                        //holder.edit_remark.setText("");
                    }else {
                        if(goodsOrderLine.getRemark()!= null && !TextUtils.isEmpty(goodsOrderLine.getRemark())){
                            holder.edit_remark.setText(goodsOrderLine.getRemark());

                        }else {
                            holder.edit_remark.setText("");
                        }
                    }
                }
            });
            holder.edit_remark.addTextChangedListener(new TextWatcher() {
                private CharSequence temp;
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                        temp = s ;
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String value = temp.toString();
//判断TAG是否为当前Position 并且该控件是否获得焦点
                    if ((Integer) holder.edit_remark.getTag() == position
                            && holder.edit_remark.hasFocus()) {
                        Log.d("test", "value = " + value);
//设定BEAN值
                        if (TextUtils.isEmpty(value)) {
                            goodsOrderLine.setRemark("");
                        } else {

                            goodsOrderLine.setRemark(value);
                        }
                    }
                }


            });
        }else {
            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        if(mDataList != null && mDataList.size()>0){
            return mDataList.size();
        }
        return 0;
    }

    class  ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_goods_name;
        TextView tv_order_quantity;
        EditText edit_stock_out_quantity;
        TextView tv_has_stock_out_qtty;
        EditText edit_remark ;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_goods_name = (TextView) itemView.findViewById(R.id.tv_goods_name);
            tv_order_quantity = (TextView) itemView.findViewById(R.id.tv_order_quantity);
            edit_stock_out_quantity = (EditText) itemView.findViewById(R.id.edit_stock_out_quantity);
            tv_has_stock_out_qtty = (TextView) itemView.findViewById(R.id.tv_has_stock_out_qtty);
            edit_remark = (EditText) itemView.findViewById(R.id.edit_remark);
        }
    }


}
