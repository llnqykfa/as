package com.aixianshengxian.listener;

import com.xmzynt.storm.service.goods.UnitPrice;

import java.util.List;
import java.util.Map;

/**
 * Created by yongyuan.w on 2017/11/18.
 *
 * @author yongyuan.w
 * @date 2017/11/18
 */
public interface UnitInterface {

    void onError();

    void onUnitListener(Map<String,List<UnitPrice>> map ,int position);
}
