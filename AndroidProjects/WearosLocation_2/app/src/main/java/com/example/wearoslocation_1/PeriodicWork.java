package com.example.wearoslocation_1;

import static com.example.wearoslocation_1.MainActivity.sendEmail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.wearoslocation_1.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PeriodicWork extends Worker {

    private Context context;
    private LocationManager locationManager;

    private int noOfReads= 0;

    public PeriodicWork(@NonNull Context parentContext, @NonNull WorkerParameters workerParams) {
        super(parentContext, workerParams);
        context = parentContext;
        getLocationReaders(context);
        checkPermissions(context);

    }

    @NonNull
    @Override
    public Result doWork() {
        checkPower();
        sendEmail(locationReader());
        return Result.success();
    }

    void getLocationReaders(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // do proper permissions handling
            Log.d("RVS_001", "Permission not available");
            return;
        }
        Log.d("RVS_001", "Reading providers");
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gpsEnabled) {
            List<String> l = locationManager.getAllProviders();
            Log.d("RVS_001", "Providers: " + l.toString());
        } else {
            Log.d("RVS_001", "GPS disabled");
        }
    }

    void checkPower() {
        String PACKAGE_NAME = getApplicationContext().getPackageName();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean status = false;
        status = pm.isIgnoringBatteryOptimizations(PACKAGE_NAME);
        if (!status) {
            Log.d("RVS_001", "PM no exception");
        } else {
            Log.d("RVS_001", "PM exception");
        }
    }

    String getWifiConnection() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo;
        String ssid = null;

        wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            ssid = wifiInfo.getSSID();
        }
        return ssid;
    }

    void checkPermissions(Context CONTEXT) {
         if (ContextCompat.checkSelfPermission(
                CONTEXT, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            Log.d("RVS_001", "Location permission available");
        } else {
            Log.d("RVS_001", "Location permission NOT available");
        }
    }

    @SuppressLint("MissingPermission")
    private String locationReader(){
        // Or get last known location
        String source = null;
        String coordinates = null;
        double lat = 0;
        double lon = 0;
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            lat = location.getLatitude();
            lon = location.getLongitude();
            String message = String.format("lat: %f, lon: %f", lat, lon);
            Log.d("RVS_001", "GPS: " + message);
            source = "GPS";
            coordinates = message;
        } else {
            Log.d("RVS_001", "Last known location not found on GPS");
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                String message = String.format("lat: %f, lon: %f", lat, lon);
                Log.d("RVS_001", "NETWORK: " + message);
                source = "NETWORK";
                coordinates = message;
            } else {
                Log.d("RVS_001", "Last known location not found on NETWORK");
            }
        }
        String googleLink = String.format("https://maps.google.com/?q=%f,%f", (float)lat, (float)lon);
        Log.d("RVS_001", googleLink);
        String additionalMessage;
        if (source == null) {
            additionalMessage = String.format("[%04d]: No Location", noOfReads);
            //t.setText(fail);
        } else {
            additionalMessage = String.format("[%04d]: [Src]: %s , [Coord]: %s ", noOfReads, source, coordinates);
            //t.setText(okay);
        }

        String ssid = getWifiConnection();
        String ssidMessage = "no WiFi Connection";
        if (ssid != null) {
            ssidMessage = String.format("WiFi connected to %s", ssid);
        }

        String batteryState = getBatteryPercentage(context);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy:HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        String msg = String.format("[%s]: %s, \n%s\n%s\n%s", currentDateandTime, googleLink, additionalMessage, ssidMessage, batteryState);

        return msg;

    }

    //Ref: https://stackoverflow.com/a/75036492
    public static String getBatteryPercentage(Context context)
    {
        String bstatus       = "isChrg=false usbChrg=false acChrg=false wlChrg=false 0% t=70°F";
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);
        int status           = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging   = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        int level            = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale            = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct     = level * 100 / (float)scale;
        //How are we charging?
        int chargePlug       = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge    = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge     = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        boolean wlCharge     = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS;
        int temp             = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
        float tempTwo        = ((float) temp) / 10;
        double d             = tempTwo; //CelsiusToFahrenheit(tempTwo);
        bstatus              = String.format(Locale.US, "isChrg=%b usbChrg=%b acChrg=%b wlChrg=%b %.0f%% t=%.2f°C",
                isCharging,
                usbCharge,
                acCharge,
                wlCharge,
                batteryPct,
                d);
        return bstatus;
    }
}
