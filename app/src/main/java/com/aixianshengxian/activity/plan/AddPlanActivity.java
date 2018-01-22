package com.aixianshengxian.activity.plan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.activity.search.SearchProviderActivity;
import com.aixianshengxian.adapters.AddPlanAdapter;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.http.HttpManager;
import com.aixianshengxian.listener.UnitInterface;
import com.aixianshengxian.module.GoodsItem;
import com.aixianshengxian.util.PurchaseBillUtil;
import com.xmzynt.storm.basic.idname.IdName;
import com.xmzynt.storm.service.goods.UnitPrice;
import com.xmzynt.storm.service.purchase.plan.ForecastPurchase;
import com.xmzynt.storm.service.user.supplier.Supplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddPlanActivity extends BaseActivity implements View.OnClickListener {

    private ImageView image_personal;
    private TextView tv_head_title;
    private Button btn_save;
    private AddPlanAdapter mAdapter;
    private RecyclerView list;
    private LinearLayoutManager layoutManager;

    private List<ForecastPurchase> datas = new ArrayList<ForecastPurchase>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);
        initViews();
        initData();
        initEvents();

    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("计划新增");
        btn_save = (Button) findViewById(R.id.btn_save);
        list = (RecyclerView)findViewById(R.id.listview_add_plan_list);
        layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        list.setLayoutManager(layoutManager);
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        mAdapter.setOnItemListener(new AddPlanAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ForecastPurchase planProduct) {

            }

            @Override
            public void onCheckBoxClick(int position, Boolean isChecked) {

            }

            @Override
            public void onDeleteClick(int position) {

            }

            @Override
            public void onUnitClick(int position, View v) {
                List<String> mGoodsUuid = new ArrayList<String>();
                mGoodsUuid.add(datas.get(position).getGoods().getUuid());
                HttpManager.dropDownUnit(AddPlanActivity.this, mGoodsUuid, position, new UnitInterface() {
                    @Override
                    public void onError() {
                        showCustomToast("请求失败");
                    }

                    @Override
                    public void onUnitListener(Map<String, List<UnitPrice>> map, int position) {
                        showCustomToast("请求成功");
                        setUnitsAlertDialog(map,position);
                    }
                });
            }

            @Override
            public void onProviderClick(int position, View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("Provider_which",1);
                bundle.putSerializable("Position",position);
                startActivityforResult(SearchProviderActivity.class,bundle,DataConstant.BUNDLE_REQUEST_ADD_PLAN);
            }
        });
    }

    private void initData(){
        Intent intent = getIntent();
        Map<String,GoodsItem> goodsItemMap = (Map<String,GoodsItem>) intent.getSerializableExtra("goods");
        datas = PurchaseBillUtil.toForecastPurchase(goodsItemMap);

        mAdapter = new AddPlanAdapter(datas,this);
        list.setAdapter(mAdapter);
    }

    //单位
    private void setUnitsAlertDialog(Map<String, List<UnitPrice>> mMapUnits,final int position) {
        String GoodsUuid = datas.get(position).getGoods().getUuid();
        final List<UnitPrice> mUnit = mMapUnits.get(GoodsUuid);
        ArrayList<String> mUnits = new ArrayList<String>();
        for (int i = 0;i < mUnit.size();i ++) {
            mUnits.add(mUnit.get(i).getUnit().getName());//ArrayList可以根据长度变化
        }
        int size = mUnits.size();//获取长度
        final String[] items = new String[size];
        for (int i = 0;i < size;i ++) {//把ArrayList转换成String[]数组
            items[i] = mUnits.get(i);
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择单位");
        builder.setCancelable(true);

        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                datas.get(position).setGoodsUnit(mUnit.get(which).getUnit());
            }
        });
        //添加确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.create().show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                finish();
                break;
            case R.id.btn_save:
                List<ForecastPurchase> forecastPurchases = mAdapter.getForecastPurchase();
                HttpManager.savePlan(this, forecastPurchases, new onAddPlanInterface() {
                    @Override
                    public void onError(String message) {
                        showCustomToast("保存失败");

                        showCustomToast(message);

                    }

                    @Override
                    public void onAddPlanInterfaceSuccess(String message) {
                        showCustomToast("保存成功");
                        startActivity(PlanActivity.class);
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DataConstant.BUNDLE_REQUEST_ADD_PLAN){
            if(resultCode == DataConstant.SEARCH_PROVIDER_RESULT){
                Supplier supplier = (Supplier) data.getExtras().getSerializable("supplier");
                int position = (int)data.getExtras().getSerializable("Position");
                if(supplier != null){
                    ForecastPurchase forecastpurchase = datas.get(position);
                    String supplierName = supplier.getName();
                    String providerName =  forecastpurchase.getSupplier()==null?null:forecastpurchase.getSupplier().getName();
                    IdName idName = new IdName(supplier.getUuid(),supplier.getName());
                    datas.get(position).setSupplier(idName);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public interface onAddPlanInterface{
        void onError(String message);

        void onAddPlanInterfaceSuccess(String message);
    }
}
