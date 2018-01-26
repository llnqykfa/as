package com.aixianshengxian.http;


import android.content.Context;
import android.text.TextUtils;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.activity.machine.MachineActivity;
import com.aixianshengxian.activity.machine.SelectReceiveActivity;
import com.aixianshengxian.activity.plan.AddPlanActivity;
import com.aixianshengxian.activity.purchase.AddPurchaseActivity;
import com.aixianshengxian.activity.purchase.PurchaseActivity;
import com.aixianshengxian.activity.purchase.PurchaseDetailActivity;
import com.aixianshengxian.activity.purchase.PurchaseEditActivity;
import com.aixianshengxian.constant.DataConstant;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.listener.GetOperatorResultInterface;
import com.aixianshengxian.listener.UnitInterface;
import com.aixianshengxian.util.JsonUtil;
import com.aixianshengxian.util.ToastUtil;
import com.google.gson.reflect.TypeToken;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.constants.MDataConstants;
import com.xmzynt.storm.basic.constants.SaleConstants;
import com.xmzynt.storm.basic.idname.IdName;
import com.xmzynt.storm.basic.user.UserType;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.service.goods.UnitPrice;
import com.xmzynt.storm.service.process.ForecastProcessPlan;
import com.xmzynt.storm.service.purchase.bill.PurchaseBill;
import com.xmzynt.storm.service.purchase.plan.ForecastPurchase;
import com.xmzynt.storm.service.wms.stockout.StockOutRecord;
import com.xmzynt.storm.util.GsonUtil;
import com.xmzynt.storm.util.query.PageData;
import com.xmzynt.storm.util.query.QueryFilter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpManager {
    private static String prefix = "--";
    private static String end = "\r\n";
    private static final OkHttpClient client = new OkHttpClient();

    private static String getRequestParams(Context context, String key, String bodyParams) {
        JSONObject params = JsonUtil.buildParams(context);
        JSONObject body = new JSONObject();
        try {
            body.put(key, bodyParams);
            params.put(MDataConstants.URL_BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params.toString();
    }

    private static String getRequestParams(Context context, Map<String,Object> paramMap) {
        JSONObject params = JsonUtil.buildParams(context);
        JSONObject body = new JSONObject();
        try {
            for (Map.Entry<String, Object> entry : paramMap.entrySet()){
                String key = entry.getKey();
                Object value = entry.getValue();
                body.put(key, value);
            }
            params.put(MDataConstants.URL_BODY, body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params.toString();
    }

    //根据商品uuids批量查询下拉单位列表
    public static void dropDownUnit(Context context, List<String> mGoodsUuid, final int position, final UnitInterface listener) {
        OkHttpUtils.postString().url(UrlConstants.URL_DROP_DOWN_UNIT_PRICE)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(getRequestParams(context, DataConstant.GOODS_UUID, GsonUtil.getGson().toJson(mGoodsUuid)))
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if (listener != null) {
                            listener.onError();
                        }
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                        if (response.getErrorCode() == 0) {
                            Type listTypeA = new TypeToken<Map<String, List<UnitPrice>>>() {
                            }.getType();
                            if (response.getData() != null) {
                                Map<String, List<UnitPrice>> mMapUnits = GsonUtil.getGson().fromJson(response.getData(), listTypeA);
                                if (listener != null) {
                                    listener.onUnitListener(mMapUnits, position);
                                }
                            }
                        } else {
                            if (listener != null) {
                                listener.onError();
                            }
                        }
                    }
                });
    }

    //查询经办人列表
    public static void getDropdownOperator(Context context,final GetOperatorResultInterface listener){
        final QueryFilter filter = new QueryFilter();
        filter.setPage(0);
        filter.setPageSize(0);
        filter.setDefaultPageSize(0);
        filter.getParams().put(DataConstant.USERTYPR, UserType.employee);
        String body = GsonUtil.getGson().toJson(filter);
        OkHttpUtils.postString().url(UrlConstants.URL_PURCHASE_GET_DROPDOWN_OPERATOR)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(getRequestParams(context, SaleConstants.Field.QUERY_FILTER, body))
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if (listener != null) {
                            listener.onError();
                        }
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                        if (response.getErrorCode() == 0) {
                            Type listTypeA = new TypeToken< List<IdName>>() {
                            }.getType();
                            if (response.getData() != null) {
                                List<IdName> datas = GsonUtil.getGson().fromJson(response.getData(), listTypeA);
                                if (listener != null) {
                                    listener.onGetOperatorSuccess(datas);
                                }
                            }
                        } else {
                            if (listener != null) {
                                listener.onError();
                            }
                        }
                    }
                });
    }

    //编辑保存计划
    public static void savePlan(Context context, List<ForecastPurchase> forecastPurchases, final AddPlanActivity.onAddPlanInterface listener) {
        String body = GsonUtil.getGson().toJson(forecastPurchases);
        OkHttpUtils.postString().url(UrlConstants.URL_PLAN_BATCH_SAVE)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(getRequestParams(context, DataConstant.FORECAST_PURCHASES, body))
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if (listener != null) {
                            listener.onError("网络不给力");
                        }
                    }

                    @Override
                    public void onResponse(String s, int id) {
                        ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                        if (response.getErrorCode() == 0) {
                            if (listener != null) {
                                listener.onAddPlanInterfaceSuccess(response.getData());
                            }
                        } else {
                            if (listener != null) {
                                listener.onError(response.getMessage());
                            }
                        }
                    }
                });
    }

    //新增保存采购订单
    public static void saveNewPurchase(Context context, PurchaseBill purchaseBill, final AddPurchaseActivity.onAddPurchaseBillInterface listener) {
        String body = GsonUtil.getGson().toJson(purchaseBill);
        OkHttpUtils.postString().url(UrlConstants.URL_PURCHASE_SAVE_NEW)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(getRequestParams(context, DataConstant.PURCHASE_MODIFY, body))
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if (listener != null) {
                            listener.onError("网络不给力");
                        }
                    }

                    @Override
                    public void onResponse(String s, int id) {
                        ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                        if (response.getErrorCode() == 0) {
                            if (listener != null) {
                                listener.onAddPurchaseBillSuccess(response.getData());
                            }
                        } else {
                            if (listener != null) {
                                listener.onError(response.getMessage());
                            }
                        }
                    }
                });
    }


    //分页查询采购订单
    public static void getPurchases(Context context, int page, int pageSize, String supplierUuid, String keywordsLike,List<String> purchaseBillStatus, String chooseTime, final PurchaseActivity.onPurchaseListInterface listener) {
        final QueryFilter filter = new QueryFilter();
        filter.setPage(page);
        filter.setPageSize(pageSize);
        filter.setDefaultPageSize(0);
        if (!TextUtils.isEmpty(supplierUuid)) {
            filter.getParams().put("supplierUuid", supplierUuid);
        }
        if (!TextUtils.isEmpty(keywordsLike)) {
            filter.getParams().put("keywordsLike", keywordsLike);
        }
        if (purchaseBillStatus != null) {
            filter.getParams().put("statusIn", purchaseBillStatus);
        }
        if (chooseTime != null) {
            filter.getParams().put("deliveryTime", chooseTime);
        }
        String body = GsonUtil.getGson().toJson(filter);
        OkHttpUtils.postString().url(UrlConstants.URL_PURCHASE_GET)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(getRequestParams(context, SaleConstants.Field.QUERY_FILTER, body))
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if (listener != null) {
                            listener.onError("网络不给力");
                        }
                    }

                    @Override
                    public void onResponse(String s, int id) {
                        ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                        if (response.getErrorCode() == 0) {
                            Type listTypeA = new TypeToken<PageData<PurchaseBill>>() {
                            }.getType();
                            if (response.getData() != null|| !response.getData().equals("[]")) {
                                PageData data = GsonUtil.getGson().fromJson(response.getData(), listTypeA);
                                if (listener != null) {
                                    listener.onPurchaseListSuccess(data);
                                }
                            }
                        } else {
                            if (listener != null) {
                                listener.onError(response.getMessage());
                            }
                        }
                    }
                });
    }

    //获取采购单详情
    public static void getPurchaseDetail(Context context, String uuid, final PurchaseDetailActivity.onGetPurchaseDetailInterface listener) {

        OkHttpUtils.postString().url(UrlConstants.URL_PURCHASE_GET_DETAIL)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(getRequestParams(context, DataConstant.PLAN_UUID, uuid))
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if (listener != null) {
                            listener.onError("网络不给力");
                        }
                    }

                    @Override
                    public void onResponse(String s, int id) {
                        ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                        if (response.getErrorCode() == 0) {
                            Type listTypeA = new TypeToken<PurchaseBill>() {
                            }.getType();
                            if (response.getData() != null) {
                                PurchaseBill data = GsonUtil.getGson().fromJson(response.getData(), listTypeA);
                                if (listener != null) {
                                    listener.onGetPurchaseDetailSuccess(data);
                                }
                            }
                        } else {
                            if (listener != null) {
                                listener.onError(response.getMessage());
                            }
                        }
                    }
                });

    }

    //操作采购单
    public static void operatePurchasesBill(Context context, String uuid, long version, final int tag, final PurchaseDetailActivity.onOperatePurchasesBillInterface listener) {
        String url = "";
        switch (tag) {
            case DataConstant.URL_PURCHASE_AUDIT:
                url = UrlConstants.URL_PURCHASE_AUDIT;
                break;
            case DataConstant.URL_PURCHASE_ANORT:
                url = UrlConstants.URL_PURCHASE_ANORT;
                break;
            case DataConstant.URL_PURCHASE_DELETE:
                url = UrlConstants.URL_PURCHASE_DELETE;
                break;
            case DataConstant.URL_PURCHASE_COMPLETE:
                url = UrlConstants.URL_PURCHASE_COMPLETE;
                break;
            case DataConstant.URL_PURCHASE_CANCEL_AUDIT:
                url = UrlConstants.URL_PURCHASE_CANCEL_AUDIT;
                break;
        }
        Map<String,Object> paramMap = new HashMap<String,Object>() ;
        paramMap.put(DataConstant.PLAN_UUID, uuid);
        if(version != -1){
            paramMap.put( MDataConstants.Field.VERSION, version);
        }
        OkHttpUtils.postString().url(url)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(getRequestParams(context, paramMap))
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if (listener != null) {
                            listener.onError("网络不给力");
                        }
                    }

                    @Override
                    public void onResponse(String s, int id) {
                        ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                        if (response.getErrorCode() == 0) {
                            if (listener != null) {
                                listener.onOperatePurchasesBillSuccess(response.getData(), tag);
                            }
                        } else {
                            if (listener != null) {
                                listener.onError(response.getMessage());
                            }
                        }
                    }
                });
//        if (version == -1) {
//
//        } else {
//            OkHttpUtils.postString().url(url)
//                    .addHeader("Cookie", "PHPSESSID=" + 123456)
//                    .addHeader("X-Requested-With", "XMLHttpRequest")
//                    .addHeader("Content-Type", "application/json;chartset=utf-8")
//                    .content(getRequestParams(context, DataConstant.PLAN_UUID, uuid))
//                    .content(getRequestParams(context, MDataConstants.Field.VERSION, uuid))
//                    .build()
//                    .execute(new StringCallback() {
//
//                        @Override
//                        public void onError(Call call, Exception e, int i) {
//                            if (listener != null) {
//                                listener.onError();
//                            }
//                        }
//
//                        @Override
//                        public void onResponse(String s, int id) {
//                            ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
//                            if (response.getErrorCode() == 0) {
//                                if (listener != null) {
//                                    listener.onOperatePurchasesBillSuccess(response.getData(), tag);
//                                }
//                            } else {
//                                if (listener != null) {
//                                    listener.onError();
//                                }
//                            }
//                        }
//                    });
//        }

    }

    //编辑保存采购订单
    public static void savePurchases(Context context, PurchaseBill purchaseBill, final PurchaseEditActivity.onSavePurchaseInterface listener) {
        String body = GsonUtil.getGson().toJson(purchaseBill);
        OkHttpUtils.postString().url(UrlConstants.URL_PURCHASE_SAVE)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(getRequestParams(context, DataConstant.PURCHASE_MODIFY, body))
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if (listener != null) {
                            listener.onError("网络不给力");
                        }
                    }

                    @Override
                    public void onResponse(String s, int id) {
                        ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                        if (response.getErrorCode() == 0) {
                            if (listener != null) {
                                listener.onSavePurchaseSuccess(response.getData());
                            }
                        } else {
                            if (listener != null) {
                                listener.onError(response.getMessage());
                            }
                        }
                    }
                });
    }

    //根据过滤条件查询加工计划
    public static void getForecastProcessList(Context context, int page, int pageSize,String keywordsLike,String createdTime,final MachineActivity.onGetForecastProcessListInterface listener){
        final QueryFilter filter = new QueryFilter();
        filter.setPage(page);
        filter.setPageSize(pageSize);
        filter.setDefaultPageSize(0);
        if (!TextUtils.isEmpty(keywordsLike)) {
            filter.getParams().put("keywordsLike", keywordsLike);
        }
        if (!TextUtils.isEmpty(createdTime)) {
            filter.getParams().put("createdTime", createdTime);
        }
        String body = GsonUtil.getGson().toJson(filter);
        OkHttpUtils.postString().url(UrlConstants.URL_GET_FORECAST_PROCESS_LIST)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(getRequestParams(context, SaleConstants.Field.QUERY_FILTER, body))
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if (listener != null) {
                            listener.onError("网络不给力");
                        }
                    }

                    @Override
                    public void onResponse(String s, int id) {
                        ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                        if (response.getErrorCode() == 0) {
                            Type listTypeA = new TypeToken<List<ForecastProcessPlan>>() {
                            }.getType();
                            if (response.getData() != null || !response.getData().equals("[]")) {
                                List<ForecastProcessPlan> data = GsonUtil.getGson().fromJson(response.getData(), listTypeA);
                                if (listener != null) {
                                    listener.onGetForecastProcessListSuccess(data);
                                }
                            }
                        } else {
                            if (listener != null) {
                                listener.onError(response.getMessage());
                            }
                        }
                    }
                });

    }
    //
//    //根据uuid查询加工计划
//    public static void getForecastProcessDetail(Context context, String uuid, final StockInActivity.onGetForecastProcessDetailInterface listener) {
//
//        OkHttpUtils.postString().url(UrlConstants.URL_PURCHASE_GET_DETAIL)
//                .addHeader("Cookie", "PHPSESSID=" + 123456)
//                .addHeader("X-Requested-With", "XMLHttpRequest")
//                .addHeader("Content-Type", "application/json;chartset=utf-8")
//                .content(getRequestParams(context, DataConstant.PLAN_UUID, uuid))
//                .build()
//                .execute(new StringCallback() {
//
//                    @Override
//                    public void onError(Call call, Exception e, int i) {
//                        if (listener != null) {
//                            listener.onError();
//                        }
//                    }
//
//                    @Override
//                    public void onResponse(String s, int id) {
//                        ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
//                        if (response.getErrorCode() == 0) {
//                            Type listTypeA = new TypeToken<ForecastProcessPlan>() {
//                            }.getType();
//                            if (response.getData() != null) {
//                                ForecastProcessPlan data = GsonUtil.getGson().fromJson(response.getData(), listTypeA);
//                                if (listener != null) {
//                                    listener.onGetForecastProcessDetailSuccess(data);
//                                }
//                            }
//                        } else {
//                            if (listener != null) {
//                                listener.onError();
//                            }
//                        }
//                    }
//                });
//
//    }
//
    //加工入库获取加工领料
    public static void getStockOutRecordList(Context context,String warehouseUuid,String goodsCodeOrNameLike,final SelectReceiveActivity.onGetStockOutRecordListInterface listener){
        final QueryFilter filter = new QueryFilter();
        if(!TextUtils.isEmpty(warehouseUuid)){
            filter.getParams().put("warehouseUuid", warehouseUuid);
        }
        if(!TextUtils.isEmpty(goodsCodeOrNameLike)){
            filter.getParams().put("goodsCodeOrNameLike", goodsCodeOrNameLike);
        }
        filter.getParams().put("isProcessPick", true);
        String body = GsonUtil.getGson().toJson(filter);
        OkHttpUtils.postString().url(UrlConstants.URL_GET_STOCKOUT_RECORD_LIST)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(getRequestParams(context, SaleConstants.Field.QUERY_FILTER, body))
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if (listener != null) {
                            listener.onError("网络不给力");
                        }
                    }

                    @Override
                    public void onResponse(String s, int id) {
                        ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                        if (response.getErrorCode() == 0) {
                            Type listTypeA = new TypeToken<List<StockOutRecord>>() {
                            }.getType();
                            if (response.getData() != null) {
                                List<StockOutRecord> data = GsonUtil.getGson().fromJson(response.getData(), listTypeA);
                                if (listener != null) {
                                    listener.onGetStockOutRecordListSuccess(data);
                                }
                            }
                        } else {
                            if (listener != null) {
                                listener.onError(response.getMessage());
                            }
                        }
                    }
                });
    }

    //拍照上传处
    public static void upLoadPhoto(/*Thread thread ,*/final Context context,String userUuid,List<String> imagePaths,String imgType) {
        byte[] image = null;
        try {
            for (int i = 0;i < imagePaths.size();i ++) {
                image = readStream(imagePaths.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data;boundary=android"),image);
        MultipartBody.Builder mbody=new MultipartBody.Builder().setType(MultipartBody.FORM);
        List<File> mFile = new ArrayList<>();
        for (int i = 0;i < imagePaths.size();i ++) {
            String imagepath = imagePaths.get(i);
            File file = new File(imagepath);
            String imageType = imagepath.substring(imagepath.length()-3);
            final MediaType MEDIA_TYPE = MediaType.parse("image/" + imageType);
            mbody.addFormDataPart("Content-Disposition: form-data; name=\"file\"; filename=\"",file.getName(),RequestBody.create(MEDIA_TYPE,file));
            mFile.add(file);
        }
        MultipartBody requestBody = mbody.build();

        /*//构建请求
        Request request = new Request.Builder()
                .url(UrlConstants.URL_UPLOAD_PHOTO)//地址
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .addHeader(BasicConstants.Field.UUID, userUuid)
                .addHeader(DataConstant.IMG_TYPE,imgType)
                .post(requestBody)//添加请求体
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("上传失败:e.getLocalizedMessage() = " + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBean s = GsonUtil.getGson().fromJson(String.valueOf(response), ResponseBean.class);
                //System.out.println("上传照片成功：response = " + response.body().string());
            }
        });*/

        OkHttpUtils.postFile().url(UrlConstants.URL_UPLOAD_PHOTO)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .addHeader(BasicConstants.Field.UUID, userUuid)
                .addHeader(DataConstant.IMG_TYPE,imgType)
                //.mediaType(MediaType.parse("multipart/form-data;boundary=android"))
                .tag(requestBody)
                .file(mFile.get(0))
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        ToastUtil.showCustomToast(context,"请求失败");
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        ResponseBean response = GsonUtil.getGson().fromJson(s, ResponseBean.class);
                        if (response.getErrorCode() == 0) {
                            ToastUtil.showCustomToast(context,"请求成功");
                            //showLogDebug("main", s);
                        }  else {
                            ToastUtil.showCustomToast(context,response.getMessage());
                        }
                    }
                });
    }

    public static byte[] readStream(String imagepath) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append(prefix);
        sb.append("Android");
        sb.append(end);
        sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + imagepath + "\"" + end);
        sb.append("Content-Type:image/" + imagepath.substring(imagepath.length()-3) + end);
        sb.append(end);
        FileInputStream fs = new FileInputStream(imagepath);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while (-1 != (len = fs.read(buffer))) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        fs.close();
        StringBuffer sb2 = new StringBuffer();
        sb2.append(end);
        sb2.append(prefix + "Android" + prefix);
        sb2.append(end);
        byte[] outStreamByte = outStream.toByteArray();
        byte[] sbByte = sb.toString().getBytes();
        byte[] sb2Byte = sb2.toString().getBytes();
        byte[] finalByte = new byte[sbByte.length + outStreamByte.length + sb2Byte.length];
        System.arraycopy(sbByte,0,finalByte,0,sbByte.length);
        System.arraycopy(outStreamByte,0,finalByte,sbByte.length,outStreamByte.length);
        System.arraycopy(sb2Byte,0,finalByte,sbByte.length + outStreamByte.length,sb2Byte.length);
        return finalByte;
    }

}
