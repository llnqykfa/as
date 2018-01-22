package com.aixianshengxian.activity.addOrder;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.adapters.GoodsListAdapter;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.entity.SalesCatalogLineItem;
import com.aixianshengxian.util.JsonUtil;
import com.aixianshengxian.util.SessionUtils;

import com.google.gson.reflect.TypeToken;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.constants.WMSConstants;
import com.xmzynt.storm.basic.user.UserType;

/*<<<<<<< HEAD

=======
>>>>>>> 438ba51b3593f0e0bbc7d2fa4e31266122663d90*/

import com.xmzynt.storm.service.sale.GoodsOrder;
import com.xmzynt.storm.service.sale.GoodsOrderLine;
import com.xmzynt.storm.service.sale.SalesCatalogLine;
import com.xmzynt.storm.service.user.merchant.Merchant;
import com.xmzynt.storm.util.GsonUtil;
import com.xmzynt.storm.util.query.QueryFilter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class AddGoodsActivity extends BaseActivity implements View.OnClickListener {

    private final int ADD_TEMP_GOODS_REQUEST = 1 ;
    private final int ADD_TEMP_GOODS_RESULT = 2;
    private  final int ADD_GOODS_RESUTL =3 ;
    private ImageView image_personal;
    private TextView tv_head_title;
    private TextView tv_add_temp;
    private ImageView image_search;
    private EditText edit_search_content;
    private ImageView image_close_search;
    private RecyclerView recylerView_goods_list;
    private Button btn_add_new_goods;

    private GoodsOrder goodsOrder;
    private List<SalesCatalogLine> loadDATAlist;//下载的数据
    private List<SalesCatalogLineItem> mAllDATAlist =new ArrayList<>();//本地封装数据
    private List<SalesCatalogLineItem> mDataList = new ArrayList<>();//本地封装数据
    private  GoodsListAdapter adapter ;
   // private SwipeRefreshLayout swipe_refresh_widget;
    private LinearLayoutManager linearLayoutManager;
    private int index = 0 ;
    private int page = 0 ;
    private int lastVisibleItem;
    private List<GoodsOrderLine> mGoodsOrderLines = new ArrayList<>();
    private ProgressDialog progressBar;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_TEMP_GOODS_REQUEST){
            if(resultCode == ADD_TEMP_GOODS_RESULT){
                GoodsOrderLine goodsOrderLine = (GoodsOrderLine) data.getExtras().getSerializable("goodsOrderLine");
                mGoodsOrderLines.add(goodsOrderLine);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods);
        initData();
        initViews();
        initHrvsr();
        setProgressBar();
        loadData();
        initEvents();
        setData();
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
    private void initData() {
        goodsOrder = (GoodsOrder) getIntent().getExtras().getSerializable("goodsOrder");
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_add_temp = (TextView) findViewById(R.id.tv_add_temp);
        tv_add_temp.setVisibility(View.VISIBLE);
        image_search = (ImageView) findViewById(R.id.image_search);
        edit_search_content = (EditText) findViewById(R.id.edit_search_content);
        image_close_search = (ImageView) findViewById(R.id.image_close_search);
        image_close_search.setVisibility(View.GONE);
        recylerView_goods_list = (RecyclerView) findViewById(R.id.recylerView_goods_list);
        btn_add_new_goods = (Button) findViewById(R.id.btn_add_new_goods);
        //swipe_refresh_widget = (SwipeRefreshLayout) findViewById(swipe_refresh_widget);
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        tv_add_temp.setOnClickListener(this);
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
                String value = temp.toString();
               mAllDATAlist = adapter.getmALL();
                if(mAllDATAlist != null && mAllDATAlist.size()>0){
                if( value != null && !TextUtils.isEmpty(value)){
                       mDataList.clear();
                        for(SalesCatalogLineItem salesCatalogLineItem : mAllDATAlist){
                            if(salesCatalogLineItem.getSalesCatalogLine().getGoods().getName().contains(value)){
                                mDataList.add(salesCatalogLineItem);
                            }
                        }

                    adapter.initData();
                        adapter.notifyDataSetChanged();
                }else {
                    mDataList.clear();
                    mDataList.addAll(mAllDATAlist);
                    adapter.initData();
                    adapter.notifyDataSetChanged();
                }
//                else {
//                    index = 0;
//                    mDataList.clear();
//                    if(loadDATAlist.size()> 20){
//                        for (int i = 0; i <20; i++) {
//                            mDataList.add(loadDATAlist.get(i));
//                            index++;
//                        }
//                    }else {
//                        for (int i = 0; i < loadDATAlist.size(); i++) {
//                            mDataList.add(loadDATAlist.get(i));
//                            index++;
//                        }
//                    }
//
//                    adapter.initData();
//                    adapter.notifyDataSetChanged();
//                }
            }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
               saveDataAndReturn();
                break;
            case R.id.tv_add_temp:
                Intent intent = new Intent(this,AddTempActivity.class);
                startActivityForResult(intent,ADD_TEMP_GOODS_REQUEST);
                break;
            case R.id.btn_add_new_goods:
             saveDataAndReturn();
                // startActivity(AddTempActivity.class);
                break;

            default:
                break;
        }
    }
    private void setData() {
        tv_head_title.setText("订价列表");

    }

    private void initHrvsr() {

//        //设置刷新时动画的颜色，可以设置4个
//        swipe_refresh_widget.setProgressBackgroundColorSchemeResource(android.R.color.white);
//        swipe_refresh_widget.setColorSchemeResources(android.R.color.holo_blue_light,
//                android.R.color.holo_red_light, android.R.color.holo_orange_light,
//                android.R.color.holo_green_light);
//        swipe_refresh_widget.setProgressViewOffset(false, 0, (int) TypedValue
//                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
//                        .getDisplayMetrics()));
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recylerView_goods_list.setLayoutManager(linearLayoutManager);
        adapter = new GoodsListAdapter( mDataList,getApplicationContext(),mAllDATAlist);
        recylerView_goods_list.setAdapter(adapter);

//        adapter.setOnItemClickLitener(new GoodsListAdapter.OnItemClickLitener() {
//            @Override
//            public void onItemClick(SalesCatalogLine salesCatalogLine) {//点击一下增加一条
//               // showCustomToast(salesCatalogLine.getGoods().getName());
//                GoodsOrderLine goodsOrderLine = new GoodsOrderLine();
//                goodsOrderLine.setGoods(salesCatalogLine.getGoods());
//                goodsOrderLine.setGoodsUnit(salesCatalogLine.getGoodsUnit());
//                goodsOrderLine.setOrderPrice(salesCatalogLine.getOrderPrice());
//                goodsOrderLine.setGoodsPic(salesCatalogLine.getGoodsThumbnail());
//                goodsOrderLine.setGoodsSpec(salesCatalogLine.getGoodsSpec());
//                goodsOrderLine.setGoodsAlias(salesCatalogLine.getGoodsAlias());
//                goodsOrderLine.setGoodsCategory(salesCatalogLine.getGoodsCategory());
//                mGoodsOrderLines.add(goodsOrderLine);
//                showCustomToast("添加成功");
//            }
//        });
       // swipe_refresh_widget.setRefreshing(false);//禁止刷新
//        swipe_refresh_widget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Log.d("zttjiangqq", "invoke onRefresh...");
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        index = 0;
//                        mDataList.clear();
//                        if(loadDATAlist.size()> 20){
//                            for (int i = 0; i <20; i++) {
//                                mDataList.add(loadDATAlist.get(i));
//                                index++;
//                            }
//                        }else {
//                            for (int i = 0; i < loadDATAlist.size(); i++) {
//                                mDataList.add(loadDATAlist.get(i));
//                                index++;
//                            }
//                        }
//
//                        adapter.initData();
//                        adapter.notifyDataSetChanged();
//                        swipe_refresh_widget.setRefreshing(false);
//                       // showShortToast("更新了20条数据");
//                    }
//                }, 1500);
//            }
//        });
        //RecyclerView滑动监听
//        recylerView_goods_list.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            List<SalesCatalogLine> newDatas = new ArrayList<SalesCatalogLine>();
//                            if (loadDATAlist != null && loadDATAlist.size() >= index && index >= 0) {
//                                for (int i = index ; i < loadDATAlist.size(); i++) {
//                                    newDatas.add(loadDATAlist.get(i));
//                                    index++;
//                                }
//
//                            } else if (loadDATAlist.size() == index) {
//                                showCustomToast("全部加载完毕");
//                            }
//                            adapter.addMoreItem(newDatas);
//                        }
//                    }, 1000);
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//            }
//        });
    }

    private void submit() {
        // validate
        String content = edit_search_content.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "content不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void saveDataAndReturn(){
        mAllDATAlist =adapter.getmALL();
        if(mAllDATAlist!= null && mAllDATAlist.size()>0){
            for(SalesCatalogLineItem salesCatalogLineItem: mAllDATAlist){
                if(salesCatalogLineItem.getSelector()){
                    SalesCatalogLine salesCatalogLine = salesCatalogLineItem.getSalesCatalogLine();
                    GoodsOrderLine goodsOrderLine = new GoodsOrderLine();
                    goodsOrderLine.setGoods(salesCatalogLine.getGoods());
                    goodsOrderLine.setGoodsUnit(salesCatalogLine.getGoodsUnit());
                    goodsOrderLine.setOrderPrice(salesCatalogLine.getOrderPrice());
                    goodsOrderLine.setGoodsPic(salesCatalogLine.getGoodsThumbnail());
                    goodsOrderLine.setGoodsSpec(salesCatalogLine.getGoodsSpec());
                    goodsOrderLine.setGoodsAlias(salesCatalogLine.getGoodsAlias());
                    goodsOrderLine.setGoodsCategory(salesCatalogLine.getGoodsCategory());
                    mGoodsOrderLines.add(goodsOrderLine);
                }
            }
        }
        Intent intent1 = new Intent();
        Bundle bundle = new Bundle();
//                if(goodsOrder.getLines() != null){
//                    goodsOrder.getLines().addAll(mGoodsOrderLines);
//                }else {
        goodsOrder.setLines(mGoodsOrderLines);
//                }
        bundle.putSerializable("goodsOrder",goodsOrder);
        intent1.putExtras(bundle);
        setResult(ADD_GOODS_RESUTL,intent1);
        finish();
    }

    private void loadData() {
        progressBar.show();
        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        final Merchant merchant = SessionUtils.getInstance(getApplicationContext()).getLoginMerchan();
        if (merchant != null && goodsOrder != null) {
            JSONObject body = new JSONObject();
            final QueryFilter filter = new QueryFilter();
            filter.put(WMSConstants.Field.ORG_UUID, merchant.getUserType() == UserType.admin ? merchant.getUuid() : merchant.getOrg().getId());
            filter.put(WMSConstants.Field.CUSTOMER_UUID, goodsOrder.getCustomer().getId());
            filter.put(WMSConstants.Field.DATE, goodsOrder.getDeliveryTime());
            try {
                body.put(WMSConstants.Field.QUERY_FILTER, GsonUtil.getGson().toJson(filter));
                params.put(BasicConstants.URL_BODY, body);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OkHttpUtils.postString().url(UrlConstants.URL_QUERY_LINE)
                    .addHeader("Cookie", "PHPSESSID=" + 123456)
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .addHeader("Content-Type", "application/json;chartset=utf-8")
                    .content(params.toString())
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            showCustomToast("请求失败");
                            if (progressBar != null) {
                                if (progressBar.isShowing()) {
                                    progressBar.dismiss();
                                    progressBar.cancel();
                                }
                                // progressBar = null;
                            }
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                            if (response.getErrorCode() == 0) {

                                Type listType = new TypeToken<List<SalesCatalogLine>>() {
                                }.getType();
                                if (response.getData() != null) {
                                    loadDATAlist = GsonUtil.getGson().fromJson(response.getData(), listType);
//                                    if(loadDATAlist != null &&loadDATAlist.size()>20){
//                                        for(int j = 0 ; j <20 ; j ++){
//                                            mDataList.add(loadDATAlist.get(j));
//                                        }
//                                    } else
//                                    if(loadDATAlist != null && loadDATAlist.size()>0 && loadDATAlist.size()<20){
                                    if(loadDATAlist != null && loadDATAlist.size()>0 ){
                                        for(int j = 0; j < loadDATAlist.size() ; j ++){
                                            SalesCatalogLine salesCatalogLine = loadDATAlist.get(j);
                                            //转为本地封装数据
                                            SalesCatalogLineItem salesCatalogLineItem = new SalesCatalogLineItem(salesCatalogLine,false);
                                            mAllDATAlist.add(salesCatalogLineItem);
                                            mDataList.add(salesCatalogLineItem);
                                        }
                                    }

                                    if(adapter != null){
                                        //adapter.initData();
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                                if (progressBar != null) {
                                    if (progressBar.isShowing()) {
                                        progressBar.dismiss();
                                        progressBar.cancel();
                                    }
                                    // progressBar = null;
                                }

                            } else {
                                showCustomToast(response.getMessage());
                                if (progressBar != null) {
                                    if (progressBar.isShowing()) {
                                        progressBar.dismiss();
                                        progressBar.cancel();
                                    }
                                    // progressBar = null;
                                }
                            }
                        }
                    });

        }

    }


}
