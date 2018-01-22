package com.aixianshengxian.activity.machine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.activity.search.SearchDepotActivity;

import com.aixianshengxian.adapters.StockOutRecordItemAdapter;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.module.StockOutRecordItem;
import com.aixianshengxian.view.RefreshRecyclerView;
import com.xmzynt.storm.service.wms.stockout.StockOutRecord;

import com.aixianshengxian.http.HttpManager;
import com.xmzynt.storm.service.wms.warehouse.Warehouse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectReceiveActivity extends BaseActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener,RefreshRecyclerView.OnLoadDataListener {
    public final static String TAG = "SelectReceiveActivity";
    private final int SEARCH_DEPOT_REQUEST = 9;
    private final int SEARCH_DEPOT_RESULT = 10;

    private String mCustomerUUID = "";
    private String mCustomerName = "";
    private String mParam1;

    private ImageView image_personal;
    private TextView tv_head_title,tv_depot;
    private EditText edt_search_content;
    private ImageView iv_search;
    private ImageView iv_close_search;
    private Button btn_save;

    private List<StockOutRecord> mStockOutRecord;//获取到的领料记录
    private List<StockOutRecordItem> mAllOutItem = new ArrayList<>();//向获得的商品里填入未选择的状态
    private List<StockOutRecordItem> mOutItem = new ArrayList<>();//界面中显示的数据
    private Map<String,StockOutRecordItem> mCheckedOutItem = new HashMap<>();//选中的领料记录

    private SwipeRefreshLayout swipe_refresh_widget;
    private RefreshRecyclerView listview_select_receive;
    private StockOutRecordItemAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private LinearLayout ll_nomessage;



    public static SelectReceiveActivity mactivity;

    private String warehouseUuid = null;
    private static final int  HANDLE_GET_STOCK_OUT = 0;

    private static final int HANDLE_GET_STOCK_OUT_MORE = 1;

    private int pageCount = 0;
    private int pageNo = 0;
    private int pageSize = 20;
    private int totalCount = 0;

    private int ADD_STOCK_OUT_RECORD = 1;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            List<StockOutRecordItem> datas = (List<StockOutRecordItem>)msg.obj;
            switch (msg.what){
                case HANDLE_GET_STOCK_OUT :
                    mAdapter.addItem(datas);
                    mOutItem  = mAdapter.getData();
                    listview_select_receive.notifyNewData();
                    if (mOutItem .size() == 0) {
                        ll_nomessage.setVisibility(View.VISIBLE);
                    } else {
                        ll_nomessage.setVisibility(View.GONE);
                    }
                    if(mAdapter.getItemCount() < totalCount){
                        listview_select_receive.setLoadMoreEnable(true);
                    }else{
                        listview_select_receive.setLoadMoreEnable(false);
                    }
                    break;
                case HANDLE_GET_STOCK_OUT_MORE:
                    mAdapter.addMoreItem(datas);
                    mOutItem = mAdapter.getData();
                    listview_select_receive.notifyNewData();
                    if(mAdapter.getItemCount() < totalCount){
                        listview_select_receive.setLoadMoreEnable(true);
                    }else{
                        listview_select_receive.setLoadMoreEnable(false);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_receive);
        getStockOutRecord();
        initViews();
        initStockOutRecord(HANDLE_GET_STOCK_OUT );
        initEvents();
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("选择领料记录");

        edt_search_content = (EditText) findViewById(R.id.edit_search_content);
        edt_search_content.setText(edt_search_content.getText());//显示用户的输入内容
        iv_search = (ImageView) findViewById(R.id.image_search);
        iv_close_search = (ImageView) findViewById(R.id.image_close_search);
        tv_depot = (TextView) findViewById(R.id.tv_depot);
        swipe_refresh_widget = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipe_refresh_widget.setOnRefreshListener(this);
        listview_select_receive = (RefreshRecyclerView) findViewById(R.id.listview_select_receive);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        listview_select_receive.setLayoutManager(layoutManager);
        listview_select_receive.setFooterResource(R.layout.view_footer);
        listview_select_receive.setOnLoadDataListener(this);
        mAdapter = new StockOutRecordItemAdapter(mOutItem,mCheckedOutItem,this);
        mAdapter.setOnItemListener(new StockOutRecordItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(StockOutRecordItem stockOutRecordItem) {

            }
        });
        listview_select_receive.setAdapter(mAdapter);

        ll_nomessage = (LinearLayout) findViewById(R.id.ll_nomessage);
        btn_save = (Button) findViewById(R.id.btn_save);

        mactivity=this;
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        tv_depot.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        iv_close_search.setOnClickListener(this);
        btn_save.setOnClickListener(this);
    }

    private void getStockOutRecord() {
        Intent intent = getIntent();
        ADD_STOCK_OUT_RECORD = (int) intent.getSerializableExtra("Add_stockOutRecord");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                finish();
                break;
            case R.id.image_close_search:
                edt_search_content.setText("");
                onRefresh();
                break;
            case R.id.image_search:
                contentSearch();
                break;
            case R.id.tv_depot:
                Intent intent1 = new Intent(SelectReceiveActivity.this, SearchDepotActivity.class);
                startActivityForResult(intent1,SEARCH_DEPOT_REQUEST);
                break;
            case R.id.btn_save:
                switch (ADD_STOCK_OUT_RECORD) {
                    case DataConstant.BUNDLE_ADD_STOCK_OUT_RECORD_CONSUME:
                        Intent intent2 = new Intent();
                        Bundle bundle1 = new Bundle();
                        bundle1.putSerializable("stockOutRecord", (Serializable)mAdapter.getmCheckedStockOutRecordItem());
                        intent2.putExtras(bundle1);
                        setResult(DataConstant.ADD_NEW_STOCK_OUT_RECORD_CONSUME, intent2);
                        finish();
                        break;
                }
                break;
            default:
                break;
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

    private void initStockOutRecord(final int state){
        String goodsCodeOrNameLike = edt_search_content.getText().toString();
        HttpManager.getStockOutRecordList(this,warehouseUuid,goodsCodeOrNameLike,new onGetStockOutRecordListInterface() {
            @Override
            public void onError(String message) {
                showCustomToast("请求失败");
                showCustomToast(message);
            }

            @Override
            public void onGetStockOutRecordListSuccess(List<StockOutRecord> data) {
                List<StockOutRecord> list = data;
                mAllOutItem.clear();
                if (list != null && list.size() > 0) {
                    mOutItem.clear();
                    for (int i = 0;i < list.size();i ++) {//循环放置checkbox状态
                        StockOutRecord stockOutRecord = list.get(i);
                        StockOutRecordItem stockOutRecordItem = new StockOutRecordItem(stockOutRecord,false);
                        mAllOutItem.add(stockOutRecordItem);
                    }
                    mOutItem.addAll(mAllOutItem);
                }
                handler.obtainMessage(state,mAllOutItem).sendToTarget();
            }
        });
    }

    //仓库
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SEARCH_DEPOT_REQUEST){
            if(resultCode == SEARCH_DEPOT_RESULT){
                Warehouse warehouse = (Warehouse) data.getExtras().getSerializable("depot");
                if(warehouse != null){
                    tv_depot.setText(warehouse.getName());
                    warehouseUuid = warehouse.getUuid();
                    onRefresh();
                }
            }
        }
    }

    @Override
    public void pullUpRefresh() {
        if(pageNo < pageCount){
            pageNo++;
            initStockOutRecord(HANDLE_GET_STOCK_OUT_MORE);
        }
        listview_select_receive.loadMoreComplete();
    }

    @Override
    public void onRefresh() {
        pageNo = 0;
        pageSize = 20;
        initStockOutRecord(HANDLE_GET_STOCK_OUT);
        swipe_refresh_widget.setRefreshing(false);
    }

    public interface onGetStockOutRecordListInterface{
        void onError(String message);

        void onGetStockOutRecordListSuccess(List<StockOutRecord> data);
    }

}
