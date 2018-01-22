package com.aixianshengxian.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aixianshengxian.R;
import com.aixianshengxian.module.UcodeMessage;

import java.util.List;

/**
 * Created by Administrator on 2017/9/28.
 */

public class ReturnedUcodeAdapter extends RecyclerView.Adapter<ReturnedUcodeAdapter.ViewHolder>{

    private List<UcodeMessage> mData;

    static  class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_ucode,tv_operate_time;
        View ucodeView;
        public ViewHolder(View view){//自定义内部类，继承自RecyclerView.ViewHolder
            super(view);//RecyclerView子项的最外层布局，方便获取下列id
            tv_ucode = (TextView)view.findViewById(R.id.tv_ucode);
            tv_operate_time = (TextView)view.findViewById(R.id.tv_operate_time);
            ucodeView=view;//定义一个包括文字的布局
        }
    }

    public ReturnedUcodeAdapter(List<UcodeMessage> UcodeList){//构造函数，把要展示的数据源传进来，并赋值给全局变量mFruitList
        mData=UcodeList;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){//创建一个ViewHolder
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_ucode,parent,false);//传入布局

        final ViewHolder holder=new ViewHolder(view);//把刚才定义的view传给holder
        return holder;//返回该实例
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){//对RecyclerView子项的数据进行赋值，会在每个子项被滚到屏幕内的时候执行
        UcodeMessage ucodeMessage = mData.get(position);
        //放数据
        holder.tv_ucode.setText(ucodeMessage.getUCode());
        holder.tv_operate_time.setText(ucodeMessage.getOperateTime());
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
