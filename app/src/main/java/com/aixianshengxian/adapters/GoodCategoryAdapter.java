package com.aixianshengxian.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.xmzynt.storm.service.goods.GoodsCategory;

import java.util.List;

/**
 * Created by Administrator on 2017/11/13.
 */

public class GoodCategoryAdapter extends RecyclerView.Adapter<GoodCategoryAdapter.ViewHolder> {
    private List<GoodsCategory> mGoodsCategory;
    private OnItemClickListener mOnItemClickListener;
    private Context context;
    private LayoutInflater inflater = null;

    private int selectItem = -1;
    private int lastPosition = 0;

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }
    public int getLastPosition() {
        return lastPosition;
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView item;
        LinearLayout tab;
        View myView;

        public ViewHolder(View view){//自定义内部类，继承自RecyclerView.ViewHolder
            super(view);
            item = (TextView) view.findViewById(R.id.item_list_search_category_text_unclick);
            tab = (LinearLayout) view.findViewById(R.id.tab_unselect);
            myView=view;//定义一个包括图片和文字的布局
        }
    }

    public GoodCategoryAdapter(List<GoodsCategory> MData,Context context){
        //this.mData = MData;
        this.mGoodsCategory = MData;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener{
        void onItemClick(GoodsCategory goodsCategory,int position);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(GoodCategoryAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_search_category,parent,false);//传入布局
        final ViewHolder holder = new ViewHolder(view);

        if (mOnItemClickListener!=null) {
            //整体监听
            holder.myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onItemClick(mGoodsCategory.get(position),position);
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final GoodsCategory goodcategory= mGoodsCategory.get(position);
        holder.item.setText(goodcategory.getName());
        if (position == selectItem) {
            holder.item.setTextColor(context.getResources().getColor(R.color.white));
            holder.tab.setBackgroundResource(R.drawable.green_square_bg);
        } else {
            holder.item.setTextColor(context.getResources().getColor(R.color.black));
            holder.tab.setBackgroundResource(R.drawable.gray_square_bg);
        }
    }

    @Override
    public int getItemCount() {
        return mGoodsCategory == null ? 0 : mGoodsCategory.size();
    }
}
