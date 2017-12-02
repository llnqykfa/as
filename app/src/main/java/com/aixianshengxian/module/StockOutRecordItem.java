package com.aixianshengxian.module;

import com.xmzynt.storm.common.api.base.standardentity.StandardEntity;
import com.xmzynt.storm.service.goods.Goods;

/**
 * Created by Administrator on 2017/11/13.
 */

public class GoodsItem extends StandardEntity {
    private Goods Goods;
    private Boolean isSelector ;

    public GoodsItem(Goods Goods,Boolean isSelector) {
        this.Goods = Goods;
        this.isSelector = isSelector;
    }

    public Goods getGoods() {
        return Goods ;
    }
    public void setGoods(Goods goods) {
        this.isSelector = isSelector;
    }
    public Boolean getSelector() {
        return isSelector;
    }
    public void setSelector(Boolean selector) {
        isSelector = selector;
    }
}
