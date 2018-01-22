package com.aixianshengxian.constant;

import android.os.Environment;

/**
 * Created by CWJ on 2017/2/18.
 */

public class FileConstant {

    // SD卡目录
    public static final String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    // 绿百合目录
    public static final String CRM_DIR =  ROOT_DIR + "/lvbaohe";

    // 图片目录
    public static final String FILE_IMAGE_DIR = CRM_DIR + "/image/";

    public static final String CRASH_DIR = CRM_DIR + "/crash/";




}
