package com.aixianshengxian.module;

import com.xmzynt.storm.basic.standardentity.StandardEntity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/23.
 */

public class MachineProduct extends StandardEntity {
    private String machineId;
    private String productName;
    private String unit;
    private String machineTime;
    private double planNum;
    private double competeNum;

    public MachineProduct() {
        super();
    }

    public MachineProduct(String machineId,String productName,String unit,String machineTime,double planNum,
                           double competeNum) {
        super();
        this.machineId = machineId;
        this.productName = productName;
        this.unit = unit;
        this.machineTime = machineTime;
        this.planNum = planNum;
        this.competeNum = competeNum;
    }

    public String getMachineId() {
        return this.machineId;
    }
    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
    public String getProductName() {
        return this.productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getUnit() {
        return this.unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getMachineTime() {
        return this.machineTime;
    }
    public void setMachineTime(String machineTime) {
        this.machineTime = machineTime;
    }
    public double getPlanNum() {
        return this.planNum;
    }
    public void setPlanNum(double planNum) {
        this.planNum = planNum;
    }
    public double getCompeteNum() {
        return this.competeNum;
    }
    public void setCompeteNum(double competeNum) {
        this.competeNum = competeNum;
    }
}
