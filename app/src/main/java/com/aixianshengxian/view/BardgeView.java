package com.aixianshengxian.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aixianshengxian.R;

/**
 * Created by cwj on 2017/6/5.
 */

public class BardgeView extends LinearLayout {

    private View mRootView;
    private TextView textview;
    private ImageView badgeview_target;
    private String mText;

    public BardgeView(Context context,String str) {
        super(context);
        this.mText= str;
        mRootView = inflate(context, R.layout.tab_layout_item, this);
        initView(mRootView);

    }

    public BardgeView(Context context, AttributeSet attrs,String str) {
        super(context, attrs);
        mRootView = inflate(context, R.layout.tab_layout_item, this);
        initView(mRootView);
        this.mText= str;
    }
    private void initView(View view){
        textview = (TextView) view.findViewById(R.id.textview);
        badgeview_target = (ImageView) view.findViewById(R.id.badgeview_target);
        badgeview_target.setImageResource(R.drawable.round_bg);
        textview.setText(mText);
    }

    public void showeImageView(){
        badgeview_target.setVisibility(VISIBLE);
    }
    public void hideImageView(){
        badgeview_target.setVisibility(GONE);
    }
}
