package com.example.wearoslocation_1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.renderscript.RenderScript;
import android.text.Html;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
//import com.google.android.gms.location.*;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.wearoslocation_1.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.json.JSONException;
import org.json.JSONObject;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
//import javax.websocket.*;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends Activity implements android.location.LocationListener {

    private TextView mTextView;
    private ActivityMainBinding binding;
    //private LocationCallback locationCallback;
    private boolean requestingLocationUpdates;
    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    //private FusedLocationProviderClient fusedLocationClient;
    private Timer myTimer;
    private LocationManager locationManager;
    private int noOfReads = 0;

    private PeriodicWorkRequest mPeriodicWorkRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateValuesFromBundle(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String gpsCheck;
        if (!hasGps()) {
            gpsCheck = "  No GPS!!";
        } else {
            gpsCheck = "  GPS supported!!";
        }

        binding.text.append(gpsCheck);

        mTextView = binding.text;

        checkPermissions(binding.getRoot().getContext());
        startLocationRequest(binding.getRoot().getContext());

        binding.buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(binding.getRoot().getContext());
            }
        });

        binding.buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(binding.getRoot().getContext());
            }
        });

//        myTimer = new Timer();
//        myTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                TimerMethod();
//            }
//        }, 0, 15000);

        //final Intent shareIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mainto:"));
        //email();
        //azureWebPubSub();
        //connectWebSocket();
    }

    public static void sendEmail() {


        try {
            String stringSenderEmaiI = "esamparka@gmail.com" ;
            String stringReceiverEmaiI = "shingnapurkar@gmail.com";
            String stringPasswordSenderEmai1 = "qhahasedrgplwztl";
            String stringHost = "smtp.gmail.com";
            Properties properties = System. getProperties();

            properties.put("mail.smtp.host", stringHost);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(stringSenderEmaiI, stringPasswordSenderEmai1);
                }
            });

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(stringReceiverEmaiI)));

            mimeMessage.setSubject("Subject: Test email");
            mimeMessage.setText("Hello world!");

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                        Log.d("RVS_001", "email sent...");
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            thread.start();

        } catch (AddressException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public void startService(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        mPeriodicWorkRequest = new PeriodicWorkRequest.Builder(PeriodicWork.class,
                16, TimeUnit.MINUTES).addTag("periodicWorkRequest").build();

        WorkManager.getInstance(context).enqueue(mPeriodicWorkRequest);

        Log.d("RVS_001", "Queue begins");
        //sendEmail();


//        Intent serviceIntent = new Intent(context, foregroundWork.class);
//        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
//        //ContextCompat.startForegroundService(context, serviceIntent);
//        context.startForegroundService(serviceIntent);
    }
    public void stopService(Context context) {
        //Intent serviceIntent = new Intent(context, foregroundWork.class);
        //context.stopService(serviceIntent);

        UUID getId = mPeriodicWorkRequest.getId();
        WorkManager.getInstance(context).cancelWorkById(getId);

        Log.d("RVS_001", "Queue ends");
    }



    private void email(){
        String mailId="shingnapurkar@gmail.com";
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                Uri.fromParts("mailto",mailId, null));

        emailIntent.setType("message/rfc822");

        /*emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Location update");
// you can use simple text like this
// emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,"Body text here");
// or get fancy with HTML like this
        emailIntent.putExtra(
                Intent.EXTRA_TEXT,
                Html.fromHtml(new StringBuilder()
                        .append("<p><b>Some Content</b></p>")
                        .append("<a>http://www.google.com</a>")
                        .append("<small><p>More content</p></small>")
                        .toString())
        );*/
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
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
        if (source != null) {
            String fail = String.format("[%04d]: No Location", noOfReads);
            binding.text.setText(fail);
        } else {
            String okay = String.format("[%04d]: [Src]: %s , [Coord]: %s ", noOfReads, source, coordinates);
            binding.text.setText(okay);
        }
        mTextView = binding.text;
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

    private void startLocationRequest(Context context) {
        //new LocationCallBack
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        /*
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d("RVS_001", "Location received");
                }
            }
        };*/


        getLocation(context);
        Log.d("RVS_001", "endo of startLocationRequest");
    }

    /*
    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }*/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                requestingLocationUpdates);
        // ...
        super.onSaveInstanceState(outState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                    REQUESTING_LOCATION_UPDATES_KEY);
        }

        // ...

        // Update UI to match restored state
        //updateUI();
    }


//    protected LocationRequest createLocationRequest() {
//        LocationRequest locationRequest = null;
//        try {
//            locationRequest = new LocationRequest.Builder(1000)
//                    .setDurationMillis(1000)
//                    .setIntervalMillis(1000)
//                    .setMaxUpdates(1)
//                    .build();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Log.d("RVS_001", "Location request generated");
//        return locationRequest;
//    }

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