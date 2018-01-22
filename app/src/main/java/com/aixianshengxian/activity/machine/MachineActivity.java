package com.aixianshengxian.activity.machine;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.activity.addOrder.NewAddGoodsActivity;
import com.aixianshengxian.activity.plan.AddPlanActivity;
import com.aixianshengxian.activity.receive.ReceiveActivity;
import com.aixianshengxian.adapters.MachineRefreshAdapter;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.http.HttpManager;
import com.aixianshengxian.module.GoodsItem;
import com.aixianshengxian.util.DateUtils;
import com.aixianshengxian.util.PurchaseBillUtil;
import com.aixianshengxian.util.SessionUtils;
import com.aixianshengxian.view.RefreshRecyclerView;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.service.process.ForecastProcessPlan;
import com.xmzynt.storm.util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class MachineActivity extends BaseActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener,RefreshRecyclerView.OnLoadDataListener{

    private static final int  HANDLE_GET_FORECAST_PLAN = 0;

    private static final int HANDLE_GET_FORECAST_PLAN_MORE = 1;

    private ImageView image_personal,iv_search,iv_close_search,iv_add_new,iv_narrow;
    private EditText edt_search_content;
    private TextView tv_head_title,tv_choose_time;
    private EditText edt_plan_num;
    private LinearLayout ll_nomessage;

    private List<ForecastProcessPlan> mData = new ArrayList<>();
//    private List<MachineProduct> mDataWhole;

    private RefreshRecyclerView listview_machine;
    private SwipeRefreshLayout swipe_refresh_widget;
    private MachineRefreshAdapter mAdapter;
    private LinearLayoutManager layoutManager;

    public static MachineActivity mactivity;

    private int narrowCount = 0;//记录添加按钮是否显示

    private int pageCount = 0;
    private int pageNo = 0;
    private int pageSize = 20;

    //时间选择器
    int mYear, mMonth, mDay;
    final int DATE_DIALOG = 1;

//    private MyDatabaseHelper dbHelper;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            List<ForecastProcessPlan> datas = (List<ForecastProcessPlan>)msg.obj;
            switch (msg.what){
                case HANDLE_GET_FORECAST_PLAN:
                    mAdapter.addItem(datas);
                    mData = mAdapter.getData();
                    listview_machine.notifyNewData();
                    if (mData.size() == 0) {
                        ll_nomessage.setVisibility(View.VISIBLE);
                    } else {
                        ll_nomessage.setVisibility(View.GONE);
                    }
                    break;
                case HANDLE_GET_FORECAST_PLAN_MORE:
                    mAdapter.addMoreItem(datas);
                    mData = mAdapter.getData();
                    listview_machine.notifyNewData();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine);

//        //数据库
//        dbHelper = new MyDatabaseHelper(this,"ASData.db",null,1000);//检测是否有数据库
//        dbHelper.getWritableDatabase();//无，则创建
        initViews();
        initMachine(HANDLE_GET_FORECAST_PLAN);
        initEvents();
    }

    public void onResume() {
        initMachine(HANDLE_GET_FORECAST_PLAN);
        super.onResume();
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("加工计划");

        iv_search = (ImageView) findViewById(R.id.image_search);
        iv_close_search = (ImageView) findViewById(R.id.image_close_search);
        edt_search_content = (EditText) findViewById(R.id.edit_search_content);
        edt_search_content.setText(edt_search_content.getText());//显示用户的输入内容

        iv_add_new = (ImageView) findViewById(R.id.image_add_new);
        iv_narrow = (ImageView) findViewById(R.id.iv_narrow);

        tv_choose_time = (TextView) findViewById(R.id.tv_choose_time);
        tv_choose_time.setText(DateUtils.getSomeDate(0)); //日期
        ll_nomessage = (LinearLayout) findViewById(R.id.ll_nomessage);

//        mDataWhole = new ArrayList<>();

        swipe_refresh_widget = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipe_refresh_widget.setOnRefreshListener(this);
        listview_machine = (RefreshRecyclerView) findViewById(R.id.listview_machine);

        layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        listview_machine.setLayoutManager(layoutManager);
//        listview_machine.setLoadMoreEnable(true);
//        listview_machine.setFooterResource(R.layout.view_footer);
//        listview_machine.setOnLoadDataListener(this);
        mAdapter = new MachineRefreshAdapter(mData,this);//将数据传入
        mAdapter.setOnItemListener(new MachineRefreshAdapter.OnItemClickListener(){

           @Override
           public void onStockInClick(ForecastProcessPlan forecastProcessPlan,View v) {
               edt_plan_num = (EditText) v.findViewById(R.id.edt_plan_num);
               if (forecastProcessPlan.getPlanQty().intValue() == 0) {
                   showCustomToast("请填写计划数量！");
               } else {
                   saveModify(forecastProcessPlan);
               }
           }
        });

        listview_machine.setAdapter(mAdapter);

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
    public void display(View v) {
        tv_choose_time.setText(new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
//        chooseTime(v);
        onRefresh();
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
        iv_add_new.setOnClickListener(this);
        iv_narrow.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        iv_close_search.setOnClickListener(this);
        tv_choose_time.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                finish();
                break;
            case R.id.image_search:
                contentSearch(v);
                break;
            case R.id.image_close_search:
                edt_search_content.setText("");
                onRefresh();
                break;
            case R.id.image_add_new:
                Intent intent1 = new Intent(MachineActivity.this,NewAddGoodsActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("Add_new", DataConstant.BUNDLE_ADD_GOODS_FORECAST_PROCESS_PLAN);
                intent1.putExtras(bundle1);
                startActivity(intent1);
                break;
            case R.id.iv_narrow:
                narrowCount ++;
                if (narrowCount % 2 != 0) {
                    iv_add_new.setVisibility(View.GONE);
                } else {
                    iv_add_new.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_choose_time:
                showDialog(DATE_DIALOG);
                break;
            default:
                break;
        }
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DataConstant.BUNDLE_REQUEST_FORECAST_PROCESS_PLAN){
            if(resultCode == DataConstant.ADD_NEW_GOOD_FOR_FORECAST_PROCESS_PLAN){
                if(data !=null && data.getExtras()!=null){
                    Map<String,GoodsItem> goodsItemMap =  (Map<String,GoodsItem>)data.getExtras().getSerializable("goods");
                    //转化成Purchase
                    List<ForecastProcessPlan> forecastProcessPlanList = PurchaseBillUtil.toForecastProcessPlanList(goodsItemMap);
                    mAdapter.addMoreItem(forecastProcessPlanList);
                    listview_machine.notifyNewData();
                    mData = mAdapter.getData();
                    ll_nomessage.setVisibility(View.GONE);
                } else {
                    ll_nomessage.setVisibility(View.VISIBLE);
                }
            }
        }
    }*/

    private void contentSearch(View v) {
        String searchContent = edt_search_content.getText().toString();
        if (searchContent.isEmpty()) {
            showCustomToast("请输入搜索条件");
        } else {
            onRefresh();
        }
    }

    public void initMachine(final int state) {

        String createdTime = String.valueOf(tv_choose_time.getText());
        String keywordsLike=null;
        if(edt_search_content!=null){
            keywordsLike = edt_search_content.getText().toString();
        }
        HttpManager.getForecastProcessList(this, pageNo, pageSize, keywordsLike, createdTime, new onGetForecastProcessListInterface() {
            @Override
            public void onError(String message) {
                showCustomToast(message);
            }

            @Override
            public void onGetForecastProcessListSuccess(List<ForecastProcessPlan> data) {
//                pageCount = data.getPageCount();
//                pageNo = data.getCurrentPage();
                List<ForecastProcessPlan> list = data;
                handler.obtainMessage(state,list).sendToTarget();
            }
        });
//        MachineProduct machine1 = new MachineProduct("111111111111","东北大蒜（大）","斤","2017-10-21",100,100);
//        mData.add(machine1);
//        MachineProduct machine2 = new MachineProduct("222222222222","香菇（大）","kg","2017-10-22",200,100);
//        mData.add(machine2);
//        MachineProduct machine3 = new MachineProduct("333333333333","苦瓜","g","2017-10-23",200,200);
//        mData.add(machine3);
//        MachineProduct machine4 = new MachineProduct("444444444444","带鱼","斤","2017-10-24",100,100);
//        mData.add(machine4);
//
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        db.delete("MachineProduct",null,null);
//        ContentValues values = new ContentValues();
//        for (int position = 0;position < mData.size();position ++) {
//            values.put("machineId ",mData.get(position).getMachineId());
//            values.put("productName",mData.get(position).getProductName());
//            values.put("unit",mData.get(position).getUnit());
//            values.put("machineTime",mData.get(position).getMachineTime());
//            values.put("planNum",mData.get(position).getPlanNum());
//            values.put("competeNum",mData.get(position).getCompeteNum());
//            db.insert("MachineProduct",null,values);
//            values.clear();
//        }
    }

    @Override
    public void pullUpRefresh() {
        if(pageNo <pageCount){
            pageNo++;
            initMachine(HANDLE_GET_FORECAST_PLAN_MORE);
        }
        listview_machine.loadMoreComplete();
    }

    @Override
    public void onRefresh() {
        pageNo = 0;
        pageSize = 20;
        initMachine(HANDLE_GET_FORECAST_PLAN);
        swipe_refresh_widget.setRefreshing(false);
    }

    public interface onGetForecastProcessListInterface{
        void onError(String message);

        void onGetForecastProcessListSuccess(List<ForecastProcessPlan> data);
    }

    //编辑保存
    public void saveModify(final ForecastProcessPlan forecastProcessPlan) {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();

        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            try {
                body.put(DataConstant.FORECASTPROCESSPLAN , forecastProcessPlan);
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
        OkHttpUtils.postString().url(UrlConstants.URL_FORECAST_PROCESS_PLAN_SAVE_MODIFY)
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
                            Intent intent = new Intent(MachineActivity.this,StockInActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("forecastProcessPlan",forecastProcessPlan);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            showCustomToast("保存成功");
                            showLogDebug("main", s);
                        }  else {
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }
}
