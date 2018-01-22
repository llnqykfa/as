package com.aixianshengxian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aixianshengxian.R;
import com.aixianshengxian.activity.machine.StockInActivity;
import com.aixianshengxian.module.ForecastProcessPlanItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/10/25.
 */

public class ForecastProcessPlanItemAdapter extends RecyclerView.Adapter<ForecastProcessPlanItemAdapter.ViewHolder> {
    private List<ForecastProcessPlanItem> mForecastProcessPlanItems;
    private OnItemClickListener mOnItemClickListener;
    private Context context;
    private LayoutInflater inflater = null;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_product_name;
        EditText edt_stock_in_num;
        TextView tv_unit;
        EditText edt_stock_in_price;
        EditText edt_stay_day;
        ImageView iv_delete;
        View myView;

        public ViewHolder(View view) {//自定义内部类，继承自RecyclerView.ViewHolder
            super(view);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            edt_stock_in_num = (EditText) view.findViewById(R.id.edt_stock_in_num);
            tv_unit = (TextView) view.findViewById(R.id.tv_unit);
            edt_stock_in_price = (EditText) view.findViewById(R.id.edt_stock_in_price);
            edt_stay_day = (EditText) view.findViewById(R.id.edt_stay_day);
            iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
            myView=view;//定义一个包括图片和文字的布局
        }
    }

    public ForecastProcessPlanItemAdapter(List<ForecastProcessPlanItem> MData, Context context) {
        this.mForecastProcessPlanItems = MData;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener{
        void onDeleteClick(int position);
        void onItemClick(ForecastProcessPlanItem productstockin, int position);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public ForecastProcessPlanItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_add_stock_in,parent,false);//传入布局
        final ViewHolder holder = new ViewHolder(view);

        if (mOnItemClickListener!=null) {
            //删除按钮监听
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onDeleteClick(position);
                }
            });

            holder.myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onItemClick(mForecastProcessPlanItems.get(position),position);
                }
            });
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(ForecastProcessPlanItemAdapter.ViewHolder holder, int position) {
        final ForecastProcessPlanItem productstockin = mForecastProcessPlanItems.get(position);
        holder.tv_product_name.setText(productstockin.getForecastProcessPlan().getGoods().getName());//商品名称

        //入库数
        //这个很重要，先移开TextWatcher的监听器
        if (holder.edt_stock_in_num.getTag() instanceof TextWatcher) {
            holder.edt_stock_in_num.removeTextChangedListener((TextWatcher) holder.edt_stock_in_num.getTag());
        }
        holder.edt_stock_in_num.setText(String.valueOf(BigDecimal.ZERO));
        TextWatcher watcher1 = new TextWatcher() {

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
                    productstockin.setStockInNum(BigDecimal.ZERO);
                    Toast.makeText(StockInActivity.mactivity,"入库数不能为空！",Toast.LENGTH_SHORT).show();
                } else {
                    productstockin.setStockInNum(new BigDecimal(s.toString()));
                }
            }
        };
        holder.edt_stock_in_num.addTextChangedListener(watcher1);
        holder.edt_stock_in_num.setTag(watcher1);

        //入库单价
        //这个很重要，先移开TextWatcher的监听器
        if (holder.edt_stock_in_price.getTag() instanceof TextWatcher) {
            holder.edt_stock_in_price.removeTextChangedListener((TextWatcher) holder.edt_stock_in_price.getTag());
        }
        holder.edt_stock_in_price.setText(String.valueOf(BigDecimal.ZERO));
        TextWatcher watcher2 = new TextWatcher() {

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
                    productstockin.setPrice(BigDecimal.ZERO);
                    Toast.makeText(StockInActivity.mactivity,"入库单价不能为空！",Toast.LENGTH_SHORT).show();
                } else {
                    productstockin.setPrice(new BigDecimal(s.toString()));
                }
            }
        };
        holder.edt_stock_in_price.addTextChangedListener(watcher2);
        holder.edt_stock_in_price.setTag(watcher2);

        holder.tv_unit.setText(productstockin.getForecastProcessPlan().getGoodsUnit().getName());//单位

        //保质期
        //这个很重要，先移开TextWatcher的监听器
        if (holder.edt_stay_day.getTag() instanceof TextWatcher) {
            holder.edt_stay_day.removeTextChangedListener((TextWatcher) holder.edt_stay_day.getTag());
        }
        holder.edt_stay_day.setText(String.valueOf(0));
        TextWatcher watcher3 = new TextWatcher() {

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
                    productstockin.setDay(0);
                    Toast.makeText(StockInActivity.mactivity,"保质期不能为空！",Toast.LENGTH_SHORT).show();
                } else {
                    productstockin.setDay(Integer.parseInt(s.toString()));
                }
            }
        };
        holder.edt_stay_day.addTextChangedListener(watcher3);
        holder.edt_stay_day.setTag(watcher3);
    }

    @Override
    public int getItemCount() {
        return mForecastProcessPlanItems == null ? 0 : mForecastProcessPlanItems.size();
    }

    //添加数据
    public void addItem(List<ForecastProcessPlanItem> newDatas) {
        mForecastProcessPlanItems.clear();
        mForecastProcessPlanItems.addAll(newDatas);
    }

    public void addMoreItem(List<ForecastProcessPlanItem> newDatas) {
        mForecastProcessPlanItems.addAll(newDatas);
    }

    public List<ForecastProcessPlanItem> getData() {
        return mForecastProcessPlanItems;
    }
}
