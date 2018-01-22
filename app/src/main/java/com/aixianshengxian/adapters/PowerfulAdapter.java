package com.aixianshengxian.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.aixianshengxian.view.ViewHolder;

import java.util.List;

/**
 * Created by CWJ on 2016/11/23 0023.
 */

public abstract class PowerfulAdapter<T> extends BaseAdapter {
    private List<T> dataList;
    private Context context;
    private int layoutId;

    private MyListener mListener;
    private PowerfulListener mPowerfulListener;

    private int mIndex;

    public PowerfulAdapter(Context context, List<T> dataList, int layoutId) {
        this.dataList = dataList;
        this.context = context;
        this.layoutId = layoutId;
    }

    public PowerfulAdapter(Context context, List<T> dataList, int layoutId, MyListener listener) {
        this.dataList = dataList;
        this.context = context;
        this.layoutId = layoutId;
        this.mListener = listener;
   }

    public void setPowerfulListener(PowerfulListener listener) {
        mPowerfulListener = listener;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public T getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.getInstance(convertView, context, layoutId, parent);
        convert(holder, dataList.get(position));
       // Log.d("coupon_ongoing_select_item", "getView: position is " + position);

        mIndex = position;
        mListener = new MyListener(position);


        setListener(holder, position, mListener);
        return holder.getConvertView();
    }

    public abstract void convert(ViewHolder holder, T t);

    public abstract void setListener(ViewHolder holder, int position, View.OnClickListener listener);


    private class MyListener implements View.OnClickListener{
        int mPosition;

        public MyListener(int position){
            mPosition= position;
        }

        public void setPosition(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            if (mPowerfulListener != null) {
                mPowerfulListener.onClick(v, mPosition);
            }
        }
    }

    public interface  PowerfulListener {
        void onClick(View v, int position);
    }

}

