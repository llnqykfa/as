package com.aixianshengxian;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import com.aixianshengxian.constant.FileConstant;
import com.aixianshengxian.util.FileManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cwj on 2017/6/18.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";
    private static final boolean DEBUG = true;

    private static final String PATH = FileConstant.CRASH_DIR;
    private static final String FILE_NAME  = "crash";
    private static final String FILE_NAME_SUFFIX = ".txt";

    private static CrashHandler sInstance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;

    private CrashHandler(){
    }

    public static CrashHandler getInstance(){
        return sInstance;
    }

    public void init(Context context){
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }


    /**
     * 这个是 最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaught-Exception 方法
     * @param thread 为出现未捕获异常的线程，
     * @param ex 为未捕获的异常，有了这个ex ，我们就可以得到异常信息
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        //导出异常信息到sd卡中
        try {
            dumpExceptionToSDCard(ex);
            //这里可以上传异常信息到服务器，便于开发人员分析日志从而解决BUG
            uploadExceptionToServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ex.printStackTrace();
        //如果系统提供了默认的异常处理器，则交给系统去结束程序，否则就自己结束自己
        if(mDefaultCrashHandler != null){
            mDefaultCrashHandler.uncaughtException(thread ,ex);
        }else {
            Process.killProcess(Process.myPid());
        }

    }

    private void dumpExceptionToSDCard(Throwable ex) throws IOException {
        //如果SD卡不存在或无法使用，则无法将异常信息写入SD卡
        if(! Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            if(DEBUG){
                Log.w(TAG,"sdcard unmounted,skip dump exception");
                return;
            }
        }
        File dir  = new File(PATH);
        if(! dir.exists()){
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date( current));
        File file = FileManager.createNewFile(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
        if (!file.exists()) {
            Log.d(TAG, "文件创建失败");
            return;
        }

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println();
            ex.printStackTrace(pw);
            pw.close();
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }

    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException{
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi =  pm .getPackageInfo(mContext.getPackageName(),
                PackageManager.GET_ACTIVITIES);
        pw.print("App Version :");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);

        // Android 版本号
        pw.print("OS Version : ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        //手机制造商
        pw.print("Vendor :");
        pw.println(Build.MANUFACTURER);

        //手机型号
        pw.print("Model :");
        pw.println(Build.MODEL);

        //CPU架构
        pw.print("CPU ABI :");
        pw.println(Build.CPU_ABI);
    }

    private void uploadExceptionToServer(){
        // TODO: Upload Exception Message To Your Web Server
    }

}
