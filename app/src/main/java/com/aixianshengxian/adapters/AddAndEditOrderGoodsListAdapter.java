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
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.xmzynt.storm.service.sale.GoodsOrderLine;
import com.xmzynt.storm.util.BigDecimalUtil;
import com.xmzynt.storm.util.StringUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by cwj on 2017/6/9.
 */

public class AddAndEditOrderGoodsListAdapter extends RecyclerView.Adapter<AddAndEditOrderGoodsListAdapter.ViewHolder> {

    LayoutInflater mInlayout ;
    List<GoodsOrderLine> mDataList;
    private SaveEditListener mSaveEditListener;
    private  Context mContext;

    public List<GoodsOrderLine> getmDataList() {
        return mDataList;
    }

    public void setmSaveEditListener(SaveEditListener mSaveEditListener) {
        this.mSaveEditListener = mSaveEditListener;
    }

    public interface SaveEditListener{
        void SaveOrderGoodsQTt(int position, String string);
        void SaveOrderGoodsRemark(int position,String s);
        void deleteGoodsOrderLine(int position,GoodsOrderLine goodsOrderLine);
    }

    public AddAndEditOrderGoodsListAdapter(List<GoodsOrderLine> mDataList, Context context) {
        mInlayout = LayoutInflater.from(context);
        this.mDataList = mDataList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInlayout.inflate(R.layout.item_goods_edit,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(position < 0 || position >= getItemCount()){//防止数组越界
            return;
        }else {
            holder.itemView.setTag(position);
            if(mDataList != null && mDataList.size()>0){
                final GoodsOrderLine goodsOrderLine = mDataList.get(position);
                holder.tv_goods_name.setText(goodsOrderLine.getGoods().getName());
                holder.tv_unit_price.setText("￥"+ StringUtil.toString(goodsOrderLine.getOrderPrice())+"/"+goodsOrderLine.getGoodsUnit().getName());
                holder.image_delete.setTag(position);
                holder.image_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSaveEditListener.deleteGoodsOrderLine(position,goodsOrderLine);

                    }
                });

                holder.edit_qty.setText(StringUtil.toString(goodsOrderLine.getGoodsQty()));
                holder.tv_amout.setText(""+ BigDecimalUtil.convertToScale(goodsOrderLine.getOrderPrice().multiply(BigDecimalUtil.convertToScale(goodsOrderLine.getGoodsQty(),2)),2));
                holder.edit_qty.setTag(position);//设置position为Tag
                holder.tv_amout.setTag(position);//设置position为Tag

                holder.edit_qty.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(hasFocus){//获得焦点时清除当前值
                            //holder.edit_qty.setText("");
                        }else {
                            holder.edit_qty.setText(StringUtil.toString(goodsOrderLine.getGoodsQty()));
                        }
                    }
                });
                holder.edit_qty.addTextChangedListener(new TextWatcher() {
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
                        if((Integer)holder.edit_qty.getTag()==position && (Integer)holder.tv_amout.getTag() == position
                                && holder.edit_qty.hasFocus()){
                            if(s != null && !TextUtils.isEmpty(value)){
                                //  mSaveEditListener.SaveOrderGoodsQTt(Integer.parseInt(holder.edit_qty.getTag().toString()),s.toString().trim());
                                BigDecimal quantity = new BigDecimal(value);
                                StringUtil.toString(BigDecimalUtil.convertToScale(goodsOrderLine.getOrderPrice().multiply(BigDecimalUtil.convertToScale(quantity,2)),2));
                                holder.tv_amout.setText( StringUtil.toString(BigDecimalUtil.convertToScale(goodsOrderLine.getOrderPrice().multiply(BigDecimalUtil.convertToScale(quantity,2)),2)) );

                                goodsOrderLine.setGoodsQty(quantity);
                            }else {
                                Log.d("editGoods", "qty"+"123");
                                BigDecimal quantity = BigDecimal.ZERO;
                                //goodsOrderLine.setGoodsQty(quantity);
                            }
                        }else {
                            Log.d("editGoods", "qty"+"123");
                            BigDecimal quantity = BigDecimal.ZERO;
                            // goodsOrderLine.setGoodsQty(quantity);
                        }

                    }
                });

                holder.edit_remark.setTag(position);
                if(goodsOrderLine.getRemark() != null && !TextUtils.isEmpty(goodsOrderLine.getRemark())){
                    holder.edit_remark.setText(goodsOrderLine.getRemark());
                }else {
                    Log.d("addandeidtorder", "onBindViewHolder: ");
                }
                holder.edit_remark.addTextChangedListener(new TextWatcher() {
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
                        if(s!= null && !TextUtils.isEmpty(s)){
                            //mSaveEditListener.SaveOrderGoodsRemark(Integer.parseInt(holder.edit_remark.getTag().toString()),s.toString().trim());
                            String value = temp.toString();
                            if((Integer)holder.edit_remark.getTag()==position
                                    && holder.edit_remark.hasFocus()){
                                if(s != null && !TextUtils.isEmpty(value)){
                                    goodsOrderLine.setRemark(value);
                                }else {
                                    Log.d("editGoods", "remark"+"123");
                                    // goodsOrderLine.setRemark("");
                                }
                            }else {
                                Log.d("editGoods", "remark"+"123");
                                // goodsOrderLine.setRemark("");
                            }

                        }
                    }

                });

            }else {
                holder.itemView.setTag(position);
            }
    }

    }

    @Override
    public int getItemCount() {
        if(mDataList != null && mDataList.size()>0){
            return mDataList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_goods_name;
        TextView tv_unit_price;
        TextView tv_amout ;
        EditText edit_qty ;//数量
        EditText edit_remark ;
        ImageView image_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_goods_name = (TextView) itemView.findViewById(R.id.tv_goods_name);
            tv_unit_price = (TextView) itemView.findViewById(R.id.tv_unit_price);
            tv_amout = (TextView) itemView.findViewById(R.id.tv_amout);
            edit_qty = (EditText) itemView.findViewById(R.id.edit_qty);
            edit_remark = (EditText) itemView.findViewById(R.id.edit_remark);
            image_delete = (ImageView) itemView.findViewById(R.id.image_delete);
        }
    }
}
