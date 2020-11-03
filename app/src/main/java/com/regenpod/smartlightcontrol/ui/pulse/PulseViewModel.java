package com.regenpod.smartlightcontrol.ui.pulse;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PulseViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PulseViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}