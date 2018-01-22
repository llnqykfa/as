package com.aixianshengxian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.activity.addOrder.AddGoodsActivity;
import com.aixianshengxian.adapters.AddAndEditOrderGoodsListAdapter;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.util.DateUtils;
import com.aixianshengxian.util.JsonUtil;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.constants.SaleConstants;
import com.xmzynt.storm.service.sale.GoodsOrder;
import com.xmzynt.storm.service.sale.GoodsOrderLine;
import com.xmzynt.storm.service.user.customer.Department;
import com.xmzynt.storm.service.user.customer.MerchantCustomer;
import com.xmzynt.storm.util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class EditOrderActivity extends BaseActivity implements View.OnClickListener {
    private final int EDIT_ORDER_ADD_GOODS_REQUEST = 2;
    private  final int ADD_GOODS_RESUTL =3 ;
    private ImageView image_personal;
    private TextView tv_head_title;
    private LinearLayout line_head;
    private TextView tv_date_content;
    private ImageView image_date_select;
    private RecyclerView recylerView_goods_list;
    private ImageView image_add_goods;
    private MerchantCustomer merchantCustomer;
    private Department department;
    private Button tv_add_new_order;
    private GoodsOrder mGoodsOrder;
    private List<GoodsOrderLine> mDataList;
    private TextView tv_notice;
    private Button btn_add_new_goods;
    private RelativeLayout relative_goods_List;
    private AddAndEditOrderGoodsListAdapter adapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_ORDER_ADD_GOODS_REQUEST){
            if(resultCode == ADD_GOODS_RESUTL){
                GoodsOrder goodsOrder = (GoodsOrder) data.getExtras().getSerializable("goodsOrder");
                if(goodsOrder!= null && goodsOrder.getLines()!= null && goodsOrder.getLines().size()>0){
                    mDataList.addAll(goodsOrder.getLines());
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
        initViews();
        setData();
        initEvents();

    }
    private void initData() {
        //订单由上个界面传过来
        mGoodsOrder = (GoodsOrder) getIntent().getExtras().getSerializable("goodsOrder");
        if(mGoodsOrder.getLines() != null){

            mDataList = mGoodsOrder.getLines();
        }else {
            mDataList = new ArrayList<>();
        }


    }
    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        line_head = (LinearLayout) findViewById(R.id.line_head);
        tv_date_content = (TextView) findViewById(R.id.tv_date_content);
        image_date_select = (ImageView) findViewById(R.id.image_date_select);
        recylerView_goods_list = (RecyclerView) findViewById(R.id.recylerView_goods_list);
        recylerView_goods_list.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        image_add_goods = (ImageView) findViewById(R.id.image_add_goods);
        tv_add_new_order = (Button) findViewById(R.id.btn_add_new_order);
        tv_notice = (TextView) findViewById(R.id.tv_notice);
        btn_add_new_goods = (Button) findViewById(R.id.btn_add_new_goods);
        relative_goods_List = (RelativeLayout) findViewById(R.id.relative_goods_List);

        tv_date_content.setText(DateUtils.getSomeDate(1));
        DateUtils.tvDatePick(this,tv_date_content,false);

        if(mGoodsOrder != null && mGoodsOrder.getCustomer().getName() != null&&! mGoodsOrder.getCustomer().getName().equals("")){

                if( null != mGoodsOrder.getCustomerDepartment() &&null != mGoodsOrder.getCustomerDepartment().getName() && !TextUtils.isEmpty(mGoodsOrder.getCustomerDepartment().getName())){
                    tv_head_title.setText(mGoodsOrder.getCustomer().getName()+"【"+mGoodsOrder.getCustomerDepartment().getName()+"】");//设置标题
                }  else {
                    tv_head_title.setText(mGoodsOrder.getCustomer().getName());
                }

        }
        if(mGoodsOrder !=null && mGoodsOrder.getLines() !=null && mGoodsOrder.getLines().size()>0){
            tv_notice.setVisibility(View.GONE);
            btn_add_new_goods.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        image_add_goods.setOnClickListener(this);
        tv_add_new_order.setOnClickListener(this);
        btn_add_new_goods.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
//                if(mGoodsOrder != null){
//                    Intent intent = new Intent(this,AddGoodsActivity.class);
//                    Bundle bundle = new Bundle();
//                    mGoodsOrder.setDeliveryTime(DateUtils.StringToDate(tv_date_content.getText().toString().trim(), "yyyy-MM-dd"));
//                    bundle.putSerializable("goodsOrder",mGoodsOrder);
//                    intent.putExtras(bundle);
//                    startActivityForResult(intent,EDIT_ORDER_ADD_GOODS_REQUEST);//添加商品
//                }
                finish();
                break;
            case R.id.image_add_goods:
                if(mGoodsOrder != null){
                    Intent intent = new Intent(this,AddGoodsActivity.class);
                    Bundle bundle = new Bundle();
                    mGoodsOrder.setDeliveryTime(DateUtils.StringToDate(tv_date_content.getText().toString().trim(), "yyyy-MM-dd"));
                    bundle.putSerializable("goodsOrder",mGoodsOrder);
                    intent.putExtras(bundle);
                        startActivityForResult(intent,EDIT_ORDER_ADD_GOODS_REQUEST);//添加商品
                }

                break;
            case R.id.btn_add_new_order:
                mDataList = adapter.getmDataList();
                saveEditOrder();
                break;
            case R.id.btn_add_new_goods:

                if(mGoodsOrder != null){
                    Intent intent = new Intent(this,AddGoodsActivity.class);
                    Bundle bundle = new Bundle();
                    mGoodsOrder.setDeliveryTime(DateUtils.StringToDate(tv_date_content.getText().toString().trim(), "yyyy-MM-dd"));
                    bundle.putSerializable("goodsOrder",mGoodsOrder);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,EDIT_ORDER_ADD_GOODS_REQUEST);//添加商品
                }

                break;
    }
    }

        private void setData() {

            adapter = new AddAndEditOrderGoodsListAdapter(mDataList,getApplicationContext());
            adapter.setmSaveEditListener(new AddAndEditOrderGoodsListAdapter.SaveEditListener() {
                @Override
                public void SaveOrderGoodsQTt(int position, String string) {
//                    if(string != null && !TextUtils.isEmpty(string)){
//                        if(mDataList != null && mDataList.size()>0){
//                            if(mDataList.get(position) != null){
//                                GoodsOrderLine goodsOrderLine = mDataList.get(position);
//                                BigDecimal goodsQty = new BigDecimal(string);
//                                goodsOrderLine.setGoodsQty(BigDecimalUtil.convertToScale(goodsQty,2));
//                                mDataList.remove(position);
//                                mDataList.add(position,goodsOrderLine);
//
//                            }
//                            //adapter.notifyDataSetChanged();
//                        }
//                    }
                }

                @Override
                public void SaveOrderGoodsRemark(int position, String s) {
//                    if(s != null && !TextUtils.isEmpty(s)){
//                        if(mDataList != null && mDataList.size()>0){
//                            if(mDataList.get(position) != null){
//                                GoodsOrderLine goodsOrderLine = mDataList.get(position);
//                                goodsOrderLine.setRemark(s);
//                                mDataList.remove(position);
//                                mDataList.add(position,goodsOrderLine);
//                                // adapter.notifyDataSetChanged();
//                            }
//                        }
//                    }
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
    private void saveEditOrder(){
        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        JSONObject body = new JSONObject();
        if(mDataList != null && mDataList.size()>0 ){
            mGoodsOrder.setLines(mDataList);
        }
        try {
            body.put(SaleConstants.Field.GOODS_ORDER, GsonUtil.getGson().toJson(mGoodsOrder));
            params.put(BasicConstants.URL_BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString().url(UrlConstants.URL_SAVE_ORDER_EDIT)
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
                        if(response.getErrorCode() == 0){//编辑提交成功

                             finish();
                        }else {
                            if(response.getMessage() != null){
                                showCustomToast(response.getMessage());
                            }

                        }
                    }
                });

    }
}
