package com.aixianshengxian.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.aixianshengxian.module.UcodeMessage;

import java.util.List;

/**
 * Created by Administrator on 2017/9/28.
 */

public class BasketUcodeAdapter extends RecyclerView.Adapter<BasketUcodeAdapter.ViewHolder>{

    private List<String> mData;
    private OnItemClickListener mOnItemClickListener;

    static  class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_ucode;
        ImageView iv_delete;
        View ucodeView;
        public ViewHolder(View view){//自定义内部类，继承自RecyclerView.ViewHolder
            super(view);//RecyclerView子项的最外层布局，方便获取下列id
            tv_ucode = (TextView)view.findViewById(R.id.tv_ucode);
            iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
            iv_delete.setVisibility(View.VISIBLE);
            ucodeView=view;//定义一个包括文字的布局
        }
    }

    public BasketUcodeAdapter(List<String> UcodeList){//构造函数，把要展示的数据源传进来，并赋值给全局变量mFruitList
        mData=UcodeList;
    }

    //定义一个监听接口，里面有方法
    public interface OnItemClickListener{
        void onDeleteClick(int position);
    }

    //给监听设置一个构造函数，用于main中调用
    public void setOnItemListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){//创建一个ViewHolder
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_ucode,parent,false);//传入布局

        final ViewHolder holder=new ViewHolder(view);//把刚才定义的view传给holder

        if (mOnItemClickListener!=null) {
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();//获取点击位置
                    mOnItemClickListener.onDeleteClick(position);
                }
            });
        }
        return holder;//返回该实例
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){//对RecyclerView子项的数据进行赋值，会在每个子项被滚到屏幕内的时候执行
        String ucodeMessage = mData.get(position);
        //放数据
        holder.tv_ucode.setText(ucodeMessage);

    }

    @Override
    public int getItemCount()
    {//告诉RecyclerView一共有多少子项
        if(mData !=null && mData.size() >0){
            return mData.size();//返回数据源的长度
        }
        return 0;
    }
}
