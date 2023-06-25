package com.scania.javabinderclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;

import com.scania.IMyService;
import com.scania.Constants;

import java.util.*;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private static final String PACKAGE_PHONE = "com.android.phone";
    private static final String PACKAGE_TEXT_MESSAGE = "com.android.text_message";
    private static final String PACKAGE_RADIO = "com.android.radio";
    private static final String PACKAGE_MUSIC = "com.android.music";
    private static final String PACKAGE_NAVIGATION_CLIMATE_CONTROL = "com.android.navigation_climate_control";

    private IMyService mService = null;
    private volatile boolean mIsServiceConnected = false;
    private final ConditionVariable mServiceConnectionWaitLock = new ConditionVariable();
    private HashMap<String, Boolean> mMap = new HashMap<>();
    private boolean isEnable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initControls();
    }

    public void initControls() {
        ((CheckBox) findViewById(R.id.phone)).setOnCheckedChangeListener((compoundButton, isChecked) -> {
            Log.d(Constants.LOG_TAG, "PACKAGE_PHONE is checked: isChecked" + isChecked);
            mMap.put(PACKAGE_PHONE, isChecked);
        });
        ((CheckBox) findViewById(R.id.text_message)).setOnCheckedChangeListener((compoundButton, isChecked) -> {
            Log.d(Constants.LOG_TAG, "PACKAGE_TEXT_MESSAGE is checked: isChecked" + isChecked);
            mMap.put(PACKAGE_TEXT_MESSAGE, isChecked);
        });
        ((CheckBox) findViewById(R.id.radio)).setOnCheckedChangeListener((compoundButton, isChecked) -> {
            Log.d(Constants.LOG_TAG, "PACKAGE_PHONE is checked: isChecked" + isChecked);
            mMap.put(PACKAGE_RADIO, isChecked);
        });
        ((CheckBox) findViewById(R.id.music)).setOnCheckedChangeListener((compoundButton, isChecked) -> {
            Log.d(Constants.LOG_TAG, "PACKAGE_MUSIC is checked: isChecked" + isChecked);
            mMap.put(PACKAGE_MUSIC, isChecked);
        });
        ((CheckBox) findViewById(R.id.navigation_climate_control)).setOnCheckedChangeListener((compoundButton, isChecked) -> {
            Log.d(Constants.LOG_TAG, "PACKAGE_NAVIGATION_CLIMATE_CONTROL is checked: isChecked" + isChecked);
            mMap.put(PACKAGE_NAVIGATION_CLIMATE_CONTROL, isChecked);
        });

        ((Button) findViewById(R.id.enable_disable)).setOnClickListener((View v) -> {
            callJNI(null, null, isEnable);
            isEnable = !isEnable;
        });

        ((Button) findViewById(R.id.remove_permission)).setOnClickListener((View v) -> {
            String permission = ((EditText) findViewById(R.id.permission)).getText().toString();
            callJNI(permission, null, false);
        });

        ((Button) findViewById(R.id.set_config)).setOnClickListener((View v) -> {
            String config = ((EditText) findViewById(R.id.config)).getText().toString();
            callJNI(null, config, false);
        });
    }

    private void callJNI(String permission, String config, boolean isEnable) {
        Set<String> keys = mMap.keySet();
        for (Iterator<Map.Entry<String, Boolean>> it = mMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Boolean> entry = it.next();
            if (entry.getValue()) {
                if (permission == null && config == null) {
                    try {
                        Log.d(Constants.LOG_TAG, "[App] [java] IMyService.setEnable");
                        mService.setEnable(entry.getKey(), isEnable);
                    } catch (RemoteException e) {
                        Log.e(Constants.LOG_TAG, "[App] [java] Exception when invoking IMyService.setEnable" + e.getMessage());
                        e.printStackTrace();
                    }
                } else if (permission != null) {
                    try {
                        Log.d(Constants.LOG_TAG, "[App] [java] IMyService.removePermission");
                        mService.removePermission(entry.getKey(), permission);
                    } catch (RemoteException e) {
                        Log.e(Constants.LOG_TAG, "[App] [java] Exception when invoking IMyService.removePermission" + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Log.d(Constants.LOG_TAG, "[App] [java] IMyService.setConfig");
                        mService.setConfig(entry.getKey(), config);
                    } catch (RemoteException e) {
                        Log.e(Constants.LOG_TAG, "[App] [java] Exception when invoking IMyService.setConfig" + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent();
        intent.setClassName("com.scania.ndkbinderservice", "com.scania.ndkbinderservice.MyService");

        Log.d(Constants.LOG_TAG, "[App] [java] bindService");

        bindService(intent, this, BIND_AUTO_CREATE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Not connected to service yet?
                while (!mIsServiceConnected) {
                    mServiceConnectionWaitLock.block(); // waits for service connection
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        unbindService(this);

        mIsServiceConnected = false;

        mService = null;

        Log.d(Constants.LOG_TAG, "[App] [java] unbindService");

        super.onPause();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.d(Constants.LOG_TAG, "[App] [java] onServiceConnected");

        mService = IMyService.Stub.asInterface(iBinder);

        mIsServiceConnected = true;

        mServiceConnectionWaitLock.open(); // breaks service connection waits
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mIsServiceConnected = false;

        mService = null;

        Log.d(Constants.LOG_TAG, "[App] [java] onServiceDisconnected");
    }

}
