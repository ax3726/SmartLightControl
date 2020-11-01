package com.regenpod.smartlightcontrol.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lm.common.base.BaseActivity;
import com.regenpod.smartlightcontrol.R;

public class MainActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
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

    public void onTiming(View view) {
        startActivity(new Intent(this, TimingActivity.class));
    }
    public void onDimming(View view) {
        startActivity(new Intent(this, DimmingActivity.class));
    }
    public void onPulse(View view) {
        startActivity(new Intent(this, PulseActivity.class));
    }
}
