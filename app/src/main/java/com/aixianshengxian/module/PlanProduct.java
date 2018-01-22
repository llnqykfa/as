package com.aixianshengxian.module;

/**
 * Created by Administrator on 2017/9/30.
 */

public class PlanProduct {
    private String planId;
    private String productName;
    private String unit;
    private double purchaseNum;
    private String planTime;
    private String planState;
    private String Provider;
    //private Provider provider;

    public PlanProduct() {
        super();
    }

    public PlanProduct (String planId,String productName,String unit,double purchaseNum,String planTime,
                        String planState,String Provider/*,Provider provider*/) {
        super();
        this.planId = planId;
        this.productName = productName;
        this.unit = unit;
        this.purchaseNum = purchaseNum;
        this.planTime = planTime;
        this.planState = planState;
        this.Provider = Provider;
        //this.provider = provider;
    }

    public String getPlanId() {
        return this.planId;
    }
    public void setPlanId(String planId) {
        this.planId = planId;
    }
    public String getProductName() {
        return this.productName;
    }
    public void setProductName(String productName){
        this.productName = productName;
    }
    public String getUnit() {
        return this.unit;
    }
    public void setUnit(String unit){
        this.unit = unit;
    }
    public double getPurchaseNum() {
        return this.purchaseNum;
    }
    public void setPurchaseNum(double purchaseNum){
        this.purchaseNum = purchaseNum;
    }
    public String getPlanTime() {
        return this.planTime;
    }
    public void setPlanTime(String planTime){
        this.planTime = planTime;
    }
    public String getPlanState() {
        return this.planState;
    }
    public void setPlanState(String planState) {
        this.planState = planState;
    }
    public String getProvider() {
        return this.Provider;
    }
    public void setProvider(String Provider) {
        this.Provider = Provider;
    }
    /*public Provider getprovider() {
        return provider;
    }
    public void setprovider(Provider provider) {
        this.provider = provider;
    }*/
}
