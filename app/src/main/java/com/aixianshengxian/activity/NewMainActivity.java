package com.aixianshengxian.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.activity.check.CheckActivity;
import com.aixianshengxian.activity.delivery.DeliveryActivity;
import com.aixianshengxian.activity.machine.MachineActivity;
import com.aixianshengxian.activity.plan.PlanActivity;
import com.aixianshengxian.activity.purchase.PurchaseActivity;
import com.aixianshengxian.activity.receive.ReceiveActivity;
import com.aixianshengxian.activity.returned.ReturnedActivity;
import com.aixianshengxian.activity.stock.AllocateActivity;
import com.aixianshengxian.activity.stock.InventoryActivity;
import com.aixianshengxian.activity.stock.StockRemovalActivity;
import com.aixianshengxian.activity.unable.UnableActivity;
import com.aixianshengxian.util.SessionUtils;
import com.xmzynt.storm.service.purchase.bill.PurchaseBillStatus;

import java.io.Serializable;
import java.util.List;

public class NewMainActivity extends BaseActivity implements View.OnClickListener {

    private ImageView image_personal;
    private TextView tv_plan,tv_purchase,tv_check,tv_machine,tv_sales,
             tv_stock_removal,tv_allocate,tv_inventory,
             tv_receive,tv_returned,tv_delivery,tv_unable,tv_head_title;

    private boolean isLogin = false;

    private int isVisble = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);
        initViews();
        initEvents();
    }

    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        image_personal.setImageResource(R.mipmap.account);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("绿百合");
        tv_plan = (TextView) findViewById(R.id.tv_plan);
        if (isVisble == 1) {
            tv_plan.setTextColor(this.getResources().getColor(R.color.gray_text));
            Drawable topDrawable = getResources().getDrawable(R.mipmap.plan);
            topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
            tv_plan.setCompoundDrawables(null, topDrawable, null, null);
        }
        tv_purchase = (TextView) findViewById(R.id.tv_purchase);
        tv_check = (TextView) findViewById(R.id.tv_check);
        tv_machine = (TextView) findViewById(R.id.tv_machine);
        tv_sales = (TextView) findViewById(R.id.tv_sales);
        tv_stock_removal = (TextView) findViewById(R.id.tv_stock_removal);
        tv_allocate = (TextView) findViewById(R.id.tv_allocate);
        tv_inventory = (TextView) findViewById(R.id.tv_inventory);
        tv_receive = (TextView) findViewById(R.id.tv_receive);
        tv_returned = (TextView) findViewById(R.id.tv_returned);
        tv_delivery = (TextView) findViewById(R.id.tv_delivery);
        tv_unable = (TextView) findViewById(R.id.tv_unable);
    }

    protected void initEvents() {
        image_personal.setOnClickListener(this);
        tv_plan.setOnClickListener(this);
        tv_purchase.setOnClickListener(this);
        tv_check.setOnClickListener(this);
        tv_machine.setOnClickListener(this);
        tv_sales.setOnClickListener(this);
        tv_stock_removal.setOnClickListener(this);
        tv_allocate.setOnClickListener(this);
        tv_inventory.setOnClickListener(this);
        tv_receive.setOnClickListener(this);
        tv_returned.setOnClickListener(this);
        tv_delivery.setOnClickListener(this);
        tv_unable.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_personal:
                startActivity(PersonalActivity.class);
                break;
            case R.id.tv_plan:
                isLogin = SessionUtils.getInstance(getApplicationContext()).getLoginState();
                if(isLogin){
                    if (isVisble == 1) {
                        showCustomToast("无该权限");
                    } else {
                        startActivity(PlanActivity.class);
                    }
                }else {
                    showCustomToast("请先登录");
                }
                break;
            case R.id.tv_purchase:
                isLogin = SessionUtils.getInstance(getApplicationContext()).getLoginState();
                if(isLogin){
                    Intent intent1 = new Intent(NewMainActivity.this,PurchaseActivity.class);
                    Bundle bundle1 = new Bundle();
                    List<String> purchaseBillStatus = null;
                    bundle1.putSerializable("Status", (Serializable) purchaseBillStatus);
                    intent1.putExtras(bundle1);
                    startActivity(intent1);
                    startActivity(PurchaseActivity.class);
                }else {
                    showCustomToast("请先登录");
                }
                break;
            case R.id.tv_check:
                isLogin = SessionUtils.getInstance(getApplicationContext()).getLoginState();
                if(isLogin){
                    startActivity(CheckActivity.class);
                }else {
                    showCustomToast("请先登录");
                }
                break;
            case R.id.tv_machine:
                isLogin = SessionUtils.getInstance(getApplicationContext()).getLoginState();
                if(isLogin){
                    startActivity(MachineActivity.class);
                }else {
                    showCustomToast("请先登录");
                }
                break;
            case R.id.tv_sales:
                isLogin = SessionUtils.getInstance(getApplicationContext()).getLoginState();
                if(isLogin){
                    startActivity(MainActivity.class);
                }else {
                    showCustomToast("请先登录");
                }
                break;
            case R.id.tv_delivery:
                isLogin = SessionUtils.getInstance(getApplicationContext()).getLoginState();
                if(isLogin){
                    startActivity(DeliveryActivity.class);
                }else {
                    showCustomToast("请先登录");
                }
                break;
            case R.id.tv_stock_removal:
                isLogin = SessionUtils.getInstance(getApplicationContext()).getLoginState();
                if(isLogin){
                    startActivity(StockRemovalActivity.class);
                }else {
                    showCustomToast("请先登录");
                }
                break;
            case R.id.tv_allocate:
                isLogin = SessionUtils.getInstance(getApplicationContext()).getLoginState();
                if(isLogin){
                    startActivity(AllocateActivity.class);
                }else {
                    showCustomToast("请先登录");
                }
                break;
            case R.id.tv_inventory:
                isLogin = SessionUtils.getInstance(getApplicationContext()).getLoginState();
                if(isLogin){
                    startActivity(InventoryActivity.class);
                }else {
                    showCustomToast("请先登录");
                }
                break;
            case R.id.tv_receive:
                isLogin = SessionUtils.getInstance(getApplicationContext()).getLoginState();
                if(isLogin){
                    startActivity(ReceiveActivity.class);
                }else {
                    showCustomToast("请先登录");
                }
                break;
            case R.id.tv_returned:
                isLogin = SessionUtils.getInstance(getApplicationContext()).getLoginState();
                if(isLogin){
                    startActivity(ReturnedActivity.class);
                }else {
                    showCustomToast("请先登录");
                }
                break;
            case R.id.tv_unable:
                isLogin = SessionUtils.getInstance(getApplicationContext()).getLoginState();
                if(isLogin){
                    startActivity(UnableActivity.class);
                }else {
                    showCustomToast("请先登录");
                }
                break;
            default:;
                break;
        }
    }
}
