package com.regenpod.smartlightcontrol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class BluetoothHelper {
    //声明变量
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private void init(Context context) {
        //获取buletoothAdapter并打开蓝牙
        mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            ((Activity) context).startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0);  // 弹对话框的形式提示用户开启蓝牙
        }
    }

    private BluetoothLeScanner bluetoothLeScanner;
    private List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();//存放扫描结果
    //startScan()回调函数
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult results) {
            super.onScanResult(callbackType, results);
            BluetoothDevice device = results.getDevice();
            if (!devices.contains(device)) {  //判断是否已经添加
                devices.add(device);//也可以添加devices.getName()到列表，这里省略            }
                // callbackType：回调类型
                // result：扫描的结果，不包括传统蓝牙        }
            }
            ;
        }
    };
}
