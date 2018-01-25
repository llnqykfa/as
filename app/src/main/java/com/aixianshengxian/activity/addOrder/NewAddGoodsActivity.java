package com.aixianshengxian.activity.addOrder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.activity.machine.AddMachineActivity;
import com.aixianshengxian.activity.plan.AddPlanActivity;
import com.aixianshengxian.activity.purchase.AddPurchaseActivity;
import com.aixianshengxian.adapters.GoodBrandAdapter;
import com.aixianshengxian.adapters.GoodCategoryAdapter;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.module.GoodsItem;
import com.aixianshengxian.util.SessionUtils;
import com.aixianshengxian.view.RefreshRecyclerView;
import com.google.gson.reflect.TypeToken;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.service.goods.Goods;
import com.xmzynt.storm.service.goods.GoodsCategory;

import com.xmzynt.storm.service.user.supplier.Supplier;
import com.xmzynt.storm.util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class NewAddGoodsActivity extends BaseActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener,RefreshRecyclerView.OnLoadDataListener {
    private ImageView image_personal;
    private TextView tv_head_title,tv_no_message;
    private EditText edit_search_content;
    private ImageView image_close_search;
    private RecyclerView recyclerView_parent_list;
    private SwipeRefreshLayout swipe_refresh_widget;
    private RefreshRecyclerView recyclerView_goods_list;
    private Button btn_add_new_goods;

    private List<Goods> mGoods;//获取得到的商品
    private List<GoodsItem> mAllGoodsItem = new ArrayList<>();//向获得的商品填入未选择的状态
    private List<GoodsItem> mGoodsItem = new ArrayList<>();//界面中根据标签筛选显示的数据
    private Map<String,GoodsItem> mCheckedGoodsItem = new HashMap<>();//选中的商品记录
    private Map<String,List<GoodsItem>> MapmAllGoodsItem = new HashMap<>();//商品分类存放
    private List<GoodsItem> tempGoodItem = new ArrayList<>();//临时存放分类里的东西
    private List<String> mGoodsBySupplierUuid = new ArrayList<>();
    //private List<GoodsItem> mGoodsItemBySupplier = new ArrayList<>();

    private List<GoodsCategory> mAllCategory;//分类
    private List<GoodsCategory> mGoodsCategory = new ArrayList<>();//分类

    private GoodCategoryAdapter mCategoryAdapter;
    private GoodBrandAdapter mBrandAdapter;
    private LinearLayoutManager categoryLayoutManager;
    private LinearLayoutManager brandLayoutManager;

    //private int index = 0 ;
    //private int page = 0 ;
    //private int lastVisibleItem;
    private int ADD_NEW_PLAN = 0;

    private ProgressDialog progressBar;

    private static final int HANDLE_GET_GOODS_ITEM_MORE = 1;
    private int pageStart = 0;
    private int pageEnd = 20;
    private int pageCount = 0;
    private int pageNo = 0;

    private String Type = null;
    private String KeyWordsLike = null;
    private Boolean first = true;

    private int has = 0;
    private String supplierId = null;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            List<GoodsItem> datas = (List<GoodsItem>)msg.obj;
            switch (msg.what){
                case HANDLE_GET_GOODS_ITEM_MORE:
                    mBrandAdapter.addMoreItem(datas);
                    //mOutItem = mAdapter.getData();
                    //mBrandAdapter.notifyDataSetChanged();
                    recyclerView_goods_list.notifyNewData();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_add_goods);
        getAddnew();
        initViews();
        setProgressBar();
        initHrvsr();
        initEvents();
        getCategoryTree();
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

    private void setProgressBar() {
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("正在请求数据 ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void getAddnew() {
        Intent intent = getIntent();
        ADD_NEW_PLAN = (int) intent.getSerializableExtra("Add_new");
        if (ADD_NEW_PLAN == 6) {
            Type = (String) intent.getSerializableExtra("type");
        }
        else if (ADD_NEW_PLAN == 4) {
            has = (int) intent.getSerializableExtra("hasProvider");
            if (has == 1) {
                supplierId = (String) intent.getSerializableExtra("Provider");
                getPurchaseGoodsUuidBySupplier();
            }
        }
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("选择商品");
        edit_search_content = (EditText) findViewById(R.id.edit_search_content);
        image_close_search = (ImageView) findViewById(R.id.image_close_search);
        image_close_search.setVisibility(View.GONE);
        swipe_refresh_widget = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipe_refresh_widget.setOnRefreshListener(this);
        recyclerView_parent_list = (RecyclerView) findViewById(R.id.recylerView_parent_list);
        recyclerView_goods_list = (RefreshRecyclerView) findViewById(R.id.recylerView_goods_list);
        btn_add_new_goods = (Button) findViewById(R.id.btn_add_new_goods);

        tv_no_message = (TextView) findViewById(R.id.tv_no_message);
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        //tv_add_temp.setOnClickListener(this);
        btn_add_new_goods.setOnClickListener(this);
        edit_search_content.addTextChangedListener(new TextWatcher() {
            private CharSequence temp ;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    temp = s ;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !TextUtils.isEmpty(s)) {
                    //KeyWordsLike = String.valueOf(s);
                    //first = false;
                    //dropDownGoods();

                    recyclerView_goods_list.setLoadMoreEnable(false);
                    mGoodsItem.clear();
                    if (mAllGoodsItem!= null && mAllGoodsItem.size() > 0) {
                        for(GoodsItem goodsItem : mAllGoodsItem){
                            String name = goodsItem.getGoods().getName();
                            if(name.contains(s)){
                                mGoodsItem.add(goodsItem);
                            }
                        }
                    }
                    if(mBrandAdapter != null){
                        //mBrandAdapter.notifyDataSetChanged();
                        recyclerView_goods_list.notifyNewData();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                    finish();
                    break;
                    case R.id.btn_add_new_goods:
                        switch (ADD_NEW_PLAN) {
                    case 0:
                        Intent intent1 = new Intent(NewAddGoodsActivity.this, AddPlanActivity.class);
                        Bundle bundle1 = new Bundle();
                        bundle1.putSerializable("goods", (Serializable) mCheckedGoodsItem);//放进数据流中
                        intent1.putExtras(bundle1);
                        startActivity(intent1);
                        break;
                    case 1:
                        Intent intent2 = new Intent(NewAddGoodsActivity.this, AddPurchaseActivity.class);
                        Bundle bundle2 = new Bundle();
                        bundle2.putSerializable("activity","NewAddGoodsActivity");
                        bundle2.putSerializable("goods", (Serializable) mCheckedGoodsItem);//放进数据流中
                        intent2.putExtras(bundle2);
                        startActivity(intent2);
                        break;
                    case DataConstant.BUNDLE_ADD_GOODS_PURCHASE_EDIT:
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("goods", (Serializable)mBrandAdapter.getmCheckedGoodsItem());
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    case DataConstant.BUNDLE_ADD_GOODS_PURCHASE:
                        Intent intent3 = new Intent();
                        Bundle bundle3 = new Bundle();
                        bundle3.putSerializable("goods", (Serializable)mBrandAdapter.getmCheckedGoodsItem());
                        intent3.putExtras(bundle3);
                        setResult(DataConstant.ADD_NEW_GOOD_FOR_ADD_PURCHASE_RESULT, intent3);
                        finish();
                        break;
                    case DataConstant.BUNDLE_ADD_GOODS_FORECAST_PROCESS_PLAN:
                        Intent intent4 = new Intent(NewAddGoodsActivity.this, AddMachineActivity.class);
                        Bundle bundle4 = new Bundle();
                        bundle4.putSerializable("goods", (Serializable) mCheckedGoodsItem);//放进数据流中
                        intent4.putExtras(bundle4);
                        startActivity(intent4);
                        /*Intent intent4 = new Intent();
                        Bundle bundle4 = new Bundle();
                        bundle4.putSerializable("goods", (Serializable)mBrandAdapter.getmCheckedGoodsItem());
                        intent4.putExtras(bundle4);
                        setResult(DataConstant.ADD_NEW_GOOD_FOR_FORECAST_PROCESS_PLAN, intent4);
                        finish();*/
                        break;
                    case DataConstant.BUNDLE_ADD_GOODS_FORECAST_PROCESS_PLAN_ITEM:
                        Intent intent5 = new Intent();
                        Bundle bundle5 = new Bundle();
                        bundle5.putSerializable("goods", (Serializable)mBrandAdapter.getmCheckedGoodsItem());
                        intent5.putExtras(bundle5);
                        setResult(DataConstant.ADD_NEW_GOOD_FOR_FORECAST_PROCESS_PLAN_ITEM, intent5);
                        finish();
                }
                break;
            default:
                break;
        }
    }

    private void initHrvsr() {
        categoryLayoutManager = new LinearLayoutManager(this);
        categoryLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView_parent_list.setLayoutManager(categoryLayoutManager);
        mCategoryAdapter = new GoodCategoryAdapter(mGoodsCategory,this);//将数据传入
        mCategoryAdapter.setOnItemListener(new GoodCategoryAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(GoodsCategory goodsCategory,int position) {
                int lastPosition = mCategoryAdapter.getLastPosition();
                mCategoryAdapter.setSelectItem(position);
                mCategoryAdapter.notifyItemChanged(lastPosition);
                mCategoryAdapter.notifyItemChanged(position);
                mCategoryAdapter.setLastPosition(position);

                categoryLayoutManager.scrollToPositionWithOffset(position + 1,0);
                categoryLayoutManager.setStackFromEnd(false);
                edit_search_content.setText("");
                mGoodsItem.clear();
                pageStart = 0;
                pageEnd = 20;
                if (goodsCategory.getName() == "常用") {
                    getPurchaseGoodsBySupplier();
                } else {
                    List<String> leafs = new ArrayList<String>();
                    getLeafs(goodsCategory,leafs);
                    getGoodItems(leafs);
                }

                //mBrandAdapter.notifyDataSetChanged();
                recyclerView_goods_list.notifyNewData();
            }
        });
        recyclerView_parent_list.setAdapter(mCategoryAdapter);

        brandLayoutManager = new LinearLayoutManager(this);
        brandLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView_goods_list.setLayoutManager(brandLayoutManager);
        recyclerView_goods_list.setFooterResource(R.layout.view_footer);
        recyclerView_goods_list.setOnLoadDataListener(this);
        mBrandAdapter = new GoodBrandAdapter(mGoodsItem,mCheckedGoodsItem,this);

        mBrandAdapter.setOnItemListener(new GoodBrandAdapter.OnItemClickListener(){

            @Override
            public void onItemClick(GoodsItem goodsitem) {

            }
        });
        recyclerView_goods_list.setAdapter(mBrandAdapter);
    }

    private void getCategoryTree() {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        String keyWordsLike = null;
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            try {
                if (keyWordsLike != null) {
                    body.put(DataConstant.KEY_WORDS_LIKE, GsonUtil.getGson().toJson(keyWordsLike));
                }
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

        //progressBar.show();

        OkHttpUtils.postString().url(UrlConstants.URL_CATEGORY_TREE)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(reparams.toString())
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        /*if (progressBar != null) {
                            if (progressBar.isShowing()) {
                                progressBar.dismiss();
                                progressBar.cancel();
                            }
                        }*/
                        showCustomToast("请求失败");
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                        if (response.getErrorCode() == 0) {
                            Type listTypeA = new TypeToken<List<GoodsCategory>>() {
                            }.getType();
                            if (response.getData() != null) {
                                mGoodsCategory.clear();
                                if (ADD_NEW_PLAN == 4) {
                                    if (has == 1) {
                                        GoodsCategory frequently = new GoodsCategory();
                                        frequently.setName("常用");
                                        mGoodsCategory.add(frequently);
                                    }
                                }


                                mAllCategory = GsonUtil.getGson().fromJson(response.getData(), listTypeA);
                                if (mAllCategory != null && mAllCategory.size() >0) {
                                    mCategoryAdapter.setSelectItem(0);
                                    mCategoryAdapter.setLastPosition(0);
                                    mGoodsCategory.addAll(mAllCategory);
                                }
                                mCategoryAdapter.notifyDataSetChanged();
                            }
                            dropDownGoods();//获取商品
                            showCustomToast("请求成功");
                            showLogDebug("main", s);
                            /*if (progressBar != null) {
                                if (progressBar.isShowing()) {
                                    progressBar.dismiss();
                                    progressBar.cancel();
                                }
                            }*/
                        } else {
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }

    public void getLeafs(GoodsCategory category,List<String> leafs) {
        if (category.isLeafLevel()) {
            leafs.add(category.getUuid());
        } else {
            if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
                for (GoodsCategory goodsCategory : category.getSubCategories()) {
                    getLeafs(goodsCategory,leafs);
                }
            }
        }
    }

    private void dropDownGoods() {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        //String keyWordsLike = null;
        //String goodsType = null;
        String status = null;
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            try {
                if (KeyWordsLike != null || Type !=null || status != null) {
                    body.put(DataConstant.KEY_WORDS_LIKE, KeyWordsLike);
                    body.put(DataConstant.GOODS_TYPE, Type);
                    body.put(DataConstant.STATUS, status);
                }
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

        progressBar.show();

        OkHttpUtils.postString().url(UrlConstants.URL_DROP_DOWN_GOODS)
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
                    public void onResponse(String s, int i) {
                        ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                        if (response.getErrorCode() == 0) {
                            Type listTypeA = new TypeToken<List<Goods>>() {
                            }.getType();
                            if (response.getData() != null) {
                                mAllGoodsItem.clear();

                                mGoods = GsonUtil.getGson().fromJson(response.getData(), listTypeA);
                                if (mGoods != null && mGoods.size() > 0) {
                                    for (int j = 0;j < mGoods.size();j ++) {//goodsItem分类
                                        Goods goods = mGoods.get(j);
                                        GoodsItem goodsitem = new GoodsItem(goods,false);
                                        mAllGoodsItem.add(goodsitem);//用于全局查询筛选
                                        String cateId = goods.getCategory().getId();
                                        List<GoodsItem> list = MapmAllGoodsItem.get(cateId);
                                        if (list == null) {
                                            list = new ArrayList<GoodsItem>();
                                            MapmAllGoodsItem.put(cateId,list);
                                        }
                                        MapmAllGoodsItem.get(cateId).add(goodsitem);
                                    }
                                }

                                mGoodsItem.clear();
                                if (has == 0) {
                                    GoodsCategory goodsCategory = mGoodsCategory.get(0);
                                    List<String> leafs = new ArrayList<String>();
                                    getLeafs(goodsCategory,leafs);
                                    getGoodItems(leafs);
                                } else {
                                    getPurchaseGoodsBySupplier();
                                }
                                recyclerView_goods_list.notifyNewData();

                                /*if (first) {

                                } else {
                                    mGoodsItem.addAll(mAllGoodsItem);
                                }*/

                            }
                            showCustomToast("请求成功");
                            showLogDebug("main", s);
                            if (progressBar != null) {
                                if (progressBar.isShowing()) {
                                    progressBar.dismiss();
                                    progressBar.cancel();
                                }
                            }
                        } else {
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }

    public void getGoodItems(List<String> leafs) {
        for (String leaf : leafs) {
            if (MapmAllGoodsItem.get(leaf) != null) {
                tv_no_message.setVisibility(View.GONE);
                tempGoodItem = null;
                tempGoodItem = MapmAllGoodsItem.get(leaf);
                int size = tempGoodItem.size();
                //mGoodsItem.addAll(tempGoodItem);
                pageCount = size/20;
                if (size > pageCount * 20) {
                    pageCount ++;
                }
                if (size <= 20) {
                    recyclerView_goods_list.setLoadMoreEnable(false);
                    mGoodsItem.addAll(tempGoodItem);
                } else {
                    recyclerView_goods_list.setLoadMoreEnable(true);
                    //List<GoodsItem> temp = tempGoodItem.subList(pageStart,pageEnd);
                    initGoodsItem(HANDLE_GET_GOODS_ITEM_MORE);
                    //mGoodsItem.addAll(temp);
                }
            } else {
                tv_no_message.setVisibility(View.VISIBLE);
            }
        }
        //mBrandAdapter.notifyDataSetChanged();
        //recyclerView_goods_list.notifyNewData();
    }

    private void initGoodsItem(final int state) {
        List<GoodsItem> temp = null;
        if (pageEnd > tempGoodItem.size()) {
            recyclerView_goods_list.setLoadMoreEnable(false);//不加载
            temp = tempGoodItem.subList(pageStart,tempGoodItem.size());
            //initGoodsItem(HANDLE_GET_GOODS_ITEM_MORE);
        } else {
            recyclerView_goods_list.setLoadMoreEnable(true);//加载
            temp = tempGoodItem.subList(pageStart,pageEnd);
            //initGoodsItem(HANDLE_GET_GOODS_ITEM_MORE);
        }
        //mGoodsItem.addAll(temp);

        pageStart = pageStart + 20;
        pageEnd = pageEnd + 20;
        handler.obtainMessage(state,temp).sendToTarget();
    }

    private void getPurchaseGoodsUuidBySupplier() {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            try {
                body.put(DataConstant.SUPPLIER_UUID, supplierId);
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
        OkHttpUtils.postString().url(UrlConstants.URL_PURCHASE_GET_PURCHASED_GOODS_BY_SUPPLIER)
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
                            Type listTypeA = new TypeToken<List<String>>() {
                            }.getType();
                            if (response.getData() != null) {
                                mGoodsBySupplierUuid = GsonUtil.getGson().fromJson(response.getData(), listTypeA);
                            }
                            showCustomToast("请求成功");
                            showLogDebug("main", s);
                        } else {
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }

    private void getPurchaseGoodsBySupplier() {
        swipe_refresh_widget.setRefreshing(false);
        recyclerView_goods_list.setLoadMoreEnable(false);
        if (mGoodsBySupplierUuid.size() > 0) {
            //mGoodsItemBySupplier.clear();
            for (int i = 0;i < mGoodsBySupplierUuid.size();i ++) {
                for (int j = 0;j < mAllGoodsItem.size();j ++) {
                    //String goodsUuid = mAllGoodsItem.get(j).getGoods().getUuid();
                    //String goodsUuidBySupplier = mGoodsBySupplierUuid.get(i);
                    if (mAllGoodsItem.get(j).getGoods().getUuid().equals(mGoodsBySupplierUuid.get(i))) {
                        mGoodsItem.add(mAllGoodsItem.get(j));
                    }
                }
            }
            //mGoodsItem.addAll(mGoodsItemBySupplier);
            //recyclerView_goods_list.notifyNewData();
        } else {
            tv_no_message.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void pullUpRefresh() {
        if(pageNo + 1< pageCount) {
            //pageNo++;
            initGoodsItem(HANDLE_GET_GOODS_ITEM_MORE);
        }
        recyclerView_goods_list.loadMoreComplete();
    }

    @Override
    public void onRefresh() {
        swipe_refresh_widget.setRefreshing(false);
    }
}
