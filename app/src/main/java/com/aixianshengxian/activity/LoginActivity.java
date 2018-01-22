package com.aixianshengxian.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.util.SessionUtils;

import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.service.user.merchant.Merchant;
import com.xmzynt.storm.util.GsonUtil;
import com.xmzynt.storm.util.MD5Encoder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    public final static String TAG = "LoginActivity";

    private ImageView image_personal;
    private EditText edit_phone;
    private EditText edit_pwd;
    private Button btn_login;
    private TextView tv_head_title;
    private String mSPphone ="";
    private String mSPpwd = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initData();
        initViews();
        initEvents();
    }

    private void initData(){
        mSPphone = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        mSPpwd = SessionUtils.getInstance(getApplicationContext()).getLoginPwd();
    }
    @Override
    protected void initViews() {
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("登录");
        image_personal = (ImageView) findViewById(R.id.image_personal);

        edit_phone = (EditText) findViewById(R.id.edit_phone);
        edit_pwd = (EditText) findViewById(R.id.edit_pwd);
        btn_login = (Button) findViewById(R.id.btn_login);
        if(mSPphone != null && ! TextUtils.isEmpty(mSPphone)){
            edit_phone.setText(mSPphone);
        }
        if(mSPpwd != null && !TextUtils.isEmpty(mSPpwd)){
            edit_pwd.setText(mSPpwd);
        }



    }

    @Override
    protected void initEvents() {
        btn_login.setOnClickListener(this);
        image_personal.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                finish();
                break;
            case R.id.btn_login:
                if (!isValid()) {
                    return;
                } else {
                    login(edit_phone.getText().toString().trim(),
                            edit_pwd.getText().toString().trim());
                    SessionUtils.getInstance(getApplicationContext()).saveLoginPhone(edit_phone.getText().toString().trim());
                    SessionUtils.getInstance(getApplicationContext()).saveLoginPwd(edit_pwd.getText().toString().trim());
                }
                break;
        }
    }

    public void login(String userName, String pwd) {
        JSONObject params = new JSONObject();

        try {
            JSONObject body = new JSONObject();
            body.put(BasicConstants.Field.USER_NAME, userName);
            body.put(DataConstant.PASSWORD, MD5Encoder.MD5Encode(pwd));
            body.put(BasicConstants.Field.USER_IDENTITY, UserIdentity.merchant.name());
            //body.put(DataConstant.CAPTCHA,"");
            //body.put(DataConstant.SERVICE,"http://localhost:8080/#/index/view");
            params.put(BasicConstants.URL_BODY, body);
            params.put(BasicConstants.URL_PLATFORM, Platform.android.name());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils.postString().url(UrlConstants.URL_LOGIN)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(params.toString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showShortToast("请求失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        showLogDebug("login", response);
                        ResponseBean res = GsonUtil.getGson().fromJson(response, ResponseBean.class);
                        if (res.getErrorCode() == 0) {
                            // showShortToast("请求成功");
                            Merchant merchant = GsonUtil.getGson().fromJson(res.getData(), Merchant.class);
                            SessionUtils.getInstance(getApplicationContext()).saveCustomerUUID(merchant.getUuid());
                            SessionUtils.getInstance(getApplicationContext()).saveLoginState(true);
                            SessionUtils.getInstance(getApplicationContext()).saveLoginMerchant(merchant);
                            SessionUtils.getInstance(getApplicationContext()).saveSessionId(merchant.getSessionId());
                            SessionUtils.getInstance(getApplicationContext()).saveOrg(merchant.getOrg());
                            startActivity(NewMainActivity.class);
                            finish();
                        } else {
                            showShortToast(res.getMessage());
                        }

                    }
                });
    }

    private boolean isValid() {
        // validate
        String phone = edit_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return false;
        }

        String pwd = edit_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, " 请输入密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }


}
