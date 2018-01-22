package com.aixianshengxian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.xmzynt.storm.service.wms.warehouse.Warehouse;

import java.util.List;

/**
 * Created by cwj on 2017/6/6.
 */

public class DepotRefreshAdapter extends RecyclerView.Adapter<DepotRefreshAdapter.ViewHolder>{
    private LayoutInflater mInflater;
    private List<Warehouse> warehouse = null;

    public interface OnItemClickLitener
    {
        void onItemClick(Warehouse warehouse);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public DepotRefreshAdapter(Context context, List<Warehouse> warehouse) {
        this.mInflater = LayoutInflater.from(context);
        this.warehouse = warehouse;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view=mInflater.inflate(R.layout.item_client,parent,false);
        //这边可以做一些属性设置，甚至事件监听绑定
        //view.setBackgroundColor(Color.RED);
        DepotRefreshAdapter.ViewHolder viewHolder=new DepotRefreshAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder( final ViewHolder holder,final int position) {
        holder.tv_client_name.setText("" + warehouse.get(position).getName());
        holder.itemView.setTag(position);
        if(mOnItemClickLitener != null){
            holder.tv_client_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(warehouse.get(pos));
                }
            });
        }else {
            holder.tv_client_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(warehouse.get(pos));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(warehouse != null) {
            return warehouse.size();
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
    public void addItem(List<Warehouse> newDatas) {

        //newDatas.addAll(warehouse);
        warehouse.removeAll(warehouse);
        warehouse.addAll(newDatas);
        notifyDataSetChanged();
    }

    public void addMoreItem(List<Warehouse> newDatas) {
        warehouse.removeAll(warehouse);
        warehouse.addAll(newDatas);
        notifyDataSetChanged();
    }
}
