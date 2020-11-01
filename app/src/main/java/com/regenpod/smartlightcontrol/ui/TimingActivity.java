package com.regenpod.smartlightcontrol.ui;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.lm.common.base.BaseActivity;
import com.regenpod.smartlightcontrol.R;

public class TimingActivity extends BaseActivity {

    private AppCompatSeekBar skbTiming;
    private EditText etTime;

    private int mTime = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_timing;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        skbTiming = findViewById(R.id.skb_timing);
        etTime = findViewById(R.id.et_time);
    }

    @Override
    protected void initData() {
        skbTiming.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                setTime(progress==0?0:30 * progress/100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        etTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void onAddTime(View view) {
        if (mTime == 30) {
            return;
        }
        mTime++;
        setTime(mTime);
    }

    public void onCutTime(View view) {
        if (mTime == 0) {
            return;
        }
        mTime--;
        setTime(mTime);
    }

    private void setTime(int time) {
        etTime.setText(String.valueOf(time));
    }

    @Override
    protected void releaseData() {

    }
}
