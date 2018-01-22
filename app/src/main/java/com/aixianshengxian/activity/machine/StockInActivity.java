package com.aixianshengxian.activity.machine;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.activity.addOrder.NewAddGoodsActivity;
import com.aixianshengxian.activity.search.SearchDepotActivity;
import com.aixianshengxian.activity.search.SearchOperatorActivity;
import com.aixianshengxian.adapters.ForecastProcessPlanItemAdapter;
import com.aixianshengxian.adapters.StockOutRecordConsumeAdapter;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.module.ForecastProcessPlanItem;
import com.aixianshengxian.module.GoodsItem;
import com.aixianshengxian.module.StockOutRecordConsume;
import com.aixianshengxian.module.StockOutRecordItem;
import com.aixianshengxian.util.DatesUtils;
import com.aixianshengxian.util.PurchaseBillUtil;
import com.aixianshengxian.util.SessionUtils;
import com.aixianshengxian.view.RefreshRecyclerView;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.idname.IdName;
import com.xmzynt.storm.basic.operateinfo.OperateInfo;
import com.xmzynt.storm.basic.ucn.UCN;
import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.service.process.ForecastProcessPlan;
import com.xmzynt.storm.service.wms.MaterialsTree;
import com.xmzynt.storm.service.wms.stockin.StockInRecord;
import com.xmzynt.storm.service.wms.stockin.StockInType;
import com.xmzynt.storm.service.wms.stockout.StockOutRecord;
import com.xmzynt.storm.service.wms.warehouse.Warehouse;
import com.xmzynt.storm.util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class StockInActivity extends BaseActivity implements View.OnClickListener {
    public final static String TAG = "StockInActivity";
    private final int SEARCH_DEPOT_REQUEST = 9;
    private final int SEARCH_DEPOT_RESULT = 10;
    private final int BINDING_REQUEST = 5;
    private final int BINDING_RESULT = 6;
    private final int SEARCH_OPERATOR_REQUEST = 11;
    private final int SEARCH_OPERATOR_RESULT = 12;

    private ImageView image_personal;
    private TextView tv_head_title,tv_depot,tv_operator,tv_add_stock_in,tv_add_receive;
    private Button btn_next;

    //private List<ProductStockIn> mDataStockIn = new ArrayList<>();
    //private List<ProductStockIn> mDataWholeStockIn;
    private List<ForecastProcessPlanItem> mForecastProcessPlanItems = new ArrayList<>();
    private List<StockOutRecordConsume> mStockOutRecordConsumes = new ArrayList<>();
    private List<StockInRecord> mStockInRecords = new ArrayList<>();//要保存的对象

    //private List<ProductReceive> mDataReceive = new ArrayList<>();
    //private List<ProductReceive> mDataWholeReceive;

    private List<String> mDataUcode = new ArrayList<>();

    private ForecastProcessPlan mForecastProcessPlan;//上个界面传来的东西

    private RefreshRecyclerView listview_stock_in;
    //private SwipeRefreshLayout swipe_refresh_widget;
    private ForecastProcessPlanItemAdapter mAdapterStockIn;
    private LinearLayoutManager layoutManager1;

    private RefreshRecyclerView listview_receive;
    //private SwipeRefreshLayout swipe_refresh_widget;
    private StockOutRecordConsumeAdapter mAdapterReceive;
    private LinearLayoutManager layoutManager2;

    private UCN wareHouse = null;
    private IdName operator = null;
    private List<MaterialsTree> materialsTrees = new ArrayList<>();

    //private final String[] mOperator = {"黄飞鸿","许仙","霍元甲","小明"};
    //private String Operator = "加工员";


    private List<IdName> operators = new ArrayList<IdName>();

    public static StockInActivity mactivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_in);

        getMessage();
        initViews();
        initEvents();
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("加工入库");

        tv_depot = (TextView) findViewById(R.id.tv_depot);//仓库
        tv_operator = (TextView) findViewById(R.id.tv_operator);//经办人
        tv_add_stock_in = (TextView) findViewById(R.id.tv_add_stock_in);//入库商品
        tv_add_receive = (TextView) findViewById(R.id.tv_add_receive);//领料记录

        //mDataWholeStockIn = new ArrayList<>();
        //mDataWholeReceive = new ArrayList<>();

        /*swipe_refresh_widget = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);*/
        listview_stock_in = (RefreshRecyclerView) findViewById(R.id.listview_stock_in);
        layoutManager1=new LinearLayoutManager(this);
        layoutManager1.setOrientation(OrientationHelper.VERTICAL);
        listview_stock_in.setLayoutManager(layoutManager1);
        mAdapterStockIn = new ForecastProcessPlanItemAdapter(mForecastProcessPlanItems,this);//将数据传入
        mAdapterStockIn.setOnItemListener(new ForecastProcessPlanItemAdapter.OnItemClickListener() {

            @Override
            public void onDeleteClick(int position) {
                setDeleteStockInAlertDialog(position);
            }

            @Override
            public void onItemClick(ForecastProcessPlanItem productstockin, int position) {
                Intent intent = new Intent(StockInActivity.this,BindingBasketActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("ProductStockIn", (Serializable) productstockin);
                bundle.putSerializable("Position",position);
                intent.putExtras(bundle);
                startActivityForResult(intent,BINDING_REQUEST);
            }
        });
        listview_stock_in.setAdapter(mAdapterStockIn);

        listview_receive = (RefreshRecyclerView) findViewById(R.id.listview_receive);
        layoutManager2=new LinearLayoutManager(this);
        layoutManager2.setOrientation(OrientationHelper.VERTICAL);
        listview_receive.setLayoutManager(layoutManager2);
        mAdapterReceive = new StockOutRecordConsumeAdapter(mStockOutRecordConsumes,this);//将数据传入
        mAdapterReceive.setOnItemListener(new StockOutRecordConsumeAdapter.OnItemClickListener() {

            @Override
            public void onDeleteClick(int position) {
                setDeleteReceiveAlertDialog(position);
            }
        });
        listview_receive.setAdapter(mAdapterReceive);

        btn_next = (Button) findViewById(R.id.btn_next);

        mactivity=this;

    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        tv_depot.setOnClickListener(this);
        tv_operator.setOnClickListener(this);
        tv_add_stock_in.setOnClickListener(this);
        tv_add_receive.setOnClickListener(this);
        btn_next.setOnClickListener(this);
    }

    //获取ForecastProcessPlan，上一个界面传来的加工计划
    private void getMessage() {
        Intent intent = getIntent();
        mForecastProcessPlan = (ForecastProcessPlan) intent.getSerializableExtra("forecastProcessPlan");
        ForecastProcessPlanItem forecastProcessPlanItem = new ForecastProcessPlanItem();
        forecastProcessPlanItem.setForecastProcessPlan(mForecastProcessPlan);
        forecastProcessPlanItem.setStockInNum(mForecastProcessPlan.getPlanQty());
        forecastProcessPlanItem.setPrice(BigDecimal.ZERO);
        forecastProcessPlanItem.setPrice(BigDecimal.ZERO);
        forecastProcessPlanItem.setDay(0);
        mForecastProcessPlanItems.add(forecastProcessPlanItem);
        //mAdapterStockIn.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                finish();
                break;
            case R.id.tv_depot:
                Intent intent1 = new Intent(StockInActivity.this, SearchDepotActivity.class);
                startActivityForResult(intent1,SEARCH_DEPOT_REQUEST);
                break;
            case R.id.tv_operator:
                Intent intent2 = new Intent(StockInActivity.this, SearchOperatorActivity.class);
                startActivityForResult(intent2,SEARCH_OPERATOR_REQUEST);
                break;
            case R.id.tv_add_stock_in:
                Bundle bundle1 = new Bundle();
                bundle1.putInt("Add_new", DataConstant.BUNDLE_ADD_GOODS_FORECAST_PROCESS_PLAN_ITEM);
                bundle1.putString("type", "processed");
                startActivityforResult(NewAddGoodsActivity.class,bundle1,DataConstant.BUNDLE_REQUEST_FORECAST_PROCESS_PLAN_ITEM);
                break;
            case R.id.tv_add_receive:
                Bundle bundle2 = new Bundle();
                bundle2.putInt("Add_stockOutRecord", DataConstant.BUNDLE_ADD_STOCK_OUT_RECORD_CONSUME);
                startActivityforResult(SelectReceiveActivity.class,bundle2,DataConstant.BUNDLE_REQUEST_STOCK_OUT_RECORD_CONSUME);
                break;
            case R.id.btn_next:
                check();
                break;
            default:
                break;
        }
    }

    //仓库，操作员，绑定周转筐
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SEARCH_DEPOT_REQUEST){
            if(resultCode == SEARCH_DEPOT_RESULT){
                Warehouse warehouse = (Warehouse) data.getExtras().getSerializable("depot");
                if(warehouse  != null){
                    UCN ucn = new UCN(warehouse.getUuid(),warehouse.getCode(),warehouse.getName());
                    wareHouse = ucn;
                    tv_depot.setText(warehouse.getName());
                }
            }
        }

        else if(requestCode == BINDING_REQUEST){
            if(resultCode == BINDING_RESULT){
                mDataUcode = (List<String>) data.getExtras().getSerializable("Binding");
                //ProductStockIn productstockin = (ProductStockIn) data.getExtras().getSerializable("ProductStockIn");
                int position = (int) data.getExtras().getSerializable("Position");
                mForecastProcessPlanItems.get(position).setBasketCodes(mDataUcode);
            }
        }

        else if(requestCode == SEARCH_OPERATOR_REQUEST){
            if(resultCode == SEARCH_OPERATOR_RESULT){
                IdName Operator = (IdName) data.getExtras().getSerializable("operator");
                if(Operator != null){
                    tv_operator.setText(Operator.getName());
                    operator = Operator;
                }
            }
        }

        else if(requestCode == DataConstant.BUNDLE_REQUEST_FORECAST_PROCESS_PLAN_ITEM){
            if(resultCode == DataConstant.ADD_NEW_GOOD_FOR_FORECAST_PROCESS_PLAN_ITEM){
                if(data !=null && data.getExtras()!=null){
                    Map<String,GoodsItem> goodsItemMap =  (Map<String,GoodsItem>)data.getExtras().getSerializable("goods");
                    //转化成ForecastProcessPlanItem
                    List<ForecastProcessPlanItem> forecastProcessPlanItems = PurchaseBillUtil.toForecastProcessPlanItemList(goodsItemMap);
                    mAdapterStockIn.addMoreItem(forecastProcessPlanItems);
                    listview_stock_in.notifyNewData();
                    mForecastProcessPlanItems = mAdapterStockIn.getData();
                }
            }
        }

        else if(requestCode == DataConstant.BUNDLE_REQUEST_STOCK_OUT_RECORD_CONSUME){
            if(resultCode == DataConstant.ADD_NEW_STOCK_OUT_RECORD_CONSUME){
                if(data !=null && data.getExtras()!=null){
                    Map<String,StockOutRecordItem> stockOutRecordItemMap =  (Map<String,StockOutRecordItem>)data.getExtras().getSerializable("stockOutRecord");
                    //转化成StockOutRecordConsume
                    List<StockOutRecordConsume> stockOutRecordConsumes = PurchaseBillUtil.toStockOutRecordItemList(stockOutRecordItemMap);
                    mAdapterReceive.addMoreItem(stockOutRecordConsumes);
                    listview_receive.notifyNewData();
                    mStockOutRecordConsumes = mAdapterReceive.getData();
                }
            }
        }
    }

    //删除提示对话框
    public void setDeleteStockInAlertDialog(final int position){
        final AlertDialog alert = new AlertDialog.Builder(StockInActivity.this).create();
        alert.setTitle("提示信息");
        //title.setTextColor(getResources().getColor(R.color.black));//标题文字颜色
        alert.setMessage("确认删除该入库商品\n" + mForecastProcessPlanItems.get(position).getForecastProcessPlan().getGoods().getName() +
                mForecastProcessPlanItems.get(position).getStockInNum() + mForecastProcessPlanItems.get(position).getForecastProcessPlan().getGoodsUnit().getName() + "?");
        //message.setTextColor(getResources().getColor(R.color.text_color_gray_2));
        alert.setCancelable(false);
        //添加确定按钮
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mForecastProcessPlanItems.remove(position);
                mAdapterStockIn.notifyDataSetChanged();
                listview_stock_in.notifyNewData();
            }
        });
        //添加取消按钮
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alert.dismiss();
            }
        });
        alert.show();
        //确定按钮字的颜色
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.green_text));
        //取消按钮字的颜色
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.gray_text2));
    }

    public void setDeleteReceiveAlertDialog(final int position){
        final AlertDialog alert = new AlertDialog.Builder(StockInActivity.this).create();
        alert.setTitle("提示信息");
        //title.setTextColor(getResources().getColor(R.color.black));//标题文字颜色
        alert.setMessage("确认删除该商品\n" + mStockOutRecordConsumes.get(position).getStockOutRecord().getGoods().getName() + "?");
        //message.setTextColor(getResources().getColor(R.color.text_color_gray_2));
        alert.setCancelable(false);
        //添加确定按钮
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mStockOutRecordConsumes.remove(position);
                mAdapterReceive.notifyDataSetChanged();
                listview_receive.notifyNewData();
            }
        });
        //添加取消按钮
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alert.dismiss();
            }
        });
        alert.show();
        //确定按钮字的颜色
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.green_text));
        //取消按钮字的颜色
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.gray_text2));
    }

    //检查信息
    public void check() {
        int count = 0;
        for (int i = 0;i < mForecastProcessPlanItems.size();i ++) {
            ForecastProcessPlanItem forecastProcessPlanItem = mForecastProcessPlanItems.get(i);
            /*if (forecastProcessPlanItem.getBasketCodes() != null) {*/
                if ((forecastProcessPlanItem.getStockInNum()).compareTo(BigDecimal.ZERO) > 0
                        && (forecastProcessPlanItem.getPrice()).compareTo(BigDecimal.ZERO) > 0
                        && forecastProcessPlanItem.getDay() > 0) {
                    count = count + 0;
                } else {
                    count ++;
                }
            /*} else {
                String num = forecastProcessPlanItem.getStockInNum() + forecastProcessPlanItem.getForecastProcessPlan().getGoodsUnit().getName();
                String goodsName = forecastProcessPlanItem.getForecastProcessPlan().getGoods().getName();
                showCustomToast("请检查" + num + goodsName + "的周转筐信息！");
            }*/
        }

        int amount = 0;
        for (int j = 0;j < mStockOutRecordConsumes.size();j ++) {
            if ((mStockOutRecordConsumes.get(j).getConsume()).compareTo(BigDecimal.ZERO) > 0) {
                amount  = amount + 0;
            } else {
                amount ++;
            }
        }

        if (amount == 0) {
            if (count == 0) {
                if (tv_depot.getText().equals("请选择仓库")) {
                    showCustomToast("请检查仓库的信息");
                } else {
                    batchStockIn();
                }
            } else {
                showCustomToast("请检查入库商品处信息");
            }
        } else {
            showCustomToast("请检查领料记录处信息");
        }
    }

    //原料树构建
    public List<MaterialsTree> getMaterialTrees() {
        for (int i = 0;i < mStockOutRecordConsumes.size();i ++) {
            materialsTrees.clear();
            StockOutRecordConsume stockOutRecordConsume = mStockOutRecordConsumes.get(i);
            StockOutRecord stockOutRecord = mStockOutRecordConsumes.get(i).getStockOutRecord();
            MaterialsTree materialsTree = new MaterialsTree();
            materialsTree.setTraceCode(stockOutRecord.getPlatformTraceCode());//TraceCode
            materialsTree.setBatchCode(stockOutRecord.getPlatformBatchCode());//BatchCode
            materialsTree.setMaterial(stockOutRecord.getGoods());//Material
            materialsTree.setMaterials(stockOutRecord.getMaterials());//Materials
            materialsTree.setMaterialQty(stockOutRecordConsume.getConsume());//MaterialQty(
            materialsTrees.add(materialsTree);
        }
        return materialsTrees;
    }

    //传递加工入库数据
    public void batchStockIn() {
        for (int i = 0;i < mForecastProcessPlanItems.size();i ++) {
            ForecastProcessPlanItem forecastProcessPlanItem = mForecastProcessPlanItems.get(i);
            ForecastProcessPlan forecastProcessPlan = mForecastProcessPlanItems.get(i).getForecastProcessPlan();
            StockInRecord stockInRecord = new StockInRecord();
            stockInRecord.setOrg(forecastProcessPlan.getOrg());//Org
            stockInRecord.setSupplier(forecastProcessPlan.getOrg());//Supplier
            stockInRecord.setStockInType(StockInType.finishedIn);//StockInType
            stockInRecord.setWarehouse(wareHouse);//WareHouse
            stockInRecord.setGoods(forecastProcessPlan.getGoods());//Goods
            stockInRecord.setGoodsSpec(forecastProcessPlan.getGoodsSpec());//GoodsSpec
            stockInRecord.setGoodsUnit(forecastProcessPlan.getGoodsUnit());//GoodsUnit
            stockInRecord.setSourceBillLineUuid(forecastProcessPlan.getUuid());//SourceBillLineUuid
            stockInRecord.setQuantity(forecastProcessPlanItem.getStockInNum());//Quantity
            stockInRecord.setPrice(forecastProcessPlanItem.getPrice());//Price
            stockInRecord.setInDate(new Date());//InDate
            stockInRecord.setDeliveryTime(new Date());//DeliveryTime
            stockInRecord.setOperatorName(SessionUtils.getInstance(getApplicationContext()).getLoginPhone());//OperatorName

            if (operator != null) {
                OperateInfo operateInfo = new OperateInfo();
                operateInfo.setOperateTime(new Date());
                operateInfo.setOperator(operator);
                stockInRecord.setCreatorInfo(operateInfo);//CreatorInfo
            }

            stockInRecord.setProduceDate(new Date());//ProduceDate
            stockInRecord.setEffectiveDate(DatesUtils.strToDate(DatesUtils.getStringDateShortFromToday(forecastProcessPlanItem.getDay())));//EffectiveDate
            stockInRecord.setBasketCodes(forecastProcessPlanItem.getBasketCodes());//BasketCodes
            stockInRecord.setMaterials(getMaterialTrees());//Materials
            mStockInRecords.add(stockInRecord);
        }
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            try {
                body.put(DataConstant.STOCKINRECORDS, mStockInRecords);
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
        OkHttpUtils.postString().url(UrlConstants.URL_BATCH_STOCK_IN)
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
                            showLogDebug("main", s);
                            finish();
                        }  else {
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }

}
