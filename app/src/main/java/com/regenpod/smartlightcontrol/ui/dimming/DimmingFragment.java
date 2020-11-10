package com.regenpod.smartlightcontrol.ui.dimming;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.lm.common.adapter.BaseCommonViewHolder;
import com.lm.common.base.BaseFragment;
import com.regenpod.smartlightcontrol.R;
import com.regenpod.smartlightcontrol.utils.OperateHelper;

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
                int dm660Progress = dm660OperateHelper.getProgress();
                int dm850Progress = dm850OperateHelper.getProgress();
                StringBuilder sb = new StringBuilder();
                sb.append("dm660:" + dm660Progress);
                sb.append("\ndm850:" + dm850Progress);
                Toast.makeText(aty, sb.toString(), Toast.LENGTH_SHORT).show();
            }
        });
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
        dm660OperateHelper.setProgress(20);
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
        dm850OperateHelper.setProgress(30);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void releaseData() {

    }
}