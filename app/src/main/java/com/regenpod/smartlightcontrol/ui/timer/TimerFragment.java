package com.regenpod.smartlightcontrol.ui.timer;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import com.lm.common.adapter.BaseCommonViewHolder;
import com.lm.common.base.BaseFragment;
import com.regenpod.smartlightcontrol.BluetoothHelper;
import com.regenpod.smartlightcontrol.R;
import com.regenpod.smartlightcontrol.ui.bean.ControlBean;
import com.regenpod.smartlightcontrol.ui.bean.LastTimeBean;
import com.regenpod.smartlightcontrol.ui.bean.TimeBean;
import com.regenpod.smartlightcontrol.utils.OperateHelper;
import com.regenpod.smartlightcontrol.utils.ScheduledExecutorServiceManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL_TIME;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_TIME;
import static com.regenpod.smartlightcontrol.CmdApi.createMessage;

public class TimerFragment extends BaseFragment {
    private BaseCommonViewHolder baseCommonViewHolder;
    private TimerViewModel timerViewModel;
    private OperateHelper timerOperateHelper;
    private boolean isRunningTime = false;
    private TextView tvTimeProgress;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_timer;
    }

    @Override
    protected void initView(View view) {
        tvTimeProgress = view.findViewById(R.id.tv_time_progress);
        baseCommonViewHolder = new BaseCommonViewHolder(view);
        timerViewModel =
                ViewModelProviders.of(this).get(TimerViewModel.class);
        initTimer();
        baseCommonViewHolder.setOnClickListener(R.id.img_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_TIME, timerOperateHelper.getProgress() * 60, true));
                showToast("send success！");
            }
        });
        EventBus.getDefault().register(this);
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            BluetoothHelper.getInstance().senMessage(createMessage(SYS_TIME, 0, -1));
        } else {
            isRunningTime = false;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getControlEvent(ControlBean controlBean) {
        if (controlBean.getCommand() == SYS_CONTROL_TIME) {
            timerOperateHelper.setProgress(controlBean.getValue() / 60);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getTime(TimeBean timeBean) {
        timerOperateHelper.setProgress((int) (timeBean.getTime() / 60));
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getLastTime(LastTimeBean timeBean) {
        if (!isVisible()) {
            return;
        }
        if (timeBean.getTime() == 0) {
            isRunningTime = false;
            tvTimeProgress.setText("倒计时结束，灯光关闭工作!");
        } else {
            if (!isRunningTime) {
                startTime();
            }
            if (timeBean.getTime() < 60) {
                tvTimeProgress.setText(timeBean.getTime() + "秒后关闭灯光!");
            } else {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("mm分ss秒");
                    String formatTime = formatter.format(timeBean.getTime() * 1000);
                    tvTimeProgress.setText(formatTime + "后关闭灯光。");
                } catch (Exception ex) {
                    Toast.makeText(aty, "倒计时时间异常！！", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    @Override
    protected void initData() {
    }

    private void startTime() {
        isRunningTime = true;
        ScheduledExecutorServiceManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!isRunningTime) {
                    return;
                }
                // 读取设备状态
                BluetoothHelper.getInstance().senMessage(createMessage(SYS_TIME, 0, -1));
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void releaseData() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}