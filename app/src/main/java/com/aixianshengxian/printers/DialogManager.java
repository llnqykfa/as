package com.aixianshengxian.printers;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;

import com.aixianshengxian.activity.purchase.PurchaseDetailActivity;

import java.util.Set;

/**
 * Created by Administrator on 2017/11/23.
 */

public class DialogManager {
    public static void showBluetoothDialog(Context context, final Set<BluetoothDevice> pairedDevices) {
        final String[] items = new String[pairedDevices.size()];
        int index = 0;
        for (BluetoothDevice device : pairedDevices) {
            items[index++] = device.getAddress();
        }

        new AlertDialog.Builder(context).setTitle("配对的蓝牙打印机")
                .setItems(items, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        PurchaseDetailActivity.mBixolonPrinter.connect(items[which]);
                    }
                }).show();
    }
}
