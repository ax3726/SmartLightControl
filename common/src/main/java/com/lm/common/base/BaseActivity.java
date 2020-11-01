package com.lm.common.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.gyf.immersionbar.ImmersionBar;

public abstract class BaseActivity extends FragmentActivity {

    protected FragmentActivity aty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        aty = this;
        initImmersionBar();
        initView(savedInstanceState);
        initData();
    }

    private void initImmersionBar() {
        ImmersionBar.with(this)
                .transparentStatusBar()
                .init();
    }

    protected abstract int getLayoutId();

    protected abstract void initView(Bundle savedInstanceState);

    protected abstract void initData();

    protected abstract void releaseData();

    @Override
    protected void onDestroy() {
        releaseData();
        super.onDestroy();
    }

    public void onFinish(View view) {
        finish();
    }
}
