package com.aixianshengxian.activity.machine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.adapters.AddMachineAdapter;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.http.HttpManager;
import com.aixianshengxian.listener.UnitInterface;
import com.aixianshengxian.module.GoodsItem;
import com.aixianshengxian.util.PurchaseBillUtil;
import com.aixianshengxian.util.SessionUtils;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.service.goods.UnitPrice;
import com.xmzynt.storm.service.process.ForecastProcessPlan;
import com.xmzynt.storm.util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class AddMachineActivity extends BaseActivity implements View.OnClickListener {
    private ImageView image_personal;
    private TextView tv_head_title;
    private Button btn_save;
    private AddMachineAdapter mAdapter;
    private RecyclerView list;
    private LinearLayoutManager layoutManager;

    private List<ForecastProcessPlan> datas = new ArrayList<ForecastProcessPlan>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_machine);
        initViews();
        initData();
        initEvents();
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("加工新增");
        btn_save = (Button) findViewById(R.id.btn_save);
        list = (RecyclerView)findViewById(R.id.listview_add_machine_list);
        layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        list.setLayoutManager(layoutManager);
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        mAdapter.setOnItemListener(new AddMachineAdapter.OnItemClickListener() {

            @Override
            public void onDeleteClick(int position) {

            }

            @Override
            public void onUnitClick(int position, View v) {
                List<String> mGoodsUuid = new ArrayList<String>();
                mGoodsUuid.add(datas.get(position).getGoods().getUuid());
                HttpManager.dropDownUnit(AddMachineActivity.this, mGoodsUuid, position, new UnitInterface() {
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
        });
    }

    private void initData(){
        Intent intent = getIntent();
        Map<String,GoodsItem> goodsItemMap = (Map<String,GoodsItem>) intent.getSerializableExtra("goods");
        datas = PurchaseBillUtil.toForecastProcessPlanList(goodsItemMap);

        mAdapter = new AddMachineAdapter(datas,this);
        list.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                finish();
                break;
            case R.id.btn_save:
                check();
                break;
            default:
                break;
        }
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

    //检查信息
    private void check() {
        int count = 0;
        for (int i = 0;i < datas.size();i ++) {
            BigDecimal plan = datas.get(i).getPlanQty();
            if (plan.compareTo(BigDecimal.ZERO) > 0) {

            } else {
                count ++;
            }
        }
        if (count == 0) {
            saveNew();
        } else {
            showCustomToast("请检查计划数信息！");
        }
    }

    //新增保存
    public void saveNew() {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();

        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            try {
                body.put(DataConstant.FORECASTPROCESSPLANS, datas);
                reparams.put(BasicConstants.URL_BODY, body);
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
        OkHttpUtils.postString().url(UrlConstants.URL_FORECAST_PROCESS_PLAN_SAVE_NEW)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(reparams.toString())
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
                            startActivity(MachineActivity.class);
                            showCustomToast("保存成功");
                            showLogDebug("main", s);
                        }  else {
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }
}
