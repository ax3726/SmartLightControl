package com.regenpod.smartlightcontrol.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.regenpod.smartlightcontrol.R;
import com.regenpod.smartlightcontrol.ui.bean.DeviceInfoBean;

/**
 * @auther liming
 * @date 2020-12-29
 * @desc
 */
public class DeviceInfoDialog extends Dialog {
    private Context mContext;
    private TextView tvAddress;
    private TextView tvSoftwareVersion;
    private TextView tvHardwareVersion;
    private TextView tvFactory;
    private TextView tvModel;
    private TextView tvOk;
    private DeviceInfoBean deviceInfoBean;

    public DeviceInfoDialog(@NonNull Context context, DeviceInfoBean deviceInfoBean) {
        super(context, R.style.DialogBaseStyle);
        mContext = context;
        this.deviceInfoBean = deviceInfoBean;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View inflate = View.inflate(getContext(), R.layout.dialog_device_info_layout, null);
        this.setContentView(inflate);
        WindowManager m = ((Activity) mContext).getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.width = (int) ((d.getWidth()) * 0.85);
        params.height = params.height;
        this.getWindow().setAttributes(params);
        initView();
    }

    private void initView() {
        tvAddress = findViewById(R.id.tv_address);
        tvSoftwareVersion = findViewById(R.id.tv_softwareVersion);
        tvHardwareVersion = findViewById(R.id.tv_hardwareVersion);
        tvFactory = findViewById(R.id.tv_factory);
        tvModel = findViewById(R.id.tv_model);
        tvOk = findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        String address = deviceInfoBean.getAddress() == 0 ? "" : deviceInfoBean.getAddress() + "";
        String softwareVersion = deviceInfoBean.getSoftwareVersion() == 0 ? "" : deviceInfoBean.getSoftwareVersion() + "";
        String hardwareVersion = deviceInfoBean.getHardwareVersion() == 0 ? "" : deviceInfoBean.getHardwareVersion() + "";
        String factory = TextUtils.isEmpty(deviceInfoBean.getFactory()) ? "" : deviceInfoBean.getFactory();
        String model = TextUtils.isEmpty(deviceInfoBean.getModel()) ? "" : deviceInfoBean.getModel();
        tvAddress.setText("联机地址:" + address);
        tvSoftwareVersion.setText("软件版本号:" + softwareVersion);
        tvHardwareVersion.setText("硬件版本号:" + hardwareVersion);
        tvFactory.setText("厂家:" + factory);
        tvModel.setText("机型:" + model);
    }


}
