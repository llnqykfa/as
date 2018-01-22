package com.aixianshengxian.activity.check;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.activity.purchase.PurchaseActivity;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.util.SessionUtils;
import com.xmzynt.storm.basic.BasicConstants;

import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.service.purchase.bill.PurchaseBillStatus;
import com.xmzynt.storm.service.purchase.bill.PurchaseData;
import com.xmzynt.storm.util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.xmzynt.storm.service.purchase.bill.PurchaseBillStatus.audited;
import static com.xmzynt.storm.service.purchase.bill.PurchaseBillStatus.stockIned;

public class CheckActivity extends BaseActivity implements View.OnClickListener {
    private ImageView image_personal,iv_delete;
    private TextView tv_head_title;
    private Button btn_check,btn_check_purchase;
    private EditText edt_code;

    private PurchaseData mPurchaseData;
    private List<String> purchaseBillStatus = new ArrayList<>();

    public static CheckActivity mactivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        init();
        initViews();
        initEvents();
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        edt_code.setText("");
    }*/

    void init() {
        edt_code = (EditText) findViewById(R.id.edt_code);
        //这个很重要，先移开TextWatcher的监听器
        if (edt_code.getTag() instanceof TextWatcher) {
            edt_code.removeTextChangedListener((TextWatcher) edt_code.getTag());
        }
        TextWatcher watcher1 = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ucode = String.valueOf(edt_code.getText());
                if (TextUtils.isEmpty(s)) {
                    Toast.makeText(CheckActivity.mactivity,"扫描内容不能为空",Toast.LENGTH_SHORT).show();
                } else {
                    //stockInRecord.setPrice(new BigDecimal(String.valueOf(edt_purchase_price.getText())));
                    getScanPurchaseData(ucode);
                }
            }
        };
        edt_code.addTextChangedListener(watcher1);
        edt_code.setTag(watcher1);
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("验收");
        btn_check = (Button) findViewById(R.id.btn_check);
        edt_code = (EditText) findViewById(R.id.edt_code);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);
        btn_check_purchase = (Button) findViewById(R.id.btn_check_purchase);

        mactivity = this;
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        btn_check.setOnClickListener(this);
        iv_delete.setOnClickListener(this);
        btn_check_purchase.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                finish();
                break;
            case R.id.btn_check:
                String ucode = String.valueOf(edt_code.getText());
                if (ucode.equals("")) {
                    showCustomToast("请先扫描采购标签！");
                } else {
                    getScanPurchaseData(ucode);
                    /*Intent intent1 = new Intent(CheckActivity.this,CheckDetailActivity.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("uuid",ucode);
                    intent1.putExtras(bundle1);
                    startActivity(intent1);*/
                }
                break;
            case R.id.iv_delete:
                edt_code.setText("");
                break;
            case R.id.btn_check_purchase:
                Intent intent1 = new Intent(CheckActivity.this,PurchaseActivity.class);
                Bundle bundle1 = new Bundle();
                purchaseBillStatus.add(stockIned.name());
                purchaseBillStatus.add(audited.name());
                bundle1.putSerializable("Status", (Serializable) purchaseBillStatus);
                intent1.putExtras(bundle1);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    //根据上一个活动传来的数据，获取采购信息
    private void getScanPurchaseData(String ucode) {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();

        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            try {
                body.put(BasicConstants.Field.UUID,ucode);
                reparams.put(BasicConstants.URL_BODY, body);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            reparams.put(BasicConstants.URL_PLATFORM, Platform.android.name());
            reparams.put(BasicConstants.Field.USER_IDENTITY, UserIdentity.merchant.name());
            reparams.put(DataConstant.SESSIONID, sessionId);
            reparams.put(BasicConstants.Field.USER_UUID, userUuid);
            reparams.put(BasicConstants.Field.USER_NAME, userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString().url(UrlConstants.URL_PURCHASE_SCAN_DATA)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(reparams.toString())
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        showCustomToast("请求失败");
                    }

                    @Override
                    public void onResponse(String s, int i) {

                        ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                        if (response.getErrorCode() == 0) {
                            mPurchaseData = GsonUtil.getGson().fromJson(response.getData(),PurchaseData.class);
                            Intent intent1 = new Intent(CheckActivity.this,CheckDetailActivity.class);
                            Bundle bundle1 = new Bundle();
                            bundle1.putSerializable("PurchaseData", (Serializable) mPurchaseData);
                            intent1.putExtras(bundle1);
                            startActivity(intent1);

                            showCustomToast("请求成功");
                            showLogDebug("main", s);
                        }  else {
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }
}
