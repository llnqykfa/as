package com.aixianshengxian.activity.addOrder;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.adapters.DepartmentAdapter;
import com.google.gson.reflect.TypeToken;
import com.xmzynt.storm.service.user.customer.Department;
import com.xmzynt.storm.service.user.customer.MerchantCustomer;
import com.xmzynt.storm.util.GsonUtil;

import java.lang.reflect.Type;
import java.util.List;

import static com.aixianshengxian.R.id.image_search;

public class ChooseDepartmentActivity extends BaseActivity implements View.OnClickListener {

    private ImageView image_personal;

    private TextView tv_non_deparment_name;

    private RecyclerView recylerView_department_list;
    private List<Department> mDepartments;
    private LinearLayoutManager linearLayoutManager;
    private MerchantCustomer merchant;
    private TextView tv_head_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_department);

        initData();
        initViews();
        initEvents();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getString("departments") != null) {
            String departments = bundle.getString("departments");
            Type listTypeA = new TypeToken<List<Department>>() {
            }.getType();
            mDepartments = GsonUtil.getGson().fromJson(departments, listTypeA);
        }
        if (bundle != null && bundle.getSerializable("merchantClient") != null) {
            merchant = (MerchantCustomer) bundle.getSerializable("merchantClient");
        }
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        if(merchant != null && merchant.getCustomer().getName()!= null){
            tv_head_title.setText(merchant.getCustomer().getName());
        }

        recylerView_department_list = (RecyclerView) findViewById(R.id.recylerView_department_list);
        tv_non_deparment_name = (TextView) findViewById(R.id.tv_non_deparment_name);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recylerView_department_list.setLayoutManager(linearLayoutManager);
        DepartmentAdapter adapter = new DepartmentAdapter(getApplicationContext(), mDepartments);
        adapter.setOnItemClickLitener(new DepartmentAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(Department department) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("department", department);
                bundle.putSerializable("merchantClient", merchant);
                startActivity(NewOrderActivity.class, bundle);

            }
        });
        recylerView_department_list.setAdapter(adapter);
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        tv_non_deparment_name.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                finish();
                break;
            case image_search://搜索
                break;
            case R.id.tv_non_deparment_name://不选择部门
                Bundle bundle = new Bundle();
                bundle.putSerializable("merchantClient", merchant);
                startActivity(NewOrderActivity.class, bundle);
                finish();
                break;
        }
    }



}
