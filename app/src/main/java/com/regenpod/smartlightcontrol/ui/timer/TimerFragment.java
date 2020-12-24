package com.regenpod.smartlightcontrol.ui.timer;

import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import com.lm.common.adapter.BaseCommonViewHolder;
import com.lm.common.base.BaseFragment;
import com.regenpod.smartlightcontrol.BluetoothHelper;
import com.regenpod.smartlightcontrol.R;
import com.regenpod.smartlightcontrol.ui.bean.ControlBean;
import com.regenpod.smartlightcontrol.utils.OperateHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL_RW_FER;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL_RW_PWM;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL_R_FER;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL_R_PWM;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL_TIME;
import static com.regenpod.smartlightcontrol.CmdApi.createMessage;

public class TimerFragment extends BaseFragment {
    private BaseCommonViewHolder baseCommonViewHolder;
    private TimerViewModel timerViewModel;
    private OperateHelper timerOperateHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_timer;
    }

    @Override
    protected void initView(View view) {
        baseCommonViewHolder = new BaseCommonViewHolder(view);
        timerViewModel =
                ViewModelProviders.of(this).get(TimerViewModel.class);
        initTimer();
        baseCommonViewHolder.setOnClickListener(R.id.img_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_TIME, timerOperateHelper.getProgress()));
                Toast.makeText(aty, "send success！", Toast.LENGTH_SHORT).show();
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getControlEvent(ControlBean controlBean) {
        if (controlBean.getCommand() == SYS_CONTROL_TIME) {
            timerOperateHelper.setProgress(controlBean.getValue());
        }
    }

    @Override
    protected void initData() {

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