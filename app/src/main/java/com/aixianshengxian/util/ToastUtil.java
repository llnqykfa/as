package com.aixianshengxian.util;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.aixianshengxian.R;
import com.aixianshengxian.view.HandyTextView;

/**
 * Created by cwj on 2017/6/13.
 */

public class ToastUtil {
    private static Toast mToast;

    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
            mToast=null;//toast隐藏后，将其置为null
        }
    };

    public static void showCustomToast(Context context, String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.common_toast, null);//自定义布局
        ((HandyTextView) view.findViewById(R.id.toast_text)).setText(message);//显示的提示文字
        mHandler.removeCallbacks(r);
        if (mToast == null){//只有mToast==null时才重新创建，否则只需更改提示文字
            mToast = new Toast(context);
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.BOTTOM, 0, 150);
            mToast.setView(view);
        }
        mHandler.postDelayed(r, 1000);//延迟1秒隐藏toast
        mToast.show();
    }
}
