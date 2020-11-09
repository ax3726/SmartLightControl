package com.regenpod.smartlightcontrol.ui.pulse;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.lm.common.adapter.BaseCommonViewHolder;
import com.lm.common.base.BaseFragment;
import com.regenpod.smartlightcontrol.R;
import com.regenpod.smartlightcontrol.utils.OperateHelper;

public class PulseFragment extends BaseFragment {

    private BaseCommonViewHolder baseCommonViewHolder;
    private PulseViewModel pulseViewModel;
    private OperateHelper ht660OperateHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pulse;
    }

    @Override
    protected void initView(View view) {
        baseCommonViewHolder = new BaseCommonViewHolder(view);
        pulseViewModel =
                ViewModelProviders.of(this).get(PulseViewModel.class);
        pulseViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        ht660OperateHelper = new OperateHelper();
        ht660OperateHelper.init(baseCommonViewHolder.getTextView(R.id.tv_ht_660),
                baseCommonViewHolder.getView(R.id.img_ht_add_660),
                baseCommonViewHolder.getView(R.id.img_ht_less_660),
                new OperateHelper.OperateListener() {
                    @Override
                    public int getAdd(int progress) {
                        progress = progress + (2000 / 100);
                        if (progress > 2000) {
                            progress = 2000;
                        }
                        return progress;
                    }

                    @Override
                    public int getLess(int progress) {
                        progress = progress - (2000 / 100);
                        if (progress < 1) {
                            progress = 1;
                        }
                        return progress;
                    }

                    @Override
                    public String showProgress(int progress) {
                        return progress+"HZ";
                    }
                });
        ht660OperateHelper.setProgress(200);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void releaseData() {

    }
}