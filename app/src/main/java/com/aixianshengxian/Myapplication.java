package com.aixianshengxian;

import android.app.Application;

import com.aixianshengxian.constant.FileConstant;
import com.aixianshengxian.util.FileManager;


/**
 * Created by CWJ on 2016/12/19.
 */
public class Myapplication extends Application {
  private static Myapplication sInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        // 创建crm文件夹,不能在CRM目录创建前有文件操作
        FileManager.createDir(FileConstant.CRM_DIR);
        FileManager.changeMod("777", FileConstant.CRM_DIR);
        // 创建crm图片文件夹
        FileManager.createDir(FileConstant.CRASH_DIR);
        FileManager.changeMod("777", FileConstant.CRASH_DIR);

        //在这里 为应用设置 异常处理 ，然后程序才能获取未处理的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
//
//        // 创建crm文件夹,不能在CRM目录创建前有文件操作
//        FileManager.createDir(FileConstant.CRM_DIR);
//        FileManager.changeMod("777", FileConstant.CRM_DIR);
//        // 创建crm图片文件夹
//        FileManager.createDir(FileConstant.FILE_IMAGE_DIR);
//        FileManager.changeMod("777", FileConstant.FILE_IMAGE_DIR);

    }

    public static Myapplication getsInstance(){
        return  sInstance ;
    }

}