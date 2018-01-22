package com.aixianshengxian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.util.SessionUtils;


public class GuideActivity extends BaseActivity {
    private Handler mHandler ;
  // private SharedPreferences mPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guid);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initViews();
    }

    @Override
    protected void initViews() {
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //  mPreferences = getPreferences(MODE_PRIVATE);

                Boolean ischoice = SessionUtils.getInstance(getApplicationContext()).getLoginState();//是否为第一次使用该程序
                if(ischoice){
                    Intent intent = new Intent(GuideActivity.this,NewMainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(GuideActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },3000);
    }

    @Override
    protected void initEvents() {

    }







}
