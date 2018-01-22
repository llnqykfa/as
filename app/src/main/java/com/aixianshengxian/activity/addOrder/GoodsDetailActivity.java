package com.aixianshengxian.activity.addOrder;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.util.JsonUtil;
import com.google.gson.reflect.TypeToken;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.ucn.UCN;
import com.xmzynt.storm.service.goods.Goods;
import com.xmzynt.storm.service.goods.UnitPrice;
import com.xmzynt.storm.service.sale.GoodsOrderLine;
import com.xmzynt.storm.util.BigDecimalUtil;
import com.xmzynt.storm.util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class GoodsDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "GoodsDetailActivity";
    /** 输入框小数的位数*/
    private static final int DECIMAL_DIGITS = 4;
    public static final int GOODS_DETAIL_RESULT = 2;
    private ImageView image_personal;
    private TextView tv_goods_name;
    private Spinner spinner_unit;
    private EditText edit_unit_price;
    private EditText edit_number;
    private Goods mGoods;
    private TextView tv_head_title;
    private Button btn_check_add;
    private GoodsOrderLine goodsOrderLine;
    private UnitPrice mUnitPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail);
        initData();
        loadUnitData();
        initViews();
        initEvents();

    }

    private void initData() {
        mGoods = (Goods) getIntent().getExtras().getSerializable("goods");
        goodsOrderLine = new GoodsOrderLine();
        goodsOrderLine.setGoods(new UCN(mGoods.getUuid(),mGoods.getCode(),mGoods.getName()));

    }

    private void setData() {
        List<String> contents = new ArrayList<>();
        final List<UnitPrice> unitPricesList = mGoods.getUnitPrices();
        if(mGoods != null){
            tv_goods_name.setText(mGoods.getCode()+mGoods.getName());
        }
        if (mGoods.getUnitPrices() != null && mGoods.getUnitPrices().size() > 0) {
            for (UnitPrice unitPrice : unitPricesList) {
                contents.add(unitPrice.getUnit().getName());
            }
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, contents);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_unit.setAdapter(adapter);
            spinner_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mUnitPrice = unitPricesList.get(position);
                //Toast.makeText(BalancePaymentActivity.this, "你点击的是:"+position,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_goods_name = (TextView) findViewById(R.id.tv_goods_name);
        spinner_unit = (Spinner) findViewById(R.id.spinner_unit);
        edit_unit_price = (EditText) findViewById(R.id.edit_unit_price);
        edit_number = (EditText) findViewById(R.id.edit_number);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("商品详情");
        btn_check_add  = (Button) findViewById(R.id.btn_check_add);

        /**
         *  TODO:之后版本提到工具类 设置小数位数控制
         */
        InputFilter lengthfilter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                // 删除等特殊字符，直接返回
                if ("".equals(source.toString())) {
                    return null;
                }
                String dValue = dest.toString();
                String[] splitArray = dValue.split("\\.");
                if (splitArray.length > 1) {
                    String dotValue = splitArray[1];
                    int diff = dotValue.length() + 1 - DECIMAL_DIGITS;
                    if (diff > 0) {
                        return source.subSequence(start, end - diff);
                    }
                }
                return null;
            }
        };
        edit_unit_price.setFilters(new InputFilter[] { lengthfilter, new InputFilter.LengthFilter(10) });
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        btn_check_add.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
    switch (v.getId()){
        case R.id.image_personal:
            finish();
            break;
        case R.id.btn_check_add:
            if(!isValid()){
                return;
            }else {
            goodsOrderLine.setGoodsUnit(mUnitPrice.getUnit());
               // BigDecimal price = BigDecimalUtil.convertToScale(edit_unit_price.getText().toString().trim(),2);
               BigDecimal price = new BigDecimal(edit_unit_price.getText().toString().trim());

              //  BigDecimal goodsQty = BigDecimalUtil.convertToScale(edit_number.getText().toString().trim(),2);
                BigDecimal goodsQty = new BigDecimal(edit_number.getText().toString().trim());
                goodsOrderLine.setOrderPrice( BigDecimalUtil.convertToScale(price,2));
                goodsOrderLine.setGoodsQty( BigDecimalUtil.convertToScale(goodsQty,2));
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("goodsOrderLine",goodsOrderLine);
                intent.putExtras(bundle);
                setResult(GOODS_DETAIL_RESULT,intent);
                finish();
            }
            break;
    }
    }

    private boolean isValid() {
        // validate
        String price = edit_unit_price.getText().toString().trim();
        if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "单价不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        String number = edit_number.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "数量不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void loadUnitData() {
        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        JSONObject body = new JSONObject();
        List<String> goodsUuids = new ArrayList<String>();
        if (mGoods != null) {
            goodsUuids.add(mGoods.getUuid());
            try {
                body.put(DataConstant.GOODS_UUID, GsonUtil.getGson().toJson(goodsUuids));
                params.put(BasicConstants.URL_BODY, body);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OkHttpUtils.postString().url(UrlConstants.URL_TEMP_GOODS_UNIT_LIST)
                    .addHeader("Cookie", "PHPSESSID=" + 123456)
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .addHeader("Content-Type", "application/json;chartset=utf-8")
                    .content(params.toString())
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
                                Type mapType = new TypeToken<Map<String, List<UnitPrice>>>() {
                                }.getType();
                                Map<String, List<UnitPrice>> map = GsonUtil.getGson().fromJson(response.getData(), mapType);

                                if (map != null && map.containsKey(mGoods.getUuid())) {
                                    List<UnitPrice> unitPriceList = map.get(mGoods.getUuid());
                                    if (unitPriceList != null && unitPriceList.size() > 0) {
                                        mGoods.setUnitPrices(unitPriceList);
                                        setData();
                                    }
                                }


                            } else {
                                showCustomToast(response.getMessage());
                            }
                        }
                    });
        }


    }



}
