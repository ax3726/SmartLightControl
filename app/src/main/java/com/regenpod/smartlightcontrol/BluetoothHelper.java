package com.regenpod.smartlightcontrol;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class BluetoothHelper {

    public static final int SEND_SUECSS = 1000;
    public static final int SEND_FAIL = 2000;
    public static final int READ_SUCESS = 3000;
    public static final int READ_FAIL = 4000;
    private BleDevice bleDevice;
    private BluetoothGattCharacteristic characteristicWrite = null;
    private BluetoothGattCharacteristic characteristicRead = null;
    private SendThread sendThread = null;
    private boolean isDeiceRunning = false;
    private BleGattCallback bleGattCallback = null;
    private static Handler handler = new Handler() {
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            if (msg.obj == null) {
                return;
            }
            switch (msg.what) {
                case SEND_SUECSS:
                    String sends = HexUtil.byteArrToHexString((byte[]) msg.obj);
                    Log.d("lm", "发送消息 >>>" + sends);
                    break;
                case SEND_FAIL:
                    Log.e("lm", "write fail" + msg.obj.toString());
                    break;
                case READ_SUCESS:
                    String message = HexUtil.formatHexString((byte[]) msg.obj);
                    CmdApi.analyzeInstruction(message);
                    Log.d("lm", "收到消息 >>> " + message);
                    break;
            }


        }
    };

    public void postDelayed(Runnable runnable, long delayMillis) {
        handler.postDelayed(runnable, delayMillis);
    }


    public static BluetoothHelper getInstance() {
        return Instance.instance;
    }

    public boolean isDeiceRunning() {
        return isDeiceRunning;
    }

    public void setDeiceRunning(boolean deiceRunning) {
        isDeiceRunning = deiceRunning;
    }

    public boolean init(BleDevice bleDevice) {
        close();
        this.bleDevice = bleDevice;
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
        for (BluetoothGattService service : gatt.getServices()) {
            try {
                characteristicWrite = service.getCharacteristic(UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb"));
                characteristicRead = service.getCharacteristic(UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb"));
            } catch (Exception ex) {
            }
        }

        sendThread = new SendThread();
        sendThread.start();
        if (characteristicWrite == null) {
            Log.e("lm", "没找到写入服务!");
            return false;
        }

        if (characteristicRead == null) {
            Log.e("lm", "没找到监听服务!");
            return false;
        }
        BleManager.getInstance().notify(
                bleDevice,
                characteristicRead.getService().getUuid().toString(),
                characteristicRead.getUuid().toString(),
                notifyCallback);

        return true;

    }


    private static class SendThread extends Thread {

        private BlockingQueue<byte[]> mPendingQueue = new ArrayBlockingQueue<>(100);
        /**
         * 支持并发原子类
         */
        private AtomicBoolean runFlag = new AtomicBoolean(true);

        @Override
        public void run() {
            super.run();
            while (!isInterrupted() && runFlag.get()) {
                try {
                    byte[] take = mPendingQueue.take();
                    BluetoothHelper.getInstance().sendRealMessage(take);
                    sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        public void end() {
            runFlag.set(false);
        }

        public void sendMsg(byte[] msg) {
            try {
                mPendingQueue.put(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("lm", "存入数据异常!" + e.getMessage());
            }
        }

    }

    public void senMessage(byte[] msg) {
        sendThread.sendMsg(msg);
    }


    public void sendRealMessage(byte[] msg) {
        if (bleDevice == null || characteristicWrite == null) {
            Log.e("lm", "蓝牙服务异常!");
            return;
        }
        BleManager.getInstance().write(
                bleDevice,
                characteristicWrite.getService().getUuid().toString(),
                characteristicWrite.getUuid().toString(),
                msg,
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
            Log.d("lm", "打开通知操作成功");

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

    public void setBleGattCallback(BleGattCallback bleGattCallback) {
        this.bleGattCallback = bleGattCallback;
    }

    public void connect(final BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                if (bleGattCallback != null) {
                    bleGattCallback.onStartConnect();
                }
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                if (bleGattCallback != null) {
                    bleGattCallback.onConnectFail(bleDevice, exception);
                }
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                if (bleGattCallback != null) {
                    bleGattCallback.onConnectSuccess(bleDevice, gatt, status);
                }
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                if (bleGattCallback != null) {
                    bleGattCallback.onDisConnected(isActiveDisConnected, bleDevice, gatt, status);
                }
            }
        });
    }

    private void close() {
        if (sendThread != null) {
            sendThread.end();
            sendThread = null;
        }
        if (bleDevice != null && characteristicRead != null) {
            BleManager.getInstance().removeNotifyCallback(bleDevice, characteristicRead.getUuid().toString());
        }
    }

    public void disconnect() {
        bleGattCallback = null;
        close();
        if (bleDevice != null) {
            BleManager.getInstance().disconnect(bleDevice);
            BleManager.getInstance().destroy();
            bleDevice = null;
        }

    }

    static class Instance {
        static BluetoothHelper instance = new BluetoothHelper();
    }

}
