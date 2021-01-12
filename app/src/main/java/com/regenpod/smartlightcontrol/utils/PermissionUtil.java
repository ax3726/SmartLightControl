package com.regenpod.smartlightcontrol.utils;

import android.Manifest;
import android.app.Activity;

import androidx.annotation.NonNull;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

/**
 * @author unknow
 */
public class PermissionUtil {

    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static void requestEachRxPermission(Activity activity, final OnPermissionListener listener) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(PERMISSIONS).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean granted) throws Exception {

                listener.onPermissionResult(granted);
            }
        });

    }

    public interface OnPermissionListener {
        void onPermissionResult(@NonNull Boolean granted);
    }
}
