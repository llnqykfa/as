package com.aixianshengxian.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.adapters.GoodsOrderLinesAdapter;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.popupwindows.OrderManagerPopupWindow;
import com.aixianshengxian.util.DateUtils;
import com.aixianshengxian.util.HttpUtil;
import com.aixianshengxian.util.JsonUtil;
import com.aixianshengxian.util.SessionUtils;
import com.google.gson.reflect.TypeToken;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.constants.CTMSConstants;
import com.xmzynt.storm.basic.constants.SaleConstants;
import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.service.sale.GoodsOrder;
import com.xmzynt.storm.service.transport.TransportBill;
import com.xmzynt.storm.util.BigDecimalUtil;
import com.xmzynt.storm.util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;

public class OrderDetailActivity extends BaseActivity implements View.OnClickListener {

    public final static String TAG = "OrderDetailActivity";

    public final static String ORDER_WAIT_CHECK = "待审核";

    private final String[] mListText = {"作废", "删除", "编辑", "审核","取消"};

    private ImageView image_personal; //
    private TextView tv_order_num;
    private TextView tv_order_state;
    private TextView tv_delivery_time;
    private TextView tv_amout;
    private TextView tv_head_title;
    private RecyclerView order_goods_list;
    private Button btn_order_manager;

    private GoodsOrder mSimpleGoodsOrder;
    private LinearLayoutManager linearLayoutManager;
    private GoodsOrderLinesAdapter adapter ;
    private GoodsOrder goodsOrder ;

    private String mUUid = "";
    private ProgressDialog progressBar;
    private boolean isModify =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        initData();
        initViews();
        setProgressBar();
        if(mSimpleGoodsOrder != null){
            mUUid = mSimpleGoodsOrder.getUuid();
            loadData(isModify);

        }else {
            mUUid = getIntent().getExtras().getString("uuid");
            loadData(isModify);
        }
        initEvents();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (progressBar != null) {
            if (progressBar.isShowing()) {
                progressBar.dismiss();
            }

            progressBar = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(goodsOrder != null) {
            if(goodsOrder.getStatus().getCaption().equals("待审核")) {
                loadData(true);
            }else {
                loadData(false);
            }
        }
//        } else {
//            loadData(true);
//        }
    }

    private void initData(){
        mSimpleGoodsOrder = (GoodsOrder) getIntent().getExtras().getSerializable("order");
      isModify = getIntent().getExtras().getBoolean("modify");

    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);//标题
        tv_order_num = (TextView) findViewById(R.id.tv_order_num);
        tv_order_state = (TextView) findViewById(R.id.tv_order_state);
        tv_delivery_time = (TextView) findViewById(R.id.tv_delivery_time);
        tv_amout = (TextView) findViewById(R.id.tv_amout);
        order_goods_list = (RecyclerView) findViewById(R.id.order_goods_list);
        btn_order_manager = (Button) findViewById(R.id.btn_order_manager);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        order_goods_list.setLayoutManager(linearLayoutManager);
    }


    @Override
    protected void initEvents() {
        btn_order_manager.setOnClickListener(this);
        image_personal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                startActivity(MainActivity.class);
                finish();
                break;
            case R.id.btn_order_manager:
                if(goodsOrder != null){
                    switch (goodsOrder.getStatus().getCaption()){
                        case "待审核":
                            auditManager(v);
                            break;
                        case "待发货":
                            stockOut();
                            break;
                        case "待收货":
                            signAndRefund();
                            break;
                        default:
                            break;
                    }
                }
        }
    }


    private void setProgressBar() {
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("正在请求数据 ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void setData(){
        if(goodsOrder != null) {
            if(goodsOrder.getCustomer().getName() != null &&! goodsOrder.getCustomer().getName().equals("")){
                if( null != goodsOrder.getCustomerDepartment() &&null != goodsOrder.getCustomerDepartment().getName() && !TextUtils.isEmpty(goodsOrder.getCustomerDepartment().getName())){
                    tv_head_title.setText(goodsOrder.getCustomer().getName()+"【"+goodsOrder.getCustomerDepartment().getName()+"】");//设置标题
                }  else {
                    tv_head_title.setText(goodsOrder.getCustomer().getName());
                }
            }


            tv_order_num.setText(goodsOrder.getBillNumber());//订单编号
            tv_order_state.setText(goodsOrder.getStatus().getCaption());//订单状态
            tv_delivery_time.setText(DateUtils.DateToString(goodsOrder.getDeliveryTime(),"yyyy-MM-dd"));//交货时间
            tv_amout.setText("￥"+ BigDecimalUtil.convertToScale(goodsOrder.getTotalAmount(),2));//金额

                String status = goodsOrder.getStatus().getCaption();
                switch (status){
                    case "待审核":
                        btn_order_manager.setBackgroundColor(getResources().getColor(R.color.white));
                        btn_order_manager.setText("操作");
                        break;
                    case "待发货":
                        btn_order_manager.setBackgroundColor(getResources().getColor(R.color.text_color_line_normal));
                        btn_order_manager.setTextColor(getResources().getColor(R.color.white));
                        btn_order_manager.setText("出库");

                        break;
                    case "待收货":
                        btn_order_manager.setBackgroundColor(getResources().getColor(R.color.text_color_line_normal));
                        btn_order_manager.setTextColor(getResources().getColor(R.color.white));
                        btn_order_manager.setText("签收/退货");

                        break;
                    default:
                        btn_order_manager.setVisibility(View.GONE);
                        break;
            }
        }
    }

    /**
     * 获取订单详情
     */
    private void loadData(boolean modify){
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();

            try {
                reparams.put(BasicConstants.URL_BODY, body);
                body.put(SaleConstants.Field.UUID, mUUid);
                if(modify){//判断是否待审核状态
                    body.put(SaleConstants.Field.FOR_MODIFY, modify);
                }
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
        progressBar.show();


        HttpUtil.post(UrlConstants.URL_GOODS_ORDER, reparams.toString(), new HttpUtil.HttpListener() {
            @Override
            public void successResponse(String s, int i) {
                ResponseBean response = GsonUtil.getGson().fromJson(s,ResponseBean.class);
                if(response.getErrorCode() == 0){
                    if(response.getData() != null){
                        showLogDebug("order",response.getData());
                        goodsOrder = GsonUtil.getGson().fromJson(response.getData(),GoodsOrder.class);
                        adapter = new GoodsOrderLinesAdapter(goodsOrder.getLines(),getApplicationContext());
                        order_goods_list.setAdapter(adapter);
                        setData();


                    }
                }else {
                    if(response.getMessage() != null){
                        showCustomToast(response.getMessage());
                    }
                }
                if (progressBar != null) {
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                        progressBar.cancel();
                    }
                    // progressBar = null;
                }
            }

            @Override
            public void errorResponse(Call call, Exception e, int i) {
                if (progressBar != null) {
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                        progressBar.cancel();
                    }
                    // progressBar = null;
                }

                showCustomToast("请求失败");
            }
        });



        }


    /**
     * 作废订单,删除订单
     */
    private void cancelAndDeleteOrder(String url, final boolean isDelete){
        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        JSONObject body = new JSONObject();
        if(goodsOrder != null ){
            try {
                params.put(BasicConstants.URL_BODY, body);
                body.put(SaleConstants.Field.UUID, goodsOrder.getUuid());
                body.put(SaleConstants.Field.VERSION, goodsOrder.getVersion());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpUtils.postString().url(url)
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
                            ResponseBean response = GsonUtil.getGson().fromJson(s,ResponseBean.class);
                            if(response.getErrorCode() == 0){
                                if(isDelete){
                                    showCustomToast("删除成功");
                                    finish();
                                }else {
                                    loadData(false);
                                }

                            }else {
                                if(response.getMessage() != null){
                                    showCustomToast(response.getMessage());
                                }
                            }
                        }
                    });
        }
    }
    /**
     * 审核订单
     */

    private void auditOrder(){
        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        JSONObject body = new JSONObject();
        if(goodsOrder != null ){
            try {
                params.put(BasicConstants.URL_BODY, body);
                body.put(SaleConstants.Field.UUID, goodsOrder.getUuid());
                body.put(SaleConstants.Field.VERSION, goodsOrder.getVersion());

                OkHttpUtils.postString().url(UrlConstants.URL_ORDER_AUDIT)
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
                                ResponseBean response = GsonUtil.getGson().fromJson(s,ResponseBean.class);
                                if(response.getErrorCode() == 0){//审核成功
                                    loadData(false);

                                }else {
                                    if(response.getMessage() != null){
                                        showCustomToast(response.getMessage());
                                    }

                                }
                            }
                        });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 编辑订单
     */

    private void editOrder(){
        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        final JSONObject body = new JSONObject();
        if(goodsOrder != null ) {
            try {
                body.put(SaleConstants.Field.UUID, goodsOrder.getUuid());
              //  body.put(SaleConstants.Field.GOODS_ORDER, GsonUtil.getGson().toJson(goodsOrder));
                body.put(SaleConstants.Field.FOR_MODIFY, true);
                params.put(BasicConstants.URL_BODY, body);
                OkHttpUtils.postString().url(UrlConstants.URL_ORDER_EDIT)
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
                                ResponseBean response = GsonUtil.getGson().fromJson(s,ResponseBean.class);
                                if(response.getErrorCode() == 0){//编辑提交成功
                                   Bundle bundle = new Bundle();
                                    bundle.putSerializable("goodsOrder",goodsOrder);
                                    startActivity(EditOrderActivity.class,bundle);
                                   // finish();
                                }else {
                                    if(response.getMessage() != null){
                                        showCustomToast(response.getMessage());
                                    }

                                }
                            }
                        });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }
    /**
     * 待审核订单操作
     */

    private void auditManager(View v){
        new OrderManagerPopupWindow(this, new OrderManagerPopupWindow.ResultListener() {
            @Override
            public void onResultChanged(int index) {
                switch (mListText[index]){
                    case "作废":
                        showAlertDialog("作废订单", "确定作废订单？", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelAndDeleteOrder(UrlConstants.URL_ORDER_CANCEL,false);//作废，传入false,刷新页面
                            }
                        }, "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        break;
                    case "删除":
                        showAlertDialog("删除订单", "确定删除订单？", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelAndDeleteOrder(UrlConstants.URL_ORDER_DELETE,true);//删除，传入true,finish页面
                            }
                        }, "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        break;
                    case "编辑":
                        editOrder();
                        break;
                    case "审核":
                        auditOrder();
                        break;

                }
            }
        },v,mListText);
    }

    /**
     * 出库操作
     */
    private void stockOut(){
        Bundle bundle = new Bundle();
        bundle.putSerializable("goodsOrder",goodsOrder);
        startActivity(StockOutActivity.class,bundle);
    }

    /**
     * 签收退货
     */
    private void signAndRefund(){
        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        final JSONObject body = new JSONObject();
        if(goodsOrder != null) {
            try {
                body.put(CTMSConstants.Field.SOURCE_BILL_NUMBER, goodsOrder.getBillNumber());
                params.put(BasicConstants.URL_BODY, body);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpUtil.post(UrlConstants.URL_TRANSPORT, params.toString(), new HttpUtil.HttpListener() {
                @Override
                public void successResponse(String s, int i) {
                    ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                    if (response.getErrorCode() == 0) {
                        Type listType = new TypeToken<List<TransportBill>>() {
                        }.getType();
                        if (response.getData() != null) {
                            List<TransportBill> transportBills = GsonUtil.getGson().fromJson(response.getData(), listType);
                            Bundle bundle = new Bundle();
                            if (transportBills != null && transportBills.size() > 1) {
                                bundle.putString("transportBills", response.getData());
                                startActivity(DeliveryListActivity.class, bundle);
                            } else if (transportBills != null && transportBills.size() == 1) {
                                bundle.putSerializable("transportBill", transportBills.get(0));
                                startActivity(SignAndReturnActivity.class, bundle);
                            }
                        }
                    } else {
                        if (response.getMessage() != null) {
                            showCustomToast(response.getMessage());
                        }

                    }
                }

                @Override
                public void errorResponse(Call call, Exception e, int i) {
                    showCustomToast("请求失败");
                }
            });

        }
    }

    public void requestData(String url, String params, final int type) {
        HttpUtil.post(url, params, new HttpUtil.HttpListener() {
            @Override
            public void successResponse(String s, int i) {

            }

            @Override
            public void errorResponse(Call call, Exception e, int i) {

                switch (type) {
                    case 0:

                }
            }
        });

    }
}
