package com.aixianshengxian.activity.purchase;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.activity.search.SearchProviderActivity;
import com.aixianshengxian.adapters.PurchaseRefreshAdapter;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.db.MyDatabaseHelper;
import com.aixianshengxian.http.HttpManager;
import com.aixianshengxian.popupwindows.OrderManagerPopupWindow;
import com.aixianshengxian.util.DateUtils;
import com.aixianshengxian.util.DatesUtils;
import com.aixianshengxian.util.PurchaseBillUtil;
import com.aixianshengxian.view.RefreshRecyclerView;
import com.xmzynt.storm.service.purchase.bill.PurchaseBill;
import com.xmzynt.storm.service.purchase.bill.PurchaseBillStatus;
import com.xmzynt.storm.service.user.supplier.Supplier;
import com.xmzynt.storm.util.query.PageData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.xmzynt.storm.service.purchase.bill.PurchaseBillStatus.aborted;
import static com.xmzynt.storm.service.purchase.bill.PurchaseBillStatus.audited;
import static com.xmzynt.storm.service.purchase.bill.PurchaseBillStatus.completed;
import static com.xmzynt.storm.service.purchase.bill.PurchaseBillStatus.initial;
import static com.xmzynt.storm.service.purchase.bill.PurchaseBillStatus.stockIned;

public class PurchaseActivity extends BaseActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener,RefreshRecyclerView.OnLoadDataListener {
    private ImageView image_personal,iv_select,iv_search,iv_close_search,iv_add_new,iv_narrow;
    private TextView tv_head_title,tv_choose_provider,tv_choose_time;
    private EditText edt_search_content;

    private static final int  HANDLE_GET_PUCHASE = 0;

    private static final int HANDLE_GET_PUCHASE_MORE = 1;

//    private List<PurchaseOrder> mAllList;
    private List<PurchaseBill> mData = new ArrayList<>();
    private List<PurchaseBill> mDataWhole;

    //private String[] mListState ;

    private RefreshRecyclerView listview_purchase_list;
    private SwipeRefreshLayout swipe_refresh_widget;
    private PurchaseRefreshAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private LinearLayout ll_nomessage;

    private int ADD_NEW_PLAN = 1;//用于判断是哪个界面跳入选择商品界面

    private int narrowCount = 0;//记录添加按钮是否显示

//    private MyDatabaseHelper dbHelper;

    private int index = 0;
    private int pageCount = 0;
    private int pageNo = 0;
    private int pageSize = 20;
    private int totalCount = 0;

    private String supplierUuid;//供应商uuid
    private final String[] mListState = {"全部", "待审核", "已审核","已入库","已作废","已完成"};
    private List<String> purchaseBillStatus;

    //时间选择器
    int mYear, mMonth, mDay;
    final int DATE_DIALOG = 1;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            List<PurchaseBill> datas = (List<PurchaseBill>)msg.obj;
            switch (msg.what){
                case HANDLE_GET_PUCHASE:
                    mAdapter.addItem(datas);
                    mData = mAdapter.getData();
                    listview_purchase_list.notifyNewData();
                    if (mData.size() == 0) {
                        ll_nomessage.setVisibility(View.VISIBLE);
                    } else {
                        ll_nomessage.setVisibility(View.GONE);
                    }
                    if(mAdapter.getItemCount() < totalCount){
                        listview_purchase_list.setLoadMoreEnable(true);
                    }else{
                        listview_purchase_list.setLoadMoreEnable(false);
                    }
                    break;
                case HANDLE_GET_PUCHASE_MORE:
                    listview_purchase_list.loadMoreComplete();
                    mAdapter.addMoreItem(datas);
                    mData = mAdapter.getData();
                    listview_purchase_list.notifyNewData();
                    if(mAdapter.getItemCount() < totalCount){
                        listview_purchase_list.setLoadMoreEnable(true);
                    }else{
                        listview_purchase_list.setLoadMoreEnable(false);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        getPurchaseBillStatus();

        //数据库
//        dbHelper = new MyDatabaseHelper(this,"ASData.db",null,1000);//检测是否有数据库
//        dbHelper.getWritableDatabase();//无，则创建
        initViews();
        initEvents();

        String chooseTime = (String) tv_choose_time.getText();
        initPurchaseOrder(HANDLE_GET_PUCHASE,chooseTime);

    }

    public void onResume() {
        String chooseTime = (String) tv_choose_time.getText();
        initPurchaseOrder(HANDLE_GET_PUCHASE,chooseTime);
        super.onResume();
    }

    private void getPurchaseBillStatus() {
        Intent intent = getIntent();
        purchaseBillStatus = (List<String>) intent.getSerializableExtra("Status");
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("采购单");
        iv_select = (ImageView) findViewById(R.id.iv_select);
        iv_search = (ImageView) findViewById(R.id.image_search);
        edt_search_content = (EditText) findViewById(R.id.edit_search_content);
        edt_search_content.setText(edt_search_content.getText());//显示用户的输入内容

        iv_close_search = (ImageView) findViewById(R.id.image_close_search);
        //iv_close_search.setVisibility(View.GONE);
        iv_add_new = (ImageView) findViewById(R.id.image_add_new);
        iv_narrow = (ImageView) findViewById(R.id.iv_narrow);

        tv_choose_provider = (TextView) findViewById(R.id.tv_choose_provider);
        tv_choose_time = (TextView) findViewById(R.id.tv_choose_time);
        tv_choose_time.setText(DateUtils.getSomeDate(0)); //日期
        ll_nomessage = (LinearLayout) findViewById(R.id.ll_nomessage);

        mDataWhole = new ArrayList<>();
//        readWhole();

        swipe_refresh_widget = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipe_refresh_widget.setOnRefreshListener(this);
        listview_purchase_list = (RefreshRecyclerView) findViewById(R.id.listview_purchase_list);
        layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        listview_purchase_list.setLayoutManager(layoutManager);
        listview_purchase_list.setFooterResource(R.layout.view_footer);
        listview_purchase_list.setOnLoadDataListener(this);
        mAdapter = new PurchaseRefreshAdapter(mData,this);//将数据传入
        mAdapter.setOnItemListener(new PurchaseRefreshAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(PurchaseBill purchaseBill) {
                Intent intent = new Intent(PurchaseActivity.this,PurchaseDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("purchaseBill",purchaseBill);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        listview_purchase_list.setAdapter(mAdapter);
        //时间选择器设置
        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
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
    public void display(View v) {
        tv_choose_time.setText(new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
        //chooseTime(v);
        String chooseTime = (String) tv_choose_time.getText();
        initPurchaseOrder(HANDLE_GET_PUCHASE,chooseTime);
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            display(view);
        }
    };

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        iv_select.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        iv_add_new.setOnClickListener(this);
        iv_narrow.setOnClickListener(this);

        iv_close_search.setOnClickListener(this);

        tv_choose_provider.setOnClickListener(this);
        tv_choose_time.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_personal:
                finish();
                break;
            case R.id.iv_select:
                stateAudit(v);
                break;
            case R.id.image_search:
                contentSearch();
                break;
            case R.id.image_close_search:
                edt_search_content.setText("");
                onRefresh();
                break;
            case R.id.image_add_new:
//                Intent intent1 = new Intent(PurchaseActivity.this,NewAddGoodsActivity.class);
//                Bundle bundle1 = new Bundle();
//                ADD_NEW_PLAN = 0;
//                bundle1.putSerializable("Add_new",ADD_NEW_PLAN);
//                intent1.putExtras(bundle1);
//                startActivity(intent1);
                  startActivity(AddPurchaseActivity.class);
                break;
            case R.id.iv_narrow:
                narrowCount ++;
                if (narrowCount % 2 != 0) {
                    iv_add_new.setVisibility(View.GONE);
                } else {
                    iv_add_new.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_choose_provider:
                Bundle bundle = new Bundle();
                bundle.putSerializable("Provider_which",0);
                startActivityforResult(SearchProviderActivity.class,bundle, DataConstant.BUNDLE_REQUEST_GET_PURCHASE);
                break;
            case R.id.tv_choose_time:
                showDialog(DATE_DIALOG);
                //chooseTime();
                break;
            default:;
                break;
        }
    }

    //供应商
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DataConstant.BUNDLE_REQUEST_GET_PURCHASE){
            if(resultCode == DataConstant.SEARCH_PROVIDER_RESULT){
                Supplier supplier = (Supplier) data.getExtras().getSerializable("supplier");
                if(supplier != null){
                    String supplierName = supplier.getName();
                    tv_choose_provider.setText(supplierName);
                    supplierUuid = supplier.getUuid();
                    String chooseTime = (String) tv_choose_time.getText();
                    initPurchaseOrder(HANDLE_GET_PUCHASE,chooseTime);
                    onRefresh();
                }
            }
        }
    }

    private void contentSearch() {
        String searchContent = edt_search_content.getText().toString();
        if (searchContent.isEmpty()) {
            showCustomToast("请输入搜索条件");
            //Toast.makeText(PurchaseActivity.this, "请输入搜索条件", Toast.LENGTH_SHORT).show();
        } else {
            onRefresh();
        }
    }

    private void stateAudit(View v){

        new OrderManagerPopupWindow(this, new OrderManagerPopupWindow.ResultListener() {
            @Override
            public void onResultChanged(int index) {
                /*purchaseBillStatus =  PurchaseBillStatus.values()[index];
                onRefresh();*/
                switch (mListState[index]){
                    case "全部":
                        purchaseBillStatus =  null;
                        onRefresh();
                        break;
                    case "待审核":
                        //purchaseBillStatus.clear();
                        purchaseBillStatus = new ArrayList<>();
                        purchaseBillStatus.add(initial.name());
                        onRefresh();
                        break;
                    case "已审核":
                        //purchaseBillStatus.clear();
                        purchaseBillStatus = new ArrayList<>();
                        purchaseBillStatus.add(audited.name());
                        onRefresh();
                        break;
                    case "已入库":
                        //purchaseBillStatus.clear();
                        purchaseBillStatus = new ArrayList<>();
                        purchaseBillStatus.add(stockIned.name());
                        onRefresh();
                        break;
                    case "已作废":
                        //purchaseBillStatus.clear();
                        purchaseBillStatus = new ArrayList<>();
                        purchaseBillStatus.add(aborted.name());
                        onRefresh();
                        break;
                    case "已完成":
                        //purchaseBillStatus.clear();
                        purchaseBillStatus = new ArrayList<>();
                        purchaseBillStatus.add(completed.name());
                        onRefresh();
                        break;
               }
            }
        },v,mListState);
        if (mData.size() == 0) {
            ll_nomessage.setVisibility(View.VISIBLE);
        } else {
            ll_nomessage.setVisibility(View.GONE);
        }
    }

    private void chooseTime(View v) {
        String chooseTime = (String) tv_choose_time.getText();
        if (mData != null) {
            mData.clear();
            for(int i = 0;i < mDataWhole.size();i ++) {
                String operateTime = String.valueOf(DatesUtils.dateToStr(mDataWhole.get(i).getDeliveryTime()));
               if (operateTime.equals(chooseTime)) {
                   mData.add(mDataWhole.get(i));
                }
            }
            mAdapter.notifyDataSetChanged();
            if (mData.size() == 0) {
                ll_nomessage.setVisibility(View.VISIBLE);
            } else {
                ll_nomessage.setVisibility(View.GONE);
            }
        }
    }

    private void initPurchaseOrder(final int state,final String Time) {
        //mDataWhole.clear();
        String keywordsLike=null;
        String chooseTime = null;
        if(edt_search_content!=null){
            keywordsLike = edt_search_content.getText().toString();
        }
        if(tv_choose_time!=null){
            chooseTime = (String) tv_choose_time.getText();
        }


        HttpManager.getPurchases(this, pageNo, pageSize, supplierUuid,keywordsLike,purchaseBillStatus,chooseTime,new onPurchaseListInterface() {
            @Override
            public void onError(String message) {
                showCustomToast("请求失败");
                showCustomToast(message);
            }

            @Override
            public void onPurchaseListSuccess(PageData<PurchaseBill> data) {
                totalCount = data.getTotalCount();
                pageCount = data.getPageCount();
                pageNo = data.getCurrentPage();
                List<PurchaseBill> list = data.getValues();
                handler.obtainMessage(state,list).sendToTarget();
            }
        });
        //mListState = PurchaseBillUtil.getPurchaseBillStatusList();
    }

    @Override
    public void onRefresh() {
        String chooseTime = (String) tv_choose_time.getText();
        pageNo = 0;
        pageSize = 20;
        initPurchaseOrder(HANDLE_GET_PUCHASE,chooseTime);
        swipe_refresh_widget.setRefreshing(false);
    }

    @Override
    public void pullUpRefresh() {

        String chooseTime = (String) tv_choose_time.getText();

        if (pageNo + 1 < pageCount) {
            initPurchaseOrder(HANDLE_GET_PUCHASE_MORE, chooseTime);

            /*if (pageNo < pageCount) {
                pageNo ++;
                initPurchaseOrder(HANDLE_GET_PUCHASE_MORE, chooseTime);
            }*/

            if (pageNo + 1 < pageCount) {
                pageNo ++;
                initPurchaseOrder(HANDLE_GET_PUCHASE_MORE, chooseTime);
            }

        }
    }

        public interface onPurchaseListInterface {
            void onError(String message);

            void onPurchaseListSuccess(PageData<PurchaseBill> data);
        }
}
