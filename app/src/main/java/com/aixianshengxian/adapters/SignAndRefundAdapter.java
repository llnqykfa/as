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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.xmzynt.storm.service.sale.returnbill.ReturnBillLine;
import com.xmzynt.storm.service.transport.TransportBill;
import com.xmzynt.storm.service.transport.TransportBillLine;
import com.xmzynt.storm.util.BigDecimalUtil;
import com.xmzynt.storm.util.StringUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cwj on 2017/6/8.
 */

public class SignAndRefundAdapter extends RecyclerView.Adapter<SignAndRefundAdapter.ViewHolder> {
    LayoutInflater mInflater;
    TransportBill transportBill;
    List<TransportBillLine> mDatas;
    Context mContext;

   private List<ReturnBillLine> mReturnLines = new ArrayList<>();


    public List<TransportBillLine> getmDatas() {
        return mDatas;
    }

    public List<ReturnBillLine> getmReturnLines() {
        return mReturnLines;
    }

    public SignAndRefundAdapter (List<TransportBillLine> mDatas, Context context,TransportBill transportBill) {
        this.transportBill = transportBill;
        this.mDatas =mDatas;
        this.mContext =context;
        mInflater = LayoutInflater.from(context);
        if(mDatas != null  && mDatas.size()>0){//根据订货列表的长度 构造退货列表
            for(int i =0 ; i <mDatas.size() ; i ++){
                TransportBillLine transportBillLine = mDatas.get(i);
                ReturnBillLine returnBillLine = new ReturnBillLine();
                returnBillLine.setGoods(transportBillLine.getGoods());//从签收单中获取退货单商品信息
                returnBillLine.setGoodsUnit(transportBillLine.getGoodsUnit());//从签收单中获取商品单位
                returnBillLine.setOrderPrice(transportBillLine.getSignedPrice());//退货单价默认为签收单价
                mReturnLines.add(returnBillLine);
            }
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_sign_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.itemView.setTag(position);
        holder.tvPosition.setText(""+position);
        if(mDatas != null && mDatas.size()>0){
          final   TransportBillLine transportBillLine = mDatas.get(position);
            holder.tv_goods_name.setText(transportBillLine.getGoods().getName());
            holder.tv_delivery_quantity.setText(StringUtil.toString(BigDecimalUtil.convertToScale(transportBillLine.getGoodsQty(),2))+
                    transportBillLine.getGoodsUnit().getName());//发货数
            if(transportBill != null && transportBill.getStatus()!=null){
                if(transportBill.getStatus().getCaption().equals("已签收")){
                    holder.line_refund_quantity_remark.setVisibility(View.GONE);
                    holder.edit_sign_quantity.setEnabled(false);
                    holder.edit_sign_unit_price.setEnabled(false);
                }else {
                    holder.line_refund_quantity_remark.setVisibility(View.VISIBLE);
                }
            }else {
                Log.d("test", "value = " );
            }
            transportBillLine.setSignedQty(transportBillLine.getGoodsQty());//签收数默认等于发货数
            holder.edit_sign_quantity.setText(StringUtil.toString(transportBillLine.getGoodsQty()));
            //通过设置tag，防止position紊乱
            holder.edit_sign_quantity.setTag(position);
            //添加editText的监听事件

            holder.edit_sign_quantity.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){//获得焦点时清除当前值
                       // holder.edit_sign_quantity.setText("");
                    }else {
                       // holder.edit_sign_quantity.setText(StringUtil.toString(transportBillLine.getOrderQty()));
                    }
                }
            });
            holder.edit_sign_quantity.addTextChangedListener(new TextWatcher() {
              private   CharSequence  temp;
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                        temp =s ;
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String value = temp.toString();
                    if ((Integer) holder.edit_sign_quantity.getTag() == position
                            && holder.edit_sign_quantity.hasFocus()) {
                        Log.d("test", "value = " + value);
                     //设定BEAN值
                        if (!TextUtils.isEmpty(value)) {
                            BigDecimal quantity = new BigDecimal(value);
                            quantity = BigDecimalUtil.convertToScale(quantity, 2);
                            transportBillLine.setSignedQty(quantity);
                           // transportBillLine.setSignedQty(BigDecimal.ZERO);
                        } else {
                            transportBillLine.setSignedQty(BigDecimal.ZERO);
                        }
                        mDatas.set(position,transportBillLine);
                    }else {
                        Log.d("test", "value = " + value);
                    }

                    }
            });//签收数
            holder.edit_sign_unit_price.setTag(position);//签收单价
            holder.edit_sign_unit_price.setText(StringUtil.toString(transportBillLine.getSignedPrice()) );
            holder.edit_sign_unit_price.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){//获得焦点时清除当前值
                       // holder.edit_sign_unit_price.setText("");
                    }else {
                       // holder.edit_sign_unit_price.setText(StringUtil.toString(transportBillLine.getSignedPrice()) );
                    }
                }
            });
          if((Integer)holder.edit_sign_unit_price.getTag() == position){
              if(holder.edit_sign_unit_price.getText().toString() !=null &&
                      !TextUtils.isEmpty(holder.edit_sign_unit_price.getText().toString())){
                  BigDecimal unitPrice  = new BigDecimal(holder.edit_sign_unit_price.getText().toString());
                  int r = unitPrice.compareTo(BigDecimal.ZERO);
                  if(r == 0){
                      holder.edit_sign_unit_price.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                  }else {
                      holder.edit_sign_unit_price.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                  }
              }else {
                  Log.d("test", "value = " );
                  holder.edit_sign_unit_price.setBackgroundColor(mContext.getResources().getColor(R.color.white));

              }

          }else {
              Log.d("test", "value = " );
          }




            holder.edit_sign_unit_price.addTextChangedListener(new TextWatcher() { //签收价格
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
                    if((Integer)holder.edit_sign_unit_price.getTag() ==position
                            && holder.edit_sign_unit_price.hasFocus()){
                        if (!TextUtils.isEmpty(value)) {
                            BigDecimal signPrice = new BigDecimal(value);
                            signPrice = BigDecimalUtil.convertToScale(signPrice, 2);
                            transportBillLine.setSignedPrice(signPrice);
                            //transportBillLine.setSignedPrice(BigDecimal.ZERO);
                            int r = signPrice.compareTo(BigDecimal.ZERO);
                            if(r == 0){
                                holder.edit_sign_unit_price.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                            }else {
                                holder.edit_sign_unit_price.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                            }
                        } else {
                            Log.d("test", "value = " + value);
                            holder.edit_sign_unit_price.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                        }
                        mDatas.set(position,transportBillLine);


                    }else {
                        Log.d("test", "value = " + value);

                    }
                }
            });

            final  ReturnBillLine returnBillLine = mReturnLines.get(position);
            if(returnBillLine.getReturnQty().compareTo(BigDecimal.ZERO)!= 0){
                holder.edit_return_quantity.setText(StringUtil.toString(returnBillLine.getReturnQty()));
            }else {
                holder.edit_return_quantity.setText("");
            }
            holder.edit_return_quantity.setTag(position);//退货数
            holder.edit_return_quantity.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){//获得焦点时清除当前值
                        holder.edit_sign_unit_price.setText("");
                    }else {
                        if(returnBillLine.getReturnQty().compareTo(BigDecimal.ZERO)!= 0){
                            holder.edit_return_quantity.setText(StringUtil.toString(returnBillLine.getReturnQty()));
                        }else {
                            holder.edit_return_quantity.setText("");
                        }
                    }
                }
            });
            holder.edit_return_quantity.addTextChangedListener(new TextWatcher() {
                private CharSequence temp ;
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
                    if ((Integer) holder.edit_return_quantity.getTag() == position
                            && holder.edit_return_quantity.hasFocus()) {
                        Log.d("test", "value = " + value);
                        if (!TextUtils.isEmpty(value)) {
                            BigDecimal quantity = new BigDecimal(value);
                            quantity = BigDecimalUtil.convertToScale(quantity, 2);
                            returnBillLine.setReturnQty(quantity);
                        } else {
                            Log.d("test", "value = " + value);
                        }
                        mReturnLines.set(position,returnBillLine);
                    }else {
                        Log.d("test", "value = " + value);
                    }
                }
            });
            if(returnBillLine.getRemark() != null && !TextUtils.isEmpty(returnBillLine.getRemark())){
                holder.edit_return_remark.setText(returnBillLine.getRemark());
            }else {
                holder.edit_return_remark.setText("");
            }
            holder.edit_return_remark.setTag(position);
            holder.edit_return_quantity.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){//获得焦点时清除当前值
                        holder.edit_return_remark.setText("");
                    }else {
                        if(returnBillLine.getRemark() != null && !TextUtils.isEmpty(returnBillLine.getRemark())){
                            holder.edit_return_remark.setText(returnBillLine.getRemark());
                        }else {
                            holder.edit_return_remark.setText("");
                        }
                    }
                }
            });
            holder.edit_return_remark.addTextChangedListener(new TextWatcher() {//退货备注
                private CharSequence temp ;
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
                    if ((Integer) holder.edit_return_remark.getTag() == position
                            && holder.edit_return_remark.hasFocus()) {
                        Log.d("test", "value = " + value);
                        if (!TextUtils.isEmpty(value)) {
                           // returnBillLine.setRemark("");
                            returnBillLine.setRemark(value);
                        } else {

                            Log.d("test", "value = " + value);
                        }
                        mReturnLines.set(position,returnBillLine);
                    }
                }
            });

        }else {
            holder.itemView.setTag(position);
        }

    }

    @Override
    public int getItemCount() {
        if(mDatas != null && mDatas.size()>0){
        return mDatas.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPosition;//数据标记
        TextView tv_goods_name;
        TextView tv_delivery_quantity;
        EditText edit_sign_quantity;
        EditText edit_sign_unit_price;
        LinearLayout line_refund_quantity_remark;
        EditText edit_return_quantity;
        EditText edit_return_remark;
        public ViewHolder(View itemView) {
            super(itemView);
            tvPosition  = (TextView) itemView.findViewById(R.id.tvPosition);
            tv_goods_name = (TextView) itemView.findViewById(R.id.tv_goods_name);
            tv_delivery_quantity = (TextView) itemView.findViewById(R.id.tv_delivery_quantity);
            edit_sign_quantity = (EditText) itemView.findViewById(R.id.edit_sign_quantity);
            edit_sign_unit_price = (EditText) itemView.findViewById(R.id.edit_sign_unit_price);
            line_refund_quantity_remark = (LinearLayout) itemView.findViewById(R.id.line_refund_quantity_remark);
            edit_return_quantity = (EditText) itemView.findViewById(R.id.edit_return_quantity);
            edit_return_remark = (EditText) itemView.findViewById(R.id.edit_return_remark);
        }
    }

}
