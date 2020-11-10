package com.regenpod.smartlightcontrol.ui.timer;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.lm.common.adapter.BaseCommonViewHolder;
import com.lm.common.base.BaseFragment;
import com.regenpod.smartlightcontrol.R;
import com.regenpod.smartlightcontrol.utils.OperateHelper;

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
                int timerProgress = timerOperateHelper.getProgress();
                StringBuilder sb = new StringBuilder();
                sb.append("timer:" + timerProgress);
                Toast.makeText(aty, sb.toString(), Toast.LENGTH_SHORT).show();
            }
        });
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
        timerOperateHelper.setProgress(20);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void releaseData() {

    }
}