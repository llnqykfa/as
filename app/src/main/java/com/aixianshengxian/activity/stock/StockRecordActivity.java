package com.aixianshengxian.activity.stock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.activity.search.SearchDepotActivity;
import com.aixianshengxian.adapters.GoodCategoryAdapter;
import com.aixianshengxian.adapters.StockRecordAdapter;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.util.SessionUtils;
import com.google.gson.reflect.TypeToken;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.ucn.UCN;
import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.service.goods.GoodsCategory;
import com.xmzynt.storm.service.wms.stock.Stock;
import com.xmzynt.storm.service.wms.warehouse.Warehouse;
import com.xmzynt.storm.util.GsonUtil;
import com.xmzynt.storm.util.query.PageData;
import com.xmzynt.storm.util.query.QueryFilter;
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

public class StockRecordActivity extends BaseActivity implements View.OnClickListener {
    private final int SEARCH_DEPOT_REQUEST = 9;
    private final int SEARCH_DEPOT_RESULT = 10;

    private ImageView image_personal,image_search,image_close_search;
    private TextView tv_depot,tv_no_message;
    private EditText edit_search_content;
    private RecyclerView recylerView_parent_list;
    private RecyclerView recylerView_goods_list;

    private int page = 1;
    private int index = 0;
    private int pageNo = 0;
    private int pageSize = 0;

    private List<GoodsCategory> mAllCategory;//分类
    private List<GoodsCategory> mGoodsCategory = new ArrayList<>();//分类

    private List<Stock> mAllStock;//库存
    private List<Stock> mStock = new ArrayList<>();//库存
    private List<Stock> Stocks = new ArrayList<>();

    private Map<String,List<Stock>> MapmAllStock = new HashMap<>();//商品分类存放

    private GoodCategoryAdapter mCategoryAdapter;
    private StockRecordAdapter mStockAdapter;

    private LinearLayoutManager categoryLayoutManager;
    private LinearLayoutManager stockLayoutManager;

    private ProgressDialog progressBar;

    private String Activity;

    private UCN wareHouse = null;
    private String KeyWordsLike = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_record);

        getActivity();
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

    private void getActivity() {
        Intent intent = getIntent();
        Activity = (String) intent.getSerializableExtra("Activity");
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_depot = (TextView) findViewById(R.id.tv_head_title);
        tv_depot.setText("出库仓库:");
        image_search = (ImageView) findViewById(R.id.image_search);
        edit_search_content = (EditText) findViewById(R.id.edit_search_content);
        image_close_search = (ImageView) findViewById(R.id.image_close_search);
        image_close_search.setVisibility(View.GONE);
        recylerView_parent_list = (RecyclerView) findViewById(R.id.recylerView_parent_list);
        recylerView_goods_list = (RecyclerView) findViewById(R.id.recylerView_goods_list);

        tv_no_message = (TextView) findViewById(R.id.tv_no_message);
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        tv_depot.setOnClickListener(this);
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
                    KeyWordsLike = String.valueOf(s);

                    /*mGoodsItem.clear();
                    if (mAllGoodsItem != null && mAllGoodsItem.size() > 0) {
                        for(GoodsItem goodsItem : mAllGoodsItem){
                            String name = goodsItem.getGoods().getName();
                            if(name.contains(s)){
                                mGoodsItem.add(goodsItem);
                            }
                        }
                    }
                    if(mBrandAdapter != null){
                        mBrandAdapter.notifyDataSetChanged();
                    }*/
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
            case R.id.tv_head_title:
                Intent intent1 = new Intent(StockRecordActivity.this, SearchDepotActivity.class);
                startActivityForResult(intent1,SEARCH_DEPOT_REQUEST);
                break;
            default:
                break;
        }
    }

    //仓库
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SEARCH_DEPOT_REQUEST) {
            if (resultCode == SEARCH_DEPOT_RESULT) {
                Warehouse warehouse = (Warehouse) data.getExtras().getSerializable("depot");
                if (warehouse != null) {
                    UCN ucn = new UCN(warehouse.getUuid(), warehouse.getCode(), warehouse.getName());
                    wareHouse = ucn;
                    tv_depot.setText("出库仓库:" + warehouse.getName());
                    getCategoryTree();
                }
            }
        }
    }

    private void initHrvsr() {
        categoryLayoutManager = new LinearLayoutManager(this);
        categoryLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recylerView_parent_list.setLayoutManager(categoryLayoutManager);
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
                mStock.clear();
                List<String> leafs = new ArrayList<String>();
                getLeafs(goodsCategory,leafs);
                getStock(leafs);
                mStockAdapter.notifyDataSetChanged();
            }
        });
        recylerView_parent_list.setAdapter(mCategoryAdapter);

        stockLayoutManager = new LinearLayoutManager(this);
        stockLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recylerView_goods_list.setLayoutManager(stockLayoutManager);
        mStockAdapter = new StockRecordAdapter(mStock,this);

        mStockAdapter.setOnItemListener(new StockRecordAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(Stock stock) {
                getGoodsQueryStock(stock.getGoods().getCode());
            }
        });
        recylerView_goods_list.setAdapter(mStockAdapter);
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
                                mAllCategory = GsonUtil.getGson().fromJson(response.getData(), listTypeA);
                                if (mAllCategory != null && mAllCategory.size() >0) {
                                    mCategoryAdapter.setSelectItem(0);
                                    mCategoryAdapter.setLastPosition(0);
                                    mGoodsCategory.addAll(mAllCategory);
                                }
                                mCategoryAdapter.notifyDataSetChanged();
                            }

                            getQueryStock();
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

    //仓库查询库存
    private void getQueryStock() {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            final QueryFilter filter = new QueryFilter();
            filter.setPage(pageNo);
            filter.setPageSize(pageSize);
            filter.setDefaultPageSize(0);
            filter.put(DataConstant.MORETHANZERO,true);
            if (wareHouse != null) {
                filter.put(DataConstant.WAREHOUSEUUID,wareHouse.getUuid());
            }
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

        OkHttpUtils.postString().url(UrlConstants.URL_QUERY_STOCK)
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
                            if (response.getData() != null) {
                                PageData pageData = GsonUtil.getGson().fromJson(response.getData(), PageData.class);
//                            PageData pageData = GsonUtil.getGson().fromJson(response.getData(),PageData.class);
                                //mAllStock.clear();
                                mAllStock = GsonUtil.getGson().fromJson(GsonUtil.getGson().toJson(pageData.getValues()),new TypeToken<List<Stock>>(){}.getType());

                                if (mAllStock != null && mAllStock.size() > 0) {
                                    for (int i = 0;i < mAllStock.size();i ++) {//stock分类
                                        Stock stock = mAllStock.get(i);
                                        String cateId = stock.getGoodsCategory() == null?null:stock.getGoodsCategory().getId();

                                        if (cateId != null) {
                                            List<Stock> list = MapmAllStock.get(cateId);
                                            if (list == null) {
                                                list = new ArrayList<Stock>();
                                                MapmAllStock.put(cateId,list);
                                            }
                                        }

                                        MapmAllStock.get(cateId).add(stock);
                                    }
                                }

                                mStock.clear();
                                GoodsCategory goodsCategory = mGoodsCategory.get(0);
                                List<String> leafs = new ArrayList<String>();
                                getLeafs(goodsCategory,leafs);
                                getStock(leafs);
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

    //商品编号查询库存
    private void getGoodsQueryStock(String goodsCode) {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            final QueryFilter filter = new QueryFilter();
            filter.setPage(pageNo);
            filter.setPageSize(pageSize);
            filter.setDefaultPageSize(0);
            filter.put(DataConstant.MORETHANZERO,true);
            filter.put(DataConstant.GOODSCODE,goodsCode);
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

        OkHttpUtils.postString().url(UrlConstants.URL_QUERY_STOCK)
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
                            if (response.getData() != null) {
                                PageData pageData = GsonUtil.getGson().fromJson(response.getData(), PageData.class);
//                            PageData pageData = GsonUtil.getGson().fromJson(response.getData(),PageData.class);
                                //mAllStock.clear();
                                Stocks = GsonUtil.getGson().fromJson(GsonUtil.getGson().toJson(pageData.getValues()),new TypeToken<List<Stock>>(){}.getType());

                                if (mAllStock != null && mAllStock.size() > 0) {
                                    if (Activity.equals("AllocateActivity")) {
                                        Intent intent = new Intent(StockRecordActivity.this,AllocateDetailActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("Stocks", (Serializable) Stocks);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                    else if (Activity.equals("InventoryActivity")) {
                                        Intent intent = new Intent(StockRecordActivity.this,InventoryDetailActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("Stocks", (Serializable) Stocks);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                    else if (Activity.equals("StockRemovalActivity")) {
                                        Intent intent = new Intent(StockRecordActivity.this,RemovalDetailActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("Stocks", (Serializable) Stocks);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                }
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

    public void getStock(List<String> leafs) {
        for (String leaf : leafs) {
            if (MapmAllStock.get(leaf) != null) {
                mStock.addAll(MapmAllStock.get(leaf));
                tv_no_message.setVisibility(View.GONE);
            } else {
                tv_no_message.setVisibility(View.VISIBLE);
            }
        }
        mStockAdapter.notifyDataSetChanged();
    }
}
