package com.aixianshengxian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.aixianshengxian.util.DateUtils;
import com.xmzynt.storm.service.sale.SimpleGoodsOrder;

import java.util.List;


public class OrderRefreshRecyclerAdapter extends RecyclerView.Adapter<OrderRefreshRecyclerAdapter.ViewHolder>{
    private LayoutInflater mInflater;
    private List<SimpleGoodsOrder> mDataList;



    public OrderRefreshRecyclerAdapter(Context context,List<SimpleGoodsOrder> mDataList){
        this.mInflater=LayoutInflater.from(context);
      this.mDataList = mDataList;
    }


    public interface OnItemClickLitener
    {
        void onItemClick( SimpleGoodsOrder simpleGoodsOrder);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view=mInflater.inflate(R.layout.item_orders_list,parent,false);
        //这边可以做一些属性设置，甚至事件监听绑定
        //view.setBackgroundColor(Color.RED);
        ViewHolder viewHolder=new ViewHolder(view);

        return viewHolder;
    }

    /**
     * 数据的绑定显示
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(mDataList != null ){
            if(position >= mDataList.size()){
                return;
            }else {
                Log.d("orderRefreshRecyler", "onBindViewHolder: "+position);
            }

        if(mDataList.size()>0 && mDataList.get(position) != null){

            holder.tvIndex.setText("" + position);//测试使用
            holder.tvIndex.setVisibility(View.GONE);
            holder.tv_order_client_name.setText(mDataList.get(position).getCustomerName());
           // String name = mDataList.get(position).getCustomerName();
            holder.tv_order_create_time.setText(DateUtils.DateToString(mDataList.get(position).getDeliveryTime(),"yyyy-MM-dd hh:mm:ss"));
            holder.tv_order_amount.setText("￥"+mDataList.get(position).getTotalAmount());
            holder.tv_order_num.setText(String.valueOf(mDataList.get(position).getBillNumber()));
            holder.tv_order_state.setText(String.valueOf(mDataList.get(position).getStatus().getCaption()));
                    holder.itemView.setTag(position);
            if(mOnItemClickLitener != null ){
                holder.line_item_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickLitener.onItemClick(mDataList.get(position));
                    }
                });
            }else {
                holder.itemView.setTag(position);
            }
        } else {
            holder.tv_order_client_name.setText("");
            holder.tv_order_create_time.setText("");
            holder.tv_order_amount.setText("");
            holder.tv_order_num.setText("");
            holder.tv_order_state.setText("");
            holder.itemView.setTag(position);
        }
        }else {
            Log.d("orderRefreshRecyler", "onBindViewHolder: "+position);
        }



    }
    @Override
    public int getItemCount() {
        if(mDataList != null && mDataList.size()>0){
            return mDataList.size();
        }
       return 0;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvIndex;
        public TextView tv_order_client_name;
        private TextView tv_order_create_time ;
        private  TextView tv_order_num;
        private TextView tv_order_amount;
        private TextView tv_order_state;
        private LinearLayout line_item_order;
        public ViewHolder(View view){
            super(view);
            tvIndex = (TextView) view.findViewById(R.id.tv_index);
            tv_order_client_name = (TextView) view.findViewById(R.id.tv_order_client_name);
            tv_order_create_time = (TextView) view.findViewById(R.id.tv_order_create_time);
            tv_order_num = (TextView) view.findViewById(R.id.tv_order_num);
            tv_order_amount = (TextView) view.findViewById(R.id.tv_order_amount);
            tv_order_state = (TextView) view.findViewById(R.id.tv_order_state);
            line_item_order = (LinearLayout) view.findViewById(R.id.line_item_order);
        }
    }

}
