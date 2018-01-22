package com.aixianshengxian.adapters;

import android.content.Context;
import android.renderscript.ScriptGroup;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aixianshengxian.R;
import com.aixianshengxian.activity.machine.BindingBasketActivity;
import com.aixianshengxian.module.ProductStockIn;
import com.aixianshengxian.module.UcodeMessage;

import java.util.List;

/**
 * Created by Administrator on 2017/10/30.
 */

public class BindingAdapter extends RecyclerView.Adapter<BindingAdapter.ViewHolder> {
    private List<ProductStockIn> mData;
    private List<UcodeMessage> mUcode;
    private OnItemClickListener mOnItemClickListener;
    private Context context;
    private LayoutInflater inflater = null;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_product_name;
        EditText edt_code;
        Button btn_add;
        RecyclerView binding_ucode_listview;
        View myView;

        public ViewHolder(View view){//自定义内部类，继承自RecyclerView.ViewHolder
            super(view);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            edt_code = (EditText) view.findViewById(R.id.edt_code);
            btn_add = (Button) view.findViewById(R.id.btn_add);
            binding_ucode_listview = (RecyclerView) view.findViewById(R.id.listview_receive);
            myView=view;//定义一个包括图片和文字的布局
        }
    }

    public BindingAdapter(List<ProductStockIn> MData, Context context) {
        this.mData = MData;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener{
        void onAddClick(View v);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(BindingAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public BindingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_binding,parent,false);//传入布局
        final BindingAdapter.ViewHolder holder = new ViewHolder(view);

        if (mOnItemClickListener!=null) {
            //入库按钮监听
            holder.btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onAddClick(v);
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(BindingAdapter.ViewHolder holder, int position) {
        final ProductStockIn productstockin = mData.get(position);
        holder.tv_product_name.setText(productstockin.getProductName());

        /*final UcodeMessage ucodemessage = mUcode.get(position);
        //这个很重要，先移开TextWatcher的监听器
        if (holder.edt_code.getTag() instanceof android.text.TextWatcher) {
            holder.edt_code.removeTextChangedListener((TextWatcher) holder.edt_code.getTag());
        }
        holder.edt_code.setText("");
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
                    ucodemessage.setUCode("");
                    Toast.makeText(BindingBasketActivity.mactivity,"绑定不能为空！", Toast.LENGTH_SHORT).show();
                } else {
                    ucodemessage.setUCode(s.toString());
                }
            }
        };
        holder.edt_code.addTextChangedListener(watcher1);
        holder.edt_code.setTag(watcher1);*/
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    //添加数据
    public void addItem(List<ProductStockIn> newDatas) {

        newDatas.addAll(mData);
        mData.removeAll(mData);
        mData.addAll(newDatas);
        notifyDataSetChanged();
    }

    public void addMoreItem(List<ProductStockIn> newDatas) {
        mData.addAll(newDatas);
        notifyDataSetChanged();
    }
}
