package com.regenpod.smartlightcontrol.ui.main;

import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lm.common.base.BaseActivity;
import com.regenpod.smartlightcontrol.BluetoothHelper;
import com.regenpod.smartlightcontrol.R;
import com.regenpod.smartlightcontrol.ui.dimming.DimmingFragment;
import com.regenpod.smartlightcontrol.ui.pulse.PulseFragment;
import com.regenpod.smartlightcontrol.ui.timer.TimerFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private PulseFragment mPulseFragment;
    private TimerFragment mTimerFragment;
    private DimmingFragment mDimmingFragment;
    private List<Fragment> mFragments = new ArrayList<>();
    private FragmentManager mFm;
    private FragmentTransaction mTransaction;
    private RadioGroup rgBottom;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        rgBottom = findViewById(R.id.rg_bottom);
        rgBottom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_pulse:
                        if (currentFragmentPosition != 0) {
                            changeFragment(0);
                        }
                        break;

                    case R.id.rb_timer:
                        if (currentFragmentPosition != 1) {
                            changeFragment(1);
                        }
                        break;
                    case R.id.rb_dimming:
                        if (currentFragmentPosition != 2) {
                            changeFragment(2);
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void initData() {
        initFragment();
    }

    @Override
    protected void releaseData() {

    }

    private void initFragment() {
        mPulseFragment = new PulseFragment();
        mTimerFragment = new TimerFragment();
        mDimmingFragment = new DimmingFragment();

        mFragments.add(mPulseFragment);
        mFragments.add(mTimerFragment);
        mFragments.add(mDimmingFragment);
        mFm = getSupportFragmentManager();
        mTransaction = mFm.beginTransaction();
        mTransaction.add(R.id.fly_content, mFragments.get(0), "index_0");
        mTransaction.show(mFragments.get(0));
        mTransaction.commitAllowingStateLoss();
    }

    private int currentFragmentPosition = 0;

    public void changeFragment(final int position) {
        mFm = getSupportFragmentManager();
        mTransaction = mFm.beginTransaction();
        if (mFragments.get(position) != null) {
            if (position != currentFragmentPosition) {
                mTransaction.hide(mFragments.get(currentFragmentPosition));
                if (!mFragments.get(position).isAdded()) {
                    mTransaction.add(R.id.fly_content, mFragments.get(position), "index_" + position);
                }
                mTransaction.show(mFragments.get(position));
                mTransaction.commitAllowingStateLoss();
            }
            currentFragmentPosition = position;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothHelper.getInstance().disconnect();
    }
}
