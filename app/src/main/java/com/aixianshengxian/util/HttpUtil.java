package com.aixianshengxian.util;

import android.text.TextUtils;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by cwj on 2017/6/11.
 */

    public class HttpUtil {
    public interface HttpListener {
        void successResponse(String s, int i);
        void errorResponse(Call call, Exception e, int i);
    }


    public static void post(String url, String params, final HttpListener listener) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(params)) {
            return;
        }

        OkHttpUtils.postString().url(url)
                .addHeader("Cookie", "PHPSESSID=" + 123456)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/json;chartset=utf-8")
                .content(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if (listener != null) {
                            listener.errorResponse(call, e, i);
                        }
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        if (listener != null) {
                            listener.successResponse(s, i);
                        }
                    }
                });

    }


}
