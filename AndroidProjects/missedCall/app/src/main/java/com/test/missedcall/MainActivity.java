package com.test.missedcall;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
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

    public static int timeToRevert = 10000;
    public static String debugTag = "missedcall2";

    public static String[] phoneTrack = new String[3];
    public static AudioManager am;
    public static android.content.Context appContext = null;

    Button btnStartService, btnStopService;
    Button cb_0, cb_1, cb_2;
    EditText[] ph = new EditText[3];

    public static boolean isServiceStarted = false;

    /*private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    Log.i(MainActivity.debugTag, "Permission is granted");
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    Log.i(MainActivity.debugTag, "Permission not granted");
                }
            });*/
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALL_LOG},
                    200);
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        appContext = this;

        checkPermission();

        /*
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_CALL_LOG) ==
                PackageManager.PERMISSION_DENIED) {
            // You can use the API that requires the permission.
            requestPermissionLauncher.launch(
                    Manifest.permission.READ_CALL_LOG);
        }
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED) {
            // You can use the API that requires the permission.
            requestPermissionLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }*/

        //final File externalFilesDir = this.getExternalFilesDir(null);
        //filewriter.writeFileExternalStorage(externalFilesDir);

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
    }}