package com.aixianshengxian.module;

import com.xmzynt.storm.common.api.base.standardentity.StandardEntity;

import java.util.List;

/**
 * Created by Administrator on 2017/10/25.
 */

public class ProductStockIn extends StandardEntity {
    private String stockInId;
    private String productName;
    private double stockInNum;
    private String unit;
    private double unitPrice;
    private double stayDay;
    private String ucodeMessage;

    public ProductStockIn() {
        super();
    }

    public ProductStockIn(String stockInId,String productName,double stockInNum,String unit,double unitPrice,
                          double stayDay,String ucodeMessage) {
        super();
        this.stockInId = stockInId;
        this.productName = productName;
        this.stockInNum = stockInNum;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.stayDay = stayDay;
        this.ucodeMessage = ucodeMessage;
    }

    public String getStockInId() {
        return  stockInId;
    }
    public void setStockInId(String stockInId) {
        this.stockInId = stockInId;
    }
    public String getProductName() {
        return this.productName;
    }
    public void setProductName(String productName){
        this.productName = productName;
    }
    public double getStockInNum() {
        return stockInNum;
    }
    public void setStockInNum(double stockInNum) {
        this.stockInNum = stockInNum;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public double getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    public double getStayDay() {
        return stayDay;
    }
    public void setStayDay(double stayDay) {
        this.stayDay = stayDay;
    }
    public String getUcodeMessage() {
        return ucodeMessage;
    }
    public void setUcodeMessage(String ucodeMessage) {
        this.ucodeMessage = ucodeMessage;
    }
}
