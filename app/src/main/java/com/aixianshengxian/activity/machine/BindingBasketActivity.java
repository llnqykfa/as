package com.aixianshengxian.activity.machine;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.adapters.BindingBasketUcodeAdapter;
import com.aixianshengxian.module.ForecastProcessPlanItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.aixianshengxian.R.id.edt_code;

public class BindingBasketActivity extends BaseActivity implements View.OnClickListener {
    private static  final String TAG = "BindingBasketActivity";
    private int BINDING_RESULT = 6;

    private ImageView image_personal,iv_delete;
    private TextView tv_head_title,tv_product_name;
    private EditText edt_code;
    private Button btn_add,btn_confirm;
    private RecyclerView binding_ucode_listview;

    private ForecastProcessPlanItem mProductStockIn;

    //private String uCode;

    //private SwipeRefreshLayout swipe_refresh_widget;
    private BindingBasketUcodeAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private List<String> mData;

    public static BindingBasketActivity mactivity;

    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_basket);

        init();
        initViews();
        initEvents();
        getProductStockIn();
    }

    void init() {
        edt_code = (EditText) findViewById(R.id.edt_code);
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("绑定周转筐");

        tv_product_name = (TextView) findViewById(R.id.tv_product_name);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        edt_code = (EditText) findViewById(R.id.edt_code);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);

        mData = new ArrayList<>();
        binding_ucode_listview = (RecyclerView) findViewById(R.id.binding_ucode_listview);
        layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        binding_ucode_listview.setLayoutManager(layoutManager);
        mAdapter = new BindingBasketUcodeAdapter(mData);//将数据传入适配器

        // 设置item及item中控件的点击事件
        mAdapter.setOnItemListener(new BindingBasketUcodeAdapter.OnItemClickListener() {

            @Override
            public void onDeleteClick(int position) {
                setDeleteAlertDialog(position);
            }
        });
        binding_ucode_listview.setAdapter(mAdapter);//完成适配器设置

        mactivity=this;
    }

    public void getProductStockIn() {
        Intent intent = getIntent();
        mProductStockIn = (ForecastProcessPlanItem) intent.getSerializableExtra("ProductStockIn");
        mPosition = (int) intent.getSerializableExtra("Position");
        if (mProductStockIn.getBasketCodes() != null) {
            mData.clear();
            List<String> ucode = mProductStockIn.getBasketCodes();
            mData.addAll(ucode);
            mAdapter.notifyDataSetChanged();
        }
        tv_product_name.setText(mProductStockIn.getForecastProcessPlan().getGoods().getName());
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        iv_delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                finish();
                break;
            case R.id.btn_add:
                RefreshData();
                break;
            case R.id.btn_confirm:
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                //mProductStockIn.setUcodeMessage(String.valueOf(mData));
                bundle.putSerializable("Binding", (Serializable) mData);
                bundle.putSerializable("Position",mPosition);
                intent.putExtras(bundle);
                setResult(BINDING_RESULT,intent);
                finish();
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
        //String operateTime = String.valueOf(DatesUtils.getStringDate());
        if(uCode.equals("")){
            showCustomToast("请先扫描标签");
            //Toast.makeText(DeliveryActivity.this,"请先扫描标签",Toast.LENGTH_SHORT).show();
        } else {
            int total = 0;//通过计算total判断标签是否被扫描过
            for(int i = 0;i < mData.size();i ++) {
                if (uCode.equals(mData.get(i))) {
                    showCustomToast("这个标签被扫描过");
                    //Toast.makeText(DeliveryActivity.this,"这个标签被扫描过",Toast.LENGTH_SHORT).show();
                    total = total + 1;
                } else {
                    total = total + 0;
                }
            }

            if(total == 0) {//如果total恒为0，则标签没被扫描过
                mData.add(uCode);
                mAdapter.notifyDataSetChanged();
                showCustomToast("添加成功");
                //Toast.makeText(DeliveryActivity.this,"领单成功",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setDeleteAlertDialog(final int position){
        final AlertDialog alert = new AlertDialog.Builder(BindingBasketActivity.this).create();
        alert.setTitle("提示信息");
        //title.setTextColor(getResources().getColor(R.color.black));//标题文字颜色
        alert.setMessage("确认解除绑定周转筐\n" + mData.get(position) + "?");
        //message.setTextColor(getResources().getColor(R.color.text_color_gray_2));
        alert.setCancelable(false);
        //添加确定按钮
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //SPUtil.remove(DeliveryActivity.this,"UCODE_DELIVERY_KEY");
                mData.remove(position);
                mAdapter.notifyDataSetChanged();
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

}
