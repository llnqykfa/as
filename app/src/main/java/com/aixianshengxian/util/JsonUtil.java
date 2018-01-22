package com.aixianshengxian.util;

import android.content.Context;

import com.xmzynt.storm.basic.constants.MDataConstants;
import com.xmzynt.storm.basic.user.UserIdentity;
import com.xmzynt.storm.common.Platform;
import com.xmzynt.storm.service.user.merchant.Merchant;
/*<<<<<<< HEAD

=======
>>>>>>> 438ba51b3593f0e0bbc7d2fa4e31266122663d90*/

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cwj on 2017/6/3.
 */

public class JsonUtil {

    private static JsonUtil instance;
    private Merchant merchant;

    public static JsonUtil getInstance() {
        if (instance == null) {
            instance =new JsonUtil();
        }
        return instance;
    }

    public static JSONObject buildParams(Context context) {
        Merchant merchant = SessionUtils.getInstance(context).getLoginMerchan();
        JSONObject params = new JSONObject();
        if(merchant !=null){
            try {
                params.put(MDataConstants.URL_PLATFORM, Platform.android.name());
                params.put(MDataConstants.Field.USER_IDENTITY, UserIdentity.merchant.name());
                params.put(MDataConstants.Field.USER_UUID, merchant.getUuid());
                params.put(MDataConstants.Field.USER_NAME, merchant.getAccountInfo().getUserName());
                params.put(MDataConstants.Field.SESSION_ID, merchant.getSessionId());


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return params;
    }

}
