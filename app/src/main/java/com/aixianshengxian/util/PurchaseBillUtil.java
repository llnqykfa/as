package com.aixianshengxian.util;


import com.aixianshengxian.Myapplication;
import com.aixianshengxian.module.ForecastProcessPlanItem;
import com.aixianshengxian.module.GoodsItem;
import com.aixianshengxian.module.StockOutRecordConsume;
import com.aixianshengxian.module.StockOutRecordItem;
import com.xmzynt.storm.basic.ucn.UCN;
import com.xmzynt.storm.service.goods.Goods;
import com.xmzynt.storm.service.process.ForecastProcessPlan;
import com.xmzynt.storm.service.purchase.bill.LineStatus;
import com.xmzynt.storm.service.purchase.bill.PurchaseBillLine;
import com.xmzynt.storm.service.purchase.bill.PurchaseBillStatus;
import com.xmzynt.storm.service.purchase.plan.ForecastPurchase;
import com.xmzynt.storm.service.purchase.plan.PurchaseStatus;
import com.xmzynt.storm.service.wms.stockout.StockOutRecord;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PurchaseBillUtil {

    public static String[] getPurchaseBillStatusList(){
        PurchaseBillStatus[] allBillStatus = PurchaseBillStatus.values();
        ArrayList<String> datas = new ArrayList<String>();
        for (PurchaseBillStatus billStatus : allBillStatus) {
            datas.add(billStatus.getCaption());
        }
        return (String[])datas.toArray(new String[datas.size()]);
    }

    public static List<PurchaseBillLine> toPurchaseBillLine( Map<String,GoodsItem> goodsItemMap){
        List<PurchaseBillLine> purchaseBillLineList = new ArrayList<PurchaseBillLine>();
        if(goodsItemMap == null || goodsItemMap.isEmpty()){
            return purchaseBillLineList;
        }
        for (Map.Entry<String, GoodsItem> entry : goodsItemMap.entrySet()) {
            GoodsItem goodsItem = (GoodsItem)entry.getValue();
            purchaseBillLineList.add(toPurchaseBillLine(goodsItem));
        }
        return purchaseBillLineList;
    }

    public static PurchaseBillLine  toPurchaseBillLine(GoodsItem goodsItem){
            PurchaseBillLine purchaseBillLine = new PurchaseBillLine();
            Goods item = goodsItem.getGoods();
            UCN goods = new UCN();
            goods.setUuid(item.getUuid());
            goods.setName(item.getName());
            goods.setCode(item.getCode());
            purchaseBillLine.setGoods(goods);
            purchaseBillLine.setGoodsSpec(item.getSpec());
            purchaseBillLine.setGoodsUnit(item.getSdUnit());
            purchaseBillLine.setPrice(BigDecimal.ZERO);//单价
            purchaseBillLine.setSubtotal(BigDecimal.ZERO);//小计
            purchaseBillLine.setRemark("");//备注
            purchaseBillLine.setStatus(LineStatus.notStockIn);//状态
            purchaseBillLine.setSourcePlanUuid(goodsItem.getUuid());
            return purchaseBillLine;
    }

    public static List<ForecastPurchase> toForecastPurchase( Map<String,GoodsItem> goodsItemMap){
        List<ForecastPurchase> forecastPurchases = new ArrayList<ForecastPurchase>();
        if(goodsItemMap == null || goodsItemMap.isEmpty()){
            return forecastPurchases;
        }
        for (Map.Entry<String, GoodsItem> entry : goodsItemMap.entrySet()) {
            ForecastPurchase forecastPurchase = new ForecastPurchase();
            GoodsItem goodsItem = (GoodsItem)entry.getValue();
            Goods item = goodsItem.getGoods();
            UCN goods = new UCN();
            goods.setUuid(item.getUuid());
            goods.setName(item.getName());
            goods.setCode(item.getCode());
            forecastPurchase.setGoods(goods);
            forecastPurchase.setOrg(SessionUtils.getInstance(Myapplication.getsInstance().getApplicationContext()).getOrg());
            forecastPurchase.setGoodsSpec(item.getSpec());
            forecastPurchase.setGoodsUnit(item.getSdUnit());
            forecastPurchase.setStatus(PurchaseStatus.notPurchase);//状态
            forecastPurchases.add(forecastPurchase);
        }
        return forecastPurchases;
    }

    public  static List<PurchaseBillLine> toPurchaseBillLineList(List<ForecastPurchase> mForecastPurchase)  {
        List<PurchaseBillLine> purchaseBillLineList = new ArrayList<PurchaseBillLine>();
        for (int i = 0;i < mForecastPurchase.size();i ++) {
            ForecastPurchase forecastPurchase = mForecastPurchase.get(i);
            PurchaseBillLine purchaseBillLine = new PurchaseBillLine();
            purchaseBillLine.setGoods(forecastPurchase.getGoods());
            purchaseBillLine.setGoodsSpec(forecastPurchase.getGoodsSpec());
            purchaseBillLine.setGoodsUnit(forecastPurchase.getGoodsUnit());
            purchaseBillLine.setPurchaseQty(forecastPurchase.getPurchaseQty());
            purchaseBillLine.setPrice(BigDecimal.ZERO);//单价
            purchaseBillLine.setSubtotal(BigDecimal.ZERO);//小计
            purchaseBillLine.setRemark("");//备注
            purchaseBillLine.setStatus(LineStatus.notStockIn);//状态
            purchaseBillLine.setOrigin("");
            purchaseBillLine.setSourcePlanUuid(forecastPurchase.getUuid());
            purchaseBillLineList.add(purchaseBillLine);
        }
        return purchaseBillLineList;
    }

    public static List<ForecastProcessPlan> toForecastProcessPlanList(Map<String,GoodsItem> goodsItemMap){
        List<ForecastProcessPlan> forecastProcessPlans = new ArrayList<ForecastProcessPlan>();
        if(goodsItemMap == null || goodsItemMap.isEmpty()){
            return forecastProcessPlans;
        }
        for (Map.Entry<String, GoodsItem> entry : goodsItemMap.entrySet()) {
            ForecastProcessPlan forecastProcessPlan = new ForecastProcessPlan();
            GoodsItem goodsItem = (GoodsItem)entry.getValue();
            Goods item = goodsItem.getGoods();
            UCN goods = new UCN();
            goods.setUuid(item.getUuid());
            goods.setName(item.getName());
            goods.setCode(item.getCode());
            forecastProcessPlan.setGoods(goods);
            forecastProcessPlan.setOrg(SessionUtils.getInstance(Myapplication.getsInstance().getApplicationContext()).getOrg());
            forecastProcessPlan.setGoodsSpec(item.getSpec());
            forecastProcessPlan.setGoodsUnit(item.getSdUnit());
            forecastProcessPlan.setPlanQty(BigDecimal.ZERO);
            forecastProcessPlan.setCompleteQty(BigDecimal.ZERO);
            forecastProcessPlans.add(forecastProcessPlan);
        }
        return forecastProcessPlans;
    }

    public static List<ForecastProcessPlanItem> toForecastProcessPlanItemList(Map<String,GoodsItem> goodsItemMap){
        List<ForecastProcessPlanItem> forecastProcessPlanItems = new ArrayList<ForecastProcessPlanItem>();
        if(goodsItemMap == null || goodsItemMap.isEmpty()){
            return forecastProcessPlanItems;
        }
        for (Map.Entry<String, GoodsItem> entry : goodsItemMap.entrySet()) {
            ForecastProcessPlanItem forecastProcessPlanItem = new ForecastProcessPlanItem();
            ForecastProcessPlan forecastProcessPlan = new ForecastProcessPlan();
            GoodsItem goodsItem = (GoodsItem)entry.getValue();
            Goods item = goodsItem.getGoods();
            UCN goods = new UCN();
            goods.setUuid(item.getUuid());
            goods.setName(item.getName());
            goods.setCode(item.getCode());
            forecastProcessPlan.setGoods(goods);
            forecastProcessPlan.setOrg(SessionUtils.getInstance(Myapplication.getsInstance().getApplicationContext()).getOrg());
            forecastProcessPlan.setGoodsSpec(item.getSpec());
            forecastProcessPlanItem.setForecastProcessPlan(forecastProcessPlan);
            forecastProcessPlanItem.getForecastProcessPlan().setGoodsUnit(item.getSdUnit());
            forecastProcessPlanItem.getForecastProcessPlan().setPlanQty(BigDecimal.ZERO);
            forecastProcessPlanItem.getForecastProcessPlan().setCompleteQty(BigDecimal.ZERO);
            forecastProcessPlanItem.setStockInNum(BigDecimal.ZERO);
            forecastProcessPlanItem.setPrice(BigDecimal.ZERO);
            forecastProcessPlanItem.setPrice(BigDecimal.ZERO);
            forecastProcessPlanItem.setDay(0);
            forecastProcessPlanItems.add(forecastProcessPlanItem);
        }
        return forecastProcessPlanItems;
    }

    public static List<StockOutRecordConsume> toStockOutRecordItemList(Map<String,StockOutRecordItem> stockOutRecordItemMap){
        List<StockOutRecordConsume> stockOutRecordConsumes = new ArrayList<StockOutRecordConsume>();
        if(stockOutRecordItemMap == null || stockOutRecordItemMap.isEmpty()){
            return stockOutRecordConsumes;
        }
        for (Map.Entry<String, StockOutRecordItem> entry : stockOutRecordItemMap.entrySet()) {
            StockOutRecordConsume stockOutRecordConsume = new StockOutRecordConsume();
            //StockOutRecord stockOutRecord = new StockOutRecord();
            StockOutRecordItem stockOutRecordItem = (StockOutRecordItem)entry.getValue();
            StockOutRecord item =stockOutRecordItem.getStockOutRecord();
            stockOutRecordConsume.setStockOutRecord(item);
            stockOutRecordConsume.setConsume(BigDecimal.ZERO);
            stockOutRecordConsumes.add(stockOutRecordConsume);
        }
        return stockOutRecordConsumes;
    }

    public static Map<String, Object> getMap(String jsonString) {
        JSONObject jsonObject;
        try
        {
            jsonObject = new JSONObject(jsonString);
            Iterator<String> keyIter = jsonObject.keys();
            String key;
            Object value;
            Map<String, Object> valueMap = new HashMap<String, Object>();
            while (keyIter.hasNext())
            {
                key = (String) keyIter.next();
                value = jsonObject.get(key);
                valueMap.put(key, value);
            }
            return valueMap;
        }

        catch (JSONException e)

        {

            e.printStackTrace();

        }

        return null;

    }

    public static BigDecimal getGoodsTotalQty(List<PurchaseBillLine> datas){
        BigDecimal qty = new BigDecimal(0);
        for(int i=0;i<datas.size();i++){
            qty= qty.add(datas.get(i).getPurchaseQty());
        }
        return qty;
    }

    public static BigDecimal getTotalAmount(List<PurchaseBillLine> datas){
        BigDecimal totalAmount = new BigDecimal(0);
        for(int i=0;i<datas.size();i++){
            totalAmount.add(datas.get(i).getSubtotal());
        }
        return totalAmount;
    }

}
