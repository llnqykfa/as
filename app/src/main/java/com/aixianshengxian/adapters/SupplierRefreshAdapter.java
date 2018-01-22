package com.aixianshengxian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.xmzynt.storm.service.user.supplier.Supplier;


import java.util.List;

/**
 * Created by cwj on 2017/6/6.
 */

public class SupplierRefreshAdapter extends RecyclerView.Adapter<SupplierRefreshAdapter.ViewHolder>{
    private LayoutInflater mInflater;
    private List<Supplier> supplier=null;

    public interface OnItemClickLitener
    {
        void onItemClick(Supplier supplier);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public SupplierRefreshAdapter(Context context, List<Supplier> supplier) {
        this.mInflater = LayoutInflater.from(context);
        this.supplier = supplier;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view=mInflater.inflate(R.layout.item_client,parent,false);
        //这边可以做一些属性设置，甚至事件监听绑定
        //view.setBackgroundColor(Color.RED);
        SupplierRefreshAdapter.ViewHolder viewHolder=new SupplierRefreshAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder( final ViewHolder holder,final int position) {
        holder.tv_client_name.setText(""+supplier.get(position).getName());
        holder.itemView.setTag(position);
        if(mOnItemClickLitener != null){
            holder.tv_client_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(supplier.get(pos));
                }
            });
        }else {
            holder.tv_client_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(supplier.get(pos));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(supplier != null) {
            return supplier.size();
        }
            return 0;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_client_name;

        public ViewHolder(View view){
            super(view);
            tv_client_name = (TextView) view.findViewById(R.id.tv_client_name);

        }
    }

    //添加数据
    public void addItem(List<Supplier> newDatas) {

        newDatas.addAll(supplier);
        supplier.removeAll(supplier);
        supplier.addAll(newDatas);
        notifyDataSetChanged();
    }

    public void addMoreItem(List<Supplier> newDatas) {
        supplier.addAll(newDatas);
        notifyDataSetChanged();
    }
}
