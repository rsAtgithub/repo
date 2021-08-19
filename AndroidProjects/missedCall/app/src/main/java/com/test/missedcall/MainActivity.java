package com.test.missedcall;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    public static int timeToRevert = 30000;
    public static String debugTag = "missedcall2";

    public static String wifiApnString = "RobotCompleteRobot50";

    public static String[] phoneTrack = new String[3];
    public static AudioManager am;
    public static WifiManager wifiMgr;
    public static android.content.Context appContext = null;

    Button btnStartService, btnStopService;
    Button cb_0, cb_1, cb_2;
    EditText[] ph = new EditText[3];

    public WifiReceiver w;

    public static boolean isServiceStarted = false;

    private void requestPermissions(String permission){
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    200);
        }
    }

    private void checkPermission() {
        requestPermissions(Manifest.permission.READ_CALL_LOG);
        requestPermissions(Manifest.permission.ACCESS_NETWORK_STATE);
        requestPermissions(Manifest.permission.ACCESS_WIFI_STATE);
        requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION);
        requestPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
        requestPermissions(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        appContext = this;

        checkPermission();
        wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        try{
            w = new WifiReceiver();
        } catch (Exception e){
            Log.i(MainActivity.debugTag, "Exception: " + e.getMessage());
        }

        btnStartService = findViewById(R.id.buttonStartService);
        btnStopService = findViewById(R.id.buttonStopService);
        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService();
            }
        });
        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
            }
        });
        ph[0] = findViewById(R.id.editTextPhone0);
        ph[1] = findViewById(R.id.editTextPhone1);
        ph[2] = findViewById(R.id.editTextPhone2);
        for (int i = 0; i < ph.length; i++) {
            if (phoneTrack[i] != null){
                ph[i].setText(phoneTrack[i]);
            }
        }
        cb_0 = findViewById(R.id.cb_0);
        cb_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ph[0].setText("");
                phoneTrack[0] = null;
            }
        });
        cb_1 = findViewById(R.id.cb_1);
        cb_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ph[1].setText("");
                phoneTrack[1] = null;
            }
        });
        cb_2 = findViewById(R.id.cb_2);
        cb_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ph[2].setText("");
                phoneTrack[2] = null;
            }
        });
    }
    public void startService() {
        w.enable(appContext);
        if(!MainActivity.isServiceStarted) {
            try {
                boolean isAnyPhoneEntered = false;
                String monitoredPhones = "";
                for (int i = 0; i < ph.length; i++) {
                    if (phoneTrack[i] == null) {
                        phoneTrack[i] = ph[i].getText().toString();
                    }
                    if (!phoneTrack[i].equals("")) {
                        monitoredPhones += phoneTrack[i] + ";";
                        isAnyPhoneEntered = true;
                    }
                }
                if (!isAnyPhoneEntered) {
                    Toast.makeText(MainActivity.appContext, "No phones to monitor, not starting the service", Toast.LENGTH_SHORT).show();
                    Log.i(MainActivity.debugTag, "Foreground service NOT started");
                    return;
                } else {
                    Log.i(MainActivity.debugTag, "Phones to scan: " + monitoredPhones);
                    Intent serviceIntent = new Intent(this, ForegroundService.class);
                    serviceIntent.putExtra("inputExtra", "Monitored phones: " + monitoredPhones);
                    ContextCompat.startForegroundService(this, serviceIntent);
                    MissedCallsContentObserver mcco = new MissedCallsContentObserver(getApplicationContext().getContentResolver());
                    getApplicationContext().getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, mcco);
                }
            } catch (Exception e) {
                Log.d("MissedCall2", "Exception: " + e.getMessage());
            }
            Log.i(MainActivity.debugTag, "Foreground service started");
            MainActivity.isServiceStarted = true;
        } else {
            Toast.makeText(MainActivity.appContext, "Service already started...", Toast.LENGTH_SHORT).show();
            Log.i(MainActivity.debugTag, "Service already started...");
        }
    }
    public void stopService() {
        MainActivity.isServiceStarted = false;
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
        MissedCallsContentObserver mcco = new MissedCallsContentObserver(getApplicationContext().getContentResolver());
        getApplicationContext().getContentResolver().unregisterContentObserver(mcco);
        Log.i(MainActivity.debugTag, "Foreground service stopped");
        w.disable(appContext);
    }}