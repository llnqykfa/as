package com.aixianshengxian.activity.addOrder;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.adapters.PowerfulAdapter;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.util.JsonUtil;
import com.aixianshengxian.util.SessionUtils;
import com.aixianshengxian.view.ViewHolder;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.constants.WMSConstants;
import com.xmzynt.storm.basic.user.UserType;
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

public class AddGoodsNewActivity extends BaseActivity implements View.OnClickListener {
    private final int ADD_TEMP_GOODS_REQUEST = 1;
    private final int ADD_TEMP_GOODS_RESULT = 2;
    private final int ADD_GOODS_RESUTL = 3;

    private ImageView image_personal;
    private TextView tv_head_title;
    private EditText edit_search_content;
    private ImageView image_close_search;
    private PullToRefreshListView lvGoodsList;
    private Button btn_add_new_goods;

    private GoodsOrder goodsOrder;
    private List<SalesCatalogLine> mAllDataList;
    private List<SalesCatalogLine> mDataList = new ArrayList<>();
    private PowerfulAdapter<SalesCatalogLine> adapter;
    private int index = 0;
    private int page = 0;
    private List<GoodsOrderLine> mGoodsOrderLines = new ArrayList<>();
    private PowerfulAdapter.PowerfulListener mPowerfulListener;
    private TextView tv_add_temp;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TEMP_GOODS_REQUEST) {
            if (resultCode == ADD_TEMP_GOODS_RESULT) {
                GoodsOrderLine goodsOrderLine = (GoodsOrderLine) data.getExtras().getSerializable("goodsOrderLine");
                mGoodsOrderLines.add(goodsOrderLine);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods_new);
        initData();
        loadData();
        initViews();
        initEvents();

    }

    private void initData() {
        goodsOrder = (GoodsOrder) getIntent().getExtras().getSerializable("goodsOrder");
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("订价列表");
        edit_search_content = (EditText) findViewById(R.id.edit_search_content);
        image_close_search = (ImageView) findViewById(R.id.image_close_search);
        image_close_search.setVisibility(View.GONE);
        lvGoodsList = (PullToRefreshListView) findViewById(R.id.lvGoodsList);
        btn_add_new_goods = (Button) findViewById(R.id.btn_add_new_goods);
        tv_add_temp = (TextView) findViewById(R.id.tv_add_temp);
        tv_add_temp.setVisibility(View.VISIBLE);
        adapter = new PowerfulAdapter<SalesCatalogLine>(getApplicationContext(), mDataList, R.layout.item_goods_select) {
            @Override
            public void convert(ViewHolder holder, SalesCatalogLine salesCatalogLine) {
                holder.setTextToTextView(R.id.tv_goods_no, salesCatalogLine.getGoods().getCode());
                holder.setTextToTextView(R.id.tv_goods_name, salesCatalogLine.getGoods().getName());
                holder.setTextToTextView(R.id.tv_unit_price, "￥" + salesCatalogLine.getOrderPrice() + "/" + salesCatalogLine.getGoodsUnit().getName());
            }

            @Override
            public void setListener(ViewHolder holder, int position, View.OnClickListener listener) {
                holder.setListenerToView(R.id.line_goods, listener);
            }
        };

        mPowerfulListener = new PowerfulAdapter.PowerfulListener() {
            @Override
            public void onClick(View v, int position) {
                switch (v.getId()) {
                    case R.id.line_goods:
//                        ImageView view = (ImageView) v.findViewById(R.id.image_select);
//                        view.setImageResource(R.mipmap.has_select);
                        break;
                }
            }
        };
        adapter.setPowerfulListener(mPowerfulListener);
        lvGoodsList.setAdapter(adapter);
        lvGoodsList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                index = 0;
                mDataList.clear();
                if (mAllDataList.size() > 20) {
                    for (int i = 0; i < 20; i++) {
                        mDataList.add(mAllDataList.get(i));
                        index++;
                    }
                } else {
                    for (int i = 0; i < mAllDataList.size(); i++) {
                        mDataList.add(mAllDataList.get(i));
                        index++;
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                List<SalesCatalogLine> newDatas = new ArrayList<SalesCatalogLine>();
                if (mAllDataList != null && mAllDataList.size() >= index && index >= 0) {
                    for (int i = index; i < mAllDataList.size(); i++) {
                        mDataList.add(mAllDataList.get(i));
                        index++;
                    }

                } else if (mAllDataList.size() == index) {
                    showCustomToast("全部加载完毕");
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void initEvents() {
        btn_add_new_goods.setOnClickListener(this);
        tv_add_temp.setOnClickListener(this);
        image_personal.setOnClickListener(this);
        lvGoodsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mDataList != null && mDataList.size() > 0) {
                    if (position > mDataList.size() - 1) {
                        return;
                    } else {
                        SalesCatalogLine salesCatalogLine = mDataList.get(position);
                        GoodsOrderLine goodsOrderLine = new GoodsOrderLine();
                        goodsOrderLine.setGoods(salesCatalogLine.getGoods());
                        goodsOrderLine.setGoodsUnit(salesCatalogLine.getGoodsUnit());
                        goodsOrderLine.setOrderPrice(salesCatalogLine.getOrderPrice());
                        goodsOrderLine.setGoodsPic(salesCatalogLine.getGoodsThumbnail());
                        goodsOrderLine.setGoodsSpec(salesCatalogLine.getGoodsSpec());
                        goodsOrderLine.setGoodsAlias(salesCatalogLine.getGoodsAlias());
                        goodsOrderLine.setGoodsCategory(salesCatalogLine.getGoodsCategory());
                        mGoodsOrderLines.add(goodsOrderLine);
                        showCustomToast("添加成功");
                    }
                }
            }
        });

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
                if(mAllDataList != null && mAllDataList.size()>0){
                    if( value != null && !TextUtils.isEmpty(value)){
                        mDataList.clear();
                        for(SalesCatalogLine salesCatalogLine : mAllDataList){
                            if(salesCatalogLine.getGoods().getName().contains(value)){
                                mDataList.add(salesCatalogLine);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }else {
                        index = 0;
                        mDataList.clear();
                        if(mAllDataList.size()> 20){
                            for (int i = 0; i <20; i++) {
                                mDataList.add(mAllDataList.get(i));
                                index++;
                            }
                        }else {
                            for (int i = 0; i < mAllDataList.size(); i++) {
                                mDataList.add(mAllDataList.get(i));
                                index++;
                            }
                        }

                        adapter.notifyDataSetChanged();
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
            case R.id.tv_add_temp:
                Intent intent = new Intent(this, AddTempActivity.class);
                startActivityForResult(intent, ADD_TEMP_GOODS_REQUEST);
                break;
            case R.id.btn_add_new_goods:
                Intent intent1 = new Intent();
                Bundle bundle = new Bundle();
//                if(goodsOrder.getLines() != null){
//                    goodsOrder.getLines().addAll(mGoodsOrderLines);
//                }else {
                goodsOrder.setLines(mGoodsOrderLines);
//                }
                bundle.putSerializable("goodsOrder", goodsOrder);
                intent1.putExtras(bundle);
                setResult(ADD_GOODS_RESUTL, intent1);
                finish();

                // startActivity(AddTempActivity.class);
                break;
        }
    }

    private void loadData() {
        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        Merchant merchant = SessionUtils.getInstance(getApplicationContext()).getLoginMerchan();
        if (merchant != null && goodsOrder != null) {
            JSONObject body = new JSONObject();
            QueryFilter filter = new QueryFilter();
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
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                            if (response.getErrorCode() == 0) {
                                Type listType = new TypeToken<List<SalesCatalogLine>>() {
                                }.getType();
                                if (response.getData() != null) {
                                    mAllDataList = GsonUtil.getGson().fromJson(response.getData(), listType);
                                    if (mAllDataList != null && mAllDataList.size() > 20) {
                                        for (int j = 0; j < 20; j++) {
                                            mDataList.add(mAllDataList.get(j));
                                        }
                                    } else if (mAllDataList != null && mAllDataList.size() > 0 && mAllDataList.size() < 20) {
                                        for (int j = 0; j < mAllDataList.size(); j++) {
                                            mDataList.add(mAllDataList.get(j));
                                        }
                                    }

                                    if (adapter != null) {
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                            } else {
                                showCustomToast(response.getMessage());
                            }
                        }
                    });

        }

    }


}
