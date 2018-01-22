package com.aixianshengxian.activity.delivery;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.activity.receive.ReceiveActivity;
import com.aixianshengxian.activity.returned.ReturnedActivity;
import com.aixianshengxian.adapters.DeliveryUcodeAdapter;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.module.UcodeMessage;
import com.aixianshengxian.util.DatesUtils;
import com.aixianshengxian.util.SPUtil;
import com.aixianshengxian.util.SessionUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.constants.SaleConstants;
import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.service.transport.TransportBill;
import com.xmzynt.storm.util.GsonUtil;
import com.xmzynt.storm.util.query.QueryFilter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class DeliveryActivity extends BaseActivity implements View.OnClickListener {

    private ImageView image_personal,iv_delete;
    private TextView tv_head_title;
    private Button btn_delivery;
    private EditText edt_code;
    private RecyclerView delivery_list;

    private DeliveryUcodeAdapter mAdapter;
    private List<UcodeMessage> mData;
    private List<TransportBill> mAllData;//后台获得的数据
    private List<TransportBill> mBillData = new ArrayList<>();//放入适配器的数据
    private List<TransportBill> mTransportBill = new ArrayList<>();//手动领单中有多个配送单的
    private ArrayList<String> mTransportBills = new ArrayList<String>();//把多个领单放入弹出对话框的载体
    private List<TransportBill> checkedBill = new ArrayList<>();//对话框中选择的单子
    private Map<String,long []> MapData = new HashMap<>();

    private int pageNo = 0;
    private int pageSize = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_delivery);

        init();
        initViews();
        initEvents();
        getDeliveryList();
    }

    void init() {
        edt_code = (EditText) findViewById(R.id.edt_code);
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("配送领单");
        btn_delivery = (Button) findViewById(R.id.btn_delivery);
        edt_code = (EditText) findViewById(R.id.edt_code);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        btn_delivery.setOnClickListener(this);
        iv_delete.setOnClickListener(this);

        delivery_list = (RecyclerView) findViewById(R.id.delivery_listview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        delivery_list.setLayoutManager(layoutManager);
        mAdapter = new DeliveryUcodeAdapter(mBillData);//将数据传入适配器

        // 设置item及item中控件的点击事件
        mAdapter.setOnItemListener(new DeliveryUcodeAdapter.OnItemClickListener() {

            @Override
            public void onDeleteClick(int position) {
                setAlertDialog(position);
            }
        });
        delivery_list.setAdapter(mAdapter);//完成适配器设置
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                /*if (mData.size() != 0) {
                    //利用Gson转化ArrayList<mData>成Json Array数据
                    String jsonStr = gson.toJson(mData);
                    SPUtil.put(DeliveryActivity.this,"UCODE_DELIVERY_KEY",jsonStr);
                }*/
                finish();
                break;
            case R.id.btn_delivery:
                RefreshData();
                break;
            case R.id.iv_delete:
                edt_code.setText("");
                break;
            default:
                break;
        }
    }

    private void RefreshData() {
        String uCode = String.valueOf(edt_code.getText());
        String operateTime = String.valueOf(DatesUtils.getStringDate());
        if(uCode.equals("")){
            showCustomToast("请先扫描标签");
            //Toast.makeText(DeliveryActivity.this,"请先扫描标签",Toast.LENGTH_SHORT).show();
        } else {
            getUcodeResult(uCode);
            /*if(mData.size() == 0){//判断数据的长度是否为0
                UcodeMessage newMessage = new UcodeMessage(uCode,operateTime);
                mData.add(newMessage);
                mAdapter.notifyDataSetChanged();
                showCustomToast("领单成功");
                //Toast.makeText(DeliveryActivity.this,"领单成功",Toast.LENGTH_SHORT).show();
            } else {
                int total = 0;//通过计算total判断标签是否被扫描过
                for(int i = 0;i < mData.size();i ++){
                    if(uCode.equals(mData.get(i).getUCode())){
                        showCustomToast("这个标签被扫描过");
                        //Toast.makeText(DeliveryActivity.this,"这个标签被扫描过",Toast.LENGTH_SHORT).show();
                        total = total + 1;
                    } else {
                        total = total + 0;
                    }
                }
                if(total == 0) {//如果total恒为0，则标签没被扫描过
                    UcodeMessage newMessage = new UcodeMessage(uCode,operateTime);
                    mData.add(newMessage);
                    mAdapter.notifyDataSetChanged();
                    showCustomToast("领单成功");
                    //Toast.makeText(DeliveryActivity.this,"领单成功",Toast.LENGTH_SHORT).show();
                }
            }*/
        }
    }

    public void setAlertDialog(final int position){
        final AlertDialog alert = new AlertDialog.Builder(DeliveryActivity.this).create();
        alert.setTitle("提示信息");
        //title.setTextColor(getResources().getColor(R.color.black));//标题文字颜色
        alert.setMessage("确认撤销\n" + mBillData.get(position).getBillNumber() + "?");
        //message.setTextColor(getResources().getColor(R.color.text_color_gray_2));
        alert.setCancelable(false);
        //添加确定按钮
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //SPUtil.remove(DeliveryActivity.this,"UCODE_DELIVERY_KEY");
                getDeliveryDelete(position);
            }
        });
        //添加取消按钮
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alert.dismiss();
            }
        });
        alert.show();
        //确定按钮字的颜色
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.green_text));
        //取消按钮字的颜色
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.gray_text2));
    }

    //配送手动领单
    private void setBillsAlertDialog() {
        for (int i = 0;i < mTransportBill.size();i ++) {
            mTransportBills.add(mTransportBill.get(i).getBillNumber());//ArrayList可以根据长度变化
        }
        int size = mTransportBills.size();//获取长度
        final boolean[] checkedItems = new boolean[size];
        final String[] items = new String[size];
        for (int i = 0;i < size;i ++) {//把ArrayList转换成String[]数组
            checkedItems[i] = false;
            items[i] = mTransportBills.get(i);
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryActivity.this);
        builder.setTitle("请选择要领取的单子");
        builder.setCancelable(true);

        builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedItems[which] = isChecked;
            }
        });
        //添加确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                for (int j = 0;j <checkedItems.length;j ++) {
                    if (checkedItems[j]) {
                        checkedBill.add(mTransportBill.get(j));
                    }
                }
                int size = checkedBill.size();
                long[] version = new long[size];
                for (int k = 0;k < size;k ++) {
                    version[k] = checkedBill.get(k).getVersion();
                }
                MapData.put("配送单号",version);
                getTransport(MapData);
                getDeliveryList();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //查询已领单列表
    private void getDeliveryList () {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        String today = String.valueOf(DatesUtils.getStringDateShort());
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            final QueryFilter filter = new QueryFilter();
            filter.setPage(pageNo);
            filter.setPageSize(pageSize);
            filter.setDefaultPageSize(0);
            filter.put(DataConstant.DISTRIBUTETIME,today);
            filter.put(DataConstant.DRIVERUUID,userUuid);
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
        OkHttpUtils.postString().url(UrlConstants.URL_DELIVERY_LIST )
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
                            Type listTypeA = new TypeToken<List<TransportBill>>() {
                            }.getType();
                            if (response.getData() != null) {
                                mBillData.clear();
                                mAllData = GsonUtil.getGson().fromJson(response.getData(), listTypeA);
                                if (mAllData != null && mAllData.size() > 0) {
                                    mBillData.addAll(mAllData);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                            showCustomToast("请求成功");
                            showLogDebug("main", s);
                        }  else {
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }

    public void setResultAlertDialog(String response) {
        final AlertDialog alert = new AlertDialog.Builder(DeliveryActivity.this).create();
        alert.setTitle("提示信息");
        //title.setTextColor(getResources().getColor(R.color.black));//标题文字颜色
        alert.setMessage(response);
        //message.setTextColor(getResources().getColor(R.color.text_color_gray_2));
        alert.setCancelable(false);
        //添加确定按钮
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();
        //确定按钮字的颜色
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.green_text));
    }

    //自动领单
    private void getUcodeResult (final String uCode) {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            try {
                body.put(DataConstant.SOURCEBILLNUMBER, uCode);
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
        OkHttpUtils.postString().url(UrlConstants.URL_DELIVERY_SCAN  )
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
                            if (response.getData() != null) {
                                Type listTypeA = new TypeToken<List<TransportBill>>() {
                            }.getType();
                                mTransportBill  = GsonUtil.getGson().fromJson(response.getData(), listTypeA);
                                if (mTransportBill != null && mTransportBill.size() > 0) {
                                    setBillsAlertDialog();
                                }
                            } else {
                                getDeliveryList();
                                showCustomToast("领单成功");
                            }
                        } else {
                            setResultAlertDialog(response.getMessage());
                            //showCustomToast(response.getMessage());
                        }
                    }
                });
    }

    //手动领单
    private void getTransport (Map MapData) {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            try {
                body.put(DataConstant.MAPDATA,MapData);
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
        OkHttpUtils.postString().url(UrlConstants.URL_DELIVERY_HAND)
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
                            getDeliveryList();
                            showCustomToast("领单成功");
                        } else {
                            //setResultAlertDialog(response.getMessage());
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }

    //撤销领单
    private void getDeliveryDelete(final int position) {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            try {
                body.put(DataConstant.BILLNUMBER, mBillData.get(position).getBillNumber());
                body.put(BasicConstants.Field.VERSION, mBillData.get(position).getVersion());
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
        OkHttpUtils.postString().url(UrlConstants.URL_DELIVERY_DELETE  )
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
                            //getDeliveryList();
                            mBillData.remove(position);
                            mAdapter.notifyDataSetChanged();
                            showCustomToast("撤销成功");
                        } else {
                            //setResultAlertDialog(response.getMessage());
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }
}
