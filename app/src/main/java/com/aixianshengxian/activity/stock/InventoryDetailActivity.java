package com.aixianshengxian.activity.stock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.module.BroadcastAction;
import com.aixianshengxian.util.DatesUtils;
import com.aixianshengxian.util.SessionUtils;
import com.google.gson.reflect.TypeToken;

import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.idname.IdName;

import com.xmzynt.storm.basic.operateinfo.OperateInfo;
import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.service.wms.inventory.InventoryRecord;
import com.xmzynt.storm.service.wms.stock.Stock;
import com.xmzynt.storm.service.wms.stockout.StockOutType;
import com.xmzynt.storm.util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

public class InventoryDetailActivity extends BaseActivity implements View.OnClickListener {
    private ImageView image_personal,iv_photo;
    private TextView tv_head_title,tv_associated;
    private TextView tv_depot,tv_product_name,tv_quantity,tv_unit1,tv_unit2;
    private TextView tv_stockin_time,tv_place,tv_batch_number,tv_productive_time,tv_effective_time;
    private EditText edt_inventory_num;
    private Button btn_confirm;

    private LinearLayout ll_nomessage;

    private int page = 1;
    private int index = 0;
    private int pageNo = 0;
    private int pageSize = 20;

    private List<String> mImage = new ArrayList<>();
    private List<Stock> Stocks = new ArrayList<>();
    private InventoryRecord inventoryRecord = new InventoryRecord();

    public static InventoryDetailActivity mactivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_detail);

        initViews();
        initEvents();

        getUcode();
        getQueryStock();
    }

    protected void initData() {
        //仓库
        tv_depot = (TextView) findViewById(R.id.tv_depot);
        tv_depot.setText(Stocks.get(0).getWarehouse() == null ?"":Stocks.get(0).getWarehouse().getName());
        //商品
        tv_product_name = (TextView) findViewById(R.id.tv_product_name);
        tv_product_name.setText(Stocks.get(0).getGoods().getName());
        //数量
        tv_quantity = (TextView) findViewById(R.id.tv_quantity);
        tv_quantity.setText(String.valueOf(Stocks.get(0).getAmount()));
        //单位1
        tv_unit1 = (TextView) findViewById(R.id.tv_unit1);
        tv_unit1.setText(Stocks.get(0).getGoodsUnit().getName());
        //单位2
        tv_unit2 = (TextView) findViewById(R.id.tv_unit2);
        tv_unit2.setText(Stocks.get(0).getGoodsUnit().getName());
        //入库时间
        tv_stockin_time = (TextView) findViewById(R.id.tv_stockin_time);
        tv_stockin_time.setText(DatesUtils.dateToStr(Stocks.get(0).getStockInDate()));
        //产地
        tv_place = (TextView) findViewById(R.id.tv_place);
//        tv_place.setText(Stocks.get(0).getOrigin());
        //批号
        //tv_batch_number = (TextView) findViewById(R.id.tv_batch_number);
//        tv_batch_number.setText(Stocks.get(0).getBatchNumber());
        //生产日期
        //tv_productive_time = (TextView) findViewById(R.id.tv_productive_time);
//        if (Stocks.get(0).getProduceDate() != null) {
//            tv_productive_time.setText(DatesUtils.dateToStr(Stocks.get(0).getProduceDate()));
//        } else {
//            tv_productive_time.setText("");
//        }

        //有效日期
        //tv_effective_time = (TextView) findViewById(R.id.tv_effective_time);
//        if (Stocks.get(0).getEffectiveDate() != null) {
//            tv_effective_time.setText(DatesUtils.dateToStr(Stocks.get(0).getEffectiveDate()));
//        } else {
//            tv_effective_time.setText("");
//        }
        //实际数量
        edt_inventory_num = (EditText) findViewById(R.id.edt_inventory_num);
        //这个很重要，先移开TextWatcher的监听器
        if (edt_inventory_num.getTag() instanceof TextWatcher) {
            edt_inventory_num.removeTextChangedListener((TextWatcher) edt_inventory_num.getTag());
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
                if (TextUtils.isEmpty(s)) {
                    Toast.makeText(InventoryDetailActivity.mactivity,"盘点数量不能为空",Toast.LENGTH_SHORT).show();
                } else {

                }
            }
        };
        edt_inventory_num.addTextChangedListener(watcher1);
        edt_inventory_num.setTag(watcher1);

        //tv_associated = (TextView) findViewById(R.id.tv_associated);
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("盘点详情");
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);

        ll_nomessage = (LinearLayout) findViewById(R.id.ll_nomessage);
        mactivity = this;
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        iv_photo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                Intent intent = new Intent(
                        BroadcastAction.ACTION_STOCK_RECORD_REFRESH);
                // 发送广播
                sendBroadcast(intent);
                finish();
                break;
            case R.id.btn_confirm:
                check();
                break;
            case R.id.iv_photo:
                break;
            default:
                break;
        }
    }

    //获取ucode
    private void getUcode() {
        Intent intent = getIntent();
        Stocks = (List<Stock>) intent.getSerializableExtra("Stocks");
    }

    //周转筐查询库存
    private void getQueryStock() {
        if (Stocks.size() == 0) {
            ll_nomessage.setVisibility(View.VISIBLE);
        } else {
            initData();
//            if (Stocks.get(0).getBasketCodes() != null) {
//                List<String> associatedBasket = Stocks.get(0).getBasketCodes();
//                showAssociatedBasket(associatedBasket);
//            } else {
//                tv_associated.setText("没有关联周转箱");
//            }
            ll_nomessage.setVisibility(View.GONE);
        }
    }

    //关联周转箱
    private void showAssociatedBasket(List<String> associatedBasket) {
        String basketStr = new String();
        for (int i = 0;i < associatedBasket.size();i ++) {
            if (i == associatedBasket.size() - 1) {
                basketStr = basketStr + associatedBasket.get(i);
            } else {
                basketStr = basketStr + associatedBasket.get(i) + ",";
            }
        }
        tv_associated.setText(basketStr);
    }

    //检查信息
    private void check() {

        if (!String.valueOf(edt_inventory_num.getText()).equals("") && new BigDecimal(String.valueOf(edt_inventory_num.getText())).compareTo(BigDecimal.ZERO) > 0) {
            getInventory();
        } else {
            showCustomToast("盘点数量不能为空或者为“0”");
        }
    }

    //确认
    private void getInventory() {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();

        //org
        IdName org = SessionUtils.getInstance(getApplicationContext()).getOrg() == null?null:SessionUtils.getInstance(getApplicationContext()).getOrg();

        //StockOutRecord stockOutRecord = new StockOutRecord();
        if (org == null) {//org
            IdName newOrg = new IdName();
            newOrg.setId(userUuid);
            newOrg.setName(userName);
            inventoryRecord.setOrg(newOrg);
        } else {
            inventoryRecord.setOrg(org);
        }

        inventoryRecord.setGoods(Stocks.get(0).getGoods());//goods
        inventoryRecord.setGoodsUnit(Stocks.get(0).getGoodsUnit());//goodsUnit
        inventoryRecord.setStockQty(Stocks.get(0).getAmount());//stockQty
        inventoryRecord.setInventoryQty(new BigDecimal(String.valueOf(edt_inventory_num.getText())));//inventoryQty
        inventoryRecord.setWarehouse(Stocks.get(0).getWarehouse());//warehouse
        OperateInfo operateInfo = new OperateInfo();
        IdName operate = new IdName();
        operate.setId(userUuid);
        operate.setName(userName);
        operateInfo.setOperator(operate);
        operateInfo.setOperateTime(new Date());
        inventoryRecord.setOperateInfo(operateInfo);//operateInfo
        inventoryRecord.setRemark(Stocks.get(0).getRemark());//remark
        inventoryRecord.setSourceStockUuid(Stocks.get(0).getUuid());//sourceStockUuid
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            try {
                body.put(DataConstant.INVENTORY, GsonUtil.getGson().toJson(inventoryRecord));
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
        OkHttpUtils.postString().url(UrlConstants.URL_INVENTORY )
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
                            showCustomToast("请求成功");
                            finish();
                            Intent intent = new Intent(
                                    BroadcastAction.ACTION_STOCK_RECORD_REFRESH);
                            // 发送广播
                            sendBroadcast(intent);
                            showLogDebug("main", s);
                        }  else {
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }
}
