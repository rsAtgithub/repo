package com.example.phonelocation_1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.java_websocket.client.WebSocketClient;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * reference: https://codingwithmitch.com/blog/work-manager-getting-started/
 * https://stackoverflow.com/questions/60155165/how-do-i-import-androidx-work-workmanager
 * https://stackoverflow.com/a/52252834
 */
public class myWork extends Worker {

    private LocationManager locationManager;
    private int noOfReads= 0;
    private SecWebSocketProtocolClientExample sec;

    private Timer myTimer;

    private Context thisContext;

    private String connectionString;

    public myWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        if (thisContext != null) {
            thisContext = context;

            //sendEmail();

            //WebSocketUsage(context);
        } else {
            Log.d("RVS_001", "WTF");
        }
    }

    private void WebSocketUsage(@NonNull Context context) {
        try {
            WebSocketUrlGeneration ws = new WebSocketUrlGeneration("Phone_1", context.getString(R.string.pubsub_secondary_key));
            connectionString = ws.run();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        checkPermissions(context);
        getLocationReaders(context);
        try {
            sec = new SecWebSocketProtocolClientExample(connectionString);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }
        }, 0, MainActivity.updateTimeInMilliSeconds);
    }

    @SuppressLint("MissingPermission")
    private void locationReader(){
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
        if (source == null) {
            String fail = String.format("[%04d]: No Location", noOfReads);
            //t.setText(fail);
        } else {
            String okay = String.format("[%04d]: [Src]: %s , [Coord]: %s ", noOfReads, source, coordinates);
            //t.setText(okay);
        }
        if (sec != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy:HH:mm:ss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            String msg = String.format("[%s]: %s", currentDateandTime, googleLink);
            sec.sendText(msg);
        }
    }

    void getLocationReaders(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // do proper permissions handling
            return;
        }
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gpsEnabled) {
            List<String> l = locationManager.getAllProviders();
            Log.d("RVS_001", "Providers: " + l.toString());
        }
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

    @NonNull
    @Override
    public Result doWork() {
        //Context context = getApplicationContext();

        //locationReader();
        return Result.success();
    }

    private void TimerMethod() {
        if (sec.isConnectionEstablished()) {
            noOfReads++;
            Log.d("RVS_001", "Timer Event");
            locationReader();
        } else {
            Log.d("RVS_001", "Timer Event, but no active connection");
        }
        /*else {
            try {
                Log.d("RVS_001", "New Connection");
                sec = new SecWebSocketProtocolClientExample(connectionString);
            } catch (URISyntaxException e) {
                Log.d("RVS_001", "Exception: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }*/
    }
}
