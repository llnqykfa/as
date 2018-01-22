package com.aixianshengxian.activity.search;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.adapters.ClientRefreshAdapter;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.util.JsonUtil;

import com.google.gson.reflect.TypeToken;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.constants.SaleConstants;
import com.xmzynt.storm.common.EnableStatus;
import com.xmzynt.storm.service.user.customer.MerchantCustomer;
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

public class SearchClientActivity extends BaseActivity {

    private static  final String TAG = "SearchClientActivity";

    private final int SEARCH_CLIENT_RESULT = 2;

    private ImageView image_personal;
    private TextView tv_head_title;
    private TextView tv_add_temp;
    private ImageView image_search;
    private EditText edit_search_content;
    private ImageView image_close_search;
    private RecyclerView recylerView_goods_list;
    private SwipeRefreshLayout swipe_refresh_widget;
    private ClientRefreshAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int lastVisibleItem;
    private List<MerchantCustomer> mAllList;
    private List<MerchantCustomer> mDataList =new ArrayList<>();

    private int page = 1 ;
    private int index =0 ;//MerchantCustomer索引
    //private  List<Department> mDepartments;
    private String mCustomerName = "" ;

    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_client);
        setProgressBar();
        getClientList();
        initViews();
        initHrvsr();
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

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_add_temp = (TextView) findViewById(R.id.tv_add_temp);
        image_search = (ImageView) findViewById(R.id.image_search);
        edit_search_content = (EditText) findViewById(R.id.edit_search_content);
        image_close_search = (ImageView) findViewById(R.id.image_close_search);
        image_close_search.setVisibility(View.GONE);
        recylerView_goods_list = (RecyclerView) findViewById(R.id.recylerView_goods_list);
        swipe_refresh_widget = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edit_search_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s != null && !TextUtils.isEmpty(s)){
                    mDataList.clear();
                   if(mAllList != null && mAllList.size()>0){
                       for(MerchantCustomer merchantCustomer : mAllList){
                           String name = merchantCustomer.getCustomer().getName();
                           if(name.contains(s)){
                               mDataList.add(merchantCustomer);
                           }
                       }
                   }
                    if(adapter != null){
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }

    private void setProgressBar() {
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("正在请求数据 ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void setData(){
        tv_head_title.setText("查找客户");
    }

    private void initHrvsr(){

        //设置刷新时动画的颜色，可以设置4个
        swipe_refresh_widget.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipe_refresh_widget.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        swipe_refresh_widget.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recylerView_goods_list.setLayoutManager(linearLayoutManager);
        adapter = new ClientRefreshAdapter(getApplicationContext(),mDataList);
        recylerView_goods_list.setAdapter(adapter);

        adapter.setOnItemClickLitener(new ClientRefreshAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(MerchantCustomer merchantCustomer) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("merchantCustomer",merchantCustomer);
                intent.putExtras(bundle);
                setResult(SEARCH_CLIENT_RESULT,intent);
                finish();
              //  getDepartment(merchantCustomer);
            }
        });
        swipe_refresh_widget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("zttjiangqq", "invoke onRefresh...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<MerchantCustomer> newDatas = new ArrayList<MerchantCustomer>();
                        index = 0;
                        page = 1;
                        mDataList.clear();
                        for (int i = 0; i < 20; i++) {
                            mDataList.add(mAllList.get(i));
                            index ++ ;
                            showLogDebug(TAG,"index:"+index);
                            showLogDebug(TAG,"page:"+page);
                            page = index/20;
                        }
                        adapter.notifyDataSetChanged();
//                        adapter.addItem(newDatas);
                        swipe_refresh_widget.setRefreshing(false);
                        showShortToast("数据刷新");
                    }
                }, 1500);
            }
        });
        //RecyclerView滑动监听
        recylerView_goods_list.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List<MerchantCustomer> newDatas = new ArrayList<MerchantCustomer>();
                            if(mAllList!= null && mAllList.size()>= index && index>= 0){
                                for(int i = index-1 ;i < mAllList.size() ; i ++){
                                    newDatas.add(mAllList.get(i));
                                    index++ ;
                                }

                            }else if(mAllList.size() == index){
                                showCustomToast("全部加载完毕");
                            }
                            adapter.addMoreItem(newDatas);
                        }
                    },1000);
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    /***
     * 获取客户列表
     */
    private void getClientList(){
        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        JSONObject body = new JSONObject();
        QueryFilter filter = new QueryFilter();
        filter.put(SaleConstants.Field.COOPERATION_STATUS, EnableStatus.enabled.name());
        try {
            params.put(BasicConstants.URL_BODY, body);
            body.put(SaleConstants.Field.QUERY_FILTER, GsonUtil.getGson().toJson(filter));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.show();

        OkHttpUtils.postString().url(UrlConstants.URL_CLIENTS)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(params.toString())
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
                        ResponseBean res = GsonUtil.getGson().fromJson(s,ResponseBean.class);
                        if(res.getErrorCode() == 0){
                            Type listTypeA = new TypeToken<List<MerchantCustomer>>() {}.getType();
                            mAllList = GsonUtil.getGson().fromJson(res.getData(),listTypeA);
                            if(mAllList != null && mAllList.size()>0 && mAllList.size()>index){
                                if(mAllList.size()>index+20){
                                    for(int j = 0 ; j< 20; j ++){
                                        mDataList.add(mAllList.get(index+j));
                                        index ++ ;
                                        page = index/20;
                                    }
                                }else {
                                    for(int j= 0 ; j < mAllList.size()-index; j++){
                                        mDataList.add(mAllList.get(index+j));
                                        index ++ ;
                                        page = index/20;
                                    }
                                }

                            }
                            adapter.notifyDataSetChanged();
                            //showShortToast("请求成功");
                            showLogDebug("client",res.getData());

                            if (progressBar != null) {
                                if (progressBar.isShowing()) {
                                    progressBar.dismiss();
                                    progressBar.cancel();
                                }
                            }
                        }else {
                            showCustomToast(res.getMessage());
                        }
                    }
                });

    }
}
