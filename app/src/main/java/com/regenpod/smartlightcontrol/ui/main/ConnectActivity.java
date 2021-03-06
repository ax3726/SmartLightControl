package com.regenpod.smartlightcontrol.ui.main;


import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.lm.common.adapter.BaseCommonViewHolder;
import com.lm.common.adapter.BaseRecycleViewAdapter;
import com.lm.common.base.BaseActivity;
import com.regenpod.smartlightcontrol.BluetoothHelper;
import com.regenpod.smartlightcontrol.R;
import com.regenpod.smartlightcontrol.app.LightApplication;
import com.regenpod.smartlightcontrol.utils.PermissionUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConnectActivity extends BaseActivity {
    private static final int REQUEST_CODE_OPEN_GPS = 1;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;
    private BaseRecycleViewAdapter<BleDevice> mAdapter;
    private RecyclerView rcDevice;
    private List<BleDevice> mDataList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_connect;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        rcDevice = findViewById(R.id.rc_device);
        PermissionUtil.requestEachRxPermission(this, new PermissionUtil.OnPermissionListener() {
            @Override
            public void onPermissionResult(@NonNull Boolean granted) {
                if (granted) {
                    LightApplication.makeAppDir();
                }
            }
        });

    }

    private void initSDK() {
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setSplitWriteNum(512)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }

    @Override
    protected void initData() {
        initSDK();
        initAdapter();
        showConnectedDevice();
        checkPermissions();

    }


    private void initAdapter() {
        mAdapter = new BaseRecycleViewAdapter<BleDevice>(R.layout.item_device_layout, false, true) {
            @Override
            protected void convert(@NotNull final BaseCommonViewHolder baseCommonViewHolder, final BleDevice item) {
                baseCommonViewHolder.setText(R.id.tv_device_name, item.getName())
                        .setOnClickListener(R.id.btn_connect, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!BleManager.getInstance().isConnected(item)) {
                                    BleManager.getInstance().cancelScan();
                                    connect(item);
                                }
                            }
                        });
            }
        };
        rcDevice.setLayoutManager(new LinearLayoutManager(aty));
        rcDevice.setAdapter(mAdapter);
        mAdapter.setData(mDataList);
    }

    @Override
    protected void releaseData() {

    }

    private void showConnectedDevice() {
        List<BleDevice> deviceList = BleManager.getInstance().getAllConnectedDevice();
        if (deviceList != null) {
            for (BleDevice device : deviceList) {
                if (!TextUtils.isEmpty(device.getName())) {
                    mDataList.add(device);
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private void checkPermissions() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, getString(R.string.please_open_blue), Toast.LENGTH_LONG).show();
            return;
        }

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
        }
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode,
                                                 @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_LOCATION:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            onPermissionGranted(permissions[i]);
                        }
                    }
                }
                break;
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.notifyTitle)
                            .setMessage(R.string.gpsNotifyMsg)
                            .setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                            .setPositiveButton(R.string.setting,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
                                        }
                                    })

                            .setCancelable(false)
                            .show();
                } else {
                    setScanRule();
                    startScan();
                }
                break;
        }
    }

    private void setScanRule() {
        String[] uuids = null;

        UUID[] serviceUuids = null;
        if (uuids != null && uuids.length > 0) {
            serviceUuids = new UUID[uuids.length];
            for (int i = 0; i < uuids.length; i++) {
                String name = uuids[i];
                String[] components = name.split("-");
                if (components.length != 5) {
                    serviceUuids[i] = null;
                } else {
                    serviceUuids[i] = UUID.fromString(uuids[i]);
                }
            }
        }

        String[] names = null;


        String mac = null;

        boolean isAutoConnect = false;

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, names)   // 只扫描指定广播名的设备，可选
                .setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(isAutoConnect)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                mDataList.clear();
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                if (TextUtils.isEmpty(bleDevice.getName())) {
                    return;
                }
                if (!bleDevice.getName().contains("Minew")) {
                    return;
                }
                mDataList.add(bleDevice);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {

            }
        });
    }

    private void connect(final BleDevice bleDevice) {
        BluetoothHelper.getInstance().setBleGattCallback(new BleGattCallback() {
            @Override
            public void onStartConnect() {
                showLoading();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                closeLoading();
                Toast.makeText(aty, getString(R.string.connect_fail), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                closeLoading();
                boolean result = BluetoothHelper.getInstance().init(bleDevice);
                if (!result) {
                    showToast("The Bluetooth device is not supported！");
                } else {
                    startActivity(new Intent(aty, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

            }
        });
        BluetoothHelper.getInstance().connect(bleDevice);
    }


    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }


}
