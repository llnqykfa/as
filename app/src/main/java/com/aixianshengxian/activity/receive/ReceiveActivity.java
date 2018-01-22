package com.aixianshengxian.activity.receive;

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

import com.aixianshengxian.adapters.ReceiveUcodeAdapter;
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
import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class ReceiveActivity extends BaseActivity implements View.OnClickListener {

    private ImageView image_personal,iv_delete;
    private TextView tv_head_title,tv_delete;
    private Button btn_receive;
    private EditText edt_code;
    private RecyclerView receive_list;
    private ReceiveUcodeAdapter mAdapter;
    private List<UcodeMessage> mData;
    private boolean SP = true;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_receive);

        init();
        initViews();
        initEvents();
    }

    void init() {
        edt_code = (EditText) findViewById(R.id.edt_code);
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("配送领筐");
        btn_receive = (Button) findViewById(R.id.btn_receive);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        edt_code = (EditText) findViewById(R.id.edt_code);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);

        mData = new ArrayList<>();
        SP = SPUtil.contains(ReceiveActivity.this,"UCODE_RECEIVE_KEY");
        if(SP == true) {
            String str = (String) SPUtil.get(ReceiveActivity.this, "UCODE_RECEIVE_KEY", "");
            if(str != null){
                mData = gson.fromJson(str, new TypeToken<List<UcodeMessage>>(){}.getType());
            }
        }

        receive_list = (RecyclerView) findViewById(R.id.receive_listview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        receive_list.setLayoutManager(layoutManager);
        mAdapter = new ReceiveUcodeAdapter(mData);//将数据传入适配器
        receive_list.setAdapter(mAdapter);//完成适配器设置
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        btn_receive.setOnClickListener(this);
        tv_delete.setOnClickListener(this);
        iv_delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                if (mData.size() != 0) {
                    //利用Gson转化ArrayList<mData>成Json Array数据
                    String jsonStr = gson.toJson(mData);
                    SPUtil.put(ReceiveActivity.this,"UCODE_RECEIVE_KEY",jsonStr);
                }

                finish();
                break;
            case R.id.btn_receive:
                RefreshData();
                break;
            case R.id.tv_delete:
                Delete();
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
            //Toast.makeText(ReceiveActivity.this,"请先扫描标签",Toast.LENGTH_SHORT).show();
        } else {
            getUcodeResult(uCode,operateTime);
            /*if(mData.size() == 0){//判断数据的长度是否为0
                UcodeMessage newMessage = new UcodeMessage(uCode,operateTime);
                mData.add(newMessage);
                mAdapter.notifyDataSetChanged();
                showCustomToast("领筐成功");
                //Toast.makeText(ReceiveActivity.this,"领筐成功",Toast.LENGTH_SHORT).show();
            } else {
                int total = 0;//通过计算total判断标签是否被扫描过
                for(int i = 0;i < mData.size();i ++){
                    if(uCode.equals(mData.get(i).getUCode())){
                        showCustomToast("这个标签被扫描过");
                        //Toast.makeText(ReceiveActivity.this,"这个标签被扫描过",Toast.LENGTH_SHORT).show();
                        total = total + 1;
                    } else {
                        total = total + 0;
                    }
                }
                if(total == 0) {//如果total恒为0，则标签没被扫描过
                    UcodeMessage newMessage = new UcodeMessage(uCode,operateTime);
                    mData.add(newMessage);
                    mAdapter.notifyDataSetChanged();
                    showCustomToast("领筐成功");
                    //Toast.makeText(ReceiveActivity.this,"领筐成功",Toast.LENGTH_SHORT).show();
                }
            }*/
        }
    }

    private void Delete() {
        SPUtil.remove(ReceiveActivity.this,"UCODE_RECEIVE_KEY");
        mData.clear();
        mAdapter.notifyDataSetChanged();
    }

    public void setResultAlertDialog(String response) {
        final AlertDialog alert = new AlertDialog.Builder(ReceiveActivity.this).create();
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

    private void getUcodeResult (final String uCode,final String operateTime) {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            try {
                body.put(DataConstant.UCODE, uCode );
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
        OkHttpUtils.postString().url(UrlConstants.URL_RECEIVE)
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
                            UcodeMessage newMessage = new UcodeMessage(uCode,operateTime);
                            mData.add(newMessage);
                            mAdapter.notifyDataSetChanged();
                            showCustomToast("领筐成功");
                        } else {
                            setResultAlertDialog(response.getMessage());
                            //showCustomToast(response.getMessage());
                        }
                    }
                });
    }
}
