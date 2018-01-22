package com.aixianshengxian.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.aixianshengxian.view.ChoiceListItemView;
import com.xmzynt.storm.service.wms.warehouse.Warehouse;

import java.util.List;

/**
 * Created by CWJ on 2017/4/8.
 */

public class SingleAdapter extends BaseAdapter {

    private List<Warehouse> warehouseList;
    private Context mContext;

    public SingleAdapter(List<Warehouse> groupList, Context mContext) {
        this.warehouseList = groupList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return warehouseList.size();
    }

    @Override
    public Object getItem(int position) {
        if(warehouseList.get(position) != null){
            return warehouseList.get(position);
        }
        return null;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChoiceListItemView view = new ChoiceListItemView(mContext);
        if(warehouseList.get(position).getName()!=null){
            view.setData(warehouseList.get(position).getName());
        }
        return view;
    }
}
