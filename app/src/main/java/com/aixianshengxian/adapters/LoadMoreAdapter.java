package com.aixianshengxian.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.aixianshengxian.util.DateUtils;
import com.xmzynt.storm.service.sale.GoodsOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cwj on 2017/6/16.
 */

public class LoadMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private LayoutInflater mInflater;
    private List<GoodsOrder> mDataList;

    private int normalType = 0;     // 第一种ViewType，正常的item
    private int footType = 1;       // 第二种ViewType，底部的提示View

    private boolean hasMore = true;   // 变量，是否有更多数据
    private boolean fadeTips = false; // 变量，是否隐藏了底部的提示

    private Handler mHandler = new Handler(Looper.getMainLooper()); //获取主线程的Handler


    public LoadMoreAdapter(Context context, List<GoodsOrder> mDataList, boolean hasMore){
        this.mInflater=LayoutInflater.from(context);
        this.mDataList = mDataList;
        this.hasMore = hasMore;
    }


    public interface OnItemClickLitener
    {
        void onItemClick( GoodsOrder simpleGoodsOrder);
    }

    private LoadMoreAdapter.OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(LoadMoreAdapter.OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public int getItemCount() {
        if(mDataList != null && mDataList.size()>0){
            return mDataList.size()+1;
        }
        return 1;
    }


    // 自定义方法，获取列表中数据源的最后一个位置，比getItemCount少1，因为不计上footView
    public int getRealLastPosition() {
        if(mDataList != null && mDataList.size()>0){
            return mDataList.size();
        }
        return 0;
    }

    // 根据条目位置返回ViewType，以供onCreateViewHolder方法内获取不同的Holder
    @Override
    public int getItemViewType(int position) {
        int r = getItemCount();
        if (position == getItemCount()-1) {
            return footType;
        } else {
            return normalType;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == normalType) {
            return new NormalHolder(mInflater.inflate(R.layout.item_orders_list, parent,false));
        } else {
            return new FootHolder(mInflater.inflate(R.layout.item_footer, parent,false));
        }

    }

    /**
     * 数据的绑定显示
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder( final  RecyclerView.ViewHolder holder,final int position) {
        // 如果是正常的imte，直接设置TextView的值
        if (holder instanceof NormalHolder) {
            if(mDataList != null ){
                if(position >= mDataList.size()){
                    return;
                }else {
                    if(mDataList.size()>0 && mDataList.get(position) != null){

                        ((NormalHolder)holder).tvIndex.setText("" + position);//测试使用
                        ((NormalHolder)holder).tvIndex.setVisibility(View.GONE);
                        ((NormalHolder)holder).tv_order_client_name.setText(mDataList.get(position).getCustomer()==null?"":mDataList.get(position).getCustomer().getName());
                        // String name = mDataList.get(position).getCustomerName();
                        ((NormalHolder)holder).tv_order_create_time.setText(DateUtils.DateToString(mDataList.get(position).getDeliveryTime(),"yyyy-MM-dd"));
                        ((NormalHolder)holder).tv_order_amount.setText("￥"+mDataList.get(position).getTotalAmount());
                        ((NormalHolder)holder).tv_order_num.setText(String.valueOf(mDataList.get(position).getBillNumber()));
                        ((NormalHolder)holder).tv_order_state.setText(String.valueOf(mDataList.get(position).getStatus().getCaption()));
                        ((NormalHolder)holder).itemView.setTag(position);
                        if(mOnItemClickLitener != null ){
                            ((NormalHolder)holder).line_item_order.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mOnItemClickLitener.onItemClick(mDataList.get(position));
                                }
                            });
                        }else {
                            ((NormalHolder)holder).itemView.setTag(position);
                        }
                    } else {
                        ((NormalHolder)holder).tv_order_client_name.setText("");
                        ((NormalHolder)holder).tv_order_create_time.setText("");
                        ((NormalHolder)holder).tv_order_amount.setText("");
                        ((NormalHolder)holder).tv_order_num.setText("");
                        ((NormalHolder)holder).tv_order_state.setText("");
                        ((NormalHolder)holder).itemView.setTag(position);
                    }
                }


            }else {
                Log.d("orderRefreshRecyler", "onBindViewHolder: "+position);
            }
        } else {
            // 只有获取数据为空时，hasMore为false，所以当我们拉到底部时基本都会首先显示“正在加载更多...”
            if (hasMore == true) {
                // 不隐藏footView提示
                fadeTips = false;
                // 之所以要设置可见，是因为我在没有更多数据时会隐藏了这个footView
                ((FootHolder) holder).tips.setVisibility(View.VISIBLE);
                ((FootHolder) holder).progressBar.setVisibility(View.VISIBLE);
//                if (mDataList.size() > 0) {
//                    // 如果查询数据发现增加之后，就显示正在加载更多
                    ((FootHolder) holder).tips.setText("正在加载更多...");
//                }
//                else {
//                    Log.d("orderRefreshRecyler", "onBindViewHolder: "+position);
//                }
            } else {
//                if (mDataList.size() > 0) {
                    // 如果查询数据发现并没有增加时，就显示没有更多数据了
                    ((FootHolder) holder).tips.setText("没有更多数据了");

                    // 然后通过延时加载模拟网络请求的时间，在500ms后执行
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 隐藏提示条
                            ((FootHolder) holder).tips.setVisibility(View.GONE);
                            ((FootHolder) holder).progressBar.setVisibility(View.GONE);
                            // 将fadeTips设置true
                            fadeTips = true;
                            // hasMore设为true是为了让再次拉到底时，会先显示正在加载更多
                           // hasMore = true;
                        }
                    }, 500);
//                }else {
//                    ((FootHolder) holder).tips.setVisibility(View.GONE);
//                    ((FootHolder) holder).progressBar.setVisibility(View.GONE);
//                    // 将fadeTips设置true
//                    fadeTips = true;
//                    // hasMore设为true是为了让再次拉到底时，会先显示正在加载更多
//                   // hasMore = true;
//                }
            }
        }

    }

    // 暴露接口，改变fadeTips的方法
    public boolean isFadeTips() {
        return fadeTips;
    }

    // 暴露接口，下拉刷新时，通过暴露方法将数据源置为空
    public void resetDatas() {
        mDataList = new ArrayList<>();
    }

    // 暴露接口，更新数据源，并修改hasMore的值，如果有增加数据，hasMore为true，否则为false
    public void updateList( boolean hasMore) {

        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

    public void setHasMore(boolean hasMore){
        this.hasMore = hasMore;
    }

    // // 底部footView的ViewHolder，用以缓存findView操作
    class FootHolder extends RecyclerView.ViewHolder {
        private TextView tips;
        private ProgressBar progressBar;
        public FootHolder(View itemView) {
            super(itemView);
            tips = (TextView) itemView.findViewById(R.id.tips);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }

    // 正常item的ViewHolder，用以缓存findView操作
    public static class NormalHolder extends RecyclerView.ViewHolder {
        public TextView tvIndex;
        public TextView tv_order_client_name;
        private TextView tv_order_create_time ;
        private  TextView tv_order_num;
        private TextView tv_order_amount;
        private TextView tv_order_state;
        private LinearLayout line_item_order;
        public NormalHolder(View view){
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
