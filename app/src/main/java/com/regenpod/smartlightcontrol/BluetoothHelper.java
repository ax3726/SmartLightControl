package com.regenpod.smartlightcontrol;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.util.UUID;

public class BluetoothHelper {

    public static final int SEND_SUECSS = 0;
    public static final int SEND_FAIL = 1;
    public static final int READ_SUCESS = 2;
    public static final int READ_FAIL = 2;
    private BleDevice bleDevice;
    private BluetoothGattCharacteristic characteristicWrite = null;
    private BluetoothGattCharacteristic characteristicRead = null;
    private static Handler handler = new Handler() {
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case SEND_SUECSS:
                    Log.e("lm", "发送消息 >>> " + new String((byte[]) msg.obj));
                    break;
                case SEND_FAIL:
                    Log.e("lm", "write fail" + msg.obj.toString());
                    break;
                case READ_SUCESS:
                    Log.e("lm", "收到消息 >>> " + new String((byte[]) msg.obj));
                    break;
            }


        }
    };

    public static BluetoothHelper getInstance() {
        return Instance.instance;
    }


    public void init(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
        for (BluetoothGattService service : gatt.getServices()) {
            try {
                characteristicWrite = service.getCharacteristic(UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb"));
                characteristicRead = service.getCharacteristic(UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb"));
            } catch (Exception ex) {
            }
        }
        if (characteristicRead == null) {
            Log.e("lm", "没找到监听服务!");
            return;
        }
        BleManager.getInstance().notify(
                bleDevice,
                characteristicRead.getService().getUuid().toString(),
                characteristicRead.getUuid().toString(),
                notifyCallback);
    }

    public void sendMessage(byte[] msg) {
        if (bleDevice == null || characteristicWrite == null) {
            Log.e("lm", "蓝牙服务异常!");
            return;
        }

        BleManager.getInstance().write(
                bleDevice,
                characteristicWrite.getService().getUuid().toString(),
                characteristicWrite.getUuid().toString(),
                msg,
                false,
                writeCallback);
    }

    private BleWriteCallback writeCallback = new BleWriteCallback() {

        @Override
        public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
            Message message = handler.obtainMessage(SEND_SUECSS);
            message.obj = justWrite;
            handler.sendMessage(message);
        }

        @Override
        public void onWriteFailure(final BleException exception) {
            Message message = handler.obtainMessage(SEND_FAIL);
            message.obj = exception.toString();
            handler.sendMessage(message);
        }
    };
    private BleNotifyCallback notifyCallback = new BleNotifyCallback() {
        @Override
        public void onNotifySuccess() {
            Log.e("lm", "打开通知操作成功");

        }

        @Override
        public void onNotifyFailure(BleException exception) {
            Log.e("lm", "打开通知操作失败" + exception.toString());
        }

        @Override
        public void onCharacteristicChanged(final byte[] data) {
            Message message = handler.obtainMessage(READ_SUCESS);
            message.obj = data;
            handler.sendMessage(message);
        }
    };

    public void disconnect() {
        if (bleDevice == null) {
            return;
        }
        BleManager.getInstance().disconnect(bleDevice);
    }

    static class Instance {
        static BluetoothHelper instance = new BluetoothHelper();
    }

}