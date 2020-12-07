package com.lm.common.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.gyf.immersionbar.ImmersionBar;

public abstract class BaseActivity extends FragmentActivity {

    protected FragmentActivity aty;
    private ProgressDialog progressDialog;

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

    protected void showLoading() {
        buildProgressDialog("loading...");
    }

    protected void closeLoading() {
        cancelProgressDialog();
    }

    /**
     * 加载框
     */
    public void buildProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(aty);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void cancelProgressDialog() {
        if (progressDialog != null)
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
    }

    public void onFinish(View view) {
        finish();
    }
}
