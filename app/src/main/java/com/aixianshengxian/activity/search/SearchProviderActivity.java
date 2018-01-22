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
import com.aixianshengxian.adapters.SupplierRefreshAdapter;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.util.JsonUtil;
import com.aixianshengxian.util.SessionUtils;
import com.google.gson.reflect.TypeToken;

import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.constants.SaleConstants;
import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.service.user.supplier.Supplier;
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

public class SearchProviderActivity extends BaseActivity {

    private static  final String TAG = "SearchProviderActivity";

    private final int SEARCH_PROVIDER_RESULT = 8;
    private TextView tv_add_temp;
    private ImageView image_search;
    private ImageView image_personal;
    private TextView tv_head_title;

    private EditText edit_search_content;
    private ImageView image_close_search;
    private RecyclerView recylerView_goods_list;
    private SwipeRefreshLayout swipe_refresh_widget;
    private SupplierRefreshAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int lastVisibleItem;
    private List<Supplier> mAllList;
    private List<Supplier> mDataList = new ArrayList<>();

    private ProgressDialog progressBar;

    private int page = 1 ;
    private int index =0 ;
    private int pageNo = 0;
    private int pageSize = 20;
    private String mCustomerName = "" ;

    private int mProviderWhich;//用于区分是哪个部分传来供应商选择
    private int mPosition;//用于记住是哪个子项传来供应商选择

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_client);
        setProgressBar();
        getClientList();
        initViews();
        initHrvsr();
        getPosition();
        initEvents();
        setData();
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

    protected void getPosition() {
        Intent intent = getIntent();
        mProviderWhich = (int) intent.getSerializableExtra("Provider_which");
        if (mProviderWhich == 1) {
            mPosition = (int) intent.getSerializableExtra("Position");
        }
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
                       for(Supplier supplier : mAllList){
                           String name = supplier.getName();
                           if(name.contains(s)){
                               mDataList.add(supplier);
                           }
                       }
                   }
                    if(adapter != null){
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    mDataList.clear();
                    if(mAllList != null && mAllList.size()>0){
                        mDataList.addAll(mAllList);
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
        tv_head_title.setText("查找供应商");
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
        adapter = new SupplierRefreshAdapter(getApplicationContext(),mDataList);//记住要获取供应商的数据
        recylerView_goods_list.setAdapter(adapter);

        adapter.setOnItemClickLitener(new SupplierRefreshAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(Supplier supplier) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("supplier",supplier);//放进数据流中
                if (mProviderWhich == 1) {
                    bundle.putSerializable("Position",mPosition);
                }
                intent.putExtras(bundle);
                setResult(SEARCH_PROVIDER_RESULT,intent);
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
                        List<Supplier> newDatas = new ArrayList<Supplier>();
                        index = 0;
                        page = 1;
                        mDataList.clear();
                        Supplier all = new Supplier();
                        all.setName("全部");
                        mDataList.add(all);
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
                            List<Supplier> newDatas = new ArrayList<Supplier>();
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
     * 获取供应商列表
     */
    private void getClientList() {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        JSONObject reparams = JsonUtil.buildParams(getApplicationContext());
        try {
            JSONObject body = new JSONObject();
            final QueryFilter filter = new QueryFilter();
            filter.setPage(pageNo);
            filter.setPageSize(pageSize);
            filter.setDefaultPageSize(0);
            //filter.setParams((Map<String, Object>) params);
            try {
                reparams.put(BasicConstants.URL_BODY, body);
                body.put(SaleConstants.Field.QUERY_FILTER, GsonUtil.getGson().toJson(filter));
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

        OkHttpUtils.postString().url(UrlConstants.URL_SEARCH_PROVIDER)
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
                        ResponseBean res = GsonUtil.getGson().fromJson(s,ResponseBean.class);
                        if(res.getErrorCode() == 0){
                            Type listTypeA = new TypeToken<List<Supplier>>() {}.getType();
                            mAllList = GsonUtil.getGson().fromJson(res.getData(),listTypeA);
                            if(mAllList != null && mAllList.size()>0 && mAllList.size()>index){
                                Supplier all = new Supplier();
                                all.setName("全部");
                                mDataList.add(all);
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
                            showLogDebug("supplier",res.getData());
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
