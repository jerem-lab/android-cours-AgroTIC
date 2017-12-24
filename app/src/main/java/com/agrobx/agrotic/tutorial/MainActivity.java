package com.agrobx.agrotic.tutorial;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String welcome_text = " Welcome to my amazing app !";
    private int lifeCycleTest = -1;
    boolean actionEnabled = true;
    private ActionMode mActionMode;


    @Override
    public void onClick(View _buttonView) {
        if (_buttonView.getId() == R.id.validateButtonId) {
            Log.i("myTag", "click !!");
//            Lancement simple
//            Intent intent = new Intent(this, SecondaryActivity.class);
//            super.startActivity(intent);


//            // Lancement interne avec données (interne)
//            Intent intent = new Intent(this, SecondaryActivity.class);
//            intent.putExtra("WelcomeTextKey", welcome_text);
//            startActivity(intent);

            // Lancement avec attente d'un résultat
            Intent intent = new Intent(this, SecondaryActivity.class);
            intent.putExtra("WelcomeTextKey", welcome_text);
            startActivityForResult(intent, REQUEST_CODE);

        } else if (_buttonView.getId() == R.id.framaButtonId) {
            String url = "https://framasoft.org/";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } else if (_buttonView.getId() == R.id.photoButtonId) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            // creation de notre propre action avec un message
        } else if (_buttonView.getId() == R.id.myactionId) {
            // creation de notre propre action avec un message
            Intent intent = new Intent();
            intent.setAction("agrobx.agrotic.tutorial.MyAction");

            intent.putExtra("name", "texte de mon action");
            sendBroadcast(intent);
        } else if (_buttonView.getId() == R.id.mynotificationId) {
            setNotification();
        } else if (_buttonView.getId() == R.id.itembarId) {
            Log.i("MyTag", "click activity as listener !!");
            actionEnabled = !actionEnabled;
            invalidateOptionsMenu();
        } else if (_buttonView.getId() == R.id.SensorId) {
            Log.i("SENSOR", "click activity as listener !!");
//            listSensors();
            listLocationProviders();

        } else if (_buttonView.getId() == R.id.urlId) {
            final WebView webView = (WebView) findViewById(R.id.webViewId);
            webView.setWebViewClient(new WebViewClient()
            {
                public boolean shouldOverrideUrlLoading(WebView viewx, String urlx)
                {
                    return false;
                }
            });
            NetAsyncTask netAsyncTask=new NetAsyncTask();
            netAsyncTask.setDownloadListener(new NetAsyncTask.DownloadListener()
            {
                @Override
                public void onLoadFinished(String result)
                {
                    webView.getSettings().setJavaScriptEnabled(true);
                    String baseUrl="http://www.google.fr";
                    webView.loadDataWithBaseURL(baseUrl,result,"text/html","UTF-8","about:blank");
                }
            });
            if(netAsyncTask.checkConnectivity(this)==true)
            {
                netAsyncTask.execute();
            }        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_button);

        askLocationPermissions();

//            TextView textView = (TextView) findViewById(R.id.textViewid); // permet de recuperer une variable dans les ressources et de la modifier
//            textView.setText("Ma première textview !");

        Resources res = getResources();
        String[] strings = res.getStringArray(R.array.testStringArray);
        for (String s : strings) {
            Log.i("MyTag", s);
        }

        Button btn = (Button) findViewById(R.id.validateButtonId);
        btn.setOnClickListener(this); // methode 1 qui va faire appel à onClick ligne 13

        Button btn_frama = (Button) findViewById(R.id.framaButtonId);
        btn_frama.setOnClickListener(this);

        Button btn_photo = (Button) findViewById(R.id.photoButtonId);
        btn_photo.setOnClickListener(this);

        Button btn_myaction = (Button) findViewById(R.id.myactionId);
        btn_myaction.setOnClickListener(this);

        Button btn_mynotif = (Button) findViewById(R.id.mynotificationId);
        btn_mynotif.setOnClickListener(this);

        Button btn_myitembar = (Button) findViewById(R.id.itembarId);
        btn_myitembar.setOnClickListener(this);

        Button btn_sensor = (Button) findViewById(R.id.SensorId);
        btn_sensor.setOnClickListener(this);

        Button btn_url = (Button) findViewById(R.id.urlId);
        btn_url.setOnClickListener(this);


        // methode 2
//            btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.i("myTag", "click 2 !!");
//                }
//            });

//        Log.d("MyTag", "zbraaaaaaaa !");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);

        TextView textView = (TextView) findViewById(R.id.textViewid); // permet de recuperer une variable dans notre classe application
        textView.setText(((MyApplication) getApplication()).getLongLifeTest());

        // on utilise la nouvelle version de l'action bar : la toolbar
        final Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(topToolBar);

        getSupportActionBar().setLogo(R.drawable.logo_agrobx);


        final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_context_text, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_remove: {
                        TextView textView = (TextView) findViewById(R.id.textViewid);
                        textView.setText("action mode  is  alive !");

                        mode.finish();
                        return true;

                    }
                    case R.id.action_share: {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        TextView textView = (TextView) findViewById(R.id.textViewid);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, textView.getText().toString());
                        shareIntent.setType("text/*");
                        startActivity(Intent.createChooser(shareIntent, "share"));

                        mode.finish();
                        return true;

                    }
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;
            }
        };

        textView.setOnLongClickListener(new View.OnLongClickListener() {
                                            @Override
                                            public boolean onLongClick(View v) {
                                                if (mActionMode != null) {
                                                    return false;
                                                }
                                                mActionMode = MainActivity.this.startSupportActionMode(mActionModeCallback);
                                                v.setSelected(true);
                                                return true;
                                            }
                                        }
        );

    }


    public int notificationCount = 1;

    // notif simple avec un intent web
//    public void setNotification() {
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
//
//        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.supagro.fr/"));
//        Intent activityIntent = new Intent(this, SecondaryActivity.class);
//
//        activityIntent.putExtra("WelcomeTextKey", "Tutorial notification: " + notificationCount);
//        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, webIntent, 0);
//        builder.setContentIntent(pendingIntent);
//        builder.setContentTitle("Tutorial notification: " + notificationCount);
//        builder.setContentText("This is a notification test for tutorial");
//        builder.setSubText("Tap to discover what intent will do.");
//        builder.setAutoCancel(false);
//
//        notificationManager.notify(1, builder.build());
//        notificationCount++;


    // notif avec intent web + intent secondary activity

    public void setNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationCount < 3) {


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(android.R.drawable.ic_dialog_alert);

            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.agro-bordeaux.fr"));
            Intent activityIntent = new Intent(this, SecondaryActivity.class);
            activityIntent.setAction(Intent.ACTION_SEND);
            activityIntent.putExtra("WelcomeTextKey", "Tutorial notification:" + notificationCount);

            PendingIntent webPendingIntent = PendingIntent.getActivity(this, 0, webIntent, 0);
            PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // ajout de deux bouttons vers chacune des deux activites
            builder.addAction(android.R.drawable.ic_delete, "WEB", webPendingIntent);
            builder.addAction(android.R.drawable.ic_dialog_email, "Activity", activityPendingIntent);

            builder.setContentIntent(webPendingIntent);
            builder.setContentTitle("Tutorial notification:" + notificationCount);
            builder.setContentText("This is a notification test for tutorial");
            builder.setAutoCancel(false);

            notificationManager.notify(1, builder.build());
            notificationCount++;
        } else {
            notificationManager.cancel(1);
        }


    }


    private ShareActionProvider shareActionProvider;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("action_bar", "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // SearchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("myTag", "SearchView submit:" + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Log.i("myTag", "SearchView text change:" + query);
                return false;
            }
        });

        MenuItem shareItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        updateSharedData();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("action_bar", "onOptionsItemSelected: " + item.getItemId());
        if (item.getItemId() == R.id.action_1) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.i("ActivityTag", "onPrepareOptionsMenu");
        menu.findItem(R.id.action_1).setEnabled(actionEnabled);
        return true;
    }

    private void updateSharedData() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, welcome_text);
        shareActionProvider.setShareIntent(shareIntent);
    }

    int prefIntValue;

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("ActivityTag", "OnResume:" + lifeCycleTest);
        SharedPreferences settings = getPreferences(0);
        prefIntValue = settings.getInt("testPrefIntValue", 0);
        Log.i("ActivityTag", "OnResume:PrefIntValue:" + prefIntValue);

        sensorEventListener = new MySensorEventListener();
        registerListenerOnSensor(true, Sensor.TYPE_AMBIENT_TEMPERATURE, SensorManager.SENSOR_DELAY_NORMAL);

        locationListener = new MyLocationListener();
        registerBestLocationProvider(true);

        addProximityAlert(40, 12, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("ActivityTag", "OnPause:" + lifeCycleTest);
        if (isFinishing() == true) {
            SharedPreferences settings = getPreferences(0);
            SharedPreferences.Editor editor = settings.edit();
            prefIntValue++;
            editor.putInt("testPrefIntValue", prefIntValue);
            editor.commit();
        }

        registerListenerOnSensor(false, -1, -1);
        registerBestLocationProvider(false);
        addProximityAlert(-1, -1, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //La phrase saisie dans SecondActivity est affichée dans textView (si on a cliqué sur ok)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data.hasExtra("WelcomeTextKey")) {
                welcome_text = data.getStringExtra("WelcomeTextKey");
                TextView textView = (TextView) findViewById(R.id.textViewid);
                textView.setText(welcome_text);
                updateSharedData();
            }
        }

        //La photo prise est affichée dans l'imageView
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap thumbnail = data.getParcelableExtra("data");
            ImageView imageView = (ImageView) findViewById(R.id.imageViewid);
            imageView.setImageBitmap(thumbnail);
        }
    }

    public void listSensors() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        Log.i("SENSOR", "***All sensors");
        for (Sensor sensor : sensors) {
            Log.i("SENSOR", getSensorDescription(sensor));
        }
        Log.i("SENSOR", "****Gyroscope sensors");
        List<Sensor> gyroscopes = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        for (Sensor sensor : gyroscopes) {
            Log.i("SENSOR", getSensorDescription(sensor));
        }
        Log.i("SENSOR", "***Default Gyroscope sensor");
        Sensor gyroscopeDefault = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (gyroscopeDefault != null) {
            Log.i("SENSOR", getSensorDescription(gyroscopeDefault));
        }
    }

    private String getSensorDescription(Sensor sensor) {
        StringBuffer sensorDesc = new StringBuffer();
        sensorDesc.append("Name: " + sensor.getName());
        sensorDesc.append(" /Type: " + getSensorType(sensor));
        sensorDesc.append(" /Version: " + sensor.getVersion());
        sensorDesc.append(" /Resolution (sensor unit): " + sensor.getResolution());
        sensorDesc.append(" /Power in mA:" + sensor.getPower());
        sensorDesc.append(" /Maximum range:" + sensor.getMaximumRange());
        sensorDesc.append(" /Minimum delay betwwen measuring or zero (only changes):" +
                sensor.getMinDelay());
        sensorDesc.append(" /Vendor: " + sensor.getVendor());
        return sensorDesc.toString();
    }

    private String getSensorType(Sensor sensor) {
        String strType;
        switch (sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                strType = "TYPE_ACCELEROMETER";
                break;
            case Sensor.TYPE_GRAVITY:
                strType = "TYPE_GRAVITY";
                break;
            case Sensor.TYPE_GYROSCOPE:
                strType = "TYPE_GYROSCOPE";
                break;
            case Sensor.TYPE_LIGHT:
                strType = "TYPE_LIGHT";
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                strType = "TYPE_LINEAR_ACCELERATION";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                strType = "TYPE_MAGNETIC_FIELD";
                break;
            case Sensor.TYPE_ORIENTATION:
                strType = "TYPE_ORIENTATION";
                break;
            case Sensor.TYPE_PRESSURE:
                strType = "TYPE_PRESSURE";
                break;
            case Sensor.TYPE_PROXIMITY:
                strType = "TYPE_PROXIMITY";
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                strType = "TYPE_ROTATION_VECTOR";
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                strType = "TYPE_TEMPERATURE";
                break;
            default:
                strType = "TYPE_UNKNOW";
                break;
        }
        return strType;
    }

    private class MySensorEventListener implements SensorEventListener {
        float[] oldValues;

        private boolean valueChanged(float[] newValues) {
            boolean result = false;
            if (oldValues == null) {
                result = true;
                oldValues = new float[newValues.length];
                for (int i = 0; i < newValues.length; i++) {
                    oldValues[i] = newValues[i];
                }
            } else {
                if (oldValues.length == newValues.length) {

                    for (int i = 0; (i < oldValues.length); i++)

                    {

                        float diff = Math.abs(oldValues[i] - newValues[i]);

                        if (diff != 0) {

                            result = true;
                            break;
                        }
                    }
                    for (int i = 0; i < newValues.length; i++) {
                        oldValues[i] = newValues[i];
                    }
                } else {

                    oldValues = new float[newValues.length];

                    for (int i = 0; i < newValues.length; i++) {
                        oldValues[i] = newValues[i];
                    }
                    result = true;
                }
            }
            return result;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.i("SENSOR", "onAccuracyChanged: " + sensor.getName() + " Accuracy: " + accuracy);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (valueChanged(event.values) == true) {
                String valueAsStr = "";
                for (float value : event.values) {
                    valueAsStr += value + ";";
                }
                String strOnSensorChange = "onSensorChangedValue: " + event.sensor.getName() + " At: " + event.timestamp
                        + " Accuracy: " + event.accuracy + " Values: " + valueAsStr;
                Log.i("SENSOR", strOnSensorChange);
            }
        }
    }

    SensorEventListener sensorEventListener;

    public void registerListenerOnSensor(boolean register, int type, int samplingPeriodUs) {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (register == true) {
            Sensor sensor = sensorManager.getDefaultSensor(type);

            sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL, samplingPeriodUs);
        } else {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    public void listLocationProviders() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> locationProviderNames = locationManager.getAllProviders();
        for (String locationProviderName : locationProviderNames) {
            LocationProvider locationProvider = locationManager.getProvider(locationProviderName);
            Log.i("LOC", locationProvider.getName() + " Accuracy:" + locationProvider.getAccuracy());
        }
    }

    protected void askLocationPermissions() {
        String[] permissions =
                {
                        "android.permission.ACCESS_FINE_LOCATION"
                };
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("LOC", "onLocationChanged:" + location.toString());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("LOC", "onStatusChanged:" + provider + " status");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("LOC", "onProviderEnabled:" + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("LOC", "onProviderDisabled:" + provider);
        }
    }

    private MyLocationListener locationListener;

    public void registerBestLocationProvider(boolean register) throws SecurityException {
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        if (register == true) {
            Criteria criteria = new Criteria();
            criteria.setAltitudeRequired(true);
            criteria.setSpeedRequired(true);
            criteria.setBearingRequired(true);
            String locationProviderName = locationManager.getBestProvider(criteria, true);
            LocationProvider locationProvider = locationManager.getProvider(locationProviderName);
            Log.i("LOC", "Best Location provider:" + locationProvider.getName());
            locationManager.requestLocationUpdates(locationProviderName, 5 * 1000, 10f,
                    locationListener);
        } else {
            locationManager.removeUpdates(locationListener);
        }


    }

    private BroadcastReceiver myProximityIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("LOC", "Proximity Change:" + intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false));
        }
    };

    private void addProximityAlert(double latitude, double longitude, boolean register) throws SecurityException {
        Log.i("LOC:", "addProximityAlert:" + "latitude:" + latitude + " longitude:" + longitude + "register:" + register);
        Intent intent = new Intent("com.agrotic.tutorial.proximity");
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        if (register == true) {
            locationManager.addProximityAlert(latitude, longitude, 500, -1, pendingIntent);
            IntentFilter filter = new IntentFilter("com.agrotic.tutorial.proximity");
            registerReceiver(myProximityIntentReceiver, filter);
        } else {
            locationManager.removeProximityAlert(pendingIntent);
        }
    }


}