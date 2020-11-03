package com.regenpod.smartlightcontrol.ui.dimming;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.regenpod.smartlightcontrol.R;

public class DimmingFragment extends Fragment {

    private DimmingViewModel dimmingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dimmingViewModel =
                ViewModelProviders.of(this).get(DimmingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dimming, container, false);
        dimmingViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }
}