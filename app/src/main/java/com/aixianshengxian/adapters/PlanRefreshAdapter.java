package com.aixianshengxian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aixianshengxian.R;
import com.aixianshengxian.activity.plan.PlanActivity;
import com.aixianshengxian.module.ForecastPurchaseItem;
import com.aixianshengxian.util.DatesUtils;
import com.aixianshengxian.util.PurchaseBillUtil;
import com.xmzynt.storm.service.purchase.plan.ForecastPurchase;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/30.
 */

public class PlanRefreshAdapter extends RecyclerView.Adapter<PlanRefreshAdapter.ViewHolder> {

    //private List<PlanProduct> mData;
    private List<ForecastPurchase> mForecastPurchase =null;
    private OnItemClickListener mOnItemClickListener;
    private Map<String,ForecastPurchaseItem> mCheckForecastPurchaseItem;
    private static HashMap<Integer,Boolean> isSelected;
    private Context context;
    private LayoutInflater inflater = null;

    class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbtn_choose;
        TextView tv_product_name;
        TextView tv_unit,tv_unit_check;
        EditText edt_purchase_num;
        TextView tv_plan_time;
        TextView tv_plan_state;
        TextView tv_provider;
        ImageView iv_delete;
        View myView;

        public ViewHolder(View view){//自定义内部类，继承自RecyclerView.ViewHolder
            super(view);
            cbtn_choose = (CheckBox) view.findViewById(R.id.cbtn_choose);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            tv_unit = (TextView) view.findViewById(R.id.tv_unit);
            tv_unit_check = (TextView) view.findViewById(R.id.tv_unit_check);
            edt_purchase_num = (EditText) view.findViewById(R.id.edt_purchase_num);
            tv_plan_time = (TextView) view.findViewById(R.id.tv_plan_time);
            tv_plan_state = (TextView) view.findViewById(R.id.tv_plan_state);
            tv_provider = (TextView) view.findViewById(R.id.tv_provider);
            iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
            myView=view;//定义一个包括图片和文字的布局
        }
    }

    public PlanRefreshAdapter(List<ForecastPurchase> MData,Context context){
        //this.mData = MData;
        this.mForecastPurchase = MData;
        this.context = context;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        initData();
    }

    private void initData() {
        for (int i = 0;i < 100;i ++) {
            getIsSelected().put(i,false);
        }
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener{
        void onItemClick(ForecastPurchase planProduct);
        void onCheckBoxClick(int position,Boolean isChecked);
        void onDeleteClick(int position);
        void onUnitClick(int position, View v);
        void onProviderClick(int position, View v);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public PlanRefreshAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,final int position) {
        final View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_plan,parent,false);//传入布局

        final ViewHolder holder = new ViewHolder(view);

        if (mOnItemClickListener!=null) {
            //整体监听
            holder.myView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onItemClick(mForecastPurchase.get(position));
                }
            });

            holder.itemView.setTag(position);
            holder.cbtn_choose.setTag(position);

            //复选框按钮监听
            holder.cbtn_choose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    getIsSelected().put(position,isChecked);
                    mOnItemClickListener.onCheckBoxClick(position,isChecked);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean oppositeChoose = !getIsSelected().get(position);
                    getIsSelected().put(position,oppositeChoose);
                    holder.cbtn_choose.setChecked(oppositeChoose);
                }
            });

            //删除按钮监听
            holder.iv_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onDeleteClick(position);
                }
            });

            //单位下拉框监听
            holder.tv_unit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onUnitClick(position, v);
                }
            });

            holder.tv_unit_check.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onUnitClick(position, v);
                }
            });

            //供应商下拉框监听
            holder.tv_provider.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onProviderClick(position,v);
                }
            });

        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ForecastPurchase planproduct= mForecastPurchase.get(position);
        String uuid = planproduct.getGoods().getCode();
        String name = planproduct.getGoods().getName();
        String spec =  planproduct.getGoodsSpec()==null?null:planproduct.getGoodsSpec();

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

        //holder.tv_product_name.setText(planproduct.getGoods().getName());//商品名称
        holder.tv_unit.setText(planproduct.getGoodsUnit().getName());//单位
        holder.tv_plan_state.setText(planproduct.getStatus().getCaption());//状态

        String productName =  planproduct.getSupplier()==null?null:planproduct.getSupplier().getName();
        if (productName != null) {
            holder.tv_provider.setText(planproduct.getSupplier().getName());
        } else {
            holder.tv_provider.setText("");
        }

        holder.tv_plan_time.setText(DatesUtils.dateToStr(planproduct.getCreateInfo().getOperateTime()));

        //复选框按钮监听
        Boolean choose = getIsSelected().get(position);
        holder.cbtn_choose.setChecked(choose);

        //这个很重要，先移开TextWatcher的监听器
        if (holder.edt_purchase_num.getTag() instanceof TextWatcher) {
            holder.edt_purchase_num.removeTextChangedListener((TextWatcher) holder.edt_purchase_num.getTag());
        }
        if (planproduct.getPurchaseQty().compareTo(BigDecimal.ZERO) > 0) {
            holder.edt_purchase_num.setText(String.valueOf(planproduct.getPurchaseQty().doubleValue()));//采购数变化监听
        } else {
            holder.edt_purchase_num.setText("");
        }
        TextWatcher watcher = new TextWatcher() {

            private ViewHolder mHolder;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    planproduct.setForecastQty(BigDecimal.valueOf(0));
                    planproduct.setPurchaseQty(BigDecimal.valueOf(0));
                    Toast.makeText(PlanActivity.mactivity,"采购数不能为空",Toast.LENGTH_SHORT).show();
                } else {
                    planproduct.setForecastQty(BigDecimal.valueOf(Double.parseDouble(s.toString())));
                    planproduct.setPurchaseQty(BigDecimal.valueOf(Double.parseDouble(s.toString())));
                }
            }
        };
        holder.edt_purchase_num.addTextChangedListener(watcher);
        holder.edt_purchase_num.setTag(watcher);

    }

    @Override
    public int getItemCount() {
        return mForecastPurchase == null ? 0 : mForecastPurchase.size();
    }

    public static HashMap<Integer,Boolean> getIsSelected() {
        return isSelected;
    }

    public static  void setIsSelected(HashMap<Integer,Boolean> isSelected) {
        PlanRefreshAdapter.isSelected = isSelected;
    }

    //添加数据
    public void addItem(List<ForecastPurchase> newDatas) {

        //newDatas.addAll(mForecastPurchase);
        mForecastPurchase.removeAll(mForecastPurchase);
        mForecastPurchase.addAll(newDatas);
        notifyDataSetChanged();
    }

    public void addMoreItem(List<ForecastPurchase> newDatas) {
        mForecastPurchase.removeAll(mForecastPurchase);
        mForecastPurchase.addAll(newDatas);
        notifyDataSetChanged();
    }
}
