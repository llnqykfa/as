package com.aixianshengxian.entity;


import com.xmzynt.storm.service.sale.SalesCatalogLine;

/**
 * Created by cwj on 2017/6/15.
 */

public class SalesCatalogLineItem {
    private SalesCatalogLine salesCatalogLine;
    private Boolean isSelector ;

    public SalesCatalogLineItem(SalesCatalogLine salesCatalogLine, Boolean isSelector) {
        this.salesCatalogLine = salesCatalogLine;
        this.isSelector = isSelector;
    }

    public SalesCatalogLine getSalesCatalogLine() {
        return salesCatalogLine;
    }

    public void setSalesCatalogLine(SalesCatalogLine salesCatalogLine) {
        this.salesCatalogLine = salesCatalogLine;
    }

    public Boolean getSelector() {
        return isSelector;
    }

    public void setSelector(Boolean selector) {
        isSelector = selector;
    }
}
