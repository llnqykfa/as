package com.aixianshengxian.activity.stock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.activity.search.SearchDepotActivity;
import com.aixianshengxian.adapters.BasketUcodeAdapter;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.http.HttpManager;
import com.aixianshengxian.module.BroadcastAction;
import com.aixianshengxian.util.DatesUtils;
import com.aixianshengxian.util.SessionUtils;
import com.aixianshengxian.widget.PhotoPicker;
import com.lidong.photopicker.PhotoPickerActivity;
import com.lidong.photopicker.PhotoPreviewActivity;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.idname.IdName;
import com.xmzynt.storm.basic.operateinfo.OperateInfo;
import com.xmzynt.storm.basic.ucn.UCN;
import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.service.wms.allocate.AllocateRecord;
import com.xmzynt.storm.service.wms.stock.Stock;
import com.xmzynt.storm.service.wms.warehouse.Warehouse;
import com.xmzynt.storm.util.GsonUtil;
import com.xmzynt.storm.util.IMGType;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

public class AllocateDetailActivity extends BaseActivity implements View.OnClickListener {
    private final int SEARCH_DEPOT_REQUEST = 9;
    private final int SEARCH_DEPOT_RESULT = 10;

    private ImageView image_personal,iv_photo,iv_delete;;
    private TextView tv_head_title,tv_type,tv_associated;
    private TextView tv_depot,tv_product_name,tv_quantity,tv_unit1,tv_unit2;
    private TextView tv_stockin_time,tv_place,tv_batch_number,tv_productive_time,tv_effective_time;
    private EditText edt_code,edt_allocate_num;
    private Button btn_confirm,btn_add;
    private RecyclerView listview_associated,binding_ucode_listview;

    private LinearLayout ll_nomessage,ll_message;

    private BasketUcodeAdapter mAdapter;
    private List<String> mData;

    //拍照上传处
    private static final int REQUEST_CAMERA_CODE = 19;
    private static final int REQUEST_PREVIEW_CODE = 20;
    private ArrayList<String> imagePaths = new ArrayList<>();
    private ArrayList<String> imagePathsExtra = new ArrayList<>();

    private int page = 1;
    private int index = 0;
    private int pageNo = 0;
    private int pageSize = 20;

    private List<String> mImage = new ArrayList<>();
    private List<Stock> Stocks = new ArrayList<>();
    private AllocateRecord allocateRecord = new AllocateRecord();

    static final String TAG = "Result";

    public static AllocateDetailActivity mactivity;

    private PhotoPicker photoPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allocate_detail);
        init();

        initViews();
        initEvents();

        getUcode();
        getQueryStock();
    }

    void init() {
        edt_code = (EditText) findViewById(R.id.edt_code);
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("调拨详情");
        btn_add = (Button) findViewById(R.id.btn_add);
        tv_type = (TextView) findViewById(R.id.tv_type);
        edt_code = (EditText) findViewById(R.id.edt_code);
        edt_allocate_num = (EditText) findViewById(R.id.edt_allocate_num);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);
        //iv_photo = (ImageView) findViewById(R.id.iv_photo);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        ll_nomessage = (LinearLayout) findViewById(R.id.ll_nomessage);
        ll_message = (LinearLayout) findViewById(R.id.ll_message);

        photoPicker = (PhotoPicker) findViewById(R.id.photo_view);

        mactivity = this;
    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        tv_type.setOnClickListener(this);
        iv_delete.setOnClickListener(this);
        //iv_photo.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);

        mData = new ArrayList<>();

        binding_ucode_listview = (RecyclerView) findViewById(R.id.binding_ucode_listview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding_ucode_listview.setLayoutManager(layoutManager);
        mAdapter = new BasketUcodeAdapter(mData);//将数据传入适配器

        // 设置item及item中控件的点击事件
        mAdapter.setOnItemListener(new BasketUcodeAdapter.OnItemClickListener() {

            @Override
            public void onDeleteClick(int position) {
                setAlertDialog(position);
            }
        });
        binding_ucode_listview.setAdapter(mAdapter);//完成适配器设置
    }

    protected void initData() {
        //仓库
        tv_depot = (TextView) findViewById(R.id.tv_depot);
        tv_depot.setText(Stocks.get(0).getWarehouse() == null ?"":Stocks.get(0).getWarehouse().getName());
        //商品
        tv_product_name = (TextView) findViewById(R.id.tv_product_name);
        tv_product_name.setText(Stocks.get(0).getGoods().getName());
        //数量
        tv_quantity = (TextView) findViewById(R.id.tv_quantity);
        tv_quantity.setText(String.valueOf(Stocks.get(0).getAmount()));
        //单位1
        tv_unit1 = (TextView) findViewById(R.id.tv_unit1);
        tv_unit1.setText(Stocks.get(0).getGoodsUnit().getName());
        //单位2
        tv_unit2 = (TextView) findViewById(R.id.tv_unit2);
        tv_unit2.setText(Stocks.get(0).getGoodsUnit().getName());
        //入库时间
        tv_stockin_time = (TextView) findViewById(R.id.tv_stockin_time);
        tv_stockin_time.setText(DatesUtils.dateToStr(Stocks.get(0).getStockInDate()));
        //产地
        tv_place = (TextView) findViewById(R.id.tv_place);
        //tv_place.setText(Stocks.get(0).getOrigin());
        //批号
        tv_batch_number = (TextView) findViewById(R.id.tv_batch_number);
        //tv_batch_number.setText(Stocks.get(0).getBatchNumber());
        //生产日期
        tv_productive_time = (TextView) findViewById(R.id.tv_productive_time);
        /*if (Stocks.get(0).getProduceDate() != null) {
            tv_productive_time.setText(DatesUtils.dateToStr(Stocks.get(0).getProduceDate()));
        } else {
            tv_productive_time.setText("");
        }*/

        //有效日期
        tv_effective_time = (TextView) findViewById(R.id.tv_effective_time);
        /*if (Stocks.get(0).getEffectiveDate() != null) {
            tv_effective_time.setText(DatesUtils.dateToStr(Stocks.get(0).getEffectiveDate()));
        } else {
            tv_effective_time.setText("");
        }*/

        //调拨数量
        edt_allocate_num = (EditText) findViewById(R.id.edt_allocate_num);
        //这个很重要，先移开TextWatcher的监听器
        if (edt_allocate_num.getTag() instanceof TextWatcher) {
            edt_allocate_num.removeTextChangedListener((TextWatcher) edt_allocate_num.getTag());
        }
        TextWatcher watcher1 = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    Toast.makeText(AllocateDetailActivity.mactivity,"调拨数量不能为空",Toast.LENGTH_SHORT).show();
                } else {

                }
            }
        };
        edt_allocate_num.addTextChangedListener(watcher1);
        edt_allocate_num.setTag(watcher1);

        tv_associated = (TextView) findViewById(R.id.tv_associated);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                Intent intent = new Intent(
                        BroadcastAction.ACTION_STOCK_RECORD_REFRESH);
                // 发送广播
                sendBroadcast(intent);
                finish();
                break;
            case R.id.btn_add:
                RefreshData();
                break;
            case R.id.tv_type:
                Intent intent1 = new Intent(AllocateDetailActivity.this, SearchDepotActivity.class);
                startActivityForResult(intent1,SEARCH_DEPOT_REQUEST);
                break;
            case R.id.iv_delete:
                edt_code.setText("");
                break;
            /*case R.id.iv_photo:
                break;*/
            case R.id.btn_confirm:
                check();
                break;
            default:
                break;
        }
    }

    //仓库
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SEARCH_DEPOT_REQUEST){
            if(resultCode == SEARCH_DEPOT_RESULT){
                Warehouse warehouse = (Warehouse) data.getExtras().getSerializable("depot");
                if(warehouse != null){
                    showLogDebug(TAG,"onActivityDepotResult");
                    tv_type.setText(warehouse.getName());
                    UCN outDepot = new UCN();
                    outDepot.setUuid(warehouse.getUuid());
                    outDepot.setCode(warehouse.getCode());
                    outDepot.setName(warehouse.getName());
                    allocateRecord.setInWarehouse(outDepot);
                }
            }
        }
        //照片上传处
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                // 选择照片
                case REQUEST_CAMERA_CODE:
                    imagePaths = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
                    Log.d(TAG, "list: " + "list = [" + imagePaths.size());
                    photoPicker.requestCameraCallback(imagePaths);
//                    pickloadAdpater(list);
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE:
                    imagePathsExtra = data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT);
                    Log.d(TAG, "ListExtra: " + "ListExtra = [" + imagePathsExtra.size());
//                    loadAdpater(ListExtra);
                    photoPicker.requestCameraCallback(imagePathsExtra);
                    break;
            }
        }
    }

    //获取ucode
    private void getUcode() {
        Intent intent = getIntent();
        Stocks = (List<Stock>) intent.getSerializableExtra("Stocks");
    }

    //周转筐列表展示
    private void RefreshData() {
        String uCode = String.valueOf(edt_code.getText());
        if(uCode.equals("")){
            showCustomToast("请先扫描标签");
            //Toast.makeText(DeliveryActivity.this,"请先扫描标签",Toast.LENGTH_SHORT).show();
        } else {
            if(mData.size() == 0){//判断数据的长度是否为0
                mData.add(uCode);
                mAdapter.notifyDataSetChanged();
                showCustomToast("添加成功");
                //Toast.makeText(DeliveryActivity.this,"领单成功",Toast.LENGTH_SHORT).show();
            } else {
                int total = 0;//通过计算total判断标签是否被扫描过
                for(int i = 0;i < mData.size();i ++){
                    if(uCode.equals(mData.get(i))){
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
                    //Toast.makeText(AllocateDetailActivity.this,"领单成功",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //解除绑定
    public void setAlertDialog(final int position){
        final AlertDialog alert = new AlertDialog.Builder(AllocateDetailActivity.this).create();
        alert.setTitle("提示信息");
        //title.setTextColor(getResources().getColor(R.color.black));//标题文字颜色
        alert.setMessage("确认解除绑定周转箱\n" + mData.get(position) + "?");
        //message.setTextColor(getResources().getColor(R.color.text_color_gray_2));
        alert.setCancelable(false);
        //添加确定按钮
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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

    //周转筐查询库存
    private void getQueryStock() {
        if (Stocks.size() == 0) {
            ll_nomessage.setVisibility(View.VISIBLE);
            ll_message.setVisibility(View.GONE);
        } else {
            ll_message.setVisibility(View.VISIBLE);
            initData();
            /*if (Stocks.get(0).getBasketCodes() != null) {
                List<String> associatedBasket = Stocks.get(0).getBasketCodes();
                showAssociatedBasket(associatedBasket);
            } else {
                tv_associated.setText("没有关联周转箱");
            }*/
            ll_nomessage.setVisibility(View.GONE);
        }
    }

    //关联周转箱
    private void showAssociatedBasket(List<String> associatedBasket) {
        String basketStr = new String();
        for (int i = 0;i < associatedBasket.size();i ++) {
            if (i == associatedBasket.size() - 1) {
                basketStr = basketStr + associatedBasket.get(i);
            } else {
                basketStr = basketStr + associatedBasket.get(i) + ",";
            }
        }
        tv_associated.setText(basketStr);
    }

    //检查信息
    private void check() {
        if (tv_type.getText().equals("请选择仓库")) {
            showCustomToast("请检查调入仓库的信息");
        } else {
            if (tv_type.getText().equals(tv_depot.getText())) {
                showCustomToast("调入仓库不应与出库仓库相同");
            } else {
                if (!String.valueOf(edt_allocate_num.getText()).equals("") && new BigDecimal(String.valueOf(edt_allocate_num.getText())).compareTo(BigDecimal.ZERO) > 0) {
                    getAllocate();
                    /*new Thread(){
                        @Override
                        public void run() {
                            super.run();*/
                            //HttpManager.upLoadPhoto(this,AllocateDetailActivity.this,imagePaths);
                            String userUuid = SessionUtils.getInstance(getApplication()).getCustomerUUID();
                            if (imagePaths.contains("000000")){
                                imagePaths.remove("000000");
                            }
                            if (imagePaths.size() > 0) {
                                HttpManager.upLoadPhoto(AllocateDetailActivity.this,userUuid,imagePaths, IMGType.GOODS_IMG.name());
                            }

                        /*}
                    }.start();*/
                } else {
                    showCustomToast("调拨数量不能为空或者为“0”");
                }
            }
        }
    }

    //确认
    private void getAllocate() {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();

        //org
        IdName org = SessionUtils.getInstance(getApplicationContext()).getOrg() == null?null:SessionUtils.getInstance(getApplicationContext()).getOrg();

        //AllocateRecord stockOutRecord = new AllocateRecord();
        if (org == null) {//org
            IdName newOrg = new IdName();
            newOrg.setId(userUuid);
            newOrg.setName(userName);
            allocateRecord.setOrg(newOrg);
        } else {
            allocateRecord.setOrg(org);
        }
        allocateRecord.setGoods(Stocks.get(0).getGoods());//goods
        allocateRecord.setGoodsSpec(Stocks.get(0).getGoodsSpec());//goodsSpec
        allocateRecord.setGoodsUnit(Stocks.get(0).getGoodsUnit());//goodsUnit
        allocateRecord.setAllocateQty(new BigDecimal(String.valueOf(edt_allocate_num.getText())));//allocateQty
        UCN inDepot = new UCN();
        inDepot.setUuid(Stocks.get(0).getWarehouse().getUuid());
        inDepot.setCode(Stocks.get(0).getWarehouse().getCode());
        inDepot.setName(Stocks.get(0).getWarehouse().getName());
        allocateRecord.setOutWarehouse(inDepot);
        OperateInfo operateInfo = new OperateInfo();
        IdName operate = new IdName();
        operate.setId(userUuid);
        operate.setName(userName);
        operateInfo.setOperator(operate);
        operateInfo.setOperateTime(new Date());
        allocateRecord.setOperateInfo(operateInfo);//operateInfo
        allocateRecord.setRemark(Stocks.get(0).getRemark());//remark
        allocateRecord.setSourceStockUuid(Stocks.get(0).getUuid());
        //allocateRecord.setBasketCodes(mData);
        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            try {
                body.put(DataConstant.ALLOCATERECORD, GsonUtil.getGson().toJson(allocateRecord));
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
        OkHttpUtils.postString().url(UrlConstants.URL_ALLOCATE )
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
                            showCustomToast("请求成功");
                            finish();
                            Intent intent = new Intent(
                                    BroadcastAction.ACTION_STOCK_RECORD_REFRESH);
                            // 发送广播
                            sendBroadcast(intent);
                            showLogDebug("main", s);
                        }  else {
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }
}
