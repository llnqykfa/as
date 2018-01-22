package com.aixianshengxian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.xmzynt.storm.service.user.customer.Department;

import java.util.List;

/**
 * Created by cwj on 2017/6/6.
 */

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ViewHolder>  {
    private LayoutInflater mInflater;
    private List<Department> mDepartments ;

    public interface OnItemClickLitener
    {
        void onItemClick( Department department);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
    public DepartmentAdapter(Context context, List<Department> departments){
        this.mInflater=LayoutInflater.from(context);
        this.mDepartments = departments;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view=mInflater.inflate(R.layout.item_client,parent,false);
        DepartmentAdapter.ViewHolder viewHolder = new DepartmentAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tv_depart_name.setText(""+mDepartments.get(position).getName());
        holder.itemView.setTag(position);
        if(mDepartments != null && mDepartments.size()>0){
            if(mOnItemClickLitener != null ){
                holder.tv_depart_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickLitener.onItemClick(mDepartments.get(position));
                    }
                });
            }else {
                holder.tv_depart_name.setText(""+mDepartments.get(position).getName());
            }
        }else {
            holder.tv_depart_name.setText("空白");
        }

    }

    @Override
    public int getItemCount() {
        if(mDepartments != null && mDepartments.size()>0){
            return mDepartments.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_depart_name;

        public ViewHolder(View view){
            super(view);
            tv_depart_name = (TextView) view.findViewById(R.id.tv_client_name);

        }
    }
}
