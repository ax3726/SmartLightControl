package com.regenpod.smartlightcontrol.utils;

import android.view.View;
import android.widget.TextView;

public class OperateHelper {
    private TextView tvShow = null;
    private View add = null;
    private View less = null;
    private OperateListener operateListener;
    private int progress;

    public void init(TextView textView, View add, View less, OperateListener operateListener) {
        this.tvShow = textView;
        this.add = add;
        this.less = less;
        this.operateListener = operateListener;
        initView();
    }

    private void initView() {
        add.setOnTouchListener(new LongTouchListener(){
            @Override
            protected void onClick(View view) {
                super.onClick(view);
                progress = operateListener.getAdd(progress);
                tvShow.setText(operateListener.showProgress(progress));
            }
        });
        less.setOnTouchListener(new LongTouchListener(){
            @Override
            protected void onClick(View view) {
                super.onClick(view);
                progress = operateListener.getLess(progress);
                tvShow.setText(operateListener.showProgress(progress));
            }
        });
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        tvShow.setText(operateListener.showProgress(progress));
    }

    public interface OperateListener {
        int getAdd(int progress);

        int getLess(int progress);

        String showProgress(int progress);
    }
}

