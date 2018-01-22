package com.aixianshengxian.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.adapters.DeliveryListAdapter;
import com.google.gson.reflect.TypeToken;
import com.xmzynt.storm.service.transport.TransportBill;
import com.xmzynt.storm.util.GsonUtil;

import java.lang.reflect.Type;
import java.util.List;

public class DeliveryListActivity extends BaseActivity implements View.OnClickListener {

    private ImageView image_personal;
    private TextView tv_head_title;
    private RecyclerView recylerView_delivery_list;
    private List<TransportBill> transportBills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_list);
        initData();
        initViews();
        initEvents();

    }

    private void initData(){
        String data = getIntent().getExtras().getString("transportBills");
        Type listType = new TypeToken<List<TransportBill>>(){}.getType();
        transportBills = GsonUtil.getGson().fromJson(data,listType);
    }
    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("配送列表");
        recylerView_delivery_list = (RecyclerView) findViewById(R.id.recylerView_delivery_list);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recylerView_delivery_list.setLayoutManager(linearLayoutManager);
        if(transportBills != null && transportBills.size()>0){
            DeliveryListAdapter adapter = new DeliveryListAdapter(transportBills,getApplicationContext());
            adapter.setOnItemClickLitener(new DeliveryListAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(TransportBill transportBill) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("transportBill", transportBill);
                    startActivity(SignAndReturnActivity.class, bundle);
                    finish();
                }
            });
            recylerView_delivery_list.setAdapter(adapter);
        }
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_personal:
                finish();
                break;

        }
    }

}
