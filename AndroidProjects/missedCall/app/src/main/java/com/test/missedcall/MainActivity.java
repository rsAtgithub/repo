package com.test.missedcall;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static int timeToRevert = 30000;
    public static int timeLimit = 1;    //minute
    public static String debugTag = "missedcall2";

    //public static String wifiApnString = "RobotCompleteRobot50";

    public static String[] phoneTrack = new String[3];
    public static AudioManager am;
    public static WifiManager wifiMgr;
    public static android.content.Context appContext = null;

    Button btnStartService, btnStopService;
    Button cb_0, cb_1, cb_2;
    Button about_button;
    EditText[] ph = new EditText[3];

    public WifiReceiver w;
    Switch wifiMonitoring;
    public static boolean isWifiMonitoringEnabled = false;
    public static boolean isWifiMonitoringStarted = false;
    public static String wifiApnString = null;

    public static boolean isServiceStarted = false;

    public static String VersionPrint = "";

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
        wifiMonitoring = findViewById(R.id.wifiMonitor);
        wifiMonitoring.setChecked(isWifiMonitoringEnabled);
        wifiMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean value = wifiMonitoring.isEnabled();
                isWifiMonitoringEnabled = value;
            }
        });

        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
            VersionPrint = "App Version: " + info.getLongVersionCode() + "-" + info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            VersionPrint = "App Version: very poor!";
            e.printStackTrace();
        }

        about_button = findViewById(R.id.about_button);
        about_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(appContext,
                        "Author = Rushikesh S.\n" + VersionPrint, Toast.LENGTH_LONG).show();
            }
        });

        List<String> ssidList = new ArrayList<>();
        /*WifiManager wifiManager = (WifiManager) getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);*/
        //List<WifiConfiguration> configuredList = wifiMgr.getConfiguredNetworks();
        List<ScanResult> configuredList = wifiMgr.getScanResults();
        for(ScanResult config : configuredList) {
            ssidList.add(config.SSID);
        }
        Spinner s = (Spinner) findViewById(R.id.spinner);
        //Prepar adapter
        //HERE YOU CAN ADD ITEMS WHICH COMES FROM SERVER.
        final MyData items[] = new MyData[ssidList.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = new MyData(ssidList.get(i), "value1");
        }
        ArrayAdapter<MyData> adapter = new ArrayAdapter<MyData>(this,
                android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                MyData d = items[position];

                //Get selected value of key
                String value = d.getValue();
                String key = d.getSpinnerText();
                wifiApnString = key;
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    public void startService() {
        boolean enableForegroundForWifi = false;
        boolean enableForegroundForPhone = false;
        String monitoredPhones = "";
        if(isWifiMonitoringEnabled && !isWifiMonitoringStarted) {
            isWifiMonitoringStarted = true;
            w.enable(appContext);
            enableForegroundForWifi = true;
        }
        if(!MainActivity.isServiceStarted) {
            try {
                boolean isAnyPhoneEntered = false;

                for (int i = 0; i < ph.length; i++) {
                    if (phoneTrack[i] == null) {
                        phoneTrack[i] = ph[i].getText().toString();
                    }
                    if (!phoneTrack[i].equals("")) {
                        monitoredPhones += phoneTrack[i] + ";";
                        isAnyPhoneEntered = true;
                    }
                }
                if (isAnyPhoneEntered) {
                    enableForegroundForPhone = true;
                }
            } catch (Exception e) {
                Log.d("MissedCall2", "Exception: " + e.getMessage());
            }
        } else {
            Toast.makeText(MainActivity.appContext, "Service already started...", Toast.LENGTH_SHORT).show();
            Log.i(MainActivity.debugTag, "Service already started...");
        }
        if (enableForegroundForPhone || enableForegroundForWifi) {
            MainActivity.isServiceStarted = true;
            Log.i(MainActivity.debugTag, "Phones to scan: " + monitoredPhones);
            Intent serviceIntent = new Intent(this, ForegroundService.class);
            //serviceIntent.putExtra("inputExtra", "Monitored phones: " + monitoredPhones);
            String notificationString = "";
            if (enableForegroundForPhone) {
                notificationString += "Ph: " + monitoredPhones;
            }
            if (isWifiMonitoringEnabled) {
                notificationString += " WiFi: " + wifiApnString;
            }
            serviceIntent.putExtra("inputExtra", notificationString);
            ContextCompat.startForegroundService(this, serviceIntent);
            MissedCallsContentObserver mcco = new MissedCallsContentObserver(getApplicationContext().getContentResolver());
            getApplicationContext().getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, mcco);
            Log.i(MainActivity.debugTag, "Foreground service started");
        }

    }
    public void stopService() {
        if(MainActivity.isServiceStarted) {
            MainActivity.isServiceStarted = false;
            Intent serviceIntent = new Intent(this, ForegroundService.class);
            stopService(serviceIntent);
            MissedCallsContentObserver mcco = new MissedCallsContentObserver(getApplicationContext().getContentResolver());
            getApplicationContext().getContentResolver().unregisterContentObserver(mcco);
            Log.i(MainActivity.debugTag, "Foreground service stopped");
        }
        if (isWifiMonitoringStarted) {
            w.disable(appContext);
            isWifiMonitoringStarted = false;
        }
    }}