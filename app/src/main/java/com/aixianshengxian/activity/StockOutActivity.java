package com.aixianshengxian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.adapters.StockoutListAdapter;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.util.JsonUtil;
import com.aixianshengxian.util.SessionUtils;
/*<<<<<<< HEAD

=======
>>>>>>> 438ba51b3593f0e0bbc7d2fa4e31266122663d90*/
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.constants.SaleConstants;
import com.xmzynt.storm.basic.ucn.UCN;
import com.xmzynt.storm.service.sale.GoodsOrder;
import com.xmzynt.storm.service.sale.GoodsOrderLine;
import com.xmzynt.storm.service.sale.StockOutData;
import com.xmzynt.storm.service.sale.StockOutLine;
import com.xmzynt.storm.service.user.merchant.Merchant;
import com.xmzynt.storm.service.wms.warehouse.Warehouse;
import com.xmzynt.storm.util.BigDecimalUtil;
import com.xmzynt.storm.util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class StockOutActivity extends BaseActivity implements View.OnClickListener {

    private final int STOCK_OUT_REQUEST = 1;
    private final int STOCK_OUT_RESULT = 2;
    private ImageView image_personal;
    private TextView tv_head_title;
    private TextView tv_add_temp;
    private TextView tv_search_content;
    private RecyclerView recylerView_goods_list;
    private Button btn_check_stock_out;
    private GoodsOrder goodsOrder;
    public List<GoodsOrderLine> mGoodsOrderLines;
    public LinearLayout line_search;
    private  StockoutListAdapter adapter;
    private Warehouse mWareHouse;
    private List<StockOutLine> mStockoutLines =new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_out);
        initData();
        initViews();
        initEvents();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == STOCK_OUT_REQUEST){
            if(resultCode ==STOCK_OUT_RESULT ){
                mWareHouse = (Warehouse) data.getExtras().getSerializable("warehouse");
                if(mWareHouse != null){
                    tv_search_content.setText(mWareHouse.getName());
                }

            }
        }
    }
    private void initData(){
      goodsOrder = (GoodsOrder) getIntent().getExtras().getSerializable("goodsOrder");
        if(goodsOrder != null){
            mGoodsOrderLines = goodsOrder.getLines();
        }
    }
    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("销售出库");
        tv_add_temp = (TextView) findViewById(R.id.tv_add_temp);
        tv_search_content = (TextView) findViewById(R.id.tv_search_content);
        line_search = (LinearLayout) findViewById(R.id.line_search);
        recylerView_goods_list = (RecyclerView) findViewById(R.id.recylerView_goods_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recylerView_goods_list.setLayoutManager(linearLayoutManager);
        btn_check_stock_out = (Button) findViewById(R.id.btn_check_stock_out);

        if(mGoodsOrderLines != null && mGoodsOrderLines.size()>0){
            for(int i = 0 ; i <mGoodsOrderLines.size(); i ++  ){
                GoodsOrderLine  goodsOrderLine = mGoodsOrderLines.get(i);
                StockOutLine stockOutLine = new StockOutLine();
                if(goodsOrderLine.getGoodsQty().compareTo(goodsOrderLine.getHasStockOutQty())== 1){//订单数>已出库数
                    BigDecimal stockoutQty = goodsOrderLine.getGoodsQty().subtract(goodsOrderLine.getHasStockOutQty());//出库数默认为订单数-已出库数
                    stockOutLine.setStockOutQty(BigDecimalUtil.convertToScale(stockoutQty,2));
                }else {
                    stockOutLine.setStockOutQty(BigDecimal.ZERO);//订单数<已出库数 设置为0
                }
                stockOutLine.setGoodsOrderLineUuid(goodsOrderLine.getUuid());
                stockOutLine.setVersion(goodsOrderLine.getVersion());
                mStockoutLines.add(stockOutLine);//构造出库列表，和订单商品列表一样长
            }
           adapter = new StockoutListAdapter(mGoodsOrderLines,mStockoutLines,getApplicationContext());
            recylerView_goods_list.setAdapter(adapter);
        }

    }

    @Override
    protected void initEvents() {
        btn_check_stock_out.setOnClickListener(this);
        image_personal.setOnClickListener(this);
        line_search.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_check_stock_out:
                stockOut();

                break;
            case R.id.image_personal:
                finish();
                break;
            case R.id.line_search:
                startActivityforResult(StoreListActivity.class,STOCK_OUT_REQUEST);
                break;
        }
    }
    private void stockOut(){
        mGoodsOrderLines = adapter.getmDataList();//获取最新的数据值
        Merchant merchant = SessionUtils.getInstance(getApplicationContext()).getLoginMerchan();
        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        JSONObject body = new JSONObject();
        StockOutData stockOutData = new StockOutData();
        List<StockOutLine> stockOutLinestemp  = new ArrayList<>();
        if(mWareHouse  == null){
            showCustomToast("请选择出仓仓库");
            return;
        }else if(merchant != null) {
            UCN ucn = new UCN(mWareHouse.getUuid(),mWareHouse.getCode(),mWareHouse.getName());
            stockOutData.setWarehouse(ucn);
            stockOutData.setOperatorName(merchant.getAccountInfo().getUserName());
            if(mStockoutLines != null && mStockoutLines.size()>0){
                for(StockOutLine stockOutLine :mStockoutLines){
                    if(stockOutLine.getStockOutQty() != BigDecimal.ZERO){
                        stockOutLinestemp.add(stockOutLine);
                    }
                }
                stockOutData.setLines(stockOutLinestemp);
            }
            try { body.put(SaleConstants.Field.GOODS_ORDER_UUID, goodsOrder.getUuid());
                body.put(SaleConstants.Field.VERSION, goodsOrder.getVersion());
                body.put(SaleConstants.Field.STOCKOUT_DATA, GsonUtil.getGson().toJson(stockOutData));
                params.put(BasicConstants.URL_BODY, body);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OkHttpUtils.postString().url(UrlConstants.URL_STOCK_OUT)
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
                            ResponseBean response = GsonUtil.getGson().fromJson(s,ResponseBean.class);
                            if(response.getErrorCode() == 0){//出仓成功
                                showCustomToast("出仓成功");
                                finish();
                            }else {
                                showCustomToast(response.getMessage());
                            }
                        }
                    });
        }


    }


}
