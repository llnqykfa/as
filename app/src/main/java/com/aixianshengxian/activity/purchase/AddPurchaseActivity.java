package com.aixianshengxian.activity.purchase;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.DialogPreference;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.activity.addOrder.NewAddGoodsActivity;
import com.aixianshengxian.activity.search.SearchDepotActivity;
import com.aixianshengxian.activity.search.SearchOperatorActivity;
import com.aixianshengxian.activity.search.SearchProviderActivity;
import com.aixianshengxian.activity.plan.AddPlanActivity;
import com.aixianshengxian.adapters.AddPurchaseDetailAdapter;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.db.MyDatabaseHelper;
import com.aixianshengxian.http.HttpManager;
import com.aixianshengxian.listener.GetOperatorResultInterface;
import com.aixianshengxian.listener.UnitInterface;
import com.aixianshengxian.module.AddPurchaseDetail;
import com.aixianshengxian.module.GoodsItem;
import com.aixianshengxian.popupwindows.OrderManagerPopupWindow;
import com.aixianshengxian.util.DateUtils;
import com.aixianshengxian.util.DatesUtils;
import com.aixianshengxian.util.PurchaseBillUtil;
import com.aixianshengxian.util.SessionUtils;
import com.xmzynt.storm.basic.idname.IdName;
import com.xmzynt.storm.basic.ucn.UCN;
import com.xmzynt.storm.service.goods.UnitPrice;
import com.xmzynt.storm.service.purchase.bill.PurchaseBill;
import com.xmzynt.storm.service.purchase.bill.PurchaseBillLine;
import com.xmzynt.storm.service.purchase.bill.PurchaseType;
import com.xmzynt.storm.service.purchase.plan.ForecastPurchase;
import com.xmzynt.storm.service.user.supplier.Supplier;
import com.xmzynt.storm.service.wms.warehouse.Warehouse;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class AddPurchaseActivity extends BaseActivity implements View.OnClickListener {
    public final static String TAG = "AddPurchaseActivity";
    private final int SEARCH_PROVIDER_REQUEST = 7;
    private final int SEARCH_PROVIDER_RESULT = 8;
    private final int SEARCH_DEPOT_REQUEST = 9;
    private final int SEARCH_DEPOT_RESULT = 10;
    private final int SEARCH_OPERATOR_REQUEST = 11;
    private final int SEARCH_OPERATOR_RESULT = 12;
    private  final int ADD_GOODS_RESUTL =3;

    private String mParam1;

    private ImageView image_personal,iv_add_new,iv_narrow;
    private TextView tv_head_title,tv_provider,tv_delivery_time,tv_operator,tv_depot;
    private TextView tv_unit;
    private EditText edt_car,edt_driver,edt_remark;
    private Button btn_save;

//    private List<AddPurchaseDetail> mData = new ArrayList<>();
//    private List<AddPurchaseDetail> mDataWhole;
    private List<ForecastPurchase> mForecastPurchase = new ArrayList<>();
    private List<PurchaseBillLine> mPurchaseBillLine = new ArrayList<>();
    private PurchaseBill purchaseBill = new PurchaseBill();

//    private List<String> mOperate = new ArrayList<>();

//    private final String[] mOperator = {"黄飞鸿","许仙","霍元甲","小明"};
//    private final String Operator = ("经办人");
    private IdName operator = null;
    private List<IdName> operators = new ArrayList<IdName>();

    private RecyclerView product_listview;
    //private SwipeRefreshLayout swipe_refresh_widget;
    private AddPurchaseDetailAdapter mAdapter;
    private LinearLayoutManager layoutManager;

    public static AddPurchaseActivity mactivity;

    private int PROVIDER_WHICH = 0;//用于判断是哪个供应商选择
    private String whichActivity = null;
    private int narrowCount = 0;//记录添加按钮是否显示
    private PurchaseType Type = null;

    //时间选择器
    int mYear, mMonth, mDay;
    final int DATE_DIALOG = 1;

//    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_purchase);
//
//        //数据库
//        dbHelper = new MyDatabaseHelper(this,"ASData.db",null,1000);//检测是否有数据库
//        dbHelper.getWritableDatabase();//无，则创建

        getPurchase();
//        initAddPurchase();
        initViews();
        initEvents();
        initData();
    }

    public void getPurchase() {
        Intent intent = getIntent();
        //因为不同activity传过来的对象要转成PurchaseBillLine的过程可能不一样
        if(intent == null || intent.getExtras()==null){
            return;
        }
        whichActivity = (String) intent.getSerializableExtra("activity");
        if (whichActivity.equals("PlanActivity")) {
            mForecastPurchase = (List<ForecastPurchase>) intent.getSerializableExtra("ForecastPurchase");
            mPurchaseBillLine = PurchaseBillUtil.toPurchaseBillLineList(mForecastPurchase);
            Type = PurchaseType.forecast;
        }
//        else if (whichActivity.equals("NewAddGoodsActivity")) {
//                newAddGoodsActivity();
//            }
    }


    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("采购单新增");
        iv_add_new = (ImageView) findViewById(R.id.image_add_new);
        iv_narrow = (ImageView) findViewById(R.id.iv_narrow);

        tv_provider = (TextView) findViewById(R.id.tv_provider);//供应商
        tv_delivery_time = (TextView) findViewById(R.id.tv_delivery_time);
        tv_delivery_time.setText(DateUtils.getSomeDate(0)); //日期
        tv_depot = (TextView) findViewById(R.id.tv_depot);//仓库
        tv_operator = (TextView) findViewById(R.id.tv_operator);//经办人


        edt_car = (EditText) findViewById(R.id.edt_car);
        edt_driver = (EditText) findViewById(R.id.edt_driver);
        edt_remark = (EditText) findViewById(R.id.edt_remark);
        btn_save = (Button) findViewById(R.id.btn_save);

        product_listview = (RecyclerView) findViewById(R.id.product_listview);
        layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        product_listview.setLayoutManager(layoutManager);
        mAdapter = new AddPurchaseDetailAdapter(mPurchaseBillLine,this);//将数据传入
        mAdapter.setOnItemListener(new AddPurchaseDetailAdapter.OnItemClickListener(){

            @Override
            public void onDeleteClick(int position) {
                setDeleteAlertDialog(position);
            }

            @Override
            public void onUnitClick(int position,View v) {
                tv_unit = (TextView) v.findViewById(R.id.tv_unit);
                unitAudit(v,position);
            }
        });
        product_listview.setAdapter(mAdapter);

        //时间选择器设置
        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);

        mactivity=this;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG:
                return new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay);
        }
        return null;
    }

    /**
     * 设置日期 利用StringBuffer追加
     */
    public void display() {
        tv_delivery_time.setText(new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
        purchaseBill.setDeliveryTime(DateUtils.StringToDate(tv_delivery_time.getText().toString(), DateUtils.formatYMDHMS));
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            display();
        }
    };

    private void initData(){
        if(mForecastPurchase!=null && !mForecastPurchase.isEmpty()){
            IdName supplier = mForecastPurchase.get(0).getSupplier();
            if(supplier!=null){
                tv_provider.setText(supplier.getName());
                purchaseBill.setSupplier(supplier);
            }
        }
        //getDropdownOperator();
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        iv_add_new.setOnClickListener(this);
        iv_narrow.setOnClickListener(this);

        tv_provider.setOnClickListener(this);
        tv_delivery_time.setOnClickListener(this);
        tv_depot.setOnClickListener(this);
        tv_operator.setOnClickListener(this);

        edt_car.setOnClickListener(this);
        edt_driver.setOnClickListener(this);
        edt_remark.setOnClickListener(this);
        btn_save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_personal:
                setBackAlertDialog();
                break;
            case R.id.image_add_new:
//                startActivity(AddPlanActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("Add_new", DataConstant.BUNDLE_ADD_GOODS_PURCHASE);
                if(purchaseBill.getSupplier() != null) {
                    bundle.putInt("hasProvider",1);
                    bundle.putSerializable("Provider", (Serializable) purchaseBill.getSupplier().getId());//放进数据流中
                } else {
                    bundle.putInt("hasProvider",0);//放进数据流中
                }
                startActivityforResult(NewAddGoodsActivity.class,bundle,DataConstant.BUNDLE_REQUEST_ADD_GOODS_PURCHASE);
                break;
            case R.id.iv_narrow:
                narrowCount ++;
                if (narrowCount % 2 != 0) {
                    iv_add_new.setVisibility(View.GONE);
                } else {
                    iv_add_new.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_provider:
                PROVIDER_WHICH = 0;
                Intent intent1 = new Intent(AddPurchaseActivity.this, SearchProviderActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("Provider_which",PROVIDER_WHICH);
                intent1.putExtras(bundle1);
                startActivityForResult(intent1,SEARCH_PROVIDER_REQUEST);
                break;
            case R.id.tv_delivery_time:
                showDialog(DATE_DIALOG);
                break;
            case R.id.tv_depot:
                Intent intent2 = new Intent(AddPurchaseActivity.this, SearchDepotActivity.class);
                startActivityForResult(intent2,SEARCH_DEPOT_REQUEST);
                break;
            case R.id.tv_operator:
                Intent intent3 = new Intent(AddPurchaseActivity.this, SearchOperatorActivity.class);
                startActivityForResult(intent3,SEARCH_OPERATOR_REQUEST);
                //setOperatorAlertDialog();
                break;
            case R.id.btn_save:
                check();
                break;
            default:
                break;
        }
    }

    //供应商,仓库
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SEARCH_PROVIDER_REQUEST){
            if(resultCode == SEARCH_PROVIDER_RESULT){
                Supplier supplier = (Supplier) data.getExtras().getSerializable("supplier");
                if(supplier != null){
                    IdName idName = new IdName(supplier.getUuid(),supplier.getName());
                    purchaseBill.setSupplier(idName);
                    showLogDebug(TAG,"onActivityProviderResult"+mParam1);
                    tv_provider.setText(supplier.getName());
                }

            }
        }

        else if(requestCode == SEARCH_DEPOT_REQUEST){
            if(resultCode == SEARCH_DEPOT_RESULT){
                Warehouse warehouse = (Warehouse) data.getExtras().getSerializable("depot");
                if(warehouse != null){
                    UCN ucn = new UCN(warehouse.getUuid(),warehouse.getCode(),warehouse.getName());
                    purchaseBill.setWarehouse(ucn);
                    showLogDebug(TAG,"onActivityDepotResult"+mParam1);
                    tv_depot.setText(warehouse.getName());
                }
            }
        }
        else if(requestCode == DataConstant.BUNDLE_REQUEST_ADD_GOODS_PURCHASE){
            if(resultCode == DataConstant.ADD_NEW_GOOD_FOR_ADD_PURCHASE_RESULT){
                if(data !=null && data.getExtras()!=null){
                    Map<String,GoodsItem> goodsItemMap =  (Map<String,GoodsItem>)data.getExtras().getSerializable("goods");
                    //转化成Purchase
                    List<PurchaseBillLine> purchaseBillLineList = PurchaseBillUtil.toPurchaseBillLine(goodsItemMap);
                    mAdapter.addMoreItem(purchaseBillLineList);
                    mPurchaseBillLine = mAdapter.getData();
                    Type = PurchaseType.manual;
                }
            }
        }
        else if(requestCode == SEARCH_OPERATOR_REQUEST){
            if(resultCode == SEARCH_OPERATOR_RESULT){
                IdName Operator = (IdName) data.getExtras().getSerializable("operator");
                if(Operator != null){
                    showLogDebug(TAG,"onActivityDepotResult"+mParam1);
                    tv_operator.setText(Operator.getName());
                    operator = Operator;
                }
            }
        }

    }

    //检查信息
    private void check() {
        int count = 0;
        for (int i = 0;i < mPurchaseBillLine.size();i ++) {
            if (mPurchaseBillLine.get(i).getPrice().compareTo(BigDecimal.ZERO) >= 0) {
                count = count + 0;
            } else {
                count ++;
            }
        }
        int amount = 0;
        for (int i = 0;i < mPurchaseBillLine.size();i ++) {
            if ((mPurchaseBillLine.get(i).getPurchaseQty()).compareTo(BigDecimal.ZERO) > 0) {
                amount = amount + 0;
            } else {
                amount ++;
            }
        }
        if (amount == 0) {
            if (count == 0) {
                if (tv_provider.getText().equals("请选择供应商")) {
                    showCustomToast("请检查供应商的信息");
                } else {
                    if (tv_depot.getText().equals("请选择仓库")) {
                        showCustomToast("请检查仓库的信息");
                    } else {
                        if (edt_car.getText() != null && !edt_car.getText().equals("")) {
                            if (edt_driver.getText() != null && !edt_driver.getText().equals("")) {
                                saveNewPurchaseBill();
                            }
                            else {
                                showCustomToast("请填写司机");
                            }
                        } else {
                            showCustomToast("请填写车辆");
                        }
                    }
                }
            } else {
                showCustomToast("请检查单价信息");
            }
        } else {
            showCustomToast("请检查采购量信息");
        }
    }

    //保存订单
    private void saveNewPurchaseBill(){
        String deliveryTime = String.valueOf(tv_delivery_time.getText());
        purchaseBill.setOrg(SessionUtils.getInstance(this).getOrg());
        purchaseBill.setVehicle(edt_car.getText().toString());
        purchaseBill.setDriver(edt_driver.getText().toString());
        purchaseBill.setDeliveryTime(DatesUtils.strToDate(deliveryTime));
        purchaseBill.setRemark(edt_remark.getText().toString());
        purchaseBill.setLines(mPurchaseBillLine);
        purchaseBill.setType(Type);

        if(operator != null){
            purchaseBill.setOperator(operator);
        }

        purchaseBill.setGoodsTotalQty(PurchaseBillUtil.getGoodsTotalQty(mPurchaseBillLine));
        purchaseBill.setTotalAmount(PurchaseBillUtil.getTotalAmount(mPurchaseBillLine));
        HttpManager.saveNewPurchase(this, purchaseBill, new onAddPurchaseBillInterface() {
            @Override
            public void onError(String message) {
                showCustomToast("保存失败");

                showCustomToast(message);

            }

            @Override
            public void onAddPurchaseBillSuccess(String message) {
                showCustomToast("保存成功");
                startActivity(PurchaseActivity.class);
                finish();
            }
        });
    }

    //删除提示对话框
    public void setDeleteAlertDialog(final int position){
        final AlertDialog alert = new AlertDialog.Builder(AddPurchaseActivity.this).create();
        alert.setTitle("提示信息");
        //title.setTextColor(getResources().getColor(R.color.black));//标题文字颜色
        alert.setMessage("确认删除该采购计划\n" + mPurchaseBillLine.get(position).getGoods().getName() +
                mPurchaseBillLine.get(position).getPurchaseQty() + mPurchaseBillLine.get(position).getGoodsUnit().getName() + "?");
        //message.setTextColor(getResources().getColor(R.color.text_color_gray_2));
        alert.setCancelable(false);
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mPurchaseBillLine.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alert.dismiss();
            }
        });
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.green_text));
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.gray_text2));
    }

    /*//选择经办人
    private void getDropdownOperator(){
        HttpManager.getDropdownOperator(this, new GetOperatorResultInterface() {
            @Override
            public void onError() {

            }

            @Override
            public void onGetOperatorSuccess(List<IdName> datas) {
                operators = datas;
                *//*if(operators!=null && !operators.isEmpty()){
                    tv_operator.setText(operators.get(0).getName());
                }*//*
            }
        });
    }
    //选择经办人提示对话框
    public void setOperatorAlertDialog(){
        final String[] items = new String[operators.size()];
        for(int i = 0;i<operators.size();i++){
            items[i] = operators.get(i).getName();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(AddPurchaseActivity.this);
        builder.setTitle("请选择经办人");
        //title.setTextColor(getResources().getColor(R.color.black));//标题文字颜色
        //builder.setMessage("订单未保存，确定离开？");
        builder.setCancelable(false);
        //message.setTextColor(getResources().getColor(R.color.text_color_gray_2));

        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String item = items[which];
                tv_operator.setText(item);
                operator = operators.get(which);
            }
        });
        //添加确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.create().show();
        //确定按钮字的颜色
        //builder.getPositiveButton.setTextColor(getResources().getColor(R.color.green_text));
    }*/

    //订单未保存提示对话框
    public void setBackAlertDialog(){
        final AlertDialog alert = new AlertDialog.Builder(AddPurchaseActivity.this).create();
        alert.setTitle("提示信息");
        //title.setTextColor(getResources().getColor(R.color.black));//标题文字颜色
        alert.setMessage("订单未保存，确定离开？");
        //message.setTextColor(getResources().getColor(R.color.text_color_gray_2));
        alert.setCancelable(false);
        //添加确定按钮
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //发送数据给后台
               finish();
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

    private void unitAudit(final View v,final int position){
        List<String> mGoodsUuid = new ArrayList<String>();
        mGoodsUuid.add(mPurchaseBillLine.get(position).getGoods().getUuid());
        HttpManager.dropDownUnit(this, mGoodsUuid, position, new UnitInterface() {
            @Override
            public void onError() {
                showCustomToast("请求失败");
            }

            @Override
            public void onUnitListener(Map<String, List<UnitPrice>> map, int position) {
                showCustomToast("请求成功");
                setUnitsAlertDialog(map, position);
            }
        });
    }

    private void setUnitsAlertDialog(Map<String, List<UnitPrice>> mMapUnits,final int position) {
        String GoodsUuid = mPurchaseBillLine.get(position).getGoods().getUuid();
        final List<UnitPrice> mUnit = mMapUnits.get(GoodsUuid);
        ArrayList<String> mUnits = new ArrayList<String>();
        for (int i = 0;i < mUnit.size();i ++) {
            mUnits.add(mUnit.get(i).getUnit().getName());//ArrayList可以根据长度变化
        }
        int size = mUnits.size();//获取长度
        final String[] items = new String[size];
        for (int i = 0;i < size;i ++) {//把ArrayList转换成String[]数组
            items[i] = mUnits.get(i);
        }
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("请选择单位");
        builder.setCancelable(true);

        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPurchaseBillLine.get(position).setGoodsUnit(mUnit.get(which).getUnit());
            }
        });
        //添加确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.create().show();
    }

    public interface onAddPurchaseBillInterface{
        void onError(String message);

        void onAddPurchaseBillSuccess(String message);
    }
}
