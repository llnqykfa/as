package com.aixianshengxian.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.xmzynt.storm.service.purchase.bill.PurchaseBillLine;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class PurchaseEditAdapter extends RecyclerView.Adapter<PurchaseEditAdapter.ViewHolder>{

    private List<PurchaseBillLine> mData;
    private OnItemClickListener mOnItemClickListener;
    private Context context;


    public PurchaseEditAdapter(List<PurchaseBillLine> MData, Context context) {
        this.mData = MData;
        this.context = context;
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener{
        void onUnitClick(int position, View v);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_purchase_edit, parent, false);//传入布局
        final ViewHolder holder = new ViewHolder(view);

        if (mOnItemClickListener!=null) {
            //单位下拉框监听
            holder.tv_unit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onUnitClick(position, v);
                }
            });
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PurchaseBillLine purchasedetail = mData.get(position);
        if (purchasedetail != null) {
            holder.tv_product_name.setText(purchasedetail.getGoods().getName());
            holder.tv_unit.setText(purchasedetail.getGoodsUnit().getName());
            /*String[] items = new String[] {purchasedetail.getGoodsUnit().getName()};
            //下拉框单位
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinner.setAdapter(adapter);*/

            holder.tv_purchase_total.setText(String.valueOf(purchasedetail.getSubtotal()));
            /*holder.tv_purchase_total.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String str = holder.tv_purchase_total.getText().toString();
                    if(!TextUtils.isEmpty(str)){
                        purchasedetail.setSubtotal(new BigDecimal(str));
                    }
                }
            });*/

            holder.tv_purchase_price.setText(String.valueOf(purchasedetail.getPrice()));
            holder.tv_purchase_price.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String str = holder.tv_purchase_price.getText().toString();
                    if(!TextUtils.isEmpty(str)){
                        purchasedetail.setPrice(new BigDecimal(str));
                        refreshPurchasePrice(purchasedetail,holder);
                    }
                }
            });
            holder.tv_purchase_num.setText(String.valueOf(purchasedetail.getPurchaseQty()));
            holder.tv_purchase_num.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String str = holder.tv_purchase_num.getText().toString();
                    if(!TextUtils.isEmpty(str)){
                        purchasedetail.setPurchaseQty(new BigDecimal(str));
                        refreshPurchasePrice(purchasedetail,holder);
                    }

                }
            });
            holder.tv_place.setText(purchasedetail.getOrigin());
            holder.tv_place.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String str = holder.tv_place.getText().toString();
                    if(!TextUtils.isEmpty(str)){
                        purchasedetail.setOrigin(str);
                    }

                }
            });
            String remark = purchasedetail.getRemark();
            if (remark.isEmpty()) {
                holder.tv_remark.setText("");
            } else {
                holder.tv_remark.setText(remark);
            }

            holder.tv_remark.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String str = holder.tv_remark.getText().toString();
                    if(!TextUtils.isEmpty(str)){
                        purchasedetail.setRemark(str);
                    }
                }
            });
            holder.purchase_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mData.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public List<PurchaseBillLine> getPurchaseDatas() {
        return mData;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_product_name;
        TextView tv_unit;
        //Spinner spinner;
        TextView tv_purchase_total;
        EditText tv_purchase_num;
        EditText tv_place;
        EditText tv_remark;
        EditText tv_purchase_price;
        ImageView purchase_delete;
        public ViewHolder(View view){
            super(view);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            //spinner = (Spinner)view.findViewById(R.id.spinner);
            tv_unit = (TextView) view.findViewById(R.id.tv_unit);
            tv_purchase_total = (TextView) view.findViewById(R.id.tv_purchase_total);
            tv_purchase_num = (EditText) view.findViewById(R.id.tv_purchase_num);
            tv_place = (EditText) view.findViewById(R.id.tv_place);
            tv_remark = (EditText) view.findViewById(R.id.edt_remark);
            tv_purchase_price = (EditText) view.findViewById(R.id.tv_purchase_price);
            purchase_delete = (ImageView) view.findViewById(R.id.purchase_delete);
        }
    }

    public void addData(List<PurchaseBillLine> purchaseBillLines){
        mData.addAll(purchaseBillLines);
        notifyDataSetChanged();
    }

    //实时更新小计
    public void refreshPurchasePrice(PurchaseBillLine addpurchasedetail,ViewHolder holder) {
        DecimalFormat df = new DecimalFormat("#.000000");
        BigDecimal purchasePrice = addpurchasedetail.getPurchaseQty().multiply(addpurchasedetail.getPrice());
        addpurchasedetail.setSubtotal(purchasePrice);
        holder.tv_purchase_total.setText(df.format(purchasePrice));
    }
}
