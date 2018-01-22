package com.aixianshengxian.module;

import com.xmzynt.storm.basic.standardentity.StandardEntity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/30.
 */

public class PurchaseOrder extends StandardEntity {
    private String purchaseId;
    private String provider;
    private String purchaseUcode;
    private String orderCount;
    private String operateTime;
    private String purchaseState;
    private String depot;//仓库
    private String operator;//经办人
    private String car;//车辆
    private String driver;//司机
    private String remark;//备注

    public PurchaseOrder() {
        super();
    }

    public PurchaseOrder(String purchaseId, String provider,String purchaseUcode,String orderCount, String operateTime,
                         String purchaseState,String depot,String operator,String car,String driver,String remark) {
        super();
        this.purchaseId = purchaseId;
        this.provider = provider;
        this.purchaseUcode = purchaseUcode;
        this.orderCount = orderCount;
        this.operateTime = operateTime;
        this.purchaseState = purchaseState;
        this.depot = depot;
        this.operator = operator;
        this.car = car;
        this.driver = driver;
        this.remark = remark;
    }

    public String getPurchaseId() {
        return this.purchaseId;
    }
    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }
    public String getProvider() {
        return this.provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }
    public String getPurchaseUcode() {
        return this.purchaseUcode;
    }
    public void setPurchaseUcode(String purchaseUcode) {
        this.purchaseUcode = purchaseUcode;
    }
    public String getOrderCount() {
        return this.orderCount;
    }
    public void setOrderCount(String orderCount){
        this.orderCount = orderCount;
    }
    public String getOperateTime() {
        return this.operateTime;
    }
    public void setOperateTime(String operateTime){
        this.operateTime = operateTime;
    }
    public String getPurchaseState() {
        return this.purchaseState;
    }
    public void setPurchaseState(String purchaseState){
        this.purchaseState = purchaseState;
    }
    public String getDepot() {
        return this.depot;
    }
    public void setDepot(String depot){
        this.depot = depot;
    }
    public String getOperator() {
        return this.operator;
    }
    public void setOperator(String operator){
        this.operator = operator;
    }
    public String getCar() {
        return this.car;
    }
    public void setCar(String car){
        this.car = car;
    }
    public String getDriver(){
        return driver;
    }
    public void setDriver(String driver){
        this.driver = driver;
    }
    public String getRemark(){
        return remark;
    }
    public void setRemark(String remark){
        this.remark = remark;
    }

}
