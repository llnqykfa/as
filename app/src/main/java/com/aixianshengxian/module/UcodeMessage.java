package com.aixianshengxian.module;

import com.xmzynt.storm.basic.standardentity.StandardEntity;


/**
 * Created by Administrator on 2017/9/28.
 */

public class UcodeMessage extends StandardEntity {
    private String uCode;
    private String operateTime;

    public UcodeMessage() {
        super();
    }

    public UcodeMessage(String uCode, String operateTime) {
        super();
        this.uCode = uCode;
        this.operateTime = operateTime;
    }

    public String getUCode() {
        return this.uCode;
    }
    public void setUCode(String uCode) {
        this.uCode = uCode;
    }

    public String getOperateTime() {
        return this.operateTime;
    }
    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }
}
