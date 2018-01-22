package com.aixianshengxian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.aixianshengxian.util.DateUtils;
import com.xmzynt.storm.service.transport.TransportBill;

import java.util.List;

/**
 * Created by cwj on 2017/6/8.
 */

public class DeliveryListAdapter extends RecyclerView.Adapter<DeliveryListAdapter.ViewHolder> {
        LayoutInflater mInflater;
       List<TransportBill> transportBills ;
    public interface OnItemClickLitener
    {
        void onItemClick(TransportBill transportBillr);
    }

    private DeliveryListAdapter.OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(DeliveryListAdapter.OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
    public DeliveryListAdapter(List<TransportBill> transportBills, Context context) {
        mInflater = LayoutInflater.from(context);
        this.transportBills = transportBills;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_delivery_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.itemView.setTag(position);
        holder.lineDeliver.setTag(position);
        if(transportBills.get(position) != null){
            final TransportBill transportBill = transportBills.get(position);
            holder.tv_bill_number.setText(transportBill.getBillNumber());
            holder.tv_transport_bill_state.setText(transportBill.getStatus().getCaption());
            holder.tv_delivery_time.setText(DateUtils.DateToString(transportBill.getDeliveryTime(),"yyyy-MM-dd"));
            holder.lineDeliver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((Integer) holder.lineDeliver.getTag() == position
                            && holder.lineDeliver.hasFocus()){
                     mOnItemClickLitener.onItemClick(transportBill);
                    }
                }
            });
        }else {
            holder.itemView.setTag(position);
        }

    }

    @Override
    public int getItemCount() {
        if(transportBills != null && transportBills.size()>0){
            return transportBills.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_bill_number;
        TextView tv_transport_bill_state;
        TextView tv_delivery_time;
        LinearLayout  lineDeliver;
        public ViewHolder(View itemView) {
            super(itemView);
            lineDeliver = (LinearLayout) itemView.findViewById(R.id.lineDeliver);
            tv_bill_number = (TextView) itemView.findViewById(R.id.tv_bill_number);
            tv_transport_bill_state = (TextView) itemView.findViewById(R.id.tv_transport_bill_state);
            tv_delivery_time = (TextView) itemView.findViewById(R.id.tv_delivery_time);
        }
    }
}
