package com.aixianshengxian.module;

import com.xmzynt.storm.common.api.base.standardentity.StandardEntity;
import com.xmzynt.storm.service.wms.stockout.StockOutRecord;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/11/13.
 */

public class StockOutRecordConsume extends StandardEntity {
    private StockOutRecord StockOutRecord;
    private BigDecimal consume;

    public StockOutRecordConsume(StockOutRecord StockOutRecord, BigDecimal consume) {
        this.StockOutRecord = StockOutRecord;
        this.consume = consume;
    }

    public StockOutRecord getStockOutRecord () {
        return StockOutRecord  ;
    }
    public void setStockOutRecord(StockOutRecord StockOutRecord) {
        this.StockOutRecord = StockOutRecord;
    }
    public BigDecimal getConsume() {
        return consume;
    }
    public void setConsume(BigDecimal consume) {
        this.consume = consume;
    }
}
