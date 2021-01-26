package com.regenpod.smartlightcontrol.ui.main;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGatt;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.lm.common.adapter.BaseCommonViewHolder;
import com.lm.common.base.BaseActivity;
import com.regenpod.smartlightcontrol.BluetoothHelper;
import com.regenpod.smartlightcontrol.R;
import com.regenpod.smartlightcontrol.ui.bean.ControlBean;
import com.regenpod.smartlightcontrol.ui.bean.DeviceInfoBean;
import com.regenpod.smartlightcontrol.ui.bean.LastTimeBean;
import com.regenpod.smartlightcontrol.ui.bean.StatusBean;
import com.regenpod.smartlightcontrol.ui.bean.SwitchDeviceBen;
import com.regenpod.smartlightcontrol.utils.OperateHelper;
import com.regenpod.smartlightcontrol.utils.ScheduledExecutorServiceManager;
import com.regenpod.smartlightcontrol.utils.SharedPreferencesUtils;
import com.regenpod.smartlightcontrol.widget.dialog.DeviceInfoDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL_RW_FER;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL_RW_PWM;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL_R_FER;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL_R_PWM;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL_START;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL_STOP;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL_TIME;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_INFO;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_INFO_ADDRESS;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_INFO_MODEL;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_INFO_VER;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_STATUS;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_STATUS_RUNNING;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_TIME;
import static com.regenpod.smartlightcontrol.CmdApi.createMessage;

public class MainActivity extends BaseActivity {
    private BaseCommonViewHolder baseCommonViewHolder;
    public static final String KEY_TIME = "key_time";
    private DeviceInfoBean deviceInfoBean;
    private DeviceInfoDialog deviceInfoDialog;
    private boolean isRunning = false;
    private OperateHelper ht660OperateHelper;
    private OperateHelper ht850OperateHelper;
    private OperateHelper dc660OperateHelper;
    private OperateHelper dc850OperateHelper;
    private OperateHelper timerOperateHelper;
    private boolean isRunningTime = false;
    private TextView tvTimeProgress;
    private int count = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        baseCommonViewHolder = new BaseCommonViewHolder(getWindow().getDecorView());
        tvTimeProgress = findViewById(R.id.tv_time_progress);
        initOperatingUI();
        baseCommonViewHolder.setOnClickListener(R.id.tv_device_name, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deviceInfoBean == null) {
                    showToast("No device information！");
                    return;
                }
                deviceInfoDialog = new DeviceInfoDialog(aty, deviceInfoBean);
                deviceInfoDialog.show();
            }
        }).setOnClickListener(R.id.tv_bluetooth, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(aty);
                builder.setTitle("prompt");
                builder.setMessage("After entering the Bluetooth connection interface, the current connection will be disconnected, whether to continue？");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        BluetoothHelper.getInstance().disconnect();
                        showLoading("Exiting...");
                        BluetoothHelper.getInstance().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                closeLoading();
                                startActivity(new Intent(aty, ConnectActivity.class));
                                finish();
                            }
                        }, 3000);


                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });

        baseCommonViewHolder.setOnClickListener(R.id.img_switch, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baseCommonViewHolder.isSelected(R.id.img_switch)) {
                    BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_STOP, -1));
                    showLoading("Shutting down...");
                } else {
                    BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_TIME, (int) SharedPreferencesUtils.getParam(aty, KEY_TIME, 0), true));
                    BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_START, -1));
                    showLoading("Booting up...");
                }
                checkDialog();
            }
        });
        setReConnect();
        EventBus.getDefault().register(this);

    }

    private void setReConnect() {
        BluetoothHelper.getInstance().setBleGattCallback(new BleGattCallback() {
            @Override
            public void onStartConnect() {
                showLoading("正在重新连接设备");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                closeLoading();
                Toast.makeText(aty, getString(R.string.connect_fail), Toast.LENGTH_LONG).show();
                if (count == 3) {
                    startActivity(new Intent(aty, ConnectActivity.class));
                    finish();
                    return;
                }
                BluetoothHelper.getInstance().connect(bleDevice);
                count++;
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                closeLoading();
                count = 0;
                boolean result = BluetoothHelper.getInstance().init(bleDevice);
                if (!result) {
                    showToast("The Bluetooth device is not supported！");
                    startActivity(new Intent(aty, ConnectActivity.class));
                    finish();
                }
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                if (isActiveDisConnected) {
                    return;
                }
                BluetoothHelper.getInstance().connect(device);
                count++;
            }
        });
    }

    @Override
    protected void initData() {

        showLoading();
        BluetoothHelper.getInstance().postDelayed(new Runnable() {
            @Override
            public void run() {
                timerOperateHelper.setProgress((int) SharedPreferencesUtils.getParam(aty, KEY_TIME, 0) / 60);
                loadCommand();
                closeLoading();
                keepConnect();
            }
        }, 2000);
    }

    private ScheduledFuture mainScheduledFuture;

    private void keepConnect() {
        isRunning = true;
        if (mainScheduledFuture != null) {
            mainScheduledFuture.cancel(true);
        }
        mainScheduledFuture = ScheduledExecutorServiceManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!isRunning) {
                    if (mainScheduledFuture == null) {
                        return;
                    }
                    boolean cancelled = mainScheduledFuture.isCancelled();
                    if (!cancelled) {
                        mainScheduledFuture.cancel(true);
                    }
                    return;
                }
                BluetoothHelper.getInstance().senMessage(createMessage(SYS_TIME, 0, -1));
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    @Override
    protected void releaseData() {

    }

    private void initOperatingUI() {
        initHt660();
        initHt850();
        initDc660();
        initDc850();
        initTimer();
        baseCommonViewHolder.setOnClickListener(R.id.img_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }


    /**
     *
     */
    private void save() {
        int ht660Progress = ht660OperateHelper.getProgress();
        int ht850Progress = ht850OperateHelper.getProgress();

        int dc660Progress = dc660OperateHelper.getProgress();
        int dc850Progress = dc850OperateHelper.getProgress();

        BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_R_FER, ht660Progress, true));
        BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_RW_FER, ht850Progress, true));
        BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_R_PWM, (int) (dc660Progress * 0.8)));
        BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_RW_PWM, (int) (dc850Progress * 0.8)));

        BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_TIME, timerOperateHelper.getProgress() * 60, true));
        //保存设置的时间
        SharedPreferencesUtils.setParam(aty, KEY_TIME, timerOperateHelper.getProgress() * 60);

        // 延迟1秒吼读取剩余时间
        BluetoothHelper.getInstance().postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothHelper.getInstance().senMessage(createMessage(SYS_TIME, 0, -1));
            }
        }, 1000);

        showToast("send success！");
    }

    private void initTimer() {
        timerOperateHelper = new OperateHelper();
        timerOperateHelper.init(baseCommonViewHolder.getTextView(R.id.tv_timer),
                baseCommonViewHolder.getView(R.id.img_add),
                baseCommonViewHolder.getView(R.id.img_less),
                new OperateHelper.OperateListener() {
                    @Override
                    public int getAdd(int progress) {
                        progress = progress + 1;
                        if (progress > 30) {
                            progress = 30;
                        }
                        return progress;
                    }

                    @Override
                    public int getLess(int progress) {
                        progress = progress - 1;
                        if (progress < 1) {
                            progress = 1;
                        }
                        return progress;
                    }

                    @Override
                    public String showProgress(int progress) {
                        return progress + "mins";
                    }
                });
    }

    private void initHt660() {
        ht660OperateHelper = new OperateHelper();
        ht660OperateHelper.init(baseCommonViewHolder.getTextView(R.id.tv_ht_660),
                baseCommonViewHolder.getView(R.id.img_ht_add_660),
                baseCommonViewHolder.getView(R.id.img_ht_less_660),
                new OperateHelper.OperateListener() {
                    @Override
                    public int getAdd(int progress) {
                        if (progress >= 100) {
                            progress = progress + 20;
                        } else {
                            progress = progress + 10;
                        }
                        if (progress == 11) {
                            progress = 10;
                        }
                        if (progress > 2000) {
                            progress = 2000;
                        }
                        return progress;
                    }

                    @Override
                    public int getLess(int progress) {
                        if (progress >= 100) {
                            progress = progress - 20;
                        } else {
                            progress = progress - 10;
                        }
                        if (progress < 1) {
                            progress = 1;
                        }
                        return progress;
                    }

                    @Override
                    public String showProgress(int progress) {
                        return progress + "HZ";
                    }
                });
    }

    private void initHt850() {
        ht850OperateHelper = new OperateHelper();
        ht850OperateHelper.init(baseCommonViewHolder.getTextView(R.id.tv_ht_850),
                baseCommonViewHolder.getView(R.id.img_ht_add_850),
                baseCommonViewHolder.getView(R.id.img_ht_less_850),
                new OperateHelper.OperateListener() {
                    @Override
                    public int getAdd(int progress) {
                        if (progress >= 100) {
                            progress = progress + 20;
                        } else {
                            progress = progress + 10;
                        }
                        if (progress == 11) {
                            progress = 10;
                        }
                        if (progress > 2000) {
                            progress = 2000;
                        }
                        return progress;
                    }

                    @Override
                    public int getLess(int progress) {
                        if (progress >= 100) {
                            progress = progress - 20;
                        } else {
                            progress = progress - 10;
                        }
                        if (progress < 1) {
                            progress = 1;
                        }
                        return progress;
                    }

                    @Override
                    public String showProgress(int progress) {
                        return progress + "HZ";
                    }
                });
    }

    private void initDc660() {
        dc660OperateHelper = new OperateHelper();
        dc660OperateHelper.init(baseCommonViewHolder.getTextView(R.id.tv_dc_660),
                baseCommonViewHolder.getView(R.id.img_dc_add_660),
                baseCommonViewHolder.getView(R.id.img_dc_less_660),
                new OperateHelper.OperateListener() {
                    @Override
                    public int getAdd(int progress) {
                        progress = progress + 1;
                        if (progress > 100) {
                            progress = 100;
                        }
                        return progress;
                    }

                    @Override
                    public int getLess(int progress) {
                        progress = progress - 1;
                        if (progress < 0) {
                            progress = 0;
                        }
                        return progress;
                    }

                    @Override
                    public String showProgress(int progress) {
                        return progress + "%";
                    }
                });
    }


    private void initDc850() {
        dc850OperateHelper = new OperateHelper();
        dc850OperateHelper.init(baseCommonViewHolder.getTextView(R.id.tv_dc_850),
                baseCommonViewHolder.getView(R.id.img_dc_add_850),
                baseCommonViewHolder.getView(R.id.img_dc_less_850),
                new OperateHelper.OperateListener() {
                    @Override
                    public int getAdd(int progress) {
                        progress = progress + 1;
                        if (progress > 100) {
                            progress = 100;
                        }
                        return progress;
                    }

                    @Override
                    public int getLess(int progress) {
                        progress = progress - 1;
                        if (progress < 0) {
                            progress = 0;
                        }
                        return progress;
                    }

                    @Override
                    public String showProgress(int progress) {
                        return progress + "%";
                    }
                });
    }

    /**
     * 读取设备状态
     */
    private void loadCommand() {

        // 读取设备状态
        BluetoothHelper.getInstance().senMessage(createMessage(SYS_STATUS, 0, -1));
        BluetoothHelper.getInstance().senMessage(createMessage(SYS_STATUS, 0, -1));
        BluetoothHelper.getInstance().senMessage(createMessage(SYS_STATUS, 0, -1));
        //读取剩余时间
        BluetoothHelper.getInstance().senMessage(createMessage(SYS_TIME, 0, -1));
        // 读取设备信息
        BluetoothHelper.getInstance().senMessage(createMessage(SYS_INFO, SYS_INFO_MODEL, -1));
        BluetoothHelper.getInstance().senMessage(createMessage(SYS_INFO, SYS_INFO_ADDRESS, -1));
        BluetoothHelper.getInstance().senMessage(createMessage(SYS_INFO, SYS_INFO_VER, -1));

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getDeviceInfo(DeviceInfoBean deviceInfo) {
        if (deviceInfoBean == null) {
            deviceInfoBean = new DeviceInfoBean();
        }
        switch (deviceInfo.getStatus()) {
            case SYS_INFO_ADDRESS://获取设备联机地址
                deviceInfoBean.setAddress(deviceInfo.getAddress());
                break;
            case SYS_INFO_VER://获取设备软件、硬件版本号
                deviceInfoBean.setSoftwareVersion(deviceInfo.getSoftwareVersion());
                deviceInfoBean.setHardwareVersion(deviceInfo.getHardwareVersion());
                break;
            case SYS_INFO_MODEL://获取设备机型 (必需实现)
                deviceInfoBean.setFactory(deviceInfo.getFactory());
                deviceInfoBean.setModel(deviceInfo.getModel());
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getSwitchEvent(SwitchDeviceBen switchDeviceBen) {
        closeLoading();
        showToast(switchDeviceBen.isSwitch() ? "turn on success!" : "turn off success!");
        baseCommonViewHolder.setSelect(R.id.img_switch, switchDeviceBen.isSwitch());
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getStatusEvent(StatusBean statusBean) {
        switch (statusBean.getStatus()) {
            case SYS_STATUS_RUNNING: //运行状态
                baseCommonViewHolder.setSelect(R.id.img_switch, true);
                break;
            default://设备未启动 或者异常状态
                baseCommonViewHolder.setSelect(R.id.img_switch, false);
//                BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_START, -1));
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getControlEvent(ControlBean controlBean) {
        switch (controlBean.getCommand()) {
            case SYS_CONTROL_R_FER:// 红灯频率  ht660
                ht660OperateHelper.setProgress(controlBean.getValue());
                break;
            case SYS_CONTROL_RW_FER:// 红外灯频率  ht850
                ht850OperateHelper.setProgress(controlBean.getValue());
                break;
            case SYS_CONTROL_R_PWM:// 写入红灯占空比  dc660
                dc660OperateHelper.setProgress((int) (controlBean.getValue() / 0.8));
                break;
            case SYS_CONTROL_RW_PWM:// 写入红灯频率  dc850
                dc850OperateHelper.setProgress((int) (controlBean.getValue() / 0.8));
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getLastTime(LastTimeBean timeBean) {
        if (timeBean.getTime() == 0) {
            isRunningTime = false;
            tvTimeProgress.setText("The countdown is over and the lights are turned off!");
        } else {
            if (!isRunningTime) {
                startTime();
            }
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
                String formatTime = formatter.format(timeBean.getTime() * 1000);
                tvTimeProgress.setText(formatTime + "  Turn off the lights after！");
            } catch (Exception ex) {
                Toast.makeText(aty, "The countdown time is abnormal！！", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private ScheduledFuture scheduledFuture;

    private void startTime() {
        isRunningTime = true;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        scheduledFuture = ScheduledExecutorServiceManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!isRunningTime) {
                    if (scheduledFuture == null) {
                        return;
                    }
                    boolean cancelled = scheduledFuture.isCancelled();
                    if (!cancelled) {
                        scheduledFuture.cancel(true);
                    }
                    return;
                }
                // 读取设备状态
                BluetoothHelper.getInstance().senMessage(createMessage(SYS_TIME, 0, -1));
            }
        }, 1, 1, TimeUnit.SECONDS);
    }


    /**
     * 用来计算返回键的点击间隔时间
     */
    private long exitTime = 0;
    public static final int TIME_DOUBLE_CLICK = 2000;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > TIME_DOUBLE_CLICK) {
                //弹出提示，可以有多种方式
                showToast("Press again to exit the program!");
                exitTime = System.currentTimeMillis();
            } else {
                showLoading("正在退出中...");
                BluetoothHelper.getInstance().disconnect();
                BluetoothHelper.getInstance().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                        finish();
                    }
                }, 3000);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunningTime = false;
        isRunning = false;
        EventBus.getDefault().unregister(this);
        BluetoothHelper.getInstance().disconnect();
    }
}
