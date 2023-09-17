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
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import com.azure.messaging.webpubsub.*;
import com.azure.messaging.webpubsub.models.*;
import com.azure.core.credential.AzureKeyCredential;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
//import javax.websocket.*;

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
    private WebPubSubServiceClient webPubSubServiceClient;
    private WebSocketClient mWebSocketClient;

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

    private void sendEmail() {

    }

    public void startService(Context context) {



//        Intent serviceIntent = new Intent(context, foregroundWork.class);
//        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
//        //ContextCompat.startForegroundService(context, serviceIntent);
//        context.startForegroundService(serviceIntent);
    }
    public void stopService(Context context) {
        //Intent serviceIntent = new Intent(context, foregroundWork.class);
        //context.stopService(serviceIntent);
    }

    private void connectWebSocket() {
        URI uri;
        String url = "wss://loc-1-watch4.webpubsub.azure.com/client/hubs/Hub?access_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJ3c3M6Ly9sb2MtMS13YXRjaDQud2VicHVic3ViLmF6dXJlLmNvbS9jbGllbnQvaHVicy9IdWIiLCJpYXQiOjE2ODg0ODIzNDYsImV4cCI6MTY4ODQ4NTk0Niwicm9sZSI6WyJ3ZWJwdWJzdWIuc2VuZFRvR3JvdXAiLCJ3ZWJwdWJzdWIuam9pbkxlYXZlR3JvdXAiXX0.Jy5O3muO6qyAG48Pbf2KV-ToaIKr3mYsnxZctXRj9_c";
        String url_Test2 = "wss://loc-1-watch4.webpubsub.azure.com/client/hubs/Hub?access_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJ3c3M6Ly9sb2MtMS13YXRjaDQud2VicHVic3ViLmF6dXJlLmNvbS9jbGllbnQvaHVicy9IdWIiLCJpYXQiOjE2ODg2MDg3OTIsImV4cCI6MTY4ODYxMjM5Miwicm9sZSI6WyJ3ZWJwdWJzdWIuc2VuZFRvR3JvdXAiLCJ3ZWJwdWJzdWIuam9pbkxlYXZlR3JvdXAiXSwic3ViIjoiYXBwXzEifQ.3SAcqkBWzp4kv5dK0mdqbiLWynBCoLXrYY01-5cI7nc";
        //String protocol = "json.webpubsub.azure.v1";
        try {
            uri = new URI(url_Test2);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        //WebSocket w = new WebSocketImpl();

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                Log.i("Websocket", mWebSocketClient.getProtocol().getProvidedProtocol());
                //mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
                JSONObject j1 = new JSONObject();
                try {
                    j1.accumulate("type", "joinGrooup");
                    j1.accumulate("group", "Group1");
                    j1.accumulate("ackId", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String jsonString = j1.toString();


                JSONObject j2 = new JSONObject();
                try {
                    j2.accumulate("type", "sendToGroup");
                    j2.accumulate("group", "Group1");
                    j2.accumulate("ackId", 2);
                    j2.accumulate("dataType", "text");
                    j2.accumulate("data", "Hello from watch4");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //String msg = j2.toString();
                //String j13= "{\"type\":\"sendToGroup\",\"group\":\"Group1\",\"ackId\":2,\"dataType\":\"text\",\"data\":\"Hello watch4\"}";
                //mWebSocketClient.send(j13);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TextView textView = (TextView)findViewById(R.id.messages);
                        //textView.setText(textView.getText() + "\n" + message);
                        Log.i("Websocket", "onMessage");
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };

        mWebSocketClient.connect();

        String j12= "{\"type\":\"joinGrooup\",\"group\":\"Group1\",\"ackId\":1}";
        mWebSocketClient.send(j12);
    }
    private void azureWebPubSub(){
        try {
            webPubSubServiceClient = new WebPubSubServiceClientBuilder()
                    .credential(new AzureKeyCredential("9K6Xs8ofNWvnw9MiE4JKzKJnb9Niec73OcQlMn51OHk="))
                    .endpoint("https://loc-1-watch4.webpubsub.azure.com;AccessKey=9K6Xs8ofNWvnw9MiE4JKzKJnb9Niec73OcQlMn51OHk=;Version=1.0;")
                    .hub("Hub")
                    .buildClient();
//            webPubSubServiceClient = new WebPubSubServiceClientBuilder()
//                    .connectionString("{wss://loc-1-watch4.webpubsub.azure.com/client/hubs/Hub?access_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJ3c3M6Ly9sb2MtMS13YXRjaDQud2VicHVic3ViLmF6dXJlLmNvbS9jbGllbnQvaHVicy9IdWIiLCJpYXQiOjE2ODg0MDM0OTAsImV4cCI6MTY4ODQzOTQ5MCwicm9sZSI6WyJ3ZWJwdWJzdWIuc2VuZFRvR3JvdXAiLCJ3ZWJwdWJzdWIuam9pbkxlYXZlR3JvdXAiXSwic3ViIjoidzQifQ.H_ezl0mc13l5TNrkg471CfTfR9Ovmkza3vlW_Z9BqJc}")
//                    .hub("Hub")
//                    .buildClient();

            webPubSubServiceClient.sendToGroup("Group1", "Hello Java!", WebPubSubContentType.TEXT_PLAIN);
            Log.d("RVS_001", "webpubSub message sent");
        } catch (Exception e) {
            Log.d("RVS_001", e.getMessage());
        }
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