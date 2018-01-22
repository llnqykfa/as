package com.aixianshengxian.module;

/**
 * Created by Administrator on 2017/11/1.
 */

public class Provider {
    private String providerId;
    private String providerName;

    public Provider() {
        super();
    }
    public Provider(String providerId,String providerName) {
        super();
        this.providerId = providerId;
        this.providerName = providerName;
    }

    public String getProviderId() {
        return providerId;
    }
    public void setProviderId(String providerName) {
        this.providerId = providerId;
    }
    public String getProviderName() {
        return providerName;
    }
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
}
