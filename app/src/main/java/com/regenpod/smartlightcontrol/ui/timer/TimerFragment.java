package com.regenpod.smartlightcontrol.ui.timer;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.lm.common.base.BaseFragment;
import com.regenpod.smartlightcontrol.R;

public class TimerFragment extends BaseFragment {

    private TimerViewModel timerViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_timer;
    }

    @Override
    protected void initView(View view) {
        timerViewModel =
                ViewModelProviders.of(this).get(TimerViewModel.class);
        timerViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void releaseData() {

    }
}