package com.aixianshengxian.module;

import com.xmzynt.storm.basic.standardentity.StandardEntity;
import com.xmzynt.storm.service.process.ForecastProcessPlan;


import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/11/13.
 */

public class ForecastProcessPlanItem extends StandardEntity {
    private ForecastProcessPlan forecastProcessPlan;
    private BigDecimal stockInNum;//入库数
    private BigDecimal price;//入库单价
    private int day;//保质期
    private List<String> basketCodes;//绑定码

    public ForecastProcessPlanItem() {
        super();
    }
    public ForecastProcessPlanItem(ForecastProcessPlan forecastProcessPlan, BigDecimal stockInNum, BigDecimal price,
                                   int day,List<String> basketCodes) {
        this.forecastProcessPlan = forecastProcessPlan;
        this.stockInNum = stockInNum;
        this.price = price;
        this.day = day;
        this.basketCodes = basketCodes;
    }

    public ForecastProcessPlan getForecastProcessPlan() {
        return forecastProcessPlan  ;
    }
    public void setForecastProcessPlan(ForecastProcessPlan corecastProcessPlan) {
        this.forecastProcessPlan = corecastProcessPlan;
    }
    public BigDecimal getStockInNum() {
        return stockInNum;
    }
    public void setStockInNum(BigDecimal stockInNum) {
        this.stockInNum = stockInNum;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public int getDay() {
        return day;
    }
    public void setDay(int day) {
        this.day = day;
    }
    public List<String> getBasketCodes() {
        return basketCodes;
    }
    public void setBasketCodes(List<String> basketCodes) {
        this.basketCodes = basketCodes;
    }
}
