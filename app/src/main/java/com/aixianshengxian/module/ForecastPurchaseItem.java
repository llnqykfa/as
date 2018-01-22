package com.aixianshengxian.module;

import com.xmzynt.storm.basic.standardentity.StandardEntity;
import com.xmzynt.storm.service.purchase.plan.ForecastPurchase;

/**
 * Created by Administrator on 2017/11/13.
 */

public class ForecastPurchaseItem extends StandardEntity {
    private ForecastPurchase forecastPurchase;
    private Boolean isSelector ;

    public ForecastPurchaseItem(ForecastPurchase forecastPurchase, Boolean isSelector) {
        this.forecastPurchase = forecastPurchase;
        this.isSelector = isSelector;
    }

    public ForecastPurchase getForecastPurchase() {
        return forecastPurchase ;
    }
    public void setForecastPurchase(ForecastPurchase forecastPurchase) {
        this.forecastPurchase = forecastPurchase;
    }
    public Boolean getSelector() {
        return isSelector;
    }
    public void setSelector(Boolean selector) {
        isSelector = selector;
    }
}
