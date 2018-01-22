package com.aixianshengxian.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/10/10.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private String TAG = "Database ";

    public static final String CREATE_PLANPRODUCT = "create table PlanProduct ("
            +"planId integer primary key autoincrement, "
            +"productName text, "
            +"unit text, "
            +"purchaseNum text, "
            +"planTime text, "
            +"planState text, "
            +"provider text)";

    public static final String CREATE_PURCHASEORDER = "create table PurchaseOrder ("
            +"purchaseId integer primary key autoincrement, "
            +"provider text, "
            +"purchaseUcode text, "
            +"orderCount text, "
            +"operateTime text, "
            +"purchaseState text,"
            +"depot text,"
            +"operator text,"
            +"car text,"
            +"driver text,"
            +"remark text)";

    public static final String CREATE_PURCHASEDETAIL = "create table PurchaseDetail("
            +"purchaseDetailId integer primary key autoincrement, "
            +"productName text, "
            +"unitPrice integer, "
            +"unit text, "
            +"purchaseNum integer, "
            +"place text, "
            +"subtotal text, "
            +"remark text, "
            +"purchasePrice integer, "
            +"detailState text)";

    public static final String CREATE_ADDPURCHASE = "create table AddPurchase("
            +"purchaseDetailId integer primary key autoincrement,"
            +"productName text,"
            +"unitPrice text, "
            +"unit text, "
            +"purchaseNum text,"
            +"place text,"
            +"remark text,"
            +"purchasePrice text)";

    public static final String CREATE_MACHINEPRODUCT = "create table MachineProduct("
            +"machineId integer primary key autoincrement,"
            +"productName text, "
            +"unit text, "
            +"machineTime text, "
            +"planNum text, "
            +"competeNum text)";
    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PLANPRODUCT);
        db.execSQL(CREATE_PURCHASEORDER);
        db.execSQL(CREATE_PURCHASEDETAIL);
        db.execSQL(CREATE_ADDPURCHASE);
        db.execSQL(CREATE_MACHINEPRODUCT );
        //Log.d(TAG,"Create succeeded");
        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists PlanProduct");
        db.execSQL("drop table if exists PurchaseOrder");
        db.execSQL("drop table if exists PurchaseDetail");
        db.execSQL("drop table if exists AddPurchase");
        db.execSQL("drop table if exists MachineProduct");
        onCreate(db);
    }
}
