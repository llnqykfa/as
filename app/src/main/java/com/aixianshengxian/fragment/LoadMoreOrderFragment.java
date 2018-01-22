package com.aixianshengxian.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.BaseFragment;
import com.aixianshengxian.R;
import com.aixianshengxian.activity.OrderDetailActivity;
import com.aixianshengxian.activity.search.SearchClientActivity;
import com.aixianshengxian.adapters.LoadMoreAdapter;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.util.HttpUtil;
import com.aixianshengxian.util.JsonUtil;
import com.aixianshengxian.util.MessageEvent;
import com.aixianshengxian.util.SessionUtils;
import com.google.gson.reflect.TypeToken;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.constants.SaleConstants;
import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.service.sale.GoodsOrder;
import com.xmzynt.storm.service.sort.GoodsOrderStatus;
import com.xmzynt.storm.service.user.customer.MerchantCustomer;
import com.xmzynt.storm.util.GsonUtil;
import com.xmzynt.storm.util.query.PageData;
import com.xmzynt.storm.util.query.QueryFilter;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import okhttp3.Call;

import static com.aixianshengxian.R.id.swipe_refresh_widget;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoadMoreOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoadMoreOrderFragment extends BaseFragment {
    public final static String TAG = "LoadMoreOrderFragment";
    private final int SEARCH_CLIENT_REQUEST = 1;
    private final int SEARCH_CLIENT_RESULT = 2;
    private static final String ARG_PARAM1 = "title";

    private int page = 1;
    private int index = 0;
    private int pageNo = 0;
    private int pageSize = 20;

    private final int PAGE_SIZE = 20;
    private String status ;
    private String mParam1;
    private String mParam2;
    private ImageView image_search;
    private TextView tv_search_content;
    private ImageView image_close_search;
    private RecyclerView order_list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private LoadMoreAdapter adapter;
    private int lastVisibleItem;
    private List<GoodsOrder> mDataList = new ArrayList<>();
    private String mCustomerUUID = "";
    private String mCustomerName = "";
    private Boolean isLogin = true;
    // private ProgressDialog progressBar;
    private boolean mIsRunning = false;

    private Handler mHandler = new Handler(Looper.getMainLooper());


    public LoadMoreOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.

     * @return A new instance of fragment LoadMoreOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoadMoreOrderFragment newInstance(String param1) {
        LoadMoreOrderFragment fragment = new LoadMoreOrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SEARCH_CLIENT_REQUEST){
            if(resultCode == SEARCH_CLIENT_RESULT){
                MerchantCustomer merchantCustomer = (MerchantCustomer) data.getExtras().getSerializable("merchantCustomer");
                if(merchantCustomer != null){
                    mCustomerUUID = merchantCustomer.getCustomer().getUuid();
                    mCustomerName = merchantCustomer.getCustomer().getName();
                    showLogDebug(TAG,"onActivityResult"+mParam1+mCustomerName);
                    tv_search_content.setText(merchantCustomer.getCustomer().getName());
                    //SessionUtils.getInstance(getActivity().getApplicationContext()).saveCustomerUUID(mCustomerUUID);
                    //SessionUtils.getInstance(getActivity().getApplicationContext()).saveCustomerName(mCustomerName);
                    //adapter.resetDatas();
                    //updateRecyclerView(0);
                    pageNo= 0 ;
                    mDataList.clear();

                    getOrders(pageNo);
                    // showCustomToast(merchantCustomer.getCustomer().getName(),getActivity().getApplicationContext());
                }

            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: "+mCustomerName+mParam1);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            Log.d(TAG, "onCreate: "+mCustomerName+mParam1);

        }
        //注册EventBus
        EventBus.getDefault().register(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_order_list, container, false);
        showLogDebug(TAG,"onCreateView"+mParam1+mParam1);
        isLogin = SessionUtils.getInstance(getActivity().getApplicationContext()).getLoginState();

        //mCustomerName = SessionUtils.getInstance(getActivity().getApplicationContext()).getCustomerName();
        //mCustomerUUID = SessionUtils.getInstance(getActivity().getApplicationContext()).getCustomerUUID();
        loadData();
        initViews();
//        if(savedInstanceState !=null && savedInstanceState.getString("mCustomerUUID")!=null){
//            mCustomerUUID = savedInstanceState.getString("mCustomerUUID");
//            mCustomerName = savedInstanceState.getString("mCustomerName");
//
//            Log.d(TAG, "onCreateView: "+mParam1);
//        }
        initHrvsr();
        initEvents();

        return mView;
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: "+mCustomerName+mParam1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //SessionUtils.getInstance(getActivity().getApplicationContext()).saveCustomerUUID("");
        //SessionUtils.getInstance(getActivity().getApplicationContext()).saveCustomerName("");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //mCustomerName = SessionUtils.getInstance(getActivity().getApplicationContext()).getCustomerName();
        //mCustomerUUID = SessionUtils.getInstance(getActivity().getApplicationContext()).getCustomerUUID();
        showLogDebug(TAG,"onResume"+mCustomerName+mParam1);
        pageNo = 0 ;
        mDataList.clear();
        isLogin = SessionUtils.getInstance(getActivity().getApplicationContext()).getLoginState();

        if(isLogin){
            if(tv_search_content != null &&  mCustomerName != null){
                if(TextUtils.isEmpty(mCustomerName)){
                    tv_search_content.setText("");
                }else {
                    tv_search_content.setText(mCustomerName);
                }
            }else {
                tv_search_content.setText("");
            }
                getOrders(pageNo);

            }


    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: "+mCustomerName+mParam1);
        //onSaveInstanceState(new Bundle());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("mCustomerUUID", mCustomerUUID);
        outState.putString("mCustomerName",mCustomerName);
        Log.d(TAG, "onSaveInstanceState: "+mCustomerName);
    }


    public void onEventMainThread(MessageEvent event) {
        //TODO  evenBUS 回调
        if(tv_search_content != null){
            tv_search_content.setText(event.getMesage());
            mCustomerUUID = "";
            mCustomerName = "";
            mDataList .clear();
            pageNo = 0 ;
            getOrders(pageNo);
        }



    }

//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//        Log.d(TAG, "onViewStateRestored: "+mParam1);
//        if(savedInstanceState !=null && savedInstanceState.getString("mCustomerUUID")!=null){
//            mCustomerUUID = savedInstanceState.getString("mCustomerUUID");
//            if(tv_search_content != null){
//                tv_search_content.setText(mCustomerUUID);
//            }
//
//          //  Log.d(TAG, "onCreateView: "+mParam1);
//        }
//    }

    @Override
    protected void initViews() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(swipe_refresh_widget);
        image_search = (ImageView) mView.findViewById(R.id.image_search);
        tv_search_content = (TextView) mView.findViewById(R.id.tv_search_content);
        image_close_search = (ImageView) mView.findViewById(R.id.image_close_search);
        order_list = (RecyclerView) mView.findViewById(R.id.order_list);
    }

    @Override
    protected void initEvents() {
        adapter.setOnItemClickLitener(new LoadMoreAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(GoodsOrder simpleGoodsOrder) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("order",simpleGoodsOrder);
                bundle.putBoolean("modify",false);//查看订单，不可编辑
                startActivity(OrderDetailActivity.class,bundle);
            }
        });
        tv_search_content.setOnClickListener(new View.OnClickListener() {//按客户搜索
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchClientActivity.class);
                startActivityForResult(intent,SEARCH_CLIENT_REQUEST);
            }
        });
        image_close_search.setOnClickListener(new View.OnClickListener() {//清楚客户
            @Override
            public void onClick(View v) {
                //mCustomerUUID = "";
                //mCustomerName = "";
                tv_search_content.setText("");
                //SessionUtils.getInstance(getActivity().getApplicationContext()).saveCustomerUUID("");
                //SessionUtils.getInstance(getActivity().getApplicationContext()).saveCustomerName("");
                //进行消息的推送
                EventBus.getDefault().post(new MessageEvent(""));
                pageNo = 0 ;
                mDataList.clear();
               // onResume();
               // Log.d(TAG, "initEvents: "+mParam1);
               getOrders(pageNo);
            }
        });
    }

    @Override
    protected void init() {

    }
    private void loadData(){
        if(mParam1 != null){
            switch (mParam1){
                case "全部":

                    break;
                case "待审核":
                    status = GoodsOrderStatus.initial.name();
                    break;
                case "待发货":
                    status = GoodsOrderStatus.audited.name();
                    break;
                case "待收货":
                    status =  GoodsOrderStatus.delivered.name();
                    break;
            }
        }
    }
    private void initHrvsr(){

        //设置刷新时动画的颜色，可以设置4个
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));

        /**下拉刷新         */
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (!isLogin) {
                    swipeRefreshLayout.setRefreshing(false);
                    showCustomToast("尚未登录",getActivity().getApplicationContext());
                    return;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "initHrvsr: ");
                        pageNo = 0;
                        mDataList.clear();
                        getOrders(pageNo);
//                        adapter.addItem(mDataList);
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, 1500);
            }
        });

        // 初始化RecyclerView的Adapter
        // 第一个参数为数据，上拉加载的原理就是分页，所以我设置常量PAGE_COUNT=10，即每次加载10个数据
        // 第二个参数为Context
        // 第三个参数为hasMore，是否有新数据
        adapter = new LoadMoreAdapter(getActivity().getApplicationContext(),mDataList, false );

        linearLayoutManager=new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        order_list.setLayoutManager(linearLayoutManager);
        order_list.setItemAnimator(new DefaultItemAnimator());

        order_list.setAdapter(adapter);
        //RecyclerView 上拉加载
        order_list.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (mIsRunning) {
//                                showLogDebug(TAG, "当前正在请求数据");
//                                //progressBar.show();
//                                return;
//                            }
//                            //progressBar.show();
//                            adapter.setHasMore(true);
//                            getOrders(pageNo++);
////                            adapter.addMoreItem(mDataList);
//                            showLogDebug("cwj", "" + pageNo);
//                            mIsRunning = true;
//                        }
//                    },3000);
//                }

                // 在newState为滑到底部时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 如果没有隐藏footView，那么最后一个条目的位置就比我们的getItemCount少1，自己可以算一下
                    if (adapter.isFadeTips() == false && lastVisibleItem + 1 == adapter.getItemCount()) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 然后调用updateRecyclerview方法更新RecyclerView
                                if (mIsRunning) {
                                showLogDebug(TAG, "当前正在请求数据");
                                //progressBar.show();
                                return;
                            }
                            //progressBar.show();
                            adapter.setHasMore(true);
                            getOrders(pageNo++);
//                            adapter.addMoreItem(mDataList);
                            showLogDebug("cwj", "" + pageNo);
                            mIsRunning = true;
                        }
                               // updateRecyclerView(adapter.getRealLastPosition(), adapter.getRealLastPosition() + PAGE_SIZE);

                        }, 500);
                    }

                    // 如果隐藏了提示条，我们又上拉加载时，那么最后一个条目就要比getItemCount要少2
                    if (adapter.isFadeTips() == true && lastVisibleItem + 2 == adapter.getItemCount()) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 然后调用updateRecyclerview方法更新RecyclerView
                                if (mIsRunning) {
                                    showLogDebug(TAG, "当前正在请求数据");
                                    //progressBar.show();
                                    return;
                                }
                                //progressBar.show();
                                adapter.setHasMore(true);
                                getOrders(pageNo++);
//                            adapter.addMoreItem(mDataList);
                                showLogDebug("cwj", "" + pageNo);
                                mIsRunning = true;
                            }
                               // updateRecyclerView(adapter.getRealLastPosition(), adapter.getRealLastPosition() + PAGE_SIZE);

                        }, 500);
                    }
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 在滑动完成后，拿到最后一个可见的item的位置
              lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }


    // 上拉加载时调用的更新RecyclerView的方法
//    private void updateRecyclerView(int pageNo) {
//        // 获取从fromIndex到toIndex的数据
//        getOrders(pageNo);
//        List<SimpleGoodsOrder> newDatas = mDataList;
//        //List<String> newDatas = getDatas(fromIndex, toIndex);
//
//        if (newDatas != null && newDatas.size() > 0) {
//            // 然后传给Adapter，并设置hasMore为true
//            adapter.updateList(newDatas, true);
//        } else {
//            adapter.updateList(null, false);
//        }
    //    }
//
//    private List<SimpleGoodsOrder> getDatas(final int firstIndex, final int lastIndex) {
//        List<SimpleGoodsOrder> resList = new ArrayList<>();
//        for (int i = firstIndex; i < lastIndex; i++) {
//            if (i < list.size()) {
//                resList.add(list.get(i));
//            }
//        }
//        return resList;
//    }



    private void getOrders(final int page){
        String userUuid = SessionUtils.getInstance(getActivity().getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getActivity().getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getActivity().getApplicationContext()).getSessionId();
        showLogDebug(TAG, "getOrders: page=" + page);
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            final QueryFilter filter = new QueryFilter();
            filter.setPage(pageNo);
            filter.setPageSize(pageSize);
            filter.setDefaultPageSize(0);
            if(status != null){
                filter.put(SaleConstants.Field.STATUS, status);// 订单状态，不传即全部
            }
            if(mCustomerUUID != null) {
                filter.put(SaleConstants.Field.CUSTOMER_UUID, mCustomerUUID);
            }
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

        HttpUtil.post(UrlConstants.URL_ORDERS, reparams.toString(), new HttpUtil.HttpListener() {
            @Override
            public void successResponse(String s, int i) {
                ResponseBean response = GsonUtil.getGson().fromJson(s,ResponseBean.class);
                if(response.getErrorCode() == 0){ // 请求成功
                    if(response.getData() != null){
                        PageData data = GsonUtil.getGson().fromJson(response.getData(), PageData.class);
                        Type listTypeA = new TypeToken<List<GoodsOrder>>() {}.getType();
                        List<GoodsOrder> tempList = GsonUtil.getGson().fromJson(GsonUtil.getGson().toJson(data.getValues()),listTypeA);
                        if(!tempList.isEmpty()){

                             //showLogDebug(TAG, "getOrders: pageNo=" + pageNo);
                            for(GoodsOrder simpleGoodsOrder  : tempList){
                                if(!mDataList.contains(simpleGoodsOrder)){
                                    mDataList.add(simpleGoodsOrder);
                                }
                            }
                            //adapter.updateList(true);
                          adapter.notifyDataSetChanged();
                           // adapter.setHasMore(false);

                        }else {
                            adapter.setHasMore(false);
                            adapter.notifyDataSetChanged();
                            //adapter.updateList(false);
                           // showCustomToast("没有更多了", getActivity().getApplicationContext());
                        }
                       // mDataList = GsonUtil.getGson().fromJson(response.getData(),listTypeA);

                    }

                }else {
                    if(response.getMessage()!= null){
                        showCustomToast(response.getMessage(),getActivity().getApplicationContext());
                    }
                }

                mIsRunning = false;
            }

            @Override
            public void errorResponse(Call call, Exception e, int i) {
                showCustomToast("请求失败", getActivity().getApplicationContext());
                mIsRunning = false;
            }
        });
    }

}
