package com.aixianshengxian.module;

import com.xmzynt.storm.basic.standardentity.StandardEntity;
import com.xmzynt.storm.service.wms.stockin.StockInRecord;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/11/13.
 */

public class StockInRecordItem extends StandardEntity {
    private StockInRecord StockInRecord;
    private BigDecimal amount;

    public StockInRecordItem() {
        super();
    }

    public StockInRecordItem(StockInRecord StockInRecord, BigDecimal amount) {
        this.StockInRecord  = StockInRecord;
        this.amount = amount;
    }

    public StockInRecord getStockInRecord () {
        return StockInRecord  ;
    }
    public void setStockInRecord(StockInRecord StockInRecord) {
        this.StockInRecord = StockInRecord;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
