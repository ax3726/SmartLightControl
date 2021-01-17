package com.regenpod.smartlightcontrol.utils;

import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

/**
 * @auther liming
 * @date 2021-01-07
 * @desc
 */
public class LongTouchListener implements View.OnTouchListener {
    private boolean isRunning = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            if (msg.what == 100) {
                if (isRunning) {
                    postDelayed(runnable, 100);
                } else {
                    removeCallbacks(runnable);
                }
            }
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            onClick(null);
            handler.sendEmptyMessage(100);
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


    private void updateAddOrSubtract(final View view) {
        isRunning = true;
        Message msg = handler.obtainMessage(100);
        msg.obj = view;
        handler.sendMessage(msg);
        onClick(null);

    }

    private void stopAddOrSubtract() {
        isRunning = false;
        handler.removeCallbacks(runnable);
    }


    protected void onClick(View view) {

    }


}
