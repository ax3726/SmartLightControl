package com.regenpod.smartlightcontrol.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lm.common.base.BaseActivity;
import com.regenpod.smartlightcontrol.R;

public class ConnectActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_connect;
    }

    /**
     * 搜索并连接蓝牙
     *
     * @param view
     */
    public void searchAndConnect(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void releaseData() {

    }
}
