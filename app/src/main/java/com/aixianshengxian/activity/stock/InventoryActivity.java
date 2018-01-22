package com.aixianshengxian.activity.stock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.util.SessionUtils;
import com.google.gson.reflect.TypeToken;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.constants.SaleConstants;
import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.service.wms.stock.Stock;
import com.xmzynt.storm.util.GsonUtil;
import com.xmzynt.storm.util.query.PageData;
import com.xmzynt.storm.util.query.QueryFilter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class InventoryActivity extends BaseActivity implements View.OnClickListener {
    private ImageView image_personal,iv_delete;
    private TextView tv_head_title;
    private Button btn_search,btn_stock;
    private EditText edt_code;

    private int page = 1;
    private int index = 0;
    private int pageNo = 0;
    private int pageSize = 20;

    private List<Stock> Stocks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        init();
        initViews();
        initEvents();
    }

    void init() {
        edt_code = (EditText) findViewById(R.id.edt_code);
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("盘点");
        btn_search = (Button) findViewById(R.id.btn_search);
        edt_code = (EditText) findViewById(R.id.edt_code);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);
        btn_stock = (Button) findViewById(R.id.btn_stock);
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        iv_delete.setOnClickListener(this);
        btn_stock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                finish();
                break;
            case R.id.btn_search:
                String ucode = String.valueOf(edt_code.getText());
                if (ucode.equals("")) {
                    showCustomToast("请先扫描周转筐标签！");
                } else {
                    getQueryStock(ucode);
                    /*Intent intent1 = new Intent(InventoryActivity.this,InventoryDetailActivity.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("uuid",ucode);
                    intent1.putExtras(bundle1);
                    startActivity(intent1);*/
                }
                break;
            case R.id.iv_delete:
                edt_code.setText("");
                break;
            case R.id.btn_stock:
                Intent intent1 = new Intent(InventoryActivity.this,StockRecordActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("Activity","InventoryActivity");
                intent1.putExtras(bundle1);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    //周转筐查询库存
    private void getQueryStock(String ucode) {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            final QueryFilter filter = new QueryFilter();
            filter.setPage(pageNo);
            filter.setPageSize(pageSize);
            filter.setDefaultPageSize(0);
            filter.put(DataConstant.MORETHANZERO,true);
            filter.put(DataConstant.BASKETCODE,ucode);
            try {
                reparams.put(BasicConstants.URL_BODY, body);
                body.put(SaleConstants.Field.QUERY_FILTER, GsonUtil.getGson().toJson(filter));
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
        OkHttpUtils.postString().url(UrlConstants.URL_QUERY_STOCK)
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
                    public void onResponse(String s, int id) {
                        ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                        if (response.getErrorCode() == 0) {
                            PageData pageData = GsonUtil.getGson().fromJson(response.getData(), PageData.class);
//                            PageData pageData = GsonUtil.getGson().fromJson(response.getData(),PageData.class);
                            Stocks = GsonUtil.getGson().fromJson(GsonUtil.getGson().toJson(pageData.getValues()),new TypeToken<List<Stock>>(){}.getType());
                            Intent intent1 = new Intent(InventoryActivity.this,InventoryDetailActivity.class);
                            Bundle bundle1 = new Bundle();
                            bundle1.putSerializable("Stocks", (Serializable) Stocks);
                            intent1.putExtras(bundle1);
                            startActivity(intent1);

                            showCustomToast("请求成功");
                            showLogDebug("main", s);
                        } else {
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }
}
