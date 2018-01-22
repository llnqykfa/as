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
import com.aixianshengxian.activity.machine.MachineActivity;
import com.aixianshengxian.util.DatesUtils;
import com.xmzynt.storm.service.process.ForecastProcessPlan;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/10/23.
 */

public class AddMachineAdapter extends RecyclerView.Adapter<AddMachineAdapter.ViewHolder> {
    private List<ForecastProcessPlan> mData;
    private OnItemClickListener mOnItemClickListener;
    private Context context;
    private LayoutInflater inflater = null;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_product_name;
        TextView tv_unit;
        EditText edt_plan_num;
        EditText edt_remark;
        ImageView iv_delete;
//        View myView;

        public ViewHolder(View view){//自定义内部类，继承自RecyclerView.ViewHolder
            super(view);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            tv_unit = (TextView) view.findViewById(R.id.tv_unit);
            edt_plan_num = (EditText) view.findViewById(R.id.edt_plan_num);
            edt_remark = (EditText) view.findViewById(R.id.edt_remark);
            iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
//            myView=view;//定义一个包括图片和文字的布局
        }
    }

    public AddMachineAdapter(List<ForecastProcessPlan> MData, Context context) {
        this.mData = MData;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener{
        void onDeleteClick(int position);
        void onUnitClick(int position, View v);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(AddMachineAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public AddMachineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_add_machine,parent,false);//传入布局
        final AddMachineAdapter.ViewHolder holder = new ViewHolder(view);

        if (mOnItemClickListener!=null) {
                //删除按钮监听
                holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getAdapterPosition();//获取点击位置
                        mData.remove(position);
                        notifyDataSetChanged();
                    }
                });
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ForecastProcessPlan forecastProcessPlan = mData.get(position);
        holder.tv_product_name.setText(forecastProcessPlan.getGoods().getName());
        holder.tv_unit.setText(forecastProcessPlan.getGoodsUnit().getName());

        //这个很重要，先移开TextWatcher的监听器
        if (holder.edt_plan_num.getTag() instanceof TextWatcher) {
            holder.edt_plan_num.removeTextChangedListener((TextWatcher) holder.edt_plan_num.getTag());
        }
        holder.edt_plan_num.setText(String.valueOf(forecastProcessPlan.getPlanQty()));
        TextWatcher watcher1 = new TextWatcher() {

            private AddPurchaseDetailAdapter.ViewHolder mHolder;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    forecastProcessPlan.setPlanQty(new BigDecimal(0));
                    Toast.makeText(MachineActivity.mactivity,"计划数量不能为空！",Toast.LENGTH_SHORT).show();
                } else {
                    forecastProcessPlan.setPlanQty(new BigDecimal(s.toString()));
                }
            }
        };
        holder.edt_plan_num.addTextChangedListener(watcher1);
        holder.edt_plan_num.setTag(watcher1);

        //这个很重要，先移开TextWatcher的监听器
        if (holder.edt_remark.getTag() instanceof TextWatcher) {
            holder.edt_remark.removeTextChangedListener((TextWatcher) holder.edt_remark.getTag());
        }

        TextWatcher watcher2 = new TextWatcher() {

            private AddPurchaseDetailAdapter.ViewHolder mHolder;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {

                } else {
                    forecastProcessPlan.setRemark(s.toString());
                }
            }
        };
        holder.edt_remark.addTextChangedListener(watcher2);
        holder.edt_remark.setTag(watcher2);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    //添加数据
    public void addItem(List<ForecastProcessPlan> newDatas) {
        mData.clear();
        mData.addAll(newDatas);
//        notifyDataSetChanged();
    }

    public void addMoreItem(List<ForecastProcessPlan> newDatas) {
        mData.addAll(newDatas);
//        notifyDataSetChanged();
    }

    public List<ForecastProcessPlan> getData() {
        return mData;
    }
}
