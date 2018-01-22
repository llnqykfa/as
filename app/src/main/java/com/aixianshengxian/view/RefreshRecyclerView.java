package com.aixianshengxian.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;


/**
 * Author: zhuliyuan
 * Time: 下午 3:26
 */

public class RefreshRecyclerView extends RecyclerView {


    private AutoLoadAdapter autoLoadAdapter;

    public RefreshRecyclerView(Context context) {
        this(context, null);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private boolean isLoadingMore = false;//是否正在加载更多
    private int loadMorePosition = -1;//记录最后刷新位置
    private OnLoadDataListener loadDataListener;//加载数据监听
    private boolean isLoadMoreEnable = false;//是否允许加载更多
    private int footerResource;//脚布局
    private void init() {
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastVisiblePosition = getLastVisiblePosition();
                int itemCount = getAdapter().getItemCount();
                if (lastVisiblePosition >= itemCount - 1 && !isLoadingMore && isLoadMoreEnable) {
                    isLoadingMore = true;//设置正在刷新
//                    autoLoadAdapter.notifyDataSetChanged();
                    autoLoadAdapter.notifyItemChanged(itemCount-1);
                    loadMorePosition = itemCount - 1;//记录最后刷新位置
                    if (loadDataListener != null) {
                        loadDataListener.pullUpRefresh();
                    }
                }

            }
        });
    }


    @Override
    public void setAdapter(Adapter adapter) {
        autoLoadAdapter = new AutoLoadAdapter(new SlideInBottomAnimationAdapter(adapter));//添加动画
        super.setAdapter(autoLoadAdapter);
    }

    /**
     * 设置是否允许加载更多
     *
     * @param isEnable
     */
    public void setLoadMoreEnable(boolean isEnable) {
        this.isLoadMoreEnable = isEnable;
    }

    /**
     * 设置脚布局
     */
    public void setFooterResource(int footerResource) {
        this.footerResource = footerResource;
    }

    /**
     * 加载完成
     *
     */
    public void loadMoreComplete() {
        this.isLoadingMore = false;
    }

    /**
     * 获得最后一条可见
     *
     * @return
     */
    private int getLastVisiblePosition() {
        LayoutManager layoutManager = getLayoutManager();
        int position = -1;
        if (layoutManager instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            position = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] positions = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);//返回最后可见的一行position
            for (int i = 0; i < positions.length - 1; i++) {
                position = Math.max(positions[i], positions[i + 1]);
            }
        }
        return position;
    }

    /**
     * 上拉刷新回调
     *
     * @param listener
     */
    public void setOnLoadDataListener(OnLoadDataListener listener) {
        this.loadDataListener = listener;
    }

    public interface OnLoadDataListener {
        void pullUpRefresh();//上拉刷新
    }


    /**
     * 刷新更多数据
     */
    public void notifyMoreData() {
        getAdapter().notifyItemRemoved(loadMorePosition);
        loadMoreComplete();
    }

    /**
     * 刷新最新数据
     */
    public void notifyNewData(){
        getAdapter().notifyDataSetChanged();
    }

    public class AutoLoadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Adapter dataAdapter;//数据adapter
        private final int TYPE_NORMAL = 0;//正常
        private final int TYPE_FOOTER = 1;//底部布局

        public AutoLoadAdapter(RecyclerView.Adapter adapter) {
            this.dataAdapter = adapter;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1 && isLoadMoreEnable) {
                return TYPE_FOOTER;
            }
            return TYPE_NORMAL;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder holder = null;
            if (viewType == TYPE_FOOTER) {//脚部
                holder = new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(footerResource, parent, false));
            } else if (viewType == TYPE_NORMAL) {//数据
                holder = dataAdapter.onCreateViewHolder(parent, viewType);
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int itemViewType = getItemViewType(position);
            if (itemViewType != TYPE_FOOTER) {
                dataAdapter.onBindViewHolder(holder, position);
            } else {
                if (isLoadingMore) {
                    holder.itemView.setVisibility(VISIBLE);
                }else{
                    holder.itemView.setVisibility(GONE);
                }
            }
        }


        @Override
        public int getItemCount() {
            if (dataAdapter.getItemCount() != 0) {
                int count = dataAdapter.getItemCount();
                if (isLoadMoreEnable)//增加了脚布局
                    count++;
                return count;
            }
            return 0;
        }

        public class FooterViewHolder extends RecyclerView.ViewHolder {

            public FooterViewHolder(View itemView) {
                super(itemView);
            }


        }

    }


}
