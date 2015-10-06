package com.vg.airlinelistapplication.helper;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.vg.airlinelistapplication.R;
import com.vg.airlinelistapplication.storage.AirlineTravelPreferenceHelper;

public class HelperActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        AirlineTravelPreferenceHelper.setAppFirstTimeLaunch(HelperActivity.this, false);
        finish();
        overridePendingTransition(0, android.R.anim.fade_out);
        return true;
    }

}
