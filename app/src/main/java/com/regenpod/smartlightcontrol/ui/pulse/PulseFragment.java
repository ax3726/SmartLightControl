package com.regenpod.smartlightcontrol.ui.pulse;

import android.view.View;

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
import static com.regenpod.smartlightcontrol.CmdApi.SYS_STATUS;
import static com.regenpod.smartlightcontrol.CmdApi.createMessage;


public class PulseFragment extends BaseFragment {

    private BaseCommonViewHolder baseCommonViewHolder;
    private PulseViewModel pulseViewModel;
    private OperateHelper ht660OperateHelper;
    private OperateHelper ht850OperateHelper;
    private OperateHelper dc660OperateHelper;
    private OperateHelper dc850OperateHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pulse;
    }

    @Override
    protected void initView(View view) {
        baseCommonViewHolder = new BaseCommonViewHolder(view);
        pulseViewModel =
                ViewModelProviders.of(this).get(PulseViewModel.class);

        initHt660();
        initHt850();
        initDc660();
        initDc850();
        baseCommonViewHolder.setOnClickListener(R.id.img_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ht660Progress = ht660OperateHelper.getProgress();
                int ht850Progress = ht850OperateHelper.getProgress();

                int dc660Progress = dc660OperateHelper.getProgress();
                int dc850Progress = dc850OperateHelper.getProgress();

                BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_R_FER, ht660Progress,true));
                BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_RW_FER, ht850Progress,true));
                BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_R_PWM, (int) (dc660Progress*0.8)));
                BluetoothHelper.getInstance().senMessage(createMessage(SYS_CONTROL, SYS_CONTROL_RW_PWM, (int) (dc850Progress*0.8)));
                showToast("send success！");
            }


        });
        EventBus.getDefault().register(this);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            BluetoothHelper.getInstance().senMessage(createMessage(SYS_STATUS, 0, -1));
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
                dc660OperateHelper.setProgress(controlBean.getValue());
                break;
            case SYS_CONTROL_RW_PWM:// 写入红灯频率  dc850
                dc850OperateHelper.setProgress(controlBean.getValue());
                break;
        }
    }

    private void initHt660() {
        ht660OperateHelper = new OperateHelper();
        ht660OperateHelper.init(baseCommonViewHolder.getTextView(R.id.tv_ht_660),
                baseCommonViewHolder.getView(R.id.img_ht_add_660),
                baseCommonViewHolder.getView(R.id.img_ht_less_660),
                new OperateHelper.OperateListener() {
                    @Override
                    public int getAdd(int progress) {
                        if (progress>=100) {
                            progress = progress + 20;
                        }else {
                            progress = progress + 10;
                        }

                        if (progress > 2000) {
                            progress = 2000;
                        }
                        return progress;
                    }

                    @Override
                    public int getLess(int progress) {
                        if (progress>=100) {
                            progress = progress - 20;
                        }else {
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
                        if (progress>=100) {
                            progress = progress + 20;
                        }else {
                            progress = progress + 10;
                        }
                        if (progress > 2000) {
                            progress = 2000;
                        }
                        return progress;
                    }

                    @Override
                    public int getLess(int progress) {
                        if (progress>=100) {
                            progress = progress - 20;
                        }else {
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