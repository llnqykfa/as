package com.aixianshengxian.module;

import com.xmzynt.storm.common.api.base.standardentity.StandardEntity;
import com.xmzynt.storm.service.goods.Goods;
import com.xmzynt.storm.service.wms.stockout.StockOutRecord;

/**
 * Created by Administrator on 2017/11/13.
 */

public class StockOutRecordItem extends StandardEntity {
    private StockOutRecord StockOutRecord;
    private Boolean isSelector ;

    public StockOutRecordItem(StockOutRecord StockOutRecord, Boolean isSelector) {
        this.StockOutRecord = StockOutRecord;
        this.isSelector = isSelector;
    }

    public StockOutRecord getStockOutRecord () {
        return StockOutRecord  ;
    }
    public void setStockOutRecord(StockOutRecord StockOutRecord) {
        this.StockOutRecord = StockOutRecord;
    }
    public Boolean getSelector() {
        return isSelector;
    }
    public void setSelector(Boolean selector) {
        isSelector = selector;
    }
}
