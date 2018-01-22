package com.aixianshengxian.activity.plan;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.activity.addOrder.NewAddGoodsActivity;
import com.aixianshengxian.activity.purchase.AddPurchaseActivity;
import com.aixianshengxian.activity.search.SearchProviderActivity;
import com.aixianshengxian.adapters.PlanRefreshAdapter;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.db.MyDatabaseHelper;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.module.PlanProduct;
import com.aixianshengxian.popupwindows.OrderManagerPopupWindow;
import com.aixianshengxian.util.DateUtils;
import com.aixianshengxian.util.SessionUtils;
import com.google.gson.reflect.TypeToken;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.idname.IdName;
import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;


import com.xmzynt.storm.service.goods.UnitPrice;
import com.xmzynt.storm.service.purchase.plan.ForecastPurchase;
import com.xmzynt.storm.service.user.supplier.Supplier;
import com.xmzynt.storm.util.GsonUtil;
import com.xmzynt.storm.util.StringUtil;
import com.xmzynt.storm.util.query.QueryFilter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class PlanActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PlanActivity";
    private final int SEARCH_PROVIDER_REQUEST = 7;
    private final int SEARCH_PROVIDER_RESULT = 8;
    private int PROVIDER_WHICH = 0;//用于判断是哪个供应商选择
    private String mParam1;
    private int ADD_NEW_PLAN = 0;//用于判断是哪个界面跳入选择商品界面

    private ImageView image_personal, iv_select, iv_search, iv_close_search, iv_add_new, iv_narrow;
    private EditText edt_search_content;
    private TextView tv_head_title, tv_choose_provider, tv_choose_time;
    private TextView tv_unit, tv_provider;
    private EditText edt_purchase_num;
    private Button btn_allcheck, btn_cancelcheck, btn_save;
    private CheckBox cbtn_choose;
    private RecyclerView listview_plan_list;
    //private SwipeRefreshLayout swipe_refresh_widget;
    private PlanRefreshAdapter mAdapter;
    private int lastVisibleItem;
    private LinearLayout ll_nomessage;
    private LinearLayoutManager layoutManager;
    //private List<PlanProduct> mAllList;
    private List<PlanProduct> mData = new ArrayList<>();
    private List<PlanProduct> mDataWhole;

    private List<ForecastPurchase> mAllDataList;
    private List<ForecastPurchase> mForecastPurchase = new ArrayList<>();
    private List<ForecastPurchase> mDataChecked = new ArrayList<>();
    /*private List<Integer> mCheckItems;*/
    SparseBooleanArray mCheckStates = new SparseBooleanArray();
    private List<String> mGoodsUuid = new ArrayList<>();
    private Map<String,List<UnitPrice>> mMapUnits;
    private List<UnitPrice> mUnit = new ArrayList<>();
    private ArrayList<String> mUnits = new ArrayList<String>();


    /*private boolean SP = true;
    Gson gson = new Gson();*/

    //private final String[] mListUnit = {"斤", "kg", "g", "个"};
    //private String[] mListProvider;//= {"哈哈农场","艾鲜农场"}
    private final String[] mListChooseProvider = {"全部", "哈哈农场", "艾鲜农场", "百合农场"};
    private final String[] mListState = {"全部", "未采购", "已采购"};

    private int page = 1;
    private int index = 0;
    private int pageNo = 0;
    private int pageSize = 20;
    private String status = null;
    private String today;

    private BigDecimal purchaseMin = BigDecimal.ZERO;//采购数最低标准
    private String compareProvider;
    private int narrowCount = 0;//记录添加按钮是否显示

    //时间选择器
    int mYear, mMonth, mDay;
    final int DATE_DIALOG = 1;

    public static PlanActivity mactivity;

    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        //数据库
        /*dbHelper = new MyDatabaseHelper(this, "ASData.db", null, 1000);//检测是否有数据库
        dbHelper.getWritableDatabase();//无，则创建*/

        setProgressBar();
        initViews();
        //initHrvsr();
        initEvents();
        getPlanList();
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("采购计划");
        iv_select = (ImageView) findViewById(R.id.iv_select);

        iv_search = (ImageView) findViewById(R.id.image_search);
        iv_close_search = (ImageView) findViewById(R.id.image_close_search);
        edt_search_content = (EditText) findViewById(R.id.edit_search_content);
        edt_search_content.setText(edt_search_content.getText());//显示用户的输入内容

        //iv_close_search.setVisibility(View.GONE);
        iv_add_new = (ImageView) findViewById(R.id.image_add_new);
        iv_narrow = (ImageView) findViewById(R.id.iv_narrow);
        btn_allcheck = (Button) findViewById(R.id.btn_allcheck);
        btn_cancelcheck = (Button) findViewById(R.id.btn_cancelcheck);
        btn_save = (Button) findViewById(R.id.btn_save);

        tv_choose_provider = (TextView) findViewById(R.id.tv_choose_provider);
        tv_choose_time = (TextView) findViewById(R.id.tv_choose_time);
        tv_choose_time.setText(DateUtils.getSomeDate(0)); //日期
        ll_nomessage = (LinearLayout) findViewById(R.id.ll_nomessage);

        mDataWhole = new ArrayList<>();
        /*SP = SPUtil.contains(PlanActivity.this,"PLAN");
        if(SP == true) {
            String str = (String) SPUtil.get(PlanActivity.this, "PLAN", "");
            if(str != null){
                mDataForever = gson.fromJson(str, new TypeToken<List<PlanProduct>>(){}.getType());
            }
        }*/

        mData.clear();
        mData.addAll(mDataWhole);

        //swipe_refresh_widget = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        listview_plan_list = (RecyclerView) findViewById(R.id.listview_plan_list);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        listview_plan_list.setLayoutManager(layoutManager);
        mAdapter = new PlanRefreshAdapter(mForecastPurchase, this);//将数据传入

        mAdapter.setOnItemListener(new PlanRefreshAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ForecastPurchase planProduct) {
            }

            @Override
            public void onCheckBoxClick(int position, Boolean isChecked) {
                cbtn_choose = (CheckBox) findViewById(R.id.cbtn_choose);
                if (isChecked) {
                    mCheckStates.put(position, true);
                } else {
                    mCheckStates.delete(position);
                }
            }

            @Override
            public void onDeleteClick(int position) {
                setDeleteAlertDialog(position);
            }

            @Override
            public void onUnitClick(int position, View v) {
                tv_unit = (TextView) v.findViewById(R.id.tv_unit);
                //unitAudit(v, position);
                dropDownUnit(position);
            }

            @Override
            public void onProviderClick(int position, View v) {
                tv_provider = (TextView) v.findViewById(R.id.tv_provider);
                PROVIDER_WHICH = 1;
                Intent intent = new Intent(PlanActivity.this, SearchProviderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Provider_which",PROVIDER_WHICH);
                bundle.putSerializable("Position",position);
                intent.putExtras(bundle);
                startActivityForResult(intent,SEARCH_PROVIDER_REQUEST);
            }

        });

        listview_plan_list.setAdapter(mAdapter);

        //时间选择器设置
        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);

        mactivity = this;
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
        mForecastPurchase.clear();
        getPlanList();
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
        iv_close_search.setOnClickListener(this);

        iv_add_new.setOnClickListener(this);
        iv_narrow.setOnClickListener(this);
        btn_allcheck.setOnClickListener(this);
        btn_cancelcheck.setOnClickListener(this);
        btn_save.setOnClickListener(this);

        tv_choose_provider.setOnClickListener(this);
        tv_choose_time.setOnClickListener(this);
    }

    private void setProgressBar() {
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("正在请求数据 ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    /*//下拉刷新处
    private void initHrvsr() {
        //设置刷新时动画的颜色，可以设置4个
        swipe_refresh_widget.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipe_refresh_widget.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        swipe_refresh_widget.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));

        swipe_refresh_widget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("zttjiangqq", "invoke onRefresh...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<ForecastPurchase> newDatas = new ArrayList<ForecastPurchase>();
                        index = 1;
                        mAllDataList.clear();
                        getPlanList();
                        mForecastPurchase.clear();
                        for (int i = 0; i < mAllDataList.size(); i++) {
                            mForecastPurchase.add(mAllDataList.get(i));
                            newDatas.add(mAllDataList.get(i));
                            index++;
                            page = index / 20;
                        }
                        mAdapter.addItem(newDatas);
                        swipe_refresh_widget.setRefreshing(false);
                        showShortToast("数据更新");
                    }
                }, 1500);
            }
        });
        //RecyclerView滑动监听
        listview_plan_list.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mAdapter.getItemCount()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List<ForecastPurchase> newDatas = new ArrayList<ForecastPurchase>();
                            if (mAllDataList != null && mAllDataList.size() > 0) {
                                mForecastPurchase.clear();
                                for (int i = 0; i < mAllDataList.size(); i++) {
                                    mForecastPurchase.add(mAllDataList.get(i));
                                    newDatas.add(mAllDataList.get(i));
                                    index++;
                                }

                            } else if (mAllDataList.size() == index) {
                                showCustomToast("全部加载完毕");
                            }
                            mAdapter.addMoreItem(newDatas);
                        }
                    }, 1000);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                finish();
                break;
            case R.id.iv_select:
                stateAudit(v);
                break;
            case R.id.image_search:
                contentSearch(v);
                break;
            case R.id.image_close_search:
                edt_search_content.setText("");
                mForecastPurchase.clear();
                mForecastPurchase.addAll(mAllDataList);
                mAdapter.notifyDataSetChanged();
                if (mForecastPurchase.size() == 0) {
                    ll_nomessage.setVisibility(View.VISIBLE);
                } else {
                    ll_nomessage.setVisibility(View.GONE);
                }
                break;
            case R.id.image_add_new:
                Intent intent1 = new Intent(PlanActivity.this,NewAddGoodsActivity.class);
                Bundle bundle1 = new Bundle();
                ADD_NEW_PLAN = 0;
                bundle1.putSerializable("Add_new",ADD_NEW_PLAN);
                intent1.putExtras(bundle1);
                startActivity(intent1);
                break;
            case R.id.iv_narrow:
                narrowCount++;
                if (narrowCount % 2 != 0) {
                    iv_add_new.setVisibility(View.GONE);
                } else {
                    iv_add_new.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_allcheck:
                for (int i = 0; i < mForecastPurchase.size(); i++) {
                    PlanRefreshAdapter.getIsSelected().put(i, true);
                }
                mAdapter.notifyDataSetChanged();
                btn_allcheck.setVisibility(View.GONE);
                btn_cancelcheck.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_cancelcheck:
                for (int i = 0; i < mForecastPurchase.size(); i++) {
                    PlanRefreshAdapter.getIsSelected().put(i, false);
                }
                mAdapter.notifyDataSetChanged();
                btn_allcheck.setVisibility(View.VISIBLE);
                btn_cancelcheck.setVisibility(View.GONE);
                break;
            case R.id.btn_save:
                checkedMessage();
                break;
            case R.id.tv_choose_provider:
                PROVIDER_WHICH = 0;
                Intent intent2 = new Intent(PlanActivity.this, SearchProviderActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable("Provider_which",PROVIDER_WHICH);
                intent2.putExtras(bundle2);
                startActivityForResult(intent2,SEARCH_PROVIDER_REQUEST);
                //chooseProviderAudit(v);
                break;
            case R.id.tv_choose_time:
                showDialog(DATE_DIALOG);
                //chooseTime();
                break;
            default:
                break;
        }
    }

    //供应商
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (PROVIDER_WHICH == 0) {//值为0时获得的数据显示在顶部供应商处
            //根据供应商搜索数据
            if(requestCode == SEARCH_PROVIDER_REQUEST){
                if(resultCode == SEARCH_PROVIDER_RESULT){
                    Supplier supplier = (Supplier) data.getExtras().getSerializable("supplier");
                    if(supplier != null){
                        showLogDebug(TAG,"onPlanActivityProviderResult"+mParam1);
                        String supplierId = supplier.getUuid();
                        String supplierName = supplier.getName();
                        tv_choose_provider.setText(supplierName);
                        mForecastPurchase.clear();
                        if (StringUtil.isNullOrBlank(supplierId)) {
                            mForecastPurchase.addAll(mAllDataList);
                        } else {
                            for (int i = 0;i < mAllDataList.size();i ++) {
                                ForecastPurchase forecastPurchase = mAllDataList.get(i);
                                String providerId =  forecastPurchase.getSupplier()==null?null:forecastPurchase.getSupplier().getId();
                                if (providerId != null) {
                                    if (providerId.equals(supplierId)) {
                                        mForecastPurchase.add(mAllDataList.get(i));
                                    }
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();

                        if (mForecastPurchase.size() == 0) {
                            ll_nomessage.setVisibility(View.VISIBLE);
                        } else {
                            ll_nomessage.setVisibility(View.GONE);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        } else {
            //根据供应商选择更新数据
            if(requestCode == SEARCH_PROVIDER_REQUEST){
                if(resultCode == SEARCH_PROVIDER_RESULT){
                    Supplier supplier = (Supplier) data.getExtras().getSerializable("supplier");
                    int position = (int)data.getExtras().getSerializable("Position");
                    if(supplier != null){
                        showLogDebug(TAG,"onPlanListActivityProviderResult"+mParam1);
                        ForecastPurchase forecastpurchase = mForecastPurchase.get(position);
                        //String supplierName = supplier.getName();
                        String supplierName =  forecastpurchase.getSupplier()==null?null:forecastpurchase.getSupplier().getName();
                        IdName idName = new IdName(supplier.getUuid(),supplier.getName());
                        mForecastPurchase.get(position).setSupplier(idName);
                        tv_provider.setText(supplierName);
                        mAdapter.notifyDataSetChanged();
                        saveModify(position);
                    }
                }
            }
        }
    }

    //删除提示对话框
    public void setDeleteAlertDialog(final int position) {
        final AlertDialog alert = new AlertDialog.Builder(PlanActivity.this).create();
        alert.setTitle("提示信息");
        //title.setTextColor(getResources().getColor(R.color.black));//标题文字颜色
        alert.setMessage("确认删除该采购计划\n" + mForecastPurchase.get(position).getGoods().getName() +
                mForecastPurchase.get(position).getPurchaseQty() + mForecastPurchase.get(position).getGoodsUnit().getName() + "?");
        //message.setTextColor(getResources().getColor(R.color.text_color_gray_2));
        alert.setCancelable(false);
        //添加确定按钮
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                /*//删除数据库相应内容
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete("PlanProduct", "planId = ?", new String[]{String.valueOf(mData.get(position).getPlanId())});*/
                mAdapter.notifyDataSetChanged();
                planDelete(position);
                mForecastPurchase.remove(position);
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

    //同一个供应商提示对话框
    public void setCheckProviderAlertDialog() {
        final AlertDialog alert = new AlertDialog.Builder(PlanActivity.this).create();
        alert.setTitle("提示信息");
        //title.setTextColor(getResources().getColor(R.color.black));//标题文字颜色
        alert.setMessage("只能选择同一个供应商的采购计划！");
        //message.setTextColor(getResources().getColor(R.color.text_color_gray_2));
        alert.setCancelable(false);
        //添加确定按钮
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();
        //确定按钮字的颜色
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.green_text));
    }
    //采购数是否符合标准提示对话框
    public void setPurchaseNumAlertDialog() {
        final AlertDialog alert = new AlertDialog.Builder(PlanActivity.this).create();
        alert.setTitle("提示信息");
        //title.setTextColor(getResources().getColor(R.color.black));//标题文字颜色
        alert.setMessage("请检查采购数信息，采购数必须大于0！");
        //message.setTextColor(getResources().getColor(R.color.text_color_gray_2));
        alert.setCancelable(false);
        //添加确定按钮
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();
        //确定按钮字的颜色
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.green_text));
    }

    //采购状态是否符合标准提示对话框
    public void setPurchaseStatusDialog() {
        final AlertDialog alert = new AlertDialog.Builder(PlanActivity.this).create();
        alert.setTitle("提示信息");
        //title.setTextColor(getResources().getColor(R.color.black));//标题文字颜色
        alert.setMessage("请检查采购信息，采购状态必须为未采购！");
        //message.setTextColor(getResources().getColor(R.color.text_color_gray_2));
        alert.setCancelable(false);
        //添加确定按钮
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();
        //确定按钮字的颜色
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.green_text));
    }

    //检查信息是否符合标准
    public void checkedMessage() {
        if (mCheckStates != null && mCheckStates.size() > 0) {
            //循环获得checked状态为true的mData位置
            ArrayList<Integer> selItems = new ArrayList<>();
            if (mCheckStates != null && mCheckStates.size() > 0) {
                for (int i = 0; i < mCheckStates.size(); i++) {
                    if (mCheckStates.valueAt(i)) {
                        selItems.add(mCheckStates.keyAt(i));
                    }
                }
            }
            //把check为true的数据存放到另外一个数组中
            if (selItems != null && selItems.size() > 0) {
                mDataChecked.clear();
                for (int i = 0; i < selItems.size(); i++) {
                    int position = selItems.get(i);//获取check为true的mData的位置
                    mDataChecked.add(mForecastPurchase.get(position));//通过该位置获取mData的值，存在mDataChecked中
                }
            }
            //进行供应商对比
            if (mDataChecked != null && mDataChecked.size() > 0) {
                int All = 0;//统计

                for (int i = 0;i < mDataChecked.size();i ++) {//获取一个用于比较的供应商名称
                    ForecastPurchase forecastPurchase = mDataChecked.get(i);
                    compareProvider = forecastPurchase.getSupplier()==null?null:forecastPurchase
                            .getSupplier().getName();
                }
                //String firstProvider = mDataChecked.get(0).getSupplier().getName();
                //List<String> cPlist = new ArrayList<>();
                for (int i = 0; i < mDataChecked.size(); i++) {//进行比较
                    ForecastPurchase forecastPurchase = mDataChecked.get(i);
                    String provider = forecastPurchase.getSupplier()==null?null:forecastPurchase
                            .getSupplier().getName();
                    if (provider != null) {
                        if (forecastPurchase.getSupplier().getName().equals(compareProvider)) {
                            All = All + 0;
                        } else {
                            All = All + 1;
                        }
                    }
                }

                //根据All值来弹出提示框，0则供应商相同，1则不同
                if (All == 0) {//对页面的数据进行统计
                    int Amount = 0;
                    //判断选中的数据采购数是否符合最低标准，Amount用来统计
                    if (mDataChecked != null && mDataChecked.size() > 0) {
                        for (int i = 0; i < mDataChecked.size(); i++) {
                            BigDecimal purchaseQty = mDataChecked.get(i).getPurchaseQty();
                            int result = purchaseQty.compareTo(purchaseMin);
                            if ( result > 0) {
                                Amount = Amount + 0;
                            } else {
                                Amount = Amount + 1;
                            }
                        }
                    }
                    if (Amount == 0) {
                        int Count = 0;
                        //勾选的项目是否都为未采购
                        if (mDataChecked != null && mDataChecked.size() > 0) {
                            for (int i = 0; i < mDataChecked.size(); i++) {
                                if (mDataChecked.get(i).getStatus().getCaption().equals("未采购")) {
                                    Count = Count + 0;
                                } else {
                                    Count = Count + 1;
                                }
                            }
                        }
                        if (Count == 0) {
                            setSendMessageAlertDialog();//这里是信息无误要连接接口的提示窗口
                        } else {
                            setPurchaseStatusDialog();
                        }
                    } else {
                        setPurchaseNumAlertDialog();//检查采购数的提示窗口
                    }

                } else {
                    setCheckProviderAlertDialog();
                }
            }
        } else {
            showCustomToast("采购单为空，请选择采购子项！");
            //Toast.makeText(PlanActivity.this,"采购单为空，请选择采购子项！",Toast.LENGTH_LONG).show();
        }
    }

    //确认信息提示对话框
    public void setSendMessageAlertDialog() {
        final AlertDialog alert = new AlertDialog.Builder(PlanActivity.this).create();
        alert.setTitle("提示信息");
        //title.setTextColor(getResources().getColor(R.color.black));//标题文字颜色
        alert.setMessage("采购信息正确，点击确认将生成购物单~");
        //message.setTextColor(getResources().getColor(R.color.text_color_gray_2));
        alert.setCancelable(false);
        //添加确定按钮
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //发送数据给下一个界面
                Intent intent = new Intent(PlanActivity.this,AddPurchaseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("activity","PlanActivity");
                bundle.putSerializable("ForecastPurchase", (Serializable) mDataChecked);//放进数据流中
                intent.putExtras(bundle);
                startActivity(intent);
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

    private void contentSearch(View v) {
        String searchContent = edt_search_content.getText().toString();
        if (searchContent.isEmpty()) {
            showCustomToast("请输入搜索条件");
            //Toast.makeText(PlanActivity.this, "请输入搜索条件", Toast.LENGTH_SHORT).show();
        } else {
            mForecastPurchase.clear();
            for (int i = 0; i < mAllDataList.size(); i++) {
                String productName = mAllDataList.get(i).getGoods().getName();
                if (productName.indexOf(searchContent) != -1) {
                    mForecastPurchase.add(mAllDataList.get(i));
                }
            }
            mAdapter.notifyDataSetChanged();
            if (mForecastPurchase.size() == 0) {
                ll_nomessage.setVisibility(View.VISIBLE);
            } else {
                ll_nomessage.setVisibility(View.GONE);
            }
        }
    }

    private void stateAudit(View v) {

        new OrderManagerPopupWindow(this, new OrderManagerPopupWindow.ResultListener() {
            @Override
            public void onResultChanged(int index) {
                switch (mListState[index]) {
                    case "全部":
                        status = "";
                        getPlanList();
                        break;
                    case "未采购":
                        status = "notPurchase";
                        getPlanList();
                        break;
                    case "已采购":
                        status = "hasPurchase";
                        getPlanList();
                        break;

                }
            }
        }, v, mListState);
        if (mForecastPurchase.size() == 0) {
            ll_nomessage.setVisibility(View.VISIBLE);
        } else {
            ll_nomessage.setVisibility(View.GONE);
        }

    }

    private void chooseProviderAudit(View v) {
        mDataWhole.clear();
        /*SP = SPUtil.contains(PlanActivity.this,"PLAN");
        if(SP == true) {
            String str = (String) SPUtil.get(PlanActivity.this, "PLAN", "");
            if(str != null){
                mDataWhole = gson.fromJson(str, new TypeToken<List<PlanProduct>>(){}.getType());
            }
        }*/

        new OrderManagerPopupWindow(this, new OrderManagerPopupWindow.ResultListener() {
            @Override
            public void onResultChanged(int index) {
                switch (mListChooseProvider[index]) {
                    case "全部":
                        tv_choose_provider.setText("全部");
                        mData.clear();
                        for (int i = 0; i < mDataWhole.size(); i++) {
                            mData.add(mDataWhole.get(i));
                        }
                        mAdapter.notifyDataSetChanged();
                        break;
                    case "哈哈农场":
                        tv_choose_provider.setText("哈哈农场");
                        if (mData != null) {
                            mData.clear();
                            for (int i = 0; i < mDataWhole.size(); i++) {
                                String[] PList = null;
                                String providerList = mDataWhole.get(i).getProvider();
                                PList = providerList.split("-");
                                String Provider1 = PList[0];
                                if (Provider1.equals("哈哈农场")) {
                                    mData.add(mDataWhole.get(i));
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                        break;
                    case "艾鲜农场":
                        tv_choose_provider.setText("艾鲜农场");
                        if (mData != null) {
                            mData.clear();
                            for (int i = 0; i < mDataWhole.size(); i++) {
                                String[] PList = null;
                                String providerList = mDataWhole.get(i).getProvider();
                                PList = providerList.split("-");
                                String Provider2 = PList[0];
                                if (Provider2.equals("艾鲜农场")) {
                                    mData.add(mDataWhole.get(i));
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                        break;
                    case "百合农场":
                        tv_choose_provider.setText("百合农场");
                        if (mData != null) {
                            mData.clear();
                            for (int i = 0; i < mDataWhole.size(); i++) {
                                String[] PList = null;
                                String providerList = mDataWhole.get(i).getProvider();
                                PList = providerList.split("-");
                                String Provider3 = PList[0];
                                if (Provider3.equals("百合农场")) {
                                    mData.add(mDataWhole.get(i));
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                        break;
                }
                if (mData.size() == 0) {
                    ll_nomessage.setVisibility(View.VISIBLE);
                } else {
                    ll_nomessage.setVisibility(View.GONE);
                }
            }
        }, v, mListChooseProvider);

    }

    private void chooseTime(View v) {
        mDataWhole.clear();
        /*SP = SPUtil.contains(PlanActivity.this,"PLAN");
        if(SP == true) {
            String str = (String) SPUtil.get(PlanActivity.this, "PLAN", "");
            if(str != null){
                mDataWhole = gson.fromJson(str, new TypeToken<List<PlanProduct>>(){}.getType());
            }
        }*/

        String chooseTime = (String) tv_choose_time.getText();
        if (mData != null) {
            mData.clear();
            for (int i = 0; i < mDataWhole.size(); i++) {
                String planTime = mDataWhole.get(i).getPlanTime();
                if (planTime.equals(chooseTime)) {
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

    //获取计划列表
    private void getPlanList() {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        today = String.valueOf(tv_choose_time.getText());
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            final QueryFilter filter = new QueryFilter();
            filter.setPage(pageNo);
            filter.setPageSize(pageSize);
            filter.setDefaultPageSize(0);
            filter.put(BasicConstants.Field.CREATED_TIME, today);
            filter.put(BasicConstants.Field.STATUS, status);
            //filter.setParams((Map<String, Object>) params);
            try {
                reparams.put(BasicConstants.URL_BODY, body);
                body.put(BasicConstants.Field.QUERY_FILTER, GsonUtil.getGson().toJson(filter));
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

        OkHttpUtils.postString().url(UrlConstants.URL_PLAN_GET_LIST)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(reparams.toString())
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if (progressBar != null) {
                            if (progressBar.isShowing()) {
                                progressBar.dismiss();
                                progressBar.cancel();
                            }
                        }
                        showCustomToast("请求失败");
                    }

                    @Override
                    public void onResponse(String s, int id) {
                        ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                        if (response.getErrorCode() == 0) {
                            Type listTypeA = new TypeToken<List<ForecastPurchase>>() {
                            }.getType();
                            if (response.getData() != null) {
                                mForecastPurchase.clear();
                                mAllDataList = GsonUtil.getGson().fromJson(response.getData(), listTypeA);
                                if (mAllDataList != null && mAllDataList.size() > 0) {
                                    mForecastPurchase.addAll(mAllDataList);
                                    ll_nomessage.setVisibility(View.GONE);
                                    if(mAllDataList.size()>20){
                                        for (int j = 0; j < 20; j++) {
                                            //mForecastPurchase.add(mAllDataList.get(j));
                                            index++;
                                            page = index / 20;
                                        }
                                    }else {
                                        for (int j = 0; j < mAllDataList.size(); j++) {
                                            //mForecastPurchase.add(mAllDataList.get(j));
                                            index++;
                                            page = index / 20;
                                        }
                                    }
                                } else {
                                    ll_nomessage.setVisibility(View.VISIBLE);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                            /*List<ForecastPurchase> forecastPurchase = GsonUtil.getGson().fromJson(response.getData(), listTypeA);
                            mForecastPurchase.addAll(forecastPurchase);
                            if (mForecastPurchase.size() == 0) {
                                ll_nomessage.setVisibility(View.VISIBLE);
                            } else {
                                ll_nomessage.setVisibility(View.GONE);
                                mAdapter.notifyDataSetChanged();
                            }*/
                            if (progressBar != null) {
                                if (progressBar.isShowing()) {
                                    progressBar.dismiss();
                                    progressBar.cancel();
                                }
                            }
                            showCustomToast("请求成功");
                            showLogDebug("main", s);
                        } else {
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }

    //计划删除
    private void planDelete(int position) {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        String uuid = mForecastPurchase.get(position).getUuid();
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            try {
                body.put(DataConstant.PLAN_UUID, uuid);
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
        OkHttpUtils.postString().url(UrlConstants.URL_PLAN_DELETE)
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
                        }  else {
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }

    //获取单位
    private void dropDownUnit(final int position) {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        for (int i = 0;i < mForecastPurchase.size();i ++) {
            String goodsUnit = mForecastPurchase.get(i).getGoods().getUuid();
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
        String GoodsUuid = mForecastPurchase.get(position).getGoods().getUuid();
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(PlanActivity.this);
        builder.setTitle("请选择单位");
        builder.setCancelable(true);

        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mForecastPurchase.get(position).setGoodsUnit(mUnit.get(which).getUnit());
            }
        });
        //添加确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                mAdapter.notifyDataSetChanged();//应该在点确定之后再更新界面
                saveModify(position);//单位编辑保存
            }
        });
        builder.create().show();
    }

    //编辑保存
    private void saveModify(final int position) {//编辑保存
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        ForecastPurchase change = mForecastPurchase.get(position);
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            try {
                body.put(DataConstant.FORECAST_PURCHASE, change);
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
        OkHttpUtils.postString().url(UrlConstants.URL_PLAN_SAVE_MODIFY)
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
                            get(position);
                            showCustomToast("请求成功");
                            showLogDebug("main", s);
                        }  else {
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }

    //根据uuid查询
    private void get(final int position) {//编辑保存后更新数据
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        String uuid = mForecastPurchase.get(position).getUuid();
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            try {
                reparams.put(BasicConstants.URL_BODY, body);
                body.put(DataConstant.PLAN_UUID, uuid);
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
        OkHttpUtils.postString().url(UrlConstants.URL_PLAN_GET)
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
                            Type listTypeA = new TypeToken<ForecastPurchase>() {
                            }.getType();
                            if (response.getData() != null) {
                                ForecastPurchase forecastPurchase = GsonUtil.getGson().fromJson(response.getData(), listTypeA);
                                //mForecastPurchase.remove(position);
                                mForecastPurchase.set(position,forecastPurchase);
                                mAdapter.notifyDataSetChanged();
                            }
                            showCustomToast("请求成功");
                            showLogDebug("main", s);
                        }  else {
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }
}
