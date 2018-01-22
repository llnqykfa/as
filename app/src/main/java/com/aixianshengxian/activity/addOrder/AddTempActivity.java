package com.aixianshengxian.activity.addOrder;

import android.content.Context;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.adapters.TempGoodsListAdapter;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.util.JsonUtil;
import com.google.gson.reflect.TypeToken;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.constants.WMSConstants;
import com.xmzynt.storm.service.goods.Goods;
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

public class AddTempActivity extends BaseActivity  {

    public static final String TAG ="AddTempActivity";
    public static final int GOODS_DETAIL_REQUEST = 1;
    public static final int GOODS_DETAIL_RESULT = 2 ;
    private final int ADD_TEMP_GOODS_RESULT = 2;
    private String keywords = "";
    private List<Goods> mDataList = new ArrayList<>();
    private int pageNo = 0;
    private int pageSize = 20;
    private ImageView image_personal;
    private TextView tv_head_title;
    private TextView tv_add_temp;
    private ImageView image_search;
    private EditText edit_search_content;
    private ImageView image_close_search;
    private RecyclerView recylerView_goods_list;
    private SwipeRefreshLayout swipe_refresh_widget;
    private LinearLayoutManager linearLayoutManager;
    private Button btn_add_new_goods;
    private TempGoodsListAdapter adapter ;
    private int lastVisibleItem;
    private boolean mIsRunning = false;
    private RelativeLayout relay_image;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOODS_DETAIL_REQUEST){
            if(resultCode == GoodsDetailActivity.GOODS_DETAIL_RESULT){
                setResult(ADD_TEMP_GOODS_RESULT,data);
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_temp_activity);
        initViews();
        loadData();
        initHrvsr();
        initEvents();

    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("临时添加");
        tv_add_temp = (TextView) findViewById(R.id.tv_add_temp);
        relay_image = (RelativeLayout) findViewById(R.id.relay_image);
        image_search = (ImageView) findViewById(R.id.image_search);
        edit_search_content = (EditText) findViewById(R.id.edit_search_content);
        //image_close_search = (ImageView) findViewById(R.id.image_close_search);
        recylerView_goods_list = (RecyclerView) findViewById(R.id.recylerView_goods_list);
        swipe_refresh_widget = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        btn_add_new_goods = (Button) findViewById(R.id.btn_add_new_goods);
        btn_add_new_goods.setVisibility(View.GONE);

    }
    private void initHrvsr() {

        //设置刷新时动画的颜色，可以设置4个
        swipe_refresh_widget.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipe_refresh_widget.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        swipe_refresh_widget.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recylerView_goods_list.setLayoutManager(linearLayoutManager);
        adapter = new TempGoodsListAdapter( mDataList,getApplicationContext());

        adapter.setOnItemClickLitener(new TempGoodsListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(Goods goods) {
                Intent intent  = new Intent(AddTempActivity.this,GoodsDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("goods",goods);
                intent.putExtras(bundle);
               startActivityForResult(intent,GOODS_DETAIL_REQUEST);
            }
        });
        recylerView_goods_list.setAdapter(adapter);

        swipe_refresh_widget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {//上拉刷新
                Log.d("zttjiangqq", "invoke onRefresh...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       pageNo = 0;
                        mDataList.clear();
                        loadData();
                        swipe_refresh_widget.setRefreshing(false);
                       // showShortToast("更新了20条数据");
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
                            if (mIsRunning) {
                                showLogDebug(TAG, "当前正在请求数据");
                                return;
                            }

                            mIsRunning = true;
                           pageNo ++ ;
                            loadData();

                        }
                    }, 1000);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }
    @Override
    protected void initEvents() {

        //edit_search_content.setOnKeyListener( onKeyListener);
        edit_search_content.addTextChangedListener(new TextWatcher() {
            private  CharSequence temp ;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = temp.toString();
                if(value != null && !TextUtils.isEmpty(value)){
                    keywords = value;
                    showLogDebug(TAG,keywords);
                    pageNo = 0 ;
                    mDataList.clear();
                    loadData();
                }else {
                    keywords ="";
                    pageNo = 0 ;
                    mDataList.clear();
                    loadData();
                }
            }
        });
        image_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        relay_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keywords = edit_search_content.getText().toString().trim();
                showLogDebug(TAG,keywords);
                pageNo = 0 ;
                mDataList.clear();
                loadData();
            }
        });
    }
    private View.OnKeyListener onKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                /*隐藏软键盘*/
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputMethodManager.isActive()){
                    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                keywords = edit_search_content.getText().toString().trim();
                showLogDebug(TAG,keywords);
                pageNo = 0 ;
                mDataList.clear();
                loadData();

                return true;
            }
            return false;
        }
    };

    private void loadData() {
        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        JSONObject body = new JSONObject();
        final QueryFilter filter = new QueryFilter();
        filter.setPage(pageNo);
        filter.setPageSize(pageSize);
        if (keywords != null && !TextUtils.isEmpty(keywords)) {
            filter.put(DataConstant.KEY_WORDS_LIKE , keywords);
        }
        try {
            params.put(BasicConstants.URL_BODY, body);
            body.put(WMSConstants.Field.QUERY_FILTER, GsonUtil.getGson().toJson(filter));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString().url(UrlConstants.URL_TEMP_GOODS_LIST)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(params.toString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        showCustomToast("请求失败");
                        mIsRunning =false;
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                        if (response.getErrorCode() == 0) {
                            Type listType = new TypeToken<List<Goods>>() {
                            }.getType();
                            if (response.getData() != null) {
                                List<Goods> tempList = GsonUtil.getGson().fromJson(response.getData(), listType);
                                if(!tempList.isEmpty()){
                                    showLogDebug(TAG, "getOrders: pageNo=" + pageNo);
                                    mDataList.addAll(tempList);
                                    if(adapter != null){
                                        adapter.notifyDataSetChanged();
                                    }

                                }else {
                                    showCustomToast("没有请求到数据");
//                                    pageNo -- ;
                                }

                            }

                        }else {
                            if(response.getMessage()!= null){
                                showCustomToast(response.getMessage());
                            }
                        }
                        mIsRunning = false ;

                    }
                });

    }




    private void submit() {
        // validate
        String content = edit_search_content.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "content不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something


    }
}
