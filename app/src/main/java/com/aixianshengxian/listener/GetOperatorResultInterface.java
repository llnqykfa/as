package com.aixianshengxian.listener;

import com.xmzynt.storm.basic.idname.IdName;
import com.xmzynt.storm.basic.ucn.UCN;

import java.util.List;


public interface GetOperatorResultInterface {

    void onError();

    void onGetOperatorSuccess(List<IdName> datas);
}
