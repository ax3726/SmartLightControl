package com.regenpod.smartlightcontrol.ui.dimming;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
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
import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL_RW_PWM;
import static com.regenpod.smartlightcontrol.CmdApi.SYS_CONTROL_R_PWM;
import static com.regenpod.smartlightcontrol.CmdApi.createMessage;

public class DimmingFragment extends BaseFragment {

    private DimmingViewModel dimmingViewModel;
    private BaseCommonViewHolder baseCommonViewHolder;
    private OperateHelper dm660OperateHelper;
    private OperateHelper dm850OperateHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dimming;
    }

    @Override
    protected void initView(View view) {
        baseCommonViewHolder = new BaseCommonViewHolder(view);
        dimmingViewModel =
                ViewModelProviders.of(this).get(DimmingViewModel.class);
        dimmingViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        initDm660();
        initDm850();
        baseCommonViewHolder.setOnClickListener(R.id.img_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BluetoothHelper.getInstance().isDeiceRunning()) {
                    showToast("Device is not turned on!");
                    return;
                }
                int dm660Progress = dm660OperateHelper.getProgress();
                int dm850Progress = dm850OperateHelper.getProgress();
                BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_R_PWM, dm660Progress));
                BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_RW_PWM, dm850Progress));
                showToast("send success！");
            }
        });
        EventBus.getDefault().register(this);
    }

    private void initDm660() {
        dm660OperateHelper = new OperateHelper();
        dm660OperateHelper.init(baseCommonViewHolder.getTextView(R.id.tv_dm_660),
                baseCommonViewHolder.getView(R.id.img_add_660),
                baseCommonViewHolder.getView(R.id.img_less_660),
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

    private void initDm850() {
        dm850OperateHelper = new OperateHelper();
        dm850OperateHelper.init(baseCommonViewHolder.getTextView(R.id.tv_dm_850),
                baseCommonViewHolder.getView(R.id.img_add_850),
                baseCommonViewHolder.getView(R.id.img_less_850),
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getControlEvent(ControlBean controlBean) {
        switch (controlBean.getCommand()) {
            case SYS_CONTROL_R_PWM:// 写入红灯占空比  dc660
                dm660OperateHelper.setProgress(controlBean.getValue());
                break;
            case SYS_CONTROL_RW_PWM:// 写入红灯频率  dc850
                dm850OperateHelper.setProgress(controlBean.getValue());
                break;
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