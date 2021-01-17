package com.regenpod.smartlightcontrol.ui.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lm.common.adapter.BaseCommonViewHolder;
import com.lm.common.base.BaseActivity;
import com.regenpod.smartlightcontrol.BluetoothHelper;
import com.regenpod.smartlightcontrol.R;
import com.regenpod.smartlightcontrol.ui.bean.DeviceInfoBean;
import com.regenpod.smartlightcontrol.ui.bean.StatusBean;
import com.regenpod.smartlightcontrol.ui.bean.SwitchDeviceBen;
import com.regenpod.smartlightcontrol.ui.dimming.DimmingFragment;
import com.regenpod.smartlightcontrol.ui.pulse.PulseFragment;
import com.regenpod.smartlightcontrol.ui.timer.TimerFragment;
import com.regenpod.smartlightcontrol.utils.ScheduledExecutorServiceManager;
import com.regenpod.smartlightcontrol.widget.dialog.DeviceInfoDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL_START;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL_STOP;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL_TIME;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_INFO;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_INFO_ADDRESS;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_INFO_MODEL;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_INFO_VER;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_STATUS;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_STATUS_RUNNING;
import static com.regenpod.smartlightcontrol.CmdApi.createMessage;

public class MainActivity extends BaseActivity {
    private BaseCommonViewHolder baseCommonViewHolder;
    private PulseFragment mPulseFragment;
    private TimerFragment mTimerFragment;
    private DimmingFragment mDimmingFragment;
    private List<Fragment> mFragments = new ArrayList<>();
    private FragmentManager mFm;
    private FragmentTransaction mTransaction;
    private RadioGroup rgBottom;
    private DeviceInfoBean deviceInfoBean;
    private DeviceInfoDialog deviceInfoDialog;
    private boolean isRunning = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        baseCommonViewHolder = new BaseCommonViewHolder(getWindow().getDecorView());
        baseCommonViewHolder.setOnClickListener(R.id.tv_device_name, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deviceInfoBean == null) {
                    showToast("无设备信息！");
                    return;
                }
                deviceInfoDialog = new DeviceInfoDialog(aty, deviceInfoBean);
                deviceInfoDialog.show();
            }
        }).setOnClickListener(R.id.tv_bluetooth, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(aty);
                builder.setTitle("提示");
                builder.setMessage("进入蓝牙连接界面后会断开当前连接，是否继续？");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        BluetoothHelper.getInstance().disconnect();
                        showLoading("正在退出中...");
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
                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });
        rgBottom = findViewById(R.id.rg_bottom);
        rgBottom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_pulse:
                        if (currentFragmentPosition != 0) {
                            changeFragment(0);
                        }
                        break;
                    case R.id.rb_timer:
                        if (currentFragmentPosition != 1) {
                            changeFragment(1);
                        }
                        break;
                    case R.id.rb_dimming:
                        if (currentFragmentPosition != 2) {
                            changeFragment(2);
                        }
                        break;
                }
            }
        });
        baseCommonViewHolder.setOnClickListener(R.id.img_switch, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baseCommonViewHolder.isSelected(R.id.img_switch)) {
                    BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_STOP, -1));
                    BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_TIME, 0, true));
                    showLoading("Shutting down...");
                } else {
                    BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_START, -1));
                    showLoading("Booting up...");
                }
                checkDialog();
            }
        });
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initData() {
        initFragment();
        showLoading();
        BluetoothHelper.getInstance().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadCommand();
                closeLoading();
                keepConnect();
            }
        }, 3000);
    }

    private void keepConnect() {
        isRunning = true;
        ScheduledExecutorServiceManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!isRunning) {
                    return;
                }
                // 读取设备状态
                BluetoothHelper.getInstance().senMessage(createMessage(SYS_STATUS, 0, -1));
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    @Override
    protected void releaseData() {

    }


    private void initFragment() {
        mPulseFragment = new PulseFragment();
        mTimerFragment = new TimerFragment();
        mDimmingFragment = new DimmingFragment();

        mFragments.add(mPulseFragment);
        mFragments.add(mTimerFragment);
        mFragments.add(mDimmingFragment);
        mFm = getSupportFragmentManager();
        mTransaction = mFm.beginTransaction();
        mTransaction.add(R.id.fly_content, mFragments.get(0), "index_0");
        mTransaction.show(mFragments.get(0));
        mTransaction.commitAllowingStateLoss();
    }

    private int currentFragmentPosition = 0;

    public void changeFragment(final int position) {
        mFm = getSupportFragmentManager();
        mTransaction = mFm.beginTransaction();
        if (mFragments.get(position) != null) {
            if (position != currentFragmentPosition) {
                mTransaction.hide(mFragments.get(currentFragmentPosition));
                if (!mFragments.get(position).isAdded()) {
                    mTransaction.add(R.id.fly_content, mFragments.get(position), "index_" + position);
                }
                mTransaction.show(mFragments.get(position));
                mTransaction.commitAllowingStateLoss();
            }
            currentFragmentPosition = position;
        }
    }


    /**
     * 读取设备状态
     */
    private void loadCommand() {
        // 读取设备状态
        BluetoothHelper.getInstance().senMessage(createMessage(SYS_STATUS, 0, -1));
        BluetoothHelper.getInstance().senMessage(createMessage(SYS_STATUS, 0, -1));
        BluetoothHelper.getInstance().senMessage(createMessage(SYS_STATUS, 0, -1));

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
                BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_START, -1));
                break;
        }
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
        isRunning = false;
        EventBus.getDefault().unregister(this);
        BluetoothHelper.getInstance().disconnect();

    }
}
