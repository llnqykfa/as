package com.aixianshengxian.activity.addOrder;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.adapters.ClientRefreshAdapter;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.util.JsonUtil;
import com.google.gson.reflect.TypeToken;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.constants.SaleConstants;
import com.xmzynt.storm.basic.constants.WMSConstants;
import com.xmzynt.storm.common.EnableStatus;
import com.xmzynt.storm.service.user.customer.Department;
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

public class ChooseClientActivity extends BaseActivity {

    private ImageView image_personal;
    private RecyclerView recylerView_goods_list;
    private SwipeRefreshLayout swipe_refresh_widget;
    private LinearLayout top_bar_linear_back;
    private TextView top_bar_title;
    private ClientRefreshAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int lastVisibleItem;
    private List<MerchantCustomer> mAllList;
    private List<MerchantCustomer> mDataList = new ArrayList<>();
    private int page = 1;
    private int index = 1;//索引
    private List<Department> mDepartments;
    private EditText edit_search_content;
    private TextView tv_head_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_client);
        initView();
        getClientList();
        initViews();
        initHrvsr();
        initEvents();


    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        recylerView_goods_list = (RecyclerView) findViewById(R.id.recylerView_goods_list);
        swipe_refresh_widget = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        edit_search_content = (EditText) findViewById(R.id.edit_search_content);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("选择客户");
        image_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        adapter = new ClientRefreshAdapter(getApplicationContext(), mDataList);
        recylerView_goods_list.setAdapter(adapter);

        adapter.setOnItemClickLitener(new ClientRefreshAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(MerchantCustomer merchantCustomer) {
                getDepartment(merchantCustomer);
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
                        index = 1;
                        for (int i = 0; i < 20; i++) {
                            newDatas.add(mAllList.get(i));
                            index++;
                            page = index / 20;
                        }
                        adapter.addItem(newDatas);
                        swipe_refresh_widget.setRefreshing(false);
                        showShortToast("更新了20条数据");
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
                            if (mAllList != null && mAllList.size() >= index && index >= 0) {
                                for (int i = index - 1; i < mAllList.size(); i++) {
                                    newDatas.add(mAllList.get(i));
                                    index++;
                                }

                            } else if (mAllList.size() == index) {
                                showCustomToast("全部加载完毕");
                            }
                            adapter.addMoreItem(newDatas);
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
        edit_search_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !TextUtils.isEmpty(s)) {
                    mDataList.clear();
                    index = 1 ;
                    if (mAllList != null && mAllList.size() > 0) {
                        for (MerchantCustomer merchantCustomer : mAllList) {
                            String name = merchantCustomer.getCustomer().getName();
                            if (name.contains(s)) {
                                mDataList.add(merchantCustomer);
                            }
                        }
                    }
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }else {
                    mDataList.clear();
                    index = 1 ;
                    if (mAllList != null && mAllList.size() > 0) {
                        for (int j = 0; j < 20; j++) {
                            mDataList.add(mAllList.get(j));
                            index++;
                            page = index / 20;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    /***
     * 获取客户列表
     */
    private void getClientList() {
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
        OkHttpUtils.postString().url(UrlConstants.URL_CLIENTS)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(params.toString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        showCustomToast("请求失败");
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        ResponseBean res = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                        if (res.getErrorCode() == 0) {
                            Type listTypeA = new TypeToken<List<MerchantCustomer>>() {
                            }.getType();
                            mAllList = GsonUtil.getGson().fromJson(res.getData(), listTypeA);

                            if (mAllList != null && mAllList.size() > 0) {
                                if(mAllList.size()>20){
                                    for (int j = 0; j < 20; j++) {
                                        mDataList.add(mAllList.get(j));
                                        index++;
                                        page = index / 20;
                                    }
                                }else {
                                    for (int j = 0; j < mAllList.size(); j++) {
                                        mDataList.add(mAllList.get(j));
                                        index++;
                                        page = index / 20;
                                    }
                                }

                            }
                            adapter.notifyDataSetChanged();
                            //showShortToast("请求成功");
                            showLogDebug("client", res.getData());
                        } else {
                            showCustomToast(res.getMessage());
                        }
                    }
                });

    }

    private void getDepartment(final MerchantCustomer merchantCustomer) {
        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        final JSONObject body = new JSONObject();
        try {
            body.put(WMSConstants.Field.ORG_UUID, merchantCustomer.getCustomer().getUuid());
            params.put(BasicConstants.URL_BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString().url(UrlConstants.URL_DEPARTMENTS)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(params.toString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        showCustomToast("网络请求失败");
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        ResponseBean res = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                        if (res.getErrorCode() == 0) {
                            //showCustomToast("请求成功");
                            showLogDebug("depar", "" + res.getData());

                            Type listTypeA = new TypeToken<List<Department>>() {
                            }.getType();
                            mDepartments = GsonUtil.getGson().fromJson(res.getData(), listTypeA);
                            if (mDepartments != null && mDepartments.size() > 0) {
                                Bundle bundle = new Bundle();
                                bundle.putString("departments", res.getData());
                                bundle.putSerializable("merchantClient", merchantCustomer);
                                startActivity(ChooseDepartmentActivity.class, bundle);
                                //finish();
                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("merchantClient", merchantCustomer);
                                startActivity(NewOrderActivity.class, bundle);
                               // finish();
                            }
                        } else {
                            showCustomToast(res.getMessage());
                        }
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

    private void initView() {
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
    }
}
