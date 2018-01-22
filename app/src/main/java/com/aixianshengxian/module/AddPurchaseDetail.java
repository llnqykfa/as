package com.aixianshengxian.module;

/**
 * Created by Administrator on 2017/10/19.
 */

public class AddPurchaseDetail {
    private String purchaseDetailId;
    private String productName;
    private double unitPrice;
    private String unit;
    private double purchaseNum;
    private String place;
    //private String subtotal;//客户部门
    private String remark;
    private double purchasePrice;
    //private String detailState;
    //private String purchaseState;

    public AddPurchaseDetail() {
        super();
    }

    public AddPurchaseDetail(String purchaseDetailId, String productName, double unitPrice, String unit, double purchaseNum,
                             String place, String remark, double purchasePrice) {
        super();
        this.purchaseDetailId = purchaseDetailId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.unit = unit;
        this.purchaseNum = purchaseNum;
        this.place = place;
        //this.subtotal = subtotal;
        this.remark = remark;
        this.purchasePrice = purchasePrice;
        //this.detailState = detailState;
        /*this.purchaseState = purchaseState;*/
    }

    public String getPurchaseDetailId() {
        return purchaseDetailId;
    }
    public void setPurchaseDetailId(String purchaseDetailId) {
        this.purchaseDetailId = purchaseDetailId;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
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
    public double getPurchaseNum() {
        return  purchaseNum;
    }
    public void setPurchaseNum(double purchaseNum) {
        this.purchaseNum = purchaseNum;
    }
    public String getPlace() {
        return  place;
    }
    public void setPlace(String place) {
        this.place = place;
    }
    /*public String getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }*/
    public String getRemark() {
        return remark;
    }
    public void setRemark(String subtotal) {
        this.remark = remark;
    }
    public double getPurchasePrice() {
        return purchasePrice;
    }
    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    /*public String getDetailState() {
        return detailState;
    }
    public void setDetailState(String purchaseState) {
        this.detailState = detailState;
    }*/
    /*public String getPurchaseState() {
        return purchaseState;
    }
    public void setPurchaseState(String purchaseState) {
        this.purchaseState = purchaseState;
    }*/
}
