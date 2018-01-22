package com.aixianshengxian.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.util.HttpUtil;
import com.aixianshengxian.util.JsonUtil;
import com.aixianshengxian.util.SessionUtils;
import com.xmzynt.storm.util.GsonUtil;

import org.json.JSONObject;

import okhttp3.Call;

public class PersonalActivity extends BaseActivity implements View.OnClickListener {

    private ImageView image_personal;
    private Button btn_login_quit;
    private TextView tv_head_title;
    private boolean isLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        initViews();
        initEvents();
    }

    @Override
    protected void initViews() {
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("个人中心");
        image_personal = (ImageView) findViewById(R.id.image_personal);
        btn_login_quit = (Button) findViewById(R.id.btn_login_quit);
         isLogin = SessionUtils.getInstance(getApplicationContext()).getLoginState();//获取登录状态
        if(isLogin){//已登录，注销
            btn_login_quit.setText("注销");}
        else {
            btn_login_quit.setText("登录");
        }
    }

    @Override
    protected void initEvents() {
        btn_login_quit.setOnClickListener(this);
        image_personal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                finish();
                break;
            case R.id.btn_login_quit:
                if(isLogin){//已登录，注销
                    logout();
                }else {//未登录跳转到登录界面
                    startActivity(LoginActivity.class);
                    finish();
                }
                break;
        }
    }
    private  void logout(){
        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        HttpUtil.post(UrlConstants.URL_LOGOUT, params.toString(), new HttpUtil.HttpListener() {
            @Override
            public void successResponse(String s, int i) {
                ResponseBean responseBean = GsonUtil.getGson().fromJson(s,ResponseBean.class);
                if(responseBean.getErrorCode() == 0){
                    showCustomToast("注销成功");
                    SessionUtils.getInstance(getApplicationContext()).saveLoginState(false);//保存注销状态
                    SessionUtils.getInstance(getApplicationContext()).saveLoginMerchant(null);
                    startActivity(LoginActivity.class);
                    finish();
                }else {
                    showCustomToast(responseBean.getMessage());
                }
            }

            @Override
            public void errorResponse(Call call, Exception e, int i) {
                showCustomToast("请求失败");
            }
        });
    }


}
