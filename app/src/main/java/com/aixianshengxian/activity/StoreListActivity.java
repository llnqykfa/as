package com.aixianshengxian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.adapters.SingleAdapter;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.util.HttpUtil;
import com.aixianshengxian.util.JsonUtil;
import com.google.gson.reflect.TypeToken;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.service.wms.warehouse.Warehouse;
import com.xmzynt.storm.util.GsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class StoreListActivity extends BaseActivity implements View.OnClickListener {
    private final int STOCK_OUT_RESULT = 2;
    private ImageView image_personal;
    private TextView tv_head_title;
    private ImageView image_search;
    private EditText edit_search_content;
    private ImageView image_close_search;
    private ListView listview_store_list;
    private SingleAdapter adapter;
    private List<Warehouse> AlldataList =new ArrayList<>();
    private List<Warehouse> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        initViews();
        setData();
        loadData();
        initEvents();
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("选择仓库");
        image_search = (ImageView) findViewById(R.id.image_search);
        edit_search_content = (EditText) findViewById(R.id.edit_search_content);
        image_close_search = (ImageView) findViewById(R.id.image_close_search);
        image_close_search.setVisibility(View.GONE);
        listview_store_list = (ListView) findViewById(R.id.listview_store_list);
    }

    private void setData(){
     if(adapter != null){
         listview_store_list.setAdapter(adapter);
     }

    }
    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        image_search.setOnClickListener(this);
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
                if(value != null && !TextUtils.isEmpty(value)){
                    mDataList.clear();
                    if(AlldataList != null && AlldataList.size()>0){
                        for(Warehouse warehouse : AlldataList){
                            if(warehouse.getName().contains(value)){
                                mDataList.add(warehouse);
                            }
                        }
                        adapter.notifyDataSetChanged();

                    }
                }else {
                    mDataList.clear();
                    mDataList.addAll(AlldataList);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        listview_store_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //showCustomToast("选择的是"+position);
                int index = listview_store_list .getCheckedItemPosition();     // 即获取选中位置
                if(ListView.INVALID_POSITION != index){
                    if(mDataList!= null && mDataList.size()>0){
                        showCustomToast(mDataList.get(index).getName());
                        if(mDataList.get(index)!= null){
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("warehouse",mDataList.get(index));
                            Intent intent = new Intent();
                            intent.putExtras(bundle);
                            setResult(STOCK_OUT_RESULT,intent);
                            finish();
                        }
                    }
                }


            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_personal:
                finish();
                break;
            case R.id.image_search://搜索

                break;
        }
    }

    private void loadData(){
        JSONObject params = JsonUtil.buildParams(getApplicationContext());
        JSONObject body = new JSONObject();
        try {
            params.put(BasicConstants.URL_BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtil.post(UrlConstants.URL_STORE_LIST, params.toString(), new HttpUtil.HttpListener() {
            @Override
            public void successResponse(String s, int i) {
                ResponseBean response = GsonUtil.getGson().fromJson(s,ResponseBean.class);
                if(response.getErrorCode() == 0){//审核成功
                    if(response.getData() != null){
                        Type listType = new TypeToken<List<Warehouse>>(){}.getType();
                        AlldataList = GsonUtil.getGson().fromJson(response.getData(),listType);
                        if(AlldataList != null && AlldataList.size()>0){
                            mDataList.clear();
                            mDataList.addAll(AlldataList);
                            adapter = new SingleAdapter(mDataList,getApplicationContext());
                            listview_store_list.setAdapter(adapter);

                        }else {
                            showCustomToast("数据下发为0");
                        }

                    }
                }else {
                    if(response.getMessage() != null){
                        showCustomToast(response.getMessage());
                    }

                }
            }

            @Override
            public void errorResponse(Call call, Exception e, int i) {
                showCustomToast("请求失败");
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

}
