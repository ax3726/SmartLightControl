package com.regenpod.smartlightcontrol.utils;

import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @auther liming
 * @date 2021-01-07
 * @desc
 */
public class LongTouchListener implements View.OnTouchListener {
    private boolean isRunning=false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 100) {
                onClick(msg.obj == null ? null : (View) msg.obj);
            }
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            updateAddOrSubtract(v);    //手指按下时触发不停的发送消息
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            stopAddOrSubtract();    //手指抬起时停止发送
        }
        return true;
    }
    private ScheduledExecutorService scheduledExecutor;

    private void updateAddOrSubtract(final View view) {
        isRunning=true;
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (!isRunning) {
                    return;
                }
                Message msg = handler.obtainMessage(100);
                msg.obj = view;
                handler.sendMessage(msg);
            }
        }, 0, 100, TimeUnit.MILLISECONDS);    //每间隔100ms发送Message
    }

    private void stopAddOrSubtract() {
        isRunning=false;
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdownNow();
            scheduledExecutor = null;
        }
    }


    protected void onClick(View view) {

    }


}
