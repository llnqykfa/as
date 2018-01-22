package com.aixianshengxian.module;

/**
 * Created by Administrator on 2017/10/26.
 */

public class ProductReceive {
    private String receiveId;
    private String productName;
    private double unitPrice;
    private String unit;
    private double receiveNum;
    private double consumeNum;
    private double presentConsume;
    private String receiveTime;

    public ProductReceive() {
        super();
    }

    public ProductReceive(String receiveId,String productName,double unitPrice,String unit,double receiveNum,
                          double consumeNum,double presentConsume,String receiveTime) {
        super();
        this.receiveId = receiveId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.unit = unit;
        this.receiveNum = receiveNum;
        this.consumeNum = consumeNum;
        this.presentConsume = presentConsume;
        this.receiveTime = receiveTime;
    }

    public String getReceiveId() {
        return receiveId;
    }
    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }
    public String getProductName() {
        return this.productName;
    }
    public void setProductName(String productName){
        this.productName = productName;
    }
    public double getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public double getReceiveNum() {
        return  receiveNum;
    }
    public void setReceiveNum (double receiveNum) {
        this.receiveNum = receiveNum;
    }
    public double getConsumeNum () {
        return consumeNum;
    }
    public void setConsumeNum(double consumeNum) {
        this.consumeNum = consumeNum;
    }
    public double getPresentConsume() {
        return  presentConsume;
    }
    public void setPresentConsume(double presentConsume) {
        this.presentConsume = presentConsume;
    }
    public String getReceiveTime() {
        return receiveTime;
    }
    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }
}
