package com.aixianshengxian.activity.addOrder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.activity.OrderDetailActivity;
import com.aixianshengxian.adapters.AddAndEditOrderGoodsListAdapter;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.util.DateUtils;
import com.aixianshengxian.util.JsonUtil;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.constants.SaleConstants;
import com.xmzynt.storm.basic.idname.IdName;
import com.xmzynt.storm.service.sale.GoodsOrder;
import com.xmzynt.storm.service.sale.GoodsOrderLine;
import com.xmzynt.storm.service.user.customer.Department;
import com.xmzynt.storm.service.user.customer.MerchantCustomer;
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

public class NewOrderActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "NewOrderActivity";
    private  final int NEW_ORDER_ADD_GOODS_REQUEST = 1 ;
    private  final int ADD_GOODS_RESUTL =3;
    private ImageView image_personal;
    private TextView tv_head_title;
    private LinearLayout line_head;
    private TextView tv_date_content;
    private ImageView image_date_select,iv_narrow;
    private RecyclerView recylerView_goods_list;
    private ImageView image_add_goods;
    private MerchantCustomer merchantCustomer;
    private Department department;
    private Button btn_add_new_order;
    private GoodsOrder mGoodsOrder;
    private List<GoodsOrderLine> mDataList = new ArrayList<>();
    private TextView tv_notice;
    private Button btn_add_new_goods;
    private RelativeLayout relative_goods_List;
    private AddAndEditOrderGoodsListAdapter adapter;

    private int narrowCount = 0;//记录添加按钮是否显示

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == NEW_ORDER_ADD_GOODS_REQUEST){
            if(resultCode == ADD_GOODS_RESUTL){
                GoodsOrder goodsOrder = (GoodsOrder) data.getExtras().getSerializable("goodsOrder");
                    if(goodsOrder!= null && goodsOrder.getLines()!= null && goodsOrder.getLines().size()>0){
                        mDataList.addAll(goodsOrder.getLines());
                        if(mDataList != null && mDataList.size()>0){
                            tv_notice.setVisibility(View.GONE);
                            btn_add_new_goods.setVisibility(View.GONE);
                        }
                        if(adapter != null){
                            adapter.notifyDataSetChanged();
                        }
                    }


            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        initData();
        createOrder();
        initViews();
        setData();
        initEvents();
    }

    private void initData() {
        merchantCustomer = (MerchantCustomer) getIntent().getExtras().getSerializable("merchantClient");
        department = (Department) getIntent().getExtras().getSerializable("department");
        mGoodsOrder = new GoodsOrder();
        if(merchantCustomer != null){
            // 选择客户后设置客户、客户级别、客户地址、联系人、联系电话
            mGoodsOrder.setCustomer(new IdName(merchantCustomer.getCustomer().getUuid(),merchantCustomer.getCustomer().getName()));

            mGoodsOrder.setCustomerLevel(merchantCustomer.getCustomerLevel());
            mGoodsOrder.setAddress(merchantCustomer.getCustomer().getAddress());
            mGoodsOrder.setLinkMan(merchantCustomer.getCustomer().getLinkMan());
            mGoodsOrder.setLinkPhone(merchantCustomer.getCustomer().getLinkPhone());

        }
        if(department != null){
            // 选择部门后设置
            mGoodsOrder.setCustomerDepartment(new IdName(department.getUuid(),department.getName()));
        }
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        line_head = (LinearLayout) findViewById(R.id.line_head);
        tv_date_content = (TextView) findViewById(R.id.tv_date_content);
        image_date_select = (ImageView) findViewById(R.id.image_date_select);
        iv_narrow = (ImageView) findViewById(R.id.iv_narrow);
        recylerView_goods_list = (RecyclerView) findViewById(R.id.recylerView_goods_list);
        image_add_goods = (ImageView) findViewById(R.id.image_add_goods);
        btn_add_new_order = (Button) findViewById(R.id.btn_add_new_order);
        tv_notice = (TextView) findViewById(R.id.tv_notice);
        btn_add_new_goods = (Button) findViewById(R.id.btn_add_new_goods);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recylerView_goods_list.setLayoutManager(linearLayoutManager);
        relative_goods_List = (RelativeLayout) findViewById(R.id.relative_goods_List);
        tv_date_content.setText(DateUtils.getSomeDate(1)); //设置默认收货日期为第二天
        /** 设置日期选择器   */
        DateUtils.tvDatePick(this,tv_date_content,false);
        /** 设置标题 */
        if (merchantCustomer != null && merchantCustomer.getCustomer().getName() != null) {
            if(department != null && department.getName() != null){
                tv_head_title.setText("" + merchantCustomer.getCustomer().getName()+"【"+department.getName()+"】");
            }else {
                tv_head_title.setText("" + merchantCustomer.getCustomer().getName());
            }

        }
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        image_add_goods.setOnClickListener(this);
        btn_add_new_order.setOnClickListener(this);
        btn_add_new_goods.setOnClickListener(this);
        iv_narrow.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                finish();
                break;
            case R.id.image_add_goods:
                if(mGoodsOrder != null){
                    Intent intent = new Intent(this,AddGoodsActivity.class);
                    Bundle bundle = new Bundle();
                    mGoodsOrder.setDeliveryTime(DateUtils.StringToDate(tv_date_content.getText().toString().trim(), "yyyy-MM-dd"));
                    bundle.putSerializable("goodsOrder",mGoodsOrder);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,NEW_ORDER_ADD_GOODS_REQUEST);//添加商品
                }

                break;
            case R.id.iv_narrow:
                narrowCount++;
                if (narrowCount % 2 != 0) {
                    image_add_goods.setVisibility(View.GONE);
                } else {
                    image_add_goods.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_add_new_order://保存新建订单
                mDataList = adapter.getmDataList();
                saveNewOrer();

                break;
            case R.id.btn_add_new_goods:

                if(mGoodsOrder != null){
                    Intent intent = new Intent(this,AddGoodsActivity.class);
                    Bundle bundle = new Bundle();
                    mGoodsOrder.setDeliveryTime(DateUtils.StringToDate(tv_date_content.getText().toString().trim(), "yyyy-MM-dd"));
                    bundle.putSerializable("goodsOrder",mGoodsOrder);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,NEW_ORDER_ADD_GOODS_REQUEST);//添加商品
                }
                break;

        }
    }

    private void setData() {
        adapter = new AddAndEditOrderGoodsListAdapter(mDataList,getApplicationContext());
        adapter.setmSaveEditListener(new AddAndEditOrderGoodsListAdapter.SaveEditListener() {
            @Override
            public void SaveOrderGoodsQTt(int position, String string) {
//                if(string != null && !TextUtils.isEmpty(string)){
//                    if(mDataList != null && mDataList.size()>0){
//                        if(mDataList.get(position) != null){
//                            GoodsOrderLine goodsOrderLine = mDataList.get(position);
//                            BigDecimal goodsQty = new BigDecimal(string);
//                            goodsOrderLine.setGoodsQty(BigDecimalUtil.convertToScale(goodsQty,2));
//                            mDataList.remove(position);
//                            mDataList.add(position,goodsOrderLine);
//
//                        }
//                        //adapter.notifyDataSetChanged();
//                    }
//                }
        }

            @Override
            public void SaveOrderGoodsRemark(int position, String s) {
//                if(s != null && !TextUtils.isEmpty(s)){
//                    if(mDataList != null && mDataList.size()>0){
//                        if(mDataList.get(position) != null){
//                            GoodsOrderLine goodsOrderLine = mDataList.get(position);
//                            goodsOrderLine.setRemark(s);
//                            mDataList.remove(position);
//                            mDataList.add(position,goodsOrderLine);
//                          // adapter.notifyDataSetChanged();
//                        }
//                    }
//                }
            }

            @Override
            public void deleteGoodsOrderLine(int position, GoodsOrderLine goodsOrderLine) {
                mDataList = adapter.getmDataList();
                mDataList.remove(position);
                adapter.notifyItemRemoved(position);

                //必须调用这行代码
                adapter.notifyItemRangeChanged(position, mDataList.size());
            }
        });

        recylerView_goods_list.setAdapter(adapter);
    }

    private void createOrder() {
        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        JSONObject body = new JSONObject();
        try {
            params.put(BasicConstants.URL_BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString().url(UrlConstants.URL_CREATE_NEW_ORDER)
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
                            if (response.getData() != null) {
                                GoodsOrder goodsOrder= GsonUtil.getGson().fromJson(response.getData(), GoodsOrder.class);
                              if(goodsOrder != null){
                                  mGoodsOrder.setOrg(goodsOrder.getOrg());
                                  mGoodsOrder.setBillNumber(goodsOrder.getBillNumber());
                              }

                                // showCustomToast("请求成功");
                            }
                        } else {
                            if (response.getMessage() != null) {
                                showCustomToast(response.getMessage());
                            }
                        }
                    }
                });

    }

    private void saveNewOrer(){
        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        final JSONObject body = new JSONObject();
        if(mGoodsOrder != null){
            if(tv_date_content.getText().toString() != null){
                mGoodsOrder.setDeliveryTime(DateUtils.StringToDate(tv_date_content.getText().toString().trim(), "yyyy-MM-dd"));//设置交货时间
            }
           if(mDataList != null &&mDataList.size()>0){
               mGoodsOrder.setLines(mDataList);//设置商品列表
               BigDecimal goodsTotalQty = BigDecimal.ZERO;
               BigDecimal totalAmount = BigDecimal.ZERO;
               for (GoodsOrderLine line: mGoodsOrder.getLines()) {
                   goodsTotalQty = goodsTotalQty.add(line.getGoodsQty());
                   totalAmount = totalAmount.add(BigDecimalUtil.convertToScale(line.getGoodsQty().multiply(line.getOrderPrice()), 2));
               }
               mGoodsOrder.setGoodsTotalQty(goodsTotalQty);
               mGoodsOrder.setTotalAmount(totalAmount);
           }
            try {
                body.put(SaleConstants.Field.GOODS_ORDER, GsonUtil.getGson().toJson(mGoodsOrder));
                params.put(BasicConstants.URL_BODY, body);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OkHttpUtils.postString().url(UrlConstants.URL_SAVE_NEW_ORDER)
                    .addHeader("Cookie", "PHPSESSID=" + 123456)
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .addHeader("Content-Type", "application/json;chartset=utf-8")
                    .content(params.toString())
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            showCustomToast("保存订单失败");
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            ResponseBean  response = GsonUtil.getGson().fromJson(s,ResponseBean.class);
                            if(response.getErrorCode() == 0) {
                                Bundle bundle = new Bundle();
                                bundle.putString("uuid",response.getData());
                                bundle.putBoolean("modify",true);//新建订单，可编辑
                                startActivity(OrderDetailActivity.class,bundle);
                                finish();
                              //  UUID uuid = GsonUtil.getGson().fromJson(response.getData(),UUID.class);
                                showCustomToast("保存成功");
                            }else {
                                showCustomToast(response.getMessage());
                            }
                        }
                    });
        }


    }


}
