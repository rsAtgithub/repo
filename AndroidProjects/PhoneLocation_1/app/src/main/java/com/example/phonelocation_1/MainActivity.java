package com.example.phonelocation_1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.phonelocation_1.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity implements android.location.LocationListener{

    private AppBarConfiguration appBarConfiguration;
    private Timer myTimer;

    private int noOfReads = 0;
    private LocationManager locationManager;
    private ActivityMainBinding binding;
    private WebSocketClient mWebSocketClient;

    private boolean connectedWebSocket = false;

    private SecWebSocketProtocolClientExample sec;

    private TextView t;

    private int updateTimeInMilliSeconds = 30000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     binding = ActivityMainBinding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        String gpsCheck;
        if (!hasGps()) {
            gpsCheck = "  No GPS!!";
        } else {
            gpsCheck = "  GPS supported!!";
        }
        Log.d("RVS_001", gpsCheck);

        t = binding.getRoot().findViewById(R.id.textview_first);
        checkPermissions(binding.getRoot().getContext());
        startLocationRequest(binding.getRoot().getContext());

        try {
            sec = new SecWebSocketProtocolClientExample();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }
        }, 0, updateTimeInMilliSeconds);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void TimerMethod() {
        this.runOnUiThread(Timer_Tick);
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
            t.setText(fail);
        } else {
            String okay = String.format("[%04d]: [Src]: %s , [Coord]: %s ", noOfReads, source, coordinates);
            t.setText(okay);
        }
        if (sec != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy:HH:mm:ss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            String msg = String.format("[%s]: %s", currentDateandTime, googleLink);
            sec.sendText(msg);
        }
    }

    private Runnable Timer_Tick = new Runnable() {
        @Override
        public void run() {
            noOfReads++;
            Log.d("RVS_001", "Timer Event");
            locationReader();
        }
    };

    private boolean hasGps() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startLocationRequest(Context context) {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        getLocation(context);
        Log.d("RVS_001", "endo of startLocationRequest");
    }

    private void checkPermissions(Context CONTEXT) {
        if (ContextCompat.checkSelfPermission(
                CONTEXT, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            Log.d("RVS_001", "Location permission available");
        } else {
            Log.d("RVS_001", "Location permission NOT available");
        }
    }

    private void getLocation(Context context) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // do proper permissions handling
            return;
        }
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gpsEnabled) {
            // Register for location updates
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0.0f, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000, 1, this);

            List<String> l = locationManager.getAllProviders();
            Log.d("RVS_001", "Providers: " + l.toString());

            locationReader();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("RVS_001", "onLocationChanged-2");
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        android.location.LocationListener.super.onLocationChanged(locations);
        Log.d("RVS_001", "onLocationChanged-1");
    }

    @Override
    public void onFlushComplete(int requestCode) {
        android.location.LocationListener.super.onFlushComplete(requestCode);
        Log.d("RVS_001", "onFlushComplete");
    }

    public void onProviderEnabled(String provider) {
        Log.d("RVS_001", "onProviderEnabled");
    }
    public void onProviderDisabled(String provider) {
        Log.d("RVS_001", "onProviderDisabled");
    }
}