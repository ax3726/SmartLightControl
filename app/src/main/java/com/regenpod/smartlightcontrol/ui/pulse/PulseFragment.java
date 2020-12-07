package com.regenpod.smartlightcontrol.ui.pulse;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import com.clj.fastble.utils.HexUtil;
import com.lm.common.adapter.BaseCommonViewHolder;
import com.lm.common.base.BaseFragment;
import com.regenpod.smartlightcontrol.BluetoothHelper;
import com.regenpod.smartlightcontrol.R;
import com.regenpod.smartlightcontrol.utils.OperateHelper;


public class PulseFragment extends BaseFragment {

    private BaseCommonViewHolder baseCommonViewHolder;
    private PulseViewModel pulseViewModel;
    private OperateHelper ht660OperateHelper;
    private OperateHelper ht850OperateHelper;
    private OperateHelper dc660OperateHelper;
    private OperateHelper dc850OperateHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pulse;
    }

    @Override
    protected void initView(View view) {
        baseCommonViewHolder = new BaseCommonViewHolder(view);
        pulseViewModel =
                ViewModelProviders.of(this).get(PulseViewModel.class);

        initHt660();
        initHt850();
        initDc660();
        initDc850();
        baseCommonViewHolder.setOnClickListener(R.id.img_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ht660Progress = ht660OperateHelper.getProgress();
                int ht850Progress = ht850OperateHelper.getProgress();
                int dc660Progress = dc660OperateHelper.getProgress();
                int dc850Progress = dc850OperateHelper.getProgress();


                StringBuilder sb = new StringBuilder();
                sb.append("ht660:" + ht660Progress);
                sb.append("ht850:" + ht850Progress);
                sb.append("dc660:" + dc660Progress);
                sb.append("dc850:" + dc850Progress);
                for (int i = 0; i < 20; i++) {
                    BluetoothHelper.getInstance().senMessage(HexUtil.hexStringToBytes("FB010203"));
                }

                Toast.makeText(aty, "设置成功！", Toast.LENGTH_SHORT).show();
            }


        });
    }




    public byte[] encrypt(byte[] bytes, int key) {
        if (bytes == null) {
            return null;
        }
        int len = bytes.length;

        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) (bytes[i] ^ key);
            key = bytes[i];
        }
        return bytes;
    }

    public byte[] decrypt(byte[] bytes, int key) {
        if (bytes == null) {
            return null;
        }
        int len = bytes.length;
        for (int i = len - 1; i > 0; i--) {
            bytes[i] = (byte) (bytes[i] ^ bytes[i - 1]);
        }
        bytes[0] = (byte) (bytes[0] ^ key);
        return bytes;
    }

    private void initHt660() {
        ht660OperateHelper = new OperateHelper();
        ht660OperateHelper.init(baseCommonViewHolder.getTextView(R.id.tv_ht_660),
                baseCommonViewHolder.getView(R.id.img_ht_add_660),
                baseCommonViewHolder.getView(R.id.img_ht_less_660),
                new OperateHelper.OperateListener() {
                    @Override
                    public int getAdd(int progress) {
                        progress = progress + 50;
                        if (progress > 2000) {
                            progress = 2000;
                        }
                        return progress;
                    }

                    @Override
                    public int getLess(int progress) {
                        progress = progress - 50;
                        if (progress < 1) {
                            progress = 1;
                        }
                        return progress;
                    }

                    @Override
                    public String showProgress(int progress) {
                        return progress + "HZ";
                    }
                });
        ht660OperateHelper.setProgress(200);
    }

    private void initHt850() {
        ht850OperateHelper = new OperateHelper();
        ht850OperateHelper.init(baseCommonViewHolder.getTextView(R.id.tv_ht_850),
                baseCommonViewHolder.getView(R.id.img_ht_add_850),
                baseCommonViewHolder.getView(R.id.img_ht_less_850),
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
                        return progress + "HZ";
                    }
                });
        ht850OperateHelper.setProgress(100);
    }

    private void initDc660() {
        dc660OperateHelper = new OperateHelper();
        dc660OperateHelper.init(baseCommonViewHolder.getTextView(R.id.tv_dc_660),
                baseCommonViewHolder.getView(R.id.img_dc_add_660),
                baseCommonViewHolder.getView(R.id.img_dc_less_660),
                new OperateHelper.OperateListener() {
                    @Override
                    public int getAdd(int progress) {
                        progress = progress + 1;
                        if (progress > 80) {
                            progress = 80;
                        }
                        return progress;
                    }

                    @Override
                    public int getLess(int progress) {
                        progress = progress - 1;
                        if (progress < 1) {
                            progress = 1;
                        }
                        return progress;
                    }

                    @Override
                    public String showProgress(int progress) {
                        return progress + "%";
                    }
                });
        dc660OperateHelper.setProgress(20);
    }


    private void initDc850() {
        dc850OperateHelper = new OperateHelper();
        dc850OperateHelper.init(baseCommonViewHolder.getTextView(R.id.tv_dc_850),
                baseCommonViewHolder.getView(R.id.img_dc_add_850),
                baseCommonViewHolder.getView(R.id.img_dc_less_850),
                new OperateHelper.OperateListener() {
                    @Override
                    public int getAdd(int progress) {
                        progress = progress + 1;
                        if (progress > 80) {
                            progress = 80;
                        }
                        return progress;
                    }

                    @Override
                    public int getLess(int progress) {
                        progress = progress - 1;
                        if (progress < 1) {
                            progress = 1;
                        }
                        return progress;
                    }

                    @Override
                    public String showProgress(int progress) {
                        return progress + "%";
                    }
                });
        dc850OperateHelper.setProgress(30);
    }


    @Override
    protected void initData() {

    }

    @Override
    protected void releaseData() {

    }
}