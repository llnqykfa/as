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

public class MachineRefreshAdapter extends RecyclerView.Adapter<MachineRefreshAdapter.ViewHolder> {
    private List<ForecastProcessPlan> mData;
    private OnItemClickListener mOnItemClickListener;
    private Context context;
    private LayoutInflater inflater = null;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_product_name;
        TextView tv_unit;
        TextView tv_machine_time;
        EditText edt_plan_num;
        TextView tv_compete_num;
        TextView btn_stock_in;
//        View myView;

        public ViewHolder(View view){//自定义内部类，继承自RecyclerView.ViewHolder
            super(view);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            tv_unit = (TextView) view.findViewById(R.id.tv_unit);
            tv_machine_time = (TextView) view.findViewById(R.id.tv_machine_time);
            edt_plan_num = (EditText) view.findViewById(R.id.edt_plan_num);
            tv_compete_num = (TextView) view.findViewById(R.id.tv_compete_num);
            btn_stock_in = (TextView) view.findViewById(R.id.btn_stock_in);
//            myView=view;//定义一个包括图片和文字的布局
        }
    }

    public MachineRefreshAdapter(List<ForecastProcessPlan> MData, Context context) {
        this.mData = MData;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener{
        void onStockInClick(ForecastProcessPlan forecastProcessPlan,View v);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(MachineRefreshAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public MachineRefreshAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_machine,parent,false);//传入布局
        final MachineRefreshAdapter.ViewHolder holder = new ViewHolder(view);

        if (mOnItemClickListener!=null) {
                //入库按钮监听
                holder.btn_stock_in.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getAdapterPosition();//获取点击位置
                        mOnItemClickListener.onStockInClick(mData.get(position),v);
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
        if(forecastProcessPlan.getCreateInfo()!=null){
            holder.tv_machine_time.setText(DatesUtils.dateToStr(forecastProcessPlan.getCreateInfo().getOperateTime()));
        }

        //这个很重要，先移开TextWatcher的监听器
        if (holder.edt_plan_num.getTag() instanceof android.text.TextWatcher) {
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

        holder.tv_compete_num.setText(String.valueOf(forecastProcessPlan.getCompleteQty()));
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
