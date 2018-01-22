package com.aixianshengxian.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.adapters.SignAndRefundAdapter;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.util.HttpUtil;
import com.aixianshengxian.util.JsonUtil;
import com.google.gson.reflect.TypeToken;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.constants.CTMSConstants;
import com.xmzynt.storm.basic.constants.SaleConstants;
import com.xmzynt.storm.basic.constants.WMSConstants;
import com.xmzynt.storm.service.sale.returnbill.ReturnBill;
import com.xmzynt.storm.service.sale.returnbill.ReturnBillLine;
import com.xmzynt.storm.service.transport.SignLineInfo;
import com.xmzynt.storm.service.transport.TransportBill;
import com.xmzynt.storm.service.transport.TransportBillLine;
import com.xmzynt.storm.util.BigDecimalUtil;
import com.xmzynt.storm.util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class SignAndReturnActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "SignAndReturnActivity";

    private ImageView image_personal;
    private TextView tv_head_title;
    private RecyclerView recylerView_goods_list;
    private Button btn_check_sign;
    private ReturnBill mReturnBill;
    private SignAndRefundAdapter adapter;
    private TransportBill transportBill;
    private List<ReturnBillLine> mReturnBillLines  = new ArrayList<>();//本地封装returnBillLine;
    private EditText edit_sign_mark;
    private EditText edit_return_remark;
    private Map<String, BigDecimal> mRstockOutRecords;//出库成本单价列表
    private   List<TransportBillLine> mDatas  =new ArrayList<>();
    private boolean isgetStockInCostPrice = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_and_return);
        initData();
        initViews();
        initEvents();
        setData();
    }

    private void initData() {
        transportBill = (TransportBill) getIntent().getExtras().getSerializable("transportBill");
        mReturnBill = loadReturnBill();

        if(transportBill.getLines() != null){
            mDatas.addAll(transportBill.getLines());
        }

    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("签收/退货");
        recylerView_goods_list = (RecyclerView) findViewById(R.id.recylerView_goods_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recylerView_goods_list.setLayoutManager(linearLayoutManager);
        btn_check_sign = (Button) findViewById(R.id.btn_check_sign);
        edit_sign_mark = (EditText) findViewById(R.id.edit_sign_mark);
        edit_return_remark = (EditText) findViewById(R.id.edit_return_remark);
    }

    @Override
    protected void initEvents() {
        btn_check_sign.setOnClickListener(this);
        image_personal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_check_sign:
               // mReturnBill.setLines(returnBillLines);
                mReturnBillLines  =  adapter .getmReturnLines();//从adapter中获得退货数据列表
                mDatas = adapter.getmDatas();//从adapter获得数据列表
                transportBill.setLines(mDatas);
                sign();
               getStockInCostPrice();
               // finish();

                break;
            case R.id.image_personal:
                finish();
                break;
        }
    }

    private void setData() {
        if (transportBill != null && mReturnBill != null) {

            adapter = new SignAndRefundAdapter(mDatas, getApplicationContext(),transportBill);

            recylerView_goods_list.setAdapter(adapter);
        }

    }

    private ReturnBill loadReturnBill() {
        final ReturnBill returnBill = new ReturnBill();

        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        OkHttpUtils.postString().url(UrlConstants.URL_CREAT_RETURN_BILL)
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
                            if (response.getData() != null) {
                                ReturnBill returnBill1 = GsonUtil.getGson().fromJson(response.getData(), ReturnBill.class);
                                if (returnBill1 != null) {
                                    returnBill.setBillNumber(returnBill1.getBillNumber());
                                    returnBill.setOrg(returnBill1.getOrg());
                                }

                            } else {
                                showCustomToast(response.getMessage());
                            }
                        }
                    }
                });
        return returnBill;
    }

    /**
     * 获取出库成本单价
     */

    private void getStockInCostPrice(){
        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        JSONObject body = new JSONObject();
        List<String> stockOutRecordUuids = new ArrayList<String>();
        for (TransportBillLine transportBillLine : transportBill.getLines()) {
            stockOutRecordUuids.add(transportBillLine.getSourceUuid());
        }
        try {
            body.put(WMSConstants.Field.UUIDS, GsonUtil.getGson().toJson(stockOutRecordUuids));
            params.put(BasicConstants.URL_BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtil.post(UrlConstants.URL_GET_STOCK_IN_RECORD, params.toString(), new HttpUtil.HttpListener() {
            @Override
            public void successResponse(String s, int i) {
                ResponseBean response = GsonUtil.getGson().fromJson(s,ResponseBean.class);
                if(response.getErrorCode() == 0){
                    showLogDebug(TAG,response.getData());
                    Type mapType = new TypeToken<Map<String, BigDecimal>>(){}.getType();
                    mRstockOutRecords = GsonUtil.getGson().fromJson(response.getData(),mapType);
                    if(mRstockOutRecords != null){
                      refund();//执行退货
                    }else {
                        finish();
                    }
                }else {
                    showCustomToast(response.getMessage());
                    finish();
                }
            }

            @Override
            public void errorResponse(Call call, Exception e, int i) {
                showCustomToast("请求失败");
            }
        });

    }
    /**
     * 签收
     */
    private void sign() {
        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        JSONObject body = new JSONObject();
        String signMark = edit_sign_mark.getText().toString().trim();
        Map<String, SignLineInfo> signLineInfoMap = new HashMap<String, SignLineInfo>();

        if (mDatas != null && mDatas.size() > 0) {
            for (TransportBillLine transportBillLine : mDatas) {
                int r=transportBillLine.getSignedQty().compareTo(BigDecimal.ZERO); //和0，Zero比较
                if( r != 0 ){//签收数量不为0 则构造
                    SignLineInfo signLineInfo = new SignLineInfo();
                    signLineInfo.setSignedQty(transportBillLine.getSignedQty());
                    signLineInfo.setSignedPrice(transportBillLine.getSignedPrice());

                    signLineInfo.setSignedSubtotal(transportBillLine.getSignedQty().multiply(transportBillLine.getSignedPrice()));
                    signLineInfoMap.put(transportBillLine.getUuid(), signLineInfo);
                }

            }
            try {
                params.put(BasicConstants.URL_BODY, body);
                body.put(CTMSConstants.Field.UUID, transportBill.getUuid());
                body.put(CTMSConstants.Field.SIGN_LINE_INFO_MAP, GsonUtil.getGson().toJson(signLineInfoMap));
                if(signMark != null){
                    body.put(CTMSConstants.Field.SIGNED_REMARK, signMark);
                }
                body.put(CTMSConstants.Field.VERSION, transportBill.getVersion());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(!signLineInfoMap.isEmpty()){//签收列表不为空
                HttpUtil.post(UrlConstants.URL_SIGN, params.toString(), new HttpUtil.HttpListener() {
                    @Override
                    public void successResponse(String s, int i) {
                        ResponseBean response = GsonUtil.getGson().fromJson(s,ResponseBean.class);
                        if(response.getErrorCode() == 0){
                            showShortToast("签收成功");
                            finish();

                        }else {
                            showCustomToast(response.getMessage());
                        }
                    }

                    @Override
                    public void errorResponse(Call call, Exception e, int i) {
                        showCustomToast("请求失败");
                    }
                });
            }else {//签收列表为空直接请求退货
               // getStockInCostPrice();
                finish();
            }

        }


    }

    /**
     * 退货
     */
    private void refund(){
       List<ReturnBillLine> tempRetrunBillLines = new ArrayList<>();//构造新的退货单
        if(mReturnBillLines != null && mReturnBillLines.size() > 0){
                for(int i = 0 ; i < mReturnBillLines.size(); i ++){//此时 退货列表和签收列表长度一样
                    ReturnBillLine returnBillLine = mReturnBillLines.get(i);//获取退货单
                    TransportBillLine transportBillLine = mDatas.get(i);//获取签收单
                    BigDecimal resourceRecord = mRstockOutRecords.get(transportBillLine.getSourceUuid());//获取出库入库单价
                            int r = returnBillLine.getReturnQty().compareTo(BigDecimal.ZERO);
                        if( r >0 ) {
                            returnBillLine.setStockInCostPrice(resourceRecord);//设置出库入库单价到退货单
                            tempRetrunBillLines.add(returnBillLine);
                        }


                }

           mReturnBill.setLines(tempRetrunBillLines);//设置退货列表到退货单
        }
        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        JSONObject body = new JSONObject();
        ReturnBill returnBill =mReturnBill;
        returnBill.setCustomer(transportBill.getCustomer());
        returnBill.setSourceBillNumber(transportBill.getSourceBillNumber());
        returnBill.setAddress(transportBill.getAddress());
        returnBill.setLinkMan(transportBill.getLinkMan());
        returnBill.setLinkPhone(transportBill.getLinkPhone());

        if(!TextUtils.isEmpty(edit_return_remark.getText().toString().trim())){
            returnBill.setRemark(edit_return_remark.getText().toString().trim());
        }

        BigDecimal refundAmount = BigDecimal.ZERO;
        if(returnBill.getLines() != null && returnBill.getLines().size()>0){
            for (ReturnBillLine line : returnBill.getLines()) {
                refundAmount = refundAmount.add(BigDecimalUtil.convertToScale(line.getOrderPrice().multiply(line.getReturnQty()), 2));
            }
            returnBill.setRefundAmount(refundAmount);
            try {
                params.put(BasicConstants.URL_BODY, body);
                body.put(SaleConstants.Field.RETURN_BILL, GsonUtil.getGson().toJson(returnBill));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(mReturnBill.getLines() != null && mReturnBill.getLines().size()>0){//退货清单不为空
                HttpUtil.post(UrlConstants.URL_REFUND, params.toString(), new HttpUtil.HttpListener() {
                    @Override
                    public void successResponse(String s, int i) {
                        ResponseBean response = GsonUtil.getGson().fromJson(s,ResponseBean.class);
                        if(response.getErrorCode() == 0){
                            showShortToast("退货成功");
                             finish();
                        }else {
                            showCustomToast(response.getMessage());
                        }
                    }

                    @Override
                    public void errorResponse(Call call, Exception e, int i) {
                        showCustomToast("请求失败");
                    }
                });
            }else {//退货清单为空
                finish();

            }

        }


    }
    private void submit() {
        // validate
        String mark = edit_sign_mark.getText().toString().trim();
        if (TextUtils.isEmpty(mark)) {
            Toast.makeText(this, "签收备注", Toast.LENGTH_SHORT).show();
            return;
        }

        String remark = edit_return_remark.getText().toString().trim();
        if (TextUtils.isEmpty(remark)) {
            Toast.makeText(this, "退货备注", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something


    }
}
