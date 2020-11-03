package com.regenpod.smartlightcontrol.ui.pulse;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.lm.common.base.BaseFragment;
import com.regenpod.smartlightcontrol.R;

public class PulseFragment extends BaseFragment {


    private PulseViewModel pulseViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pulse;
    }

    @Override
    protected void initView(View view) {
        pulseViewModel =
                ViewModelProviders.of(this).get(PulseViewModel.class);
        pulseViewModel.getText().observe(this, new Observer<String>() {
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