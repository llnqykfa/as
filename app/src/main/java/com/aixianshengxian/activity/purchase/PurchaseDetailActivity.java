package com.aixianshengxian.activity.purchase;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.activity.check.CheckDetailActivity;
import com.aixianshengxian.adapters.PurchaseDetailAdapter;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.http.HttpManager;
import com.aixianshengxian.popupwindows.OrderManagerPopupWindow;
import com.aixianshengxian.printers.DialogManager;
import com.aixianshengxian.util.DatesUtils;
import com.aixianshengxian.util.SessionUtils;
import com.bixolon.printer.BixolonPrinter;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.service.purchase.bill.PurchaseBill;
import com.xmzynt.storm.service.purchase.bill.PurchaseBillLine;
import com.xmzynt.storm.service.purchase.bill.PurchaseBillStatus;
import com.xmzynt.storm.service.purchase.bill.PurchaseData;
import com.xmzynt.storm.util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import okhttp3.Call;

import static com.aixianshengxian.constant.DataConstant.URL_PURCHASE_DELETE;
import static com.bixolon.printer.BixolonPrinter.TEXT_SIZE_VERTICAL1;
import static com.bixolon.printer.BixolonPrinter.TEXT_SIZE_VERTICAL2;

public class PurchaseDetailActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = "BixolonPrinter";
    static final String ACTION_GET_DEFINEED_NV_IMAGE_KEY_CODES = "com.bixolon.anction.GET_DEFINED_NV_IMAGE_KEY_CODES";
    static final String ACTION_COMPLETE_PROCESS_BITMAP = "com.bixolon.anction.COMPLETE_PROCESS_BITMAP";
    static final String ACTION_GET_MSR_TRACK_DATA = "com.bixolon.anction.GET_MSR_TRACK_DATA";
    static final String EXTRA_NAME_NV_KEY_CODES = "NvKeyCodes";
    static final String EXTRA_NAME_MSR_MODE = "MsrMode";
    static final String EXTRA_NAME_MSR_TRACK_DATA = "MsrTrackData";
    static final String EXTRA_NAME_BITMAP_WIDTH = "BitmapWidth";
    static final String EXTRA_NAME_BITMAP_HEIGHT = "BitmapHeight";
    static final String EXTRA_NAME_BITMAP_PIXELS = "BitmapPixels";

    static final int REQUEST_CODE_SELECT_FIRMWARE = Integer.MAX_VALUE;
    static final int RESULT_CODE_SELECT_FIRMWARE = Integer.MAX_VALUE - 1;
    static final int MESSAGE_START_WORK = Integer.MAX_VALUE - 2;
    static final int MESSAGE_END_WORK = Integer.MAX_VALUE - 3;

    static final String FIRMWARE_FILE_NAME = "FirmwareFileName";

    // Name of the connected device
    private String mConnectedDeviceName = null;

    private ImageView image_personal,iv_photo,iv_scan,iv_connect;
    private TextView tv_head_title,tv_provider,tv_purchase_ucode,tv_operate_time,tv_purchase_count,tv_purchase_state,tv_total_price;
    private TextView tv_depot,tv_operator,tv_car,tv_driver,tv_remark;
    private Button btn_operate;

    private RelativeLayout ll_bottom;

    private PurchaseBill mPurchaseBill;
    private String Operator,Car,Driver,Remark,TotalPrice;

    private List<PurchaseBillLine> mData = new ArrayList<>();
    private PurchaseData mPurchaseData;
//    private List<PurchaseDetail> mDataWhole;
    private final String[] mListNoAudit = {"标签打印","删除","编辑","审核"};
    private final String[] mListAudit = {"反审核","标签打印"};

    private RecyclerView product_listview;
    private SwipeRefreshLayout swipe_refresh_widget;
    private PurchaseDetailAdapter mAdapter;
    private LinearLayoutManager layoutManager;

//    private MyDatabaseHelper dbHelper;

    //打印机
    public static BixolonPrinter mBixolonPrinter;
    private boolean mIsConnected;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.HONEYCOMB || Build.VERSION.SDK_INT == Build.VERSION_CODES.HONEYCOMB_MR1) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        }

        mBixolonPrinter = new BixolonPrinter(this,mHandler,null);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_detail);

        //数据库
//        dbHelper = new MyDatabaseHelper(this,"ASData.db",null,1000);//检测是否有数据库
//        dbHelper.getWritableDatabase();//无，则创建
        initViews();
        initEvents();
        getPurchaseOrder();
//        initPurchaseDetail();
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);

        //右上方分享按钮
        iv_scan = (ImageView) findViewById(R.id.iv_scan);
        iv_scan.setImageResource(R.mipmap.account);


        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("采购单详情");

        //照相机是否显示
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        btn_operate = (Button) findViewById(R.id.btn_operate);
        tv_provider = (TextView) findViewById(R.id.tv_provider);
        tv_purchase_ucode = (TextView) findViewById(R.id.tv_purchase_ucode);
        tv_operate_time = (TextView) findViewById(R.id.tv_operate_time);
        tv_purchase_count = (TextView) findViewById(R.id.tv_purchase_count);
        tv_purchase_state = (TextView) findViewById(R.id.tv_purchase_state);
        tv_depot = (TextView) findViewById(R.id.tv_depot);
        tv_operator = (TextView) findViewById(R.id.tv_operator);
        tv_car = (TextView) findViewById(R.id.tv_car);
        tv_driver = (TextView) findViewById(R.id.tv_driver);
        tv_remark = (TextView) findViewById(R.id.tv_remark);
        //下方按钮不同显示
        ll_bottom = (RelativeLayout) findViewById(R.id.ll_bottom);

        /*swipe_refresh_widget = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);*/
        product_listview = (RecyclerView) findViewById(R.id.product_listview);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        product_listview.setLayoutManager(layoutManager);

        tv_total_price = (TextView) findViewById(R.id.tv_total_price);//计算总金额
//        double amount = 0;
//        if(mData != null && mData.size()>0){
//            for(int i = 0 ; i < mData.size() ; i ++){
//                    amount =  amount + mData.get(i).getPurchasePrice();
//            }
//        }

        iv_connect = (ImageView) findViewById(R.id.iv_connect);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        iv_scan.setOnClickListener(this);
        iv_photo.setOnClickListener(this);
        btn_operate.setOnClickListener(this);
        iv_connect.setOnClickListener(this);
    }

    private void initData(){
        if(mPurchaseBill != null) {
            tv_provider.setText(mPurchaseBill.getSupplier() == null?"":mPurchaseBill.getSupplier().getName());//供货商
            tv_purchase_ucode.setText(mPurchaseBill.getBillNumber());//采购单号
            tv_operate_time.setText(DatesUtils.dateToStr(mPurchaseBill.getDeliveryTime()));//交货日期
            tv_purchase_count.setText(mPurchaseBill.getLines().size()+" 项");//商品项数
            tv_purchase_state.setText(mPurchaseBill.getStatus().getCaption());//采购状态
            tv_depot.setText(mPurchaseBill.getWarehouse().getName());//仓库
            tv_car.setText(mPurchaseBill.getVehicle());//车辆
            tv_driver.setText(mPurchaseBill.getDriver());//司机
            tv_remark.setText(mPurchaseBill.getRemark());//备注
            if(mPurchaseBill.getStatus().getCaption().equals("待审核")) {
                iv_scan.setVisibility(View.GONE);
            } else {
                iv_scan.setVisibility(View.VISIBLE);
                iv_scan.setImageResource(R.mipmap.fenxiang);
            }

            if(mPurchaseBill.getStatus().getCaption().equals("待审核") || mPurchaseBill.getStatus().getCaption().equals("已完成")) {
                iv_photo.setVisibility(View.GONE);
            } else {
                iv_photo.setVisibility(View.VISIBLE);
            }

            if(mPurchaseBill.getOperator() != null){
                tv_operator.setText(mPurchaseBill.getOperator().getName());//经办人
            }else{
                tv_operator.setText("");
            }

            if(mPurchaseBill.getStatus().getCaption().equals("已完成")) {//状态为已完成时
                ll_bottom.setVisibility(View.GONE);//下方按钮不显示
            } else {
                ll_bottom.setVisibility(View.VISIBLE);//下方按钮显示
                if(mPurchaseBill.getStatus().getCaption().equals("已入库")) {//状态为已入库时
                    btn_operate.setText("完  成");//下方按钮显示
                }
            }

            tv_total_price.setText(String.valueOf(mPurchaseBill.getTotalAmount()));

            mAdapter = new PurchaseDetailAdapter(mData,this,mPurchaseBill);//将数据传入
            mAdapter.setOnItemListener(new PurchaseDetailAdapter.OnItemClickListener(){

                @Override
                public void onPinterClick(PurchaseBillLine purchasedetail) {
                    printText(purchasedetail);
                }

                @Override
                public void onItemClick(PurchaseBillLine purchasedetail) {
                    String ucode = purchasedetail.getUuid();
                    getScanPurchaseData(ucode);
                }
            });
            product_listview.setAdapter(mAdapter);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mBixolonPrinter.disconnect();
    }

    public void getPurchaseOrder() {
        Intent intent = getIntent();
        mPurchaseBill = (PurchaseBill) intent.getSerializableExtra("purchaseBill");
        HttpManager.getPurchaseDetail(this, mPurchaseBill.getUuid(), new onGetPurchaseDetailInterface() {
            @Override
            public void onError(String message) {
                showCustomToast("获取详情失败");
            }

            @Override
            public void onGetPurchaseDetailSuccess(PurchaseBill data) {
                mPurchaseBill = data;
                mData = mPurchaseBill.getLines();
                initData();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                finish();
                break;
            case R.id.iv_scan:
                break;
            case R.id.iv_photo:
                break;
            case R.id.btn_operate:
                if(mPurchaseBill.getStatus().getCaption().equals("待审核")) {
                    noAudited(v);
                }
                else if(mPurchaseBill.getStatus().getCaption().equals("已审核")) {
                        audited(v);
                    }
                else if(mPurchaseBill.getStatus().getCaption().equals("已入库")) {
                    operatePurchasesBill(DataConstant.URL_PURCHASE_COMPLETE);
                    getPurchaseOrder();
                }
                break;
            case R.id.iv_connect:
                mBixolonPrinter.findBluetoothPrinters();//蓝牙连接时
                break;
            default:
                break;
        }
    }

    //打开蓝牙打印机端口
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what) {
                case BixolonPrinter.MESSAGE_BLUETOOTH_DEVICE_SET:
                    if (msg.obj == null) {
                        showCustomToast("没有配对的蓝牙打印机");
                        //Toast.makeText(getApplicationContext(), "没有配对的蓝牙打印机", Toast.LENGTH_SHORT).show();
                    } else {//获取与蓝牙配对的打印机名称
                        DialogManager.showBluetoothDialog(PurchaseDetailActivity.this, (Set<BluetoothDevice>) msg.obj);
                    }
                    return true;
                case BixolonPrinter.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BixolonPrinter.STATE_CONNECTED:
                            showCustomToast(getString(R.string.title_connected_to, mConnectedDeviceName));
                            mIsConnected = true;
                            break;
                        case BixolonPrinter.STATE_CONNECTING:
                            showCustomToast(getString(R.string.title_connecting));
                            break;
                        case BixolonPrinter.STATE_NONE:
                            showCustomToast(getString(R.string.title_not_connected));
                            mIsConnected = false;
                            mProgressBar.setVisibility(View.INVISIBLE);
                            break;
                    }
                    return true;
                case BixolonPrinter.MESSAGE_PRINT_COMPLETE:
                    showCustomToast("打印完成");
                    return true;
                case BixolonPrinter.MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString(BixolonPrinter.KEY_STRING_DEVICE_NAME);
                    return true;
                case BixolonPrinter.MESSAGE_TOAST:
                    showCustomToast(msg.getData().getString(BixolonPrinter.KEY_STRING_TOAST));
                    return true;
            }
            return false;
        }
    });

    private void printText(PurchaseBillLine purchasedetail) {
        mBixolonPrinter.setPageMode();//页面模式
        mBixolonPrinter.setPrintArea(0,0,670,216);//打印范围

        //打印二维码
        mBixolonPrinter.setAbsolutePrintPosition(0);//X坐标
        mBixolonPrinter.setAbsoluteVerticalPrintPosition(140);//内容高
        PurchaseDetailActivity.mBixolonPrinter.printQrCode(purchasedetail.getUuid(), BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.QR_CODE_MODEL2, 5, true);

        //打印文本
        String goodsName = purchasedetail.getGoods().getName();
        mBixolonPrinter.setAbsolutePrintPosition(200);//X坐标
        mBixolonPrinter.setAbsoluteVerticalPrintPosition(45);//内容高0
        PurchaseDetailActivity.mBixolonPrinter.printText(goodsName, BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A, BixolonPrinter.TEXT_SIZE_HORIZONTAL2|TEXT_SIZE_VERTICAL2, false);

        String purchaseQty = purchasedetail.getPurchaseQty().doubleValue()+purchasedetail.getGoodsUnit().getName();
        mBixolonPrinter.setAbsolutePrintPosition(200);//X坐标
        mBixolonPrinter.setAbsoluteVerticalPrintPosition(80);//内容高
        PurchaseDetailActivity.mBixolonPrinter.printText(purchaseQty, BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A, BixolonPrinter.TEXT_SIZE_HORIZONTAL1|TEXT_SIZE_VERTICAL1, false);

        String customerName=purchasedetail.getCustomer()==null ?"暂无客户":purchasedetail.getCustomer().getName()+(purchasedetail.getCustomerDept()==null?"":"【"+purchasedetail.getCustomerDept().getName()+"】");
        mBixolonPrinter.setAbsolutePrintPosition(200);//X坐标
        mBixolonPrinter.setAbsoluteVerticalPrintPosition(110);//内容高
        PurchaseDetailActivity.mBixolonPrinter.printText(customerName, BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A, BixolonPrinter.TEXT_SIZE_HORIZONTAL1|TEXT_SIZE_VERTICAL1, false);

        String supplierName = mPurchaseBill.getSupplier() == null ?"暂无供应商":mPurchaseBill.getSupplier().getName();
        mBixolonPrinter.setAbsolutePrintPosition(200);//X坐标
        mBixolonPrinter.setAbsoluteVerticalPrintPosition(140);//内容高
        PurchaseDetailActivity.mBixolonPrinter.printText(supplierName, BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A, BixolonPrinter.TEXT_SIZE_HORIZONTAL1|TEXT_SIZE_VERTICAL1, false);

        mBixolonPrinter.formFeed(true);
    }

    public void showDeleteAlertDialog() {
        final AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle("提示信息");
        alert.setMessage("确认删除该采购单？");
        alert.setCancelable(false);
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                operatePurchasesBill(URL_PURCHASE_DELETE);
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

    private void noAudited(View v){

        new OrderManagerPopupWindow(this, new OrderManagerPopupWindow.ResultListener() {
            @Override
            public void onResultChanged(int index) {
                switch (mListNoAudit[index]) {
                    case "标签打印":
                        for (int i = 0;i < mData.size();i ++) {
                            printText(mData.get(i));
                        }
                        break;
                    case "删除":
                        showDeleteAlertDialog();
                        break;
                    case "编辑":
                        Intent intent = new Intent(PurchaseDetailActivity.this,PurchaseEditActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("purchaseBill",mPurchaseBill);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case "审核":
                        operatePurchasesBill(DataConstant.URL_PURCHASE_AUDIT);
                        break;
                }
            }
        },v,mListNoAudit);
    }

    private void audited(View v){

        new OrderManagerPopupWindow(this, new OrderManagerPopupWindow.ResultListener() {
            @Override
            public void onResultChanged(int index) {
                switch (mListAudit[index]) {
                    case "反审核":
                        operatePurchasesBill(DataConstant.URL_PURCHASE_CANCEL_AUDIT);
                        getPurchaseOrder();
                        break;
                    case "标签打印":
                        for (int i = 0;i < mData.size();i ++) {
                            printText(mData.get(i));
                        }
                        break;
                }
            }
        },v,mListAudit);
    }

    private void operatePurchasesBill(int tag){
        String uuid = mPurchaseBill.getUuid();
        long version = -1;
        if (tag != URL_PURCHASE_DELETE) {
            version = mPurchaseBill.getVersion();
        }
        HttpManager.operatePurchasesBill(this, uuid, version, tag, new onOperatePurchasesBillInterface() {
            @Override
            public void onError(String message) {
                showCustomToast(message);
            }

            @Override
            public void onOperatePurchasesBillSuccess(String data, int tag) {
                switch (tag){
                    case DataConstant.URL_PURCHASE_AUDIT:
                        showCustomToast("审核成功");
                        mPurchaseBill.setStatus(PurchaseBillStatus.audited);
                        tv_purchase_state.setText("已审核");
                        getPurchaseOrder();
                        break;
                    case DataConstant.URL_PURCHASE_ANORT:
                        showCustomToast("作废成功");
                        getPurchaseOrder();
                        break;
                    case URL_PURCHASE_DELETE:
                        showCustomToast("删除成功");
                        startActivity(PurchaseActivity.class);
                        finish();
                        break;
                    case DataConstant.URL_PURCHASE_COMPLETE:
                        showCustomToast("完成成功");
                        getPurchaseOrder();
                        break;
                    case DataConstant.URL_PURCHASE_CANCEL_AUDIT:
                        showCustomToast("取消审核成功");
                        mPurchaseBill.setStatus(PurchaseBillStatus.initial);
                        tv_purchase_state.setText("未审核");
                        getPurchaseOrder();
                        break;
                }
            }
        });
    }

    public interface onGetPurchaseDetailInterface{
        void onError(String message);

        void onGetPurchaseDetailSuccess(PurchaseBill data);
    }


    public interface onOperatePurchasesBillInterface{
        void onError(String message);

        void onOperatePurchasesBillSuccess(String data, int tag);
    }

    //获取采购信息
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
                            Intent intent1 = new Intent(PurchaseDetailActivity.this,CheckDetailActivity.class);
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

//    //作废
//    public interface onAbortPurchaseBillInterface{
//        void onError();
//
//        void onAbortPurchaseBillSuccess(PurchaseBill data);
//    }
//
//    //删除
//    public interface onDeletePurchaseBillInterface{
//        void onError();
//
//        void onDeletePurchaseBillSuccess(PurchaseBill data);
//    }
//
//    public interface onCancelAuditPurchaseBillInterface{
//        void onError();
//
//        void onCancelAuditPurchaseBillSuccess(PurchaseBill data);
//    }


//    private void readWhole() {
//        //从数据库中读取数据，用于做全局对比的对象
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Cursor cursor = db.query("PurchaseDetail",null,null,null,null,null,null);
//        PurchaseDetail purchasedetail;
//        //Boolean haha = cursor.moveToFirst();
//        if (cursor.moveToFirst()) {
//            do {
//                purchasedetail = new PurchaseDetail();//创建一个新对象
//                purchasedetail.setPurchaseDetailId(cursor.getString(cursor.getColumnIndex("purchaseDetailId")));
//                purchasedetail.setProductName(cursor.getString(cursor.getColumnIndex("productName")));
//                purchasedetail.setUnitPrice(cursor.getDouble(cursor.getColumnIndex("unitPrice")));
//                purchasedetail.setUnit(cursor.getString(cursor.getColumnIndex("unit")));
//                purchasedetail.setPurchaseNum(cursor.getDouble(cursor.getColumnIndex("purchaseNum ")));
//                purchasedetail.setPlace(cursor.getString(cursor.getColumnIndex("place")));
//                purchasedetail.setSubtotal(cursor.getString(cursor.getColumnIndex("subtotal")));
//                purchasedetail.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
//                purchasedetail.setPurchasePrice(cursor.getDouble(cursor.getColumnIndex("purchasePrice")));
//                purchasedetail.setDetailState(cursor.getString(cursor.getColumnIndex("detailState")));
//                mDataWhole.add(purchasedetail);
//            } while (cursor.moveToNext());
//            cursor.close();
//        }
//    }

//    private void initPurchaseDetail() {
//        PurchaseDetail detail1 = new PurchaseDetail("111111111111","东北大蒜（大）",10,"斤",100,"四川","采购部","瓣要大一点",1000,"未入库");
//        mData.add(detail1);
//        PurchaseDetail detail2 = new PurchaseDetail("222222222222","香菇（大）",10,"斤",100,"安徽","采购部","要新鲜的",1000,"已入库");
//        mData.add(detail2);
//        PurchaseDetail detail3 = new PurchaseDetail("333333333333","苦瓜",10,"斤",10,"福建","采购部","要籽少一些的",1000,"未入库");
//        mData.add(detail3);
//        PurchaseDetail detail4 = new PurchaseDetail("444444444444","带鱼",20,"斤",100,"安徽","采购部","要新鲜的",2000,"已入库");
//        mData.add(detail4);
//
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        db.delete("PurchaseDetail",null,null);
//        ContentValues values = new ContentValues();
//        for (int position = 0;position < mData.size();position ++) {
//            values.put("purchaseDetailId",mData.get(position).getPurchaseDetailId());
//            values.put("productName",mData.get(position).getProductName());
//            values.put("unitPrice",mData.get(position).getUnitPrice());
//            values.put("unit",mData.get(position).getUnit());
//            values.put("purchaseNum",mData.get(position).getPurchaseNum());
//            values.put("place",mData.get(position).getPlace());
//            values.put("subtotal",mData.get(position).getSubtotal());
//            values.put("remark",mData.get(position).getRemark());
//            values.put("purchasePrice",mData.get(position).getPurchasePrice());
//            values.put("detailState",mData.get(position).getDetailState());
//            db.insert("PurchaseOrder",null,values);
//            values.clear();
//        }
//    }
}
