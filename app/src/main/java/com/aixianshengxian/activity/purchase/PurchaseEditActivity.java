package com.aixianshengxian.activity.purchase;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.activity.addOrder.NewAddGoodsActivity;
import com.aixianshengxian.activity.plan.PlanActivity;
import com.aixianshengxian.activity.search.SearchOperatorActivity;
import com.aixianshengxian.activity.search.SearchProviderActivity;
import com.aixianshengxian.adapters.PurchaseDetailAdapter;
import com.aixianshengxian.adapters.PurchaseEditAdapter;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.http.HttpManager;
import com.aixianshengxian.module.GoodsItem;
import com.aixianshengxian.util.DateUtils;
import com.aixianshengxian.util.DatesUtils;
import com.aixianshengxian.util.PurchaseBillUtil;
import com.aixianshengxian.util.SessionUtils;
import com.google.gson.reflect.TypeToken;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.idname.IdName;
import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.service.goods.UnitPrice;
import com.xmzynt.storm.service.purchase.bill.PurchaseBill;
import com.xmzynt.storm.service.purchase.bill.PurchaseBillLine;
import com.xmzynt.storm.service.user.supplier.Supplier;
import com.xmzynt.storm.util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;


import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class PurchaseEditActivity extends BaseActivity implements View.OnClickListener{
    public final static String TAG = "PurchaseEditActivity";

    private TextView tv_head_title;
    private TextView tv_provider;
    private TextView tv_delivery_time;
    private TextView tv_operator;
    private EditText tv_car;
    private EditText tv_driver;
    private EditText tv_remark;
    private Button saveButton;
    private ImageView iv_add_new,iv_back;
    private RecyclerView product_listview;
    private PurchaseEditAdapter mAdapter;
    private LinearLayoutManager layoutManager;

    private PurchaseBill mPurchaseBill;
    private List<PurchaseBillLine> mData = new ArrayList<>();
    private List<String> mGoodsUuid = new ArrayList<>();
    private Map<String,List<UnitPrice>> mMapUnits;
    private List<UnitPrice> mUnit = new ArrayList<>();
    private ArrayList<String> mUnits = new ArrayList<String>();

    private final int SEARCH_OPERATOR_REQUEST = 11;
    private final int SEARCH_OPERATOR_RESULT = 12;

    private IdName operator = null;
    private List<IdName> operators = new ArrayList<IdName>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_edit);

        Intent intent = getIntent();
        mPurchaseBill = (PurchaseBill) intent.getSerializableExtra("purchaseBill");
        mData = mPurchaseBill.getLines();

        initViews();
        initEvents();
        initData();
    }


    @Override
    protected void initViews() {
        iv_back = (ImageView) findViewById(R.id.image_personal);
        tv_provider = (TextView) findViewById(R.id.tv_provider);
        tv_delivery_time = (TextView) findViewById(R.id.tv_delivery_time);
        DateUtils.initDatePick(this,tv_delivery_time,false,null);
        tv_operator = (TextView) findViewById(R.id.tv_operator);
        tv_car = (EditText) findViewById(R.id.tv_car);
        tv_driver = (EditText) findViewById(R.id.tv_driver);
        tv_remark = (EditText) findViewById(R.id.tv_remark);
        saveButton = (Button)findViewById(R.id.btn_operate);
        iv_add_new = (ImageView) findViewById(R.id.image_add_new);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("采购单编辑");
        product_listview = (RecyclerView) findViewById(R.id.product_listview);
        layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        product_listview.setLayoutManager(layoutManager);
    }

    @Override
    protected void initEvents() {
//        tv_operate_time.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        iv_add_new.setOnClickListener(this);
        tv_provider.setOnClickListener(this);
        tv_operator.setOnClickListener(this);
    }

    private void initData(){
        mAdapter = new PurchaseEditAdapter(mData,this);
        mAdapter.setOnItemListener(new PurchaseEditAdapter.OnItemClickListener() {
            @Override
            public void onUnitClick(int position, View v) {
                dropDownUnit(position);
            }
        });
        product_listview.setAdapter(mAdapter);
        tv_provider.setText(mPurchaseBill.getSupplier().getName());
        tv_delivery_time.setText(DatesUtils.dateToStr(mPurchaseBill.getDeliveryTime()));
        if(mPurchaseBill.getOperator() != null){
            tv_operator.setText(mPurchaseBill.getOperator().getName());//经办人
        }else{
        }
        if(mPurchaseBill.getVehicle() != null) {
            tv_car.setText(mPurchaseBill.getVehicle());//车辆
        }
        if(mPurchaseBill.getDriver() !=null) {
            tv_driver.setText(mPurchaseBill.getDriver());
        }
        if(mPurchaseBill.getRemark() != null) {
            tv_remark.setText(mPurchaseBill.getRemark());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_personal:
                finish();
                break;
            case R.id.btn_operate:
                savePurchase();
                break;
            case R.id.image_add_new:

                Bundle bundle = new Bundle();
                bundle.putInt("Add_new",DataConstant.BUNDLE_ADD_GOODS_PURCHASE_EDIT);
                startActivityforResult(NewAddGoodsActivity.class,bundle,DataConstant.BUNDLE_REQUEST_ADD_GOODS_PURCHASE_EDIT);
                break;
            case R.id.tv_provider:
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("Provider_which",0);
                startActivityforResult(SearchProviderActivity.class,bundle1,DataConstant.BUNDLE_REQUEST_PROVIDER_PURCHASE_EDIT);
            case R.id.tv_operator:
                Intent intent3 = new Intent(PurchaseEditActivity.this, SearchOperatorActivity.class);
                startActivityForResult(intent3,SEARCH_OPERATOR_REQUEST);
                //setOperatorAlertDialog();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DataConstant.BUNDLE_REQUEST_ADD_GOODS_PURCHASE_EDIT){
            if(data !=null && data.getExtras()!=null){
                Map<String,GoodsItem> goodsItemMap =  (Map<String,GoodsItem>)data.getExtras().getSerializable("goods");
                //转化成Purchase
                List<PurchaseBillLine> purchaseBillLineList = PurchaseBillUtil.toPurchaseBillLine(goodsItemMap);
                mAdapter.addData(purchaseBillLineList);

            }
        }
        else if(requestCode == DataConstant.BUNDLE_REQUEST_PROVIDER_PURCHASE_EDIT){
            if(resultCode == DataConstant.SEARCH_PROVIDER_RESULT){
                Supplier supplier = (Supplier) data.getExtras().getSerializable("supplier");
                if(supplier != null){
                    tv_provider.setText(supplier.getName());
                    IdName idName = new IdName(supplier.getUuid(),supplier.getName());
                    mPurchaseBill.setSupplier(idName);
                }
            }
        }

        else if(requestCode == SEARCH_OPERATOR_REQUEST){
            if(resultCode == SEARCH_OPERATOR_RESULT){
                IdName Operator = (IdName) data.getExtras().getSerializable("operator");
                if(Operator != null){
                    showLogDebug(TAG,"onActivityDepotResult");
                    tv_operator.setText(Operator.getName());
                    operator = Operator;
                }
            }
        }
    }

    private void savePurchase(){
        mPurchaseBill.setDeliveryTime(DateUtils.StringToDate(tv_delivery_time.getText().toString(), DateUtils.formatYMD));
        mPurchaseBill.setDriver(tv_driver.getText().toString());
        mPurchaseBill.getSupplier().setName(tv_provider.getText().toString());
        //if( mPurchaseBill.getOperator()!= null){
            if (operator != null) {
                mPurchaseBill.setOperator(operator);
            }
            //mPurchaseBill.getOperator().setName(tv_operator.getText().toString());
        //}
        mPurchaseBill.setVehicle(tv_car.getText().toString());
        mPurchaseBill.setRemark(tv_remark.getText().toString());
        mPurchaseBill.setLines(mAdapter.getPurchaseDatas());
        HttpManager.savePurchases(this, mPurchaseBill, new onSavePurchaseInterface() {
            @Override
            public void onError(String message) {

                showCustomToast("保存失败");

                showCustomToast(message);

            }

            @Override
            public void onSavePurchaseSuccess(String data) {
                showCustomToast("保存成功");
                Bundle bundle = new Bundle();
                bundle.putSerializable("purchaseBill",mPurchaseBill);
                startActivity(PurchaseActivity.class,bundle);
                finish();
            }
        });
    }

    //获取单位
    private void dropDownUnit(final int position) {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        for (int i = 0;i < mData.size();i ++) {
            String goodsUnit = mData.get(i).getGoods().getUuid();
            mGoodsUuid.add(goodsUnit);
        }
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            try {
                body.put(DataConstant.GOODS_UUID, GsonUtil.getGson().toJson(mGoodsUuid));
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
        OkHttpUtils.postString().url(UrlConstants.URL_DROP_DOWN_UNIT_PRICE)
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
                            Type listTypeA = new TypeToken<Map<String,List<UnitPrice>>>() {
                            }.getType();
                            if (response.getData() != null) {
                                mMapUnits = GsonUtil.getGson().fromJson(response.getData(), listTypeA);
                            }
                            showCustomToast("请求成功");
                            showLogDebug("main", s);
                            setUnitsAlertDialog(position);
                        } else {
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }

    //单位
    private void setUnitsAlertDialog(final int position) {
        String GoodsUuid = mData.get(position).getGoods().getUuid();
        mUnit = mMapUnits.get(GoodsUuid);
        mUnits.clear();
        for (int i = 0;i < mUnit.size();i ++) {
            mUnits.add(mUnit.get(i).getUnit().getName());//ArrayList可以根据长度变化
        }
        int size = mUnits.size();//获取长度
        final String[] items = new String[size];
        for (int i = 0;i < size;i ++) {//把ArrayList转换成String[]数组
            items[i] = mUnits.get(i);
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(PurchaseEditActivity.this);
        builder.setTitle("请选择单位");
        builder.setCancelable(true);

        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mData.get(position).setGoodsUnit(mUnit.get(which).getUnit());
            }
        });
        //添加确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                mAdapter.notifyDataSetChanged();//应该在点确定之后再更新界面
            }
        });
        builder.create().show();
    }

    public interface onSavePurchaseInterface{
        void onError(String message);

        void onSavePurchaseSuccess(String data);
    }

}
