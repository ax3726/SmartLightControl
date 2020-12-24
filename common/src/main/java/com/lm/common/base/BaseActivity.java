package com.lm.common.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.gyf.immersionbar.ImmersionBar;

public abstract class BaseActivity extends FragmentActivity {
    private static Handler handler = new Handler();
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

    protected void showLoading(String msg) {
        buildProgressDialog(msg);
    }

    protected void closeLoading() {
        cancelProgressDialog();
    }

    protected void checkDialog() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }, 5000);
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

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void onFinish(View view) {
        finish();
    }
}
