package com.aixianshengxian.activity.check;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.module.StockInRecordItem;
import com.aixianshengxian.util.DateUtils;
import com.aixianshengxian.util.DatesUtils;
import com.aixianshengxian.util.PurchaseBillUtil;
import com.aixianshengxian.util.SessionUtils;
import com.xmzynt.storm.basic.BasicConstants;


import com.xmzynt.storm.basic.idname.IdName;

import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.service.purchase.bill.PurchaseData;

import com.xmzynt.storm.service.wms.stockin.StockInRecord;
import com.xmzynt.storm.service.wms.stockin.StockInType;
import com.xmzynt.storm.util.DateUtil;
import com.xmzynt.storm.util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class CheckDetailActivity extends BaseActivity implements View.OnClickListener {
    private ImageView image_personal,iv_photo;
    private TextView tv_head_title,tv_produce_time,tv_effect_time;
    private TextView tv_product_name,tv_unit,tv_purchase_state,tv_purchase_num,tv_stocked_in_num;
    private TextView tv_purchase_remark,tv_subtotal;
    private TextView tv_provider,tv_purchase_bill,tv_purchase_time,tv_sd_unit;
    private EditText edt_purchase_price,edt_stock_in_price,edt_should_stock_in_num,edt_buckle_num;
    private EditText edt_real_stock_in_num1,edt_real_stock_in_num2;
    private EditText edt_check_remark,edt_cdr,edt_batch_number;
    private Button btn_over,btn_stock_in;

    private List<String> mImage = new ArrayList<>();
    private PurchaseData mPurchaseData;
    private StockInRecord stockInRecord = new StockInRecord();
    private StockInRecordItem stockInRecordItem = new StockInRecordItem();
    private String UCODE;
    private int stock = 0;

    public static CheckDetailActivity mactivity;

    //时间选择器
    int mYear, mMonth, mDay;
    final int DATE_DIALOG = 1;
    int WHICH = 0;//用于区分哪个时间选择

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_detail);

        getUcode();
        //getScanPurchaseData();

        initViews();
        initEvents();
        initData();
    }

    protected void initData() {
        //顶部仓库
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("入库仓库:" + mPurchaseData.getWarehouse() == null ?"":mPurchaseData.getWarehouse().getName());

        //商品名称
        tv_product_name = (TextView) findViewById(R.id.tv_product_name);
        String uuid = mPurchaseData.getPurchaseBillLine().getGoods().getCode();
        String name = mPurchaseData.getPurchaseBillLine().getGoods().getName();
        String spec =  mPurchaseData.getPurchaseBillLine().getGoodsSpec()==null?null:mPurchaseData.getPurchaseBillLine().getGoodsSpec();

        if (spec != null) {
            Map<String, Object> valueMap = PurchaseBillUtil.getMap(spec);
            if (spec.equals("{}")) {
                tv_product_name.setText(uuid + "  " + name);//商品名称
            } else {
                tv_product_name.setText(uuid + "  " + name + "  " + valueMap.keySet() + valueMap.values());//商品名称
            }
        } else {
            tv_product_name.setText(uuid + "  " + name);//商品名称
        }

        //tv_product_name.setText(mPurchaseData.getPurchaseBillLine().getGoods().getName());

        //单位
        tv_unit = (TextView) findViewById(R.id.tv_unit);
        tv_unit.setText(mPurchaseData.getPurchaseBillLine().getGoodsUnit().getName());

        //状态
        tv_purchase_state = (TextView) findViewById(R.id.tv_purchase_state);
        tv_purchase_state.setText(mPurchaseData.getPurchaseBillLine().getStatus().getCaption());

        //采购数
        tv_purchase_num = (TextView) findViewById(R.id.tv_purchase_num);
        tv_purchase_num.setText(String.valueOf(mPurchaseData.getPurchaseBillLine().getPurchaseQty()));

        //已入库数
        tv_stocked_in_num = (TextView) findViewById(R.id.tv_stocked_in_num);
        tv_stocked_in_num.setText(String.valueOf(mPurchaseData.getPurchaseBillLine().getHasStockInQty()));

        //采购备注
        tv_purchase_remark = (TextView) findViewById(R.id.tv_purchase_remark);
        tv_purchase_remark.setText(mPurchaseData.getPurchaseBillLine().getRemark());

        //部门
        tv_subtotal = (TextView) findViewById(R.id.tv_subtotal);
        String customeName=mPurchaseData.getPurchaseBillLine().getCustomer()==null ?"":mPurchaseData.getPurchaseBillLine().getCustomer().getName();
        if(mPurchaseData.getPurchaseBillLine().getCustomerDept()!=null){
            customeName+= "【"+mPurchaseData.getPurchaseBillLine().getCustomerDept().getName()+"】";
        }
        tv_subtotal.setText(customeName);

        //供应商
        tv_provider = (TextView) findViewById(R.id.tv_provider);
        tv_provider.setText(mPurchaseData.getSupplier() == null ?"": mPurchaseData.getSupplier().getName());

        //采购单号
        tv_purchase_bill = (TextView) findViewById(R.id.tv_purchase_bill);
        tv_purchase_bill.setText(mPurchaseData.getPurchaseBillNumber());

        //采购日期
        tv_purchase_time = (TextView) findViewById(R.id.tv_purchase_time);
        tv_purchase_time.setText(DatesUtils.dateToStr(mPurchaseData.getDeliveryTime()));

        //入库单价
        edt_purchase_price = (EditText) findViewById(R.id.edt_purchase_price);

        //这个很重要，先移开TextWatcher的监听器
        if (edt_purchase_price.getTag() instanceof TextWatcher) {
            edt_purchase_price.removeTextChangedListener((TextWatcher) edt_purchase_price.getTag());
        }
        edt_purchase_price.setText(String.valueOf(mPurchaseData.getPurchaseBillLine().getPrice()));//采购数变化监听
        stockInRecord.setPrice(mPurchaseData.getPurchaseBillLine().getPrice());
        TextWatcher watcher1 = new TextWatcher() {
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
                if (!TextUtils.isEmpty(s)) {
                    String value = temp.toString();
                    if (value.equals(".")) {
                        stockInRecord.setPrice(BigDecimal.ZERO);
                    } else {
                        stockInRecord.setPrice(new BigDecimal(String.valueOf(edt_purchase_price.getText())));
                    }
                } else {
                    //edt_purchase_price.setText(String.valueOf(BigDecimal.ZERO));
                    stockInRecord.setPrice(BigDecimal.ZERO);
                    Toast.makeText(CheckDetailActivity.mactivity,"入库单价不能为空",Toast.LENGTH_SHORT).show();
                }
                stockinPrice();
            }
        };
        edt_purchase_price.addTextChangedListener(watcher1);
        edt_purchase_price.setTag(watcher1);

        //入库金额
        edt_stock_in_price = (EditText) findViewById(R.id.edt_stock_in_price);
        //这个很重要，先移开TextWatcher的监听器
        if (edt_stock_in_price.getTag() instanceof TextWatcher) {
            edt_stock_in_price.removeTextChangedListener((TextWatcher) edt_stock_in_price.getTag());
        }
        edt_stock_in_price.setText(String.valueOf(mPurchaseData.getPurchaseBillLine().getSubtotal()));//采购数变化监听
        stockInRecordItem.setAmount(mPurchaseData.getPurchaseBillLine().getSubtotal());
        TextWatcher watcher2 = new TextWatcher() {
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
                if (!TextUtils.isEmpty(s)) {
                    if (value.equals(".")) {
                        stockInRecordItem.setAmount(BigDecimal.ZERO);
                    } else {
                        stockInRecordItem.setAmount(new BigDecimal(String.valueOf(edt_stock_in_price.getText())));
                    }
                } else {
                    //edt_stock_in_price.setText(String.valueOf(BigDecimal.ZERO));
                    stockInRecordItem.setAmount(BigDecimal.ZERO);
                    Toast.makeText(CheckDetailActivity.mactivity,"入库金额不能为空",Toast.LENGTH_SHORT).show();
                }
                purchasePrice();
            }
        };
        edt_stock_in_price.addTextChangedListener(watcher2);
        edt_stock_in_price.setTag(watcher2);

        //应入库数
        edt_should_stock_in_num = (EditText) findViewById(R.id.edt_should_stock_in_num);
        //这个很重要，先移开TextWatcher的监听器
        if (edt_should_stock_in_num.getTag() instanceof TextWatcher) {
            edt_should_stock_in_num.removeTextChangedListener((TextWatcher) edt_should_stock_in_num.getTag());
        }
        edt_should_stock_in_num.setText(String.valueOf(mPurchaseData.getPurchaseBillLine().getPurchaseQty()));//采购数变化监听
        stockInRecord.setShouldStockInQty(mPurchaseData.getPurchaseBillLine().getPurchaseQty());
        TextWatcher watcher3 = new TextWatcher() {
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
                if (!TextUtils.isEmpty(s)) {
                    if (value.equals(".")) {
                        stockInRecord.setShouldStockInQty(BigDecimal.ZERO);
                    } else {
                        stockInRecord.setShouldStockInQty(new BigDecimal (String.valueOf(edt_should_stock_in_num.getText())));
                    }
                } else {
                    //edt_should_stock_in_num.setText(String.valueOf(BigDecimal.ZERO));
                    stockInRecord.setShouldStockInQty(BigDecimal.ZERO);
                    Toast.makeText(CheckDetailActivity.mactivity,"应入库数不能为空",Toast.LENGTH_SHORT).show();
                }
                realStockinNum();
                buckleNum();
            }
        };
        edt_should_stock_in_num.addTextChangedListener(watcher3);
        edt_should_stock_in_num.setTag(watcher3);

        //扣重
        edt_buckle_num = (EditText) findViewById(R.id.edt_buckle_num);
        //这个很重要，先移开TextWatcher的监听器
        if (edt_buckle_num.getTag() instanceof TextWatcher) {
            edt_should_stock_in_num.removeTextChangedListener((TextWatcher) edt_buckle_num.getTag());
        }
        edt_buckle_num.setText(String.valueOf(BigDecimal.ZERO));//变化监听
        stockInRecord.setDeductQty(BigDecimal.ZERO);
        TextWatcher watcher4 = new TextWatcher() {
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
                if (!TextUtils.isEmpty(s)) {
                    if (value.equals(".")) {
                        stockInRecord.setDeductQty(BigDecimal.ZERO);
                    } else {
                        stockInRecord.setDeductQty(new BigDecimal (String.valueOf(edt_buckle_num.getText())));
                    }
                } else {
                    //edt_buckle_num.setText(String.valueOf(BigDecimal.ZERO));
                    stockInRecord.setDeductQty(BigDecimal.ZERO);
                    Toast.makeText(CheckDetailActivity.mactivity,"扣重数不能为空",Toast.LENGTH_SHORT).show();
                }
                realStockinNum();
            }
        };
        edt_buckle_num.addTextChangedListener(watcher4);
        edt_buckle_num.setTag(watcher4);

        //实际入库数
        edt_real_stock_in_num1 = (EditText) findViewById(R.id.edt_real_stock_in_num1);
        //这个很重要，先移开TextWatcher的监听器
        if (edt_real_stock_in_num1.getTag() instanceof TextWatcher) {
            edt_real_stock_in_num1.removeTextChangedListener((TextWatcher) edt_real_stock_in_num1.getTag());
        }
        edt_real_stock_in_num1.setText(String.valueOf(mPurchaseData.getPurchaseBillLine().getPurchaseQty()));//变化监听
        stockInRecord.setQuantity(mPurchaseData.getPurchaseBillLine().getPurchaseQty());
        TextWatcher watcher5 = new TextWatcher() {
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
                if (!TextUtils.isEmpty(s)) {
                    if (value.equals(".")) {
                        stockInRecord.setQuantity(BigDecimal.ZERO);
                    } else {
                        stockInRecord.setQuantity(new BigDecimal(String.valueOf(edt_real_stock_in_num1.getText())));
                    }
                } else {
                    //edt_real_stock_in_num1.setText(String.valueOf(BigDecimal.ZERO));
                    stockInRecord.setQuantity(BigDecimal.ZERO);
                    Toast.makeText(CheckDetailActivity.mactivity,"实际入库数不能为空",Toast.LENGTH_SHORT).show();
                }
                buckleNum();
                purchasePrice();
                //sdStockIn();
            }
        };
        edt_real_stock_in_num1.addTextChangedListener(watcher5);
        edt_real_stock_in_num1.setTag(watcher5);

        //标准单位
        /*tv_sd_unit = (TextView) findViewById(R.id.tv_sd_unit);
        tv_sd_unit.setText(mPurchaseData.getSdUnit() == null ?"":mPurchaseData.getSdUnit().getName());*/

        edt_real_stock_in_num2 = (EditText) findViewById(R.id.edt_real_stock_in_num2);
        edt_real_stock_in_num2.setVisibility(View.GONE);
        /*//标准单位入库数
        edt_real_stock_in_num2 = (EditText) findViewById(R.id.edt_real_stock_in_num2);
        //这个很重要，先移开TextWatcher的监听器
        if (edt_real_stock_in_num2.getTag() instanceof TextWatcher) {
            edt_real_stock_in_num2.removeTextChangedListener((TextWatcher) edt_real_stock_in_num2.getTag());
        }
        DecimalFormat df = new DecimalFormat("#.000000");
        BigDecimal f = mPurchaseData.getPurchaseBillLine().getPurchaseQty().multiply(mPurchaseData.getCoefficient());
        edt_real_stock_in_num2.setText(df.format(f));//变化监听
        stockInRecord.setSdStockInQty(f);
        TextWatcher watcher6 = new TextWatcher() {
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
                if (!TextUtils.isEmpty(s)) {
                    if (value.equals(".")) {
                        stockInRecord.setSdStockInQty(BigDecimal.ZERO);
                    } else {
                        stockInRecord.setSdStockInQty(new BigDecimal (String.valueOf(edt_real_stock_in_num2.getText())));
                    }
                } else {
                    //edt_real_stock_in_num2.setText(String.valueOf(BigDecimal.ZERO));
                    stockInRecord.setSdStockInQty(BigDecimal.ZERO);
                    Toast.makeText(CheckDetailActivity.mactivity,"标准单位入库数不能为空",Toast.LENGTH_SHORT).show();
                }
                sd();
            }
        };
        edt_real_stock_in_num2.addTextChangedListener(watcher6);
        edt_real_stock_in_num2.setTag(watcher6);*/

        edt_check_remark = (EditText) findViewById(R.id.edt_check_remark);
        //edt_cdr = (EditText) findViewById(R.id.edt_cdr);
        //edt_batch_number = (EditText) findViewById(R.id.edt_batch_number);
    }

    //扣重数变化
    public void buckleNum() {
        BigDecimal shouldStockinNum = stockInRecord.getShouldStockInQty();
        //BigDecimal shouldStockinNum = new BigDecimal(String.valueOf(edt_should_stock_in_num.getText()));
        if (shouldStockinNum.compareTo(BigDecimal.ZERO) > 0) {//应入库数不为0
            BigDecimal realStockinNum = stockInRecord.getQuantity();
            //BigDecimal realStockinNum = new BigDecimal (String.valueOf(edt_real_stock_in_num1.getText()));
            if (realStockinNum.compareTo(shouldStockinNum) > 0) {//实际入库数小于应入库数
                showCustomToast("实际入库数不能大于应入库数");
                //if (shouldStockinNum.compareTo(new BigDecimal(String.valueOf(edt_real_stock_in_num1.getText()))) != 0) {
                    edt_real_stock_in_num1.setText(String.valueOf(shouldStockinNum));
                //}
            } else {
                BigDecimal newBuckleNum = shouldStockinNum.subtract(realStockinNum);

                if (newBuckleNum.compareTo(stockInRecord.getDeductQty()) != 0) {
                    edt_buckle_num.setText(String.valueOf(newBuckleNum));//扣重数变化
                }
            }
        } else {
            BigDecimal realStockinNum = stockInRecord.getQuantity();
            //BigDecimal realStockinNum = new BigDecimal (String.valueOf(edt_real_stock_in_num1.getText()));
            if (realStockinNum.compareTo(shouldStockinNum) > 0) {//实际入库数小于应入库数
                showCustomToast("实际入库数不能大于应入库数");
                if (shouldStockinNum.compareTo(stockInRecord.getQuantity()) != 0) {
                    edt_real_stock_in_num1.setText(String.valueOf(shouldStockinNum));
                }
            }
            BigDecimal newBuckleNum = BigDecimal.ZERO;
            BigDecimal newRealStockinNum = BigDecimal.ZERO;

            if (newBuckleNum.compareTo(stockInRecord.getDeductQty()) != 0) {
                edt_buckle_num.setText(String.valueOf(BigDecimal.ZERO));
                stockInRecord.setDeductQty(BigDecimal.ZERO);
            }
            if (newRealStockinNum.compareTo(stockInRecord.getQuantity()) != 0) {
                edt_real_stock_in_num1.setText(String.valueOf(BigDecimal.ZERO));
                stockInRecord.setQuantity(BigDecimal.ZERO);
            }
        }
    }

    //实际入库数变化
    public void realStockinNum() {
        BigDecimal shouldStockinNum = stockInRecord.getShouldStockInQty();
        //BigDecimal shouldStockinNum = new BigDecimal(String.valueOf(edt_should_stock_in_num.getText()));
        if (shouldStockinNum.compareTo(BigDecimal.ZERO) > 0) {//应入库数不为0
            BigDecimal buckleNum = stockInRecord.getDeductQty();
            //BigDecimal buckleNum = new BigDecimal(String.valueOf(edt_buckle_num.getText()));
            if (buckleNum.compareTo(shouldStockinNum) > 0) {//实际入库数小于应入库数
                showCustomToast("扣重数不能大于应入库数");
                if (shouldStockinNum.compareTo(stockInRecord.getDeductQty()) != 0) {
                    edt_buckle_num.setText(String.valueOf(BigDecimal.ZERO));
                }
            } else {
                BigDecimal newRealStockinNum = shouldStockinNum.subtract(buckleNum);
                if (newRealStockinNum.compareTo(stockInRecord.getQuantity()) != 0) {
                    edt_real_stock_in_num1.setText(String.valueOf(newRealStockinNum));
                }
            }
        } else {
            BigDecimal newBuckleNum = BigDecimal.ZERO;
            BigDecimal newRealStockinNum = BigDecimal.ZERO;

            if (newBuckleNum.compareTo(stockInRecord.getDeductQty()) != 0) {
                edt_buckle_num.setText(String.valueOf(BigDecimal.ZERO));
                stockInRecord.setDeductQty(BigDecimal.ZERO);
            }
            if (newRealStockinNum.compareTo(stockInRecord.getQuantity()) != 0) {
                edt_real_stock_in_num1.setText(String.valueOf(BigDecimal.ZERO));
                stockInRecord.setQuantity(BigDecimal.ZERO);
            }
        }
    }

    //入库单价变化
    public void purchasePrice() {
        BigDecimal realStockinNum = stockInRecord.getQuantity();
        //BigDecimal realStockinNum = new BigDecimal(String.valueOf(edt_real_stock_in_num1.getText()));
        if (realStockinNum.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal newStockinPrice = stockInRecordItem.getAmount();
            //BigDecimal newStockinPrice = new BigDecimal(String.valueOf(edt_stock_in_price.getText()));
            BigDecimal newPurchasePrice = newStockinPrice.divide(realStockinNum,6,BigDecimal.ROUND_HALF_UP);
            if (newPurchasePrice.compareTo(stockInRecord.getPrice()) != 0) {
                edt_purchase_price.setText(String.valueOf(newPurchasePrice));
            }
        } else {
            BigDecimal newStockinPrice = BigDecimal.ZERO;
            BigDecimal newPurchasePrice = BigDecimal.ZERO;

            if (newStockinPrice.compareTo(stockInRecordItem.getAmount()) != 0) {
                edt_stock_in_price.setText(String.valueOf(BigDecimal.ZERO));
                stockInRecordItem.setAmount(BigDecimal.ZERO);
            }
            if (newPurchasePrice.compareTo(stockInRecord.getPrice()) != 0) {
                edt_purchase_price.setText(String.valueOf(BigDecimal.ZERO));
                stockInRecord.setPrice(BigDecimal.ZERO);
            }
        }
    }

    //入库金额变化
    public void stockinPrice() {
        BigDecimal newRealStockinNum = stockInRecord.getQuantity();
        //BigDecimal newRealStockinNum = new BigDecimal(String.valueOf(edt_real_stock_in_num1.getText()));

        if (newRealStockinNum.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal newPurchasePrice = stockInRecord.getPrice();
            //BigDecimal newPurchasePrice = new BigDecimal(String.valueOf(edt_purchase_price.getText()));
            DecimalFormat df = new DecimalFormat("#.000000");
            BigDecimal newStockinPrice = newPurchasePrice.multiply(newRealStockinNum);

            if (newStockinPrice.compareTo(stockInRecordItem.getAmount()) != 0) {
                edt_stock_in_price.setText(df.format(newStockinPrice));
            }
        } else {
            BigDecimal newStockinPrice = BigDecimal.ZERO;
            //BigDecimal newPurchasePrice = BigDecimal.ZERO;

            if (newStockinPrice.compareTo(stockInRecordItem.getAmount()) != 0) {
                edt_stock_in_price.setText(String.valueOf(BigDecimal.ZERO));
                stockInRecordItem.setAmount(BigDecimal.ZERO);
            }
            /*if (newPurchasePrice.compareTo(stockInRecord.getPrice()) != 0) {
                edt_purchase_price.setText(String.valueOf(BigDecimal.ZERO));
                stockInRecord.setPrice(BigDecimal.ZERO);
            }*/
        }

    }

    //标准单位系数
    public void sd() {
        BigDecimal newsdStockinNum = stockInRecord.getSdStockInQty();
        //BigDecimal newsdStockinNum = new BigDecimal(String.valueOf(edt_real_stock_in_num2.getText()));
        BigDecimal newRealStockinNum = stockInRecord.getQuantity();
        //BigDecimal newRealStockinNum = new BigDecimal(Double.valueOf(String.valueOf(edt_real_stock_in_num1.getText())));
        if (newsdStockinNum.compareTo(BigDecimal.ZERO) > 0) {
            mPurchaseData.setCoefficient(newsdStockinNum.divide(newRealStockinNum,6,BigDecimal.ROUND_HALF_UP));
        }
    }

    //标准入库数
    public void sdStockIn() {
        BigDecimal newRealStockinNum = stockInRecord.getQuantity();
        //BigDecimal newRealStockinNum = new BigDecimal(Double.valueOf(String.valueOf(edt_real_stock_in_num1.getText())));
        DecimalFormat df = new DecimalFormat("#.000000");
        BigDecimal newsdStockIn = newRealStockinNum.multiply(mPurchaseData.getCoefficient());
        if (newsdStockIn.compareTo(stockInRecord.getSdStockInQty()) != 0) {
            edt_real_stock_in_num2.setText(df.format(newsdStockIn));
        }
    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);

        //tv_produce_time = (TextView) findViewById(R.id.tv_produce_time);
        tv_produce_time.setText(DateUtils.getSomeDate(0)); //生产日期
        //tv_effect_time = (TextView) findViewById(R.id.tv_effect_time);
        tv_effect_time.setText(DateUtils.getSomeDate(0)); //有效日期
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        btn_over = (Button) findViewById(R.id.btn_over);
        btn_stock_in = (Button) findViewById(R.id.btn_stock_in);

        //时间选择器设置
        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);

        mactivity = this;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG:
                return new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay);
        }
        return null;
    }

    /**
     * 设置日期 利用StringBuffer追加
     */
    public void display() {
        if (WHICH == 0) {//为0时，是刚进入界面时，两处都显示今天的日期
            tv_produce_time.setText(new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
            tv_effect_time.setText(new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
        } else {
            if (WHICH == 1) {
                tv_produce_time.setText(new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
            } else {
                tv_effect_time.setText(new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
            }
        }
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            display();
        }
    };

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        tv_produce_time.setOnClickListener(this);
        tv_effect_time.setOnClickListener(this);
        iv_photo.setOnClickListener(this);
        btn_over.setOnClickListener(this);
        btn_stock_in.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_personal:
                finish();
                break;
            /*case R.id.tv_produce_time:
                WHICH = 1;
                showDialog(DATE_DIALOG);
                break;*/
            /*case R.id.tv_effect_time:
                WHICH = 2;
                showDialog(DATE_DIALOG);
                break;*/
            case R.id.iv_photo:
                break;
            case R.id.btn_over:
                stock = 1;
                check();
                break;
            case R.id.btn_stock_in:
                stock = 0;
                check();
                break;
            default:
                break;
        }
    }

    //获取ucode
    private void getUcode() {
        Intent intent = getIntent();
        mPurchaseData = (PurchaseData) intent.getSerializableExtra("PurchaseData");
    }

    //检查信息
    private void check() {
        int count = 0;
        //扣重
        //应入库数
        BigDecimal shouldStockinNum = new BigDecimal(String.valueOf(edt_should_stock_in_num.getText()));
        //实际入库数
        BigDecimal realStockinNum = new BigDecimal(String.valueOf(edt_real_stock_in_num1.getText()));
        //入库单价
        BigDecimal purchasePrice = new BigDecimal(String.valueOf(edt_purchase_price.getText()));
        //入库金额
        BigDecimal stockinPrice = new BigDecimal(String.valueOf(edt_stock_in_price.getText()));
        if (shouldStockinNum.compareTo(BigDecimal.ZERO) >0) {

        } else {
            showShortToast("应入库数不能为0");
            count ++;
        }
        if (realStockinNum.compareTo(BigDecimal.ZERO) > 0) {

        } else {
            showShortToast("实际入库数不能为0");
            count ++;
        }
        if (purchasePrice.compareTo(BigDecimal.ZERO) > 0) {

        } else {
            showShortToast("入库单价不能为0");
            count ++;
        }
        if (stockinPrice.compareTo(BigDecimal.ZERO) > 0) {

        } else {
            showShortToast("入库金额不能为0");
            count ++;
        }
        if (count == 0) {
            if (stock == 0) {
                getStockIn();
            } else {
                getCrossDocking();
            }
        }
    }

    //入库
    private void getStockIn() {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();

        /*//org
        IdName org = SessionUtils.getInstance(getApplicationContext()).getOrg() == null?null:SessionUtils.getInstance(getApplicationContext()).getOrg();

        //StockInRecord stockInRecord = new StockInRecord();
        if (org == null) {//org
            IdName newOrg = new IdName();
            newOrg.setId(userUuid);
            newOrg.setName(userName);
            stockInRecord.setOrg(newOrg);
        } else {
            stockInRecord.setOrg(org);
        }*/
        IdName org = SessionUtils.getInstance(getApplicationContext()).getOrg();
        stockInRecord.setOrg(org);
        stockInRecord.setSupplier(mPurchaseData.getSupplier());//supplier
        stockInRecord.setStockInType(StockInType.purchaseIn);//stockInType
        stockInRecord.setWarehouse(mPurchaseData.getWarehouse());//warehouse
        stockInRecord.setGoods(mPurchaseData.getPurchaseBillLine().getGoods());//goods
        stockInRecord.setGoodsSpec(mPurchaseData.getPurchaseBillLine().getGoodsSpec());//goodsSpec
        stockInRecord.setGoodsUnit(mPurchaseData.getPurchaseBillLine().getGoodsUnit());//goodsUnit
        stockInRecord.setSourceBillNumber(mPurchaseData.getPurchaseBillNumber());//sourceBillNumber
        stockInRecord.setSourceBillLineUuid(mPurchaseData.getPurchaseBillLine().getUuid());//sourceBillLineUuid
        //stockInRecord.setShouldStockInQty(new BigDecimal(String.valueOf(edt_should_stock_in_num.getText())));//shouldStockInQty
        //stockInRecord.setDeductQty(new BigDecimal(String.valueOf(edt_buckle_num.getText())));//deductQty
        //stockInRecord.setQuantity(new BigDecimal(String.valueOf(edt_real_stock_in_num1.getText())));//quantity
        //stockInRecord.setPrice(new BigDecimal(String.valueOf(edt_purchase_price.getText())));//price
        stockInRecord.setCoefficient(mPurchaseData.getCoefficient());//coefficient
        stockInRecord.setInDate(new Date());//stockIntime
        stockInRecord.setDeliveryTime(mPurchaseData.getDeliveryTime());//deliverytime
        stockInRecord.setRemark(String.valueOf(edt_check_remark.getText()));//remark
        stockInRecord.setOrigin(mPurchaseData.getPurchaseBillLine().getOrigin());//origin
        stockInRecord.setBatchNumber(String.valueOf(edt_batch_number.getText()));//batchNumber
        stockInRecord.setListingCertificateNo(String.valueOf(edt_cdr.getText()));//certificateNo
        stockInRecord.setProduceDate(DateUtil.convertToDate(String.valueOf(tv_produce_time.getText())));//produceDate
        stockInRecord.setEffectiveDate(DateUtil.convertToDate(String.valueOf(tv_effect_time.getText())));//effectiveDate
        //stockInRecord.setMaterials();//materials，加工入库
        stockInRecord.setImages(mImage);//image
        //stockInRecord.setSdStockInQty(new BigDecimal(String.valueOf(edt_real_stock_in_num2.getText())));//sdStockInQty
        stockInRecord.setSdUnit(mPurchaseData.getSdUnit());//sdUnit

        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            try {
                body.put(DataConstant.STOCKINRECORD, GsonUtil.getGson().toJson(stockInRecord));
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
        OkHttpUtils.postString().url(UrlConstants.URL_STOCK_IN)
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
                            showLogDebug("main", s);
                            finish();
                        } else {
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }

    //越库
    private void getCrossDocking() {
        String userUuid = SessionUtils.getInstance(getApplicationContext()).getCustomerUUID();
        String userName = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        String sessionId = SessionUtils.getInstance(getApplicationContext()).getSessionId();

        //org
        IdName org = SessionUtils.getInstance(getApplicationContext()).getOrg() == null?null:SessionUtils.getInstance(getApplicationContext()).getOrg();

        //StockInRecord stockInRecord = new StockInRecord();
        if (org == null) {//org
            IdName newOrg = new IdName();
            newOrg.setId(userUuid);
            newOrg.setName(userName);
            stockInRecord.setOrg(newOrg);
        } else {
            stockInRecord.setOrg(org);
        }
        stockInRecord.setSupplier(mPurchaseData.getSupplier());//supplier
        stockInRecord.setStockInType(StockInType.purchaseIn);//stockInType
        stockInRecord.setWarehouse(mPurchaseData.getWarehouse());//warehouse
        stockInRecord.setGoods(mPurchaseData.getPurchaseBillLine().getGoods());//goods
        stockInRecord.setGoodsSpec(mPurchaseData.getPurchaseBillLine().getGoodsSpec());//goodsSpec
        stockInRecord.setGoodsUnit(mPurchaseData.getPurchaseBillLine().getGoodsUnit());//goodsUnit
        stockInRecord.setSourceBillNumber(mPurchaseData.getPurchaseBillNumber());//sourceBillNumber
        stockInRecord.setSourceBillLineUuid(mPurchaseData.getPurchaseBillLine().getUuid());//sourceBillLineUuid
        stockInRecord.setShouldStockInQty(new BigDecimal(String.valueOf(edt_should_stock_in_num.getText())));//shouleStockInQty
        stockInRecord.setDeductQty(new BigDecimal(String.valueOf(edt_buckle_num.getText())));//deductQty
        stockInRecord.setQuantity(new BigDecimal(String.valueOf(edt_real_stock_in_num1.getText())));//quantity
        stockInRecord.setPrice(new BigDecimal(String.valueOf(edt_purchase_price.getText())));//price
        stockInRecord.setCoefficient(mPurchaseData.getCoefficient());//coefficient
        stockInRecord.setInDate(new Date());//stockIntime
        stockInRecord.setDeliveryTime(mPurchaseData.getDeliveryTime());//deliveryTime
        stockInRecord.setRemark(String.valueOf(edt_check_remark.getText()));//remark
        stockInRecord.setOrigin(mPurchaseData.getPurchaseBillLine().getOrigin());//origin
        stockInRecord.setBatchNumber(String.valueOf(edt_batch_number.getText()));//batchNumber
        stockInRecord.setListingCertificateNo(String.valueOf(edt_cdr.getText()));//certificateNo
        stockInRecord.setProduceDate(DateUtil.convertToDate(String.valueOf(tv_produce_time.getText())));//produceDate
        stockInRecord.setEffectiveDate(DateUtil.convertToDate(String.valueOf(tv_effect_time.getText())));//effectiveDate
        //stockInRecord.setMaterials();//materials，加工入库
        stockInRecord.setImages(mImage);//image
        stockInRecord.setSdStockInQty(new BigDecimal(String.valueOf(edt_real_stock_in_num2.getText())));//sdStockInQty
        stockInRecord.setSdUnit(mPurchaseData.getSdUnit());//sdUnit

        JSONObject reparams = new JSONObject();
        try {
            JSONObject body = new JSONObject();
            try {
                body.put(DataConstant.STOCKINRECORD,GsonUtil.getGson().toJson(stockInRecord));
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
        OkHttpUtils.postString().url(UrlConstants.URL_CROSS_DOCKING)
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
                            showLogDebug("main", s);
                            finish();
                        } else {
                            showCustomToast(response.getMessage());
                        }
                    }
                });
    }
}
