package com.lm.common.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public abstract class BaseFragment extends Fragment {
    protected Activity aty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(getLayoutId(), container, false);
        aty = getActivity();
        initView(root);
        initData();
        return root;
    }

    protected abstract int getLayoutId();

    protected abstract void initView(View view);

    protected abstract void initData();

    protected abstract void releaseData();

    protected void showToast(String msg) {
        if (aty == null) {
            return;
        }
        Toast.makeText(aty, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        releaseData();
        super.onDestroyView();
    }

}
