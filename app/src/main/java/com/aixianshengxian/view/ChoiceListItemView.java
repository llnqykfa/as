package com.aixianshengxian.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.aixianshengxian.R;


/**
 * Created by CWJ on 2017/4/8.
 */

public class ChoiceListItemView extends LinearLayout implements Checkable{
    LinearLayout line_item;
    TextView tv_store_name;
    RadioButton radiobutton;
    public ChoiceListItemView(Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_single_choice_list,this,true);
        initView(view);
    }

    public ChoiceListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_single_choice_list,this,true);
        initView(view);

    }

    public ChoiceListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    private void initView(View view){
       line_item   = (LinearLayout) view.findViewById(R.id.line_item);
        tv_store_name   = (TextView) view.findViewById(R.id.tv_store_name);
        radiobutton  = (RadioButton) view.findViewById(R.id.radiobutton);
    }

    @Override
    public void setChecked(boolean checked) {
        line_item.setSelected(checked);
        tv_store_name.setSelected(checked);
        radiobutton.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return radiobutton.isChecked();
    }

    @Override
    public void toggle() {
        radiobutton.toggle();
    }
    public void setData(String name){
        tv_store_name.setText(name);
    }
}
