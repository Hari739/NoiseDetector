package com.hari.noisedetector;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {
    TextView textView7,textView8,textView11,textView12;
    Thread runner;
    Thread FBrunner;
    MediaRecorder mediaRecorder;
    private final double EMA_Filter = 0.6;
    private double EMA  = 0.0;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private GPSTracker g;

    private  User myTempUser = new User();
    private String uname =null;
    private double avgSound = 0.0;

    //adding firebase to the noise storing app
    final  FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mainRef = firebaseDatabase.getReference();
    //firebase Updater thread and handler
    final Handler FBhandler = new Handler();
    final Runnable fbUpdater = new Runnable() {
        @Override
        public void run() {
            updateFirebase();
        }
    };

 //Noise label updater
    final Runnable updater = new Runnable() {
        @Override
        public void run() {
            UpdateTextView();
        };
    };
    final Handler handler = new Handler();
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean per = (grantResults[0]== PackageManager.PERMISSION_GRANTED)&&(grantResults[1]==PackageManager.PERMISSION_GRANTED);
        if(!per)
            finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        //all views here
        textView7 = (TextView)findViewById(R.id.textView7);
        textView8 = (TextView)findViewById(R.id.textView8);
        textView11 = (TextView)findViewById(R.id.textView11);
        textView12 = (TextView)findViewById(R.id.textView12);
        DatabaseReference userRef = mainRef.child("User");

        Bundle bundle ;
        bundle = getIntent().getExtras();
        uname = bundle.getString("username");
        myTempUser.setName(uname);
        //start the thread
        if(runner == null){
            runner =new Thread(){
                public void run(){
                    while(runner !=null){
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.post(updater);
                    }
                }
            };
            runner.start();
        }
        //start the thread for firebase updation
        if (FBrunner==null){
            FBrunner = new Thread(){
                public void run(){
                    while (FBrunner != null){
                        try{
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        FBhandler.post(fbUpdater);
                    }
                }
            };
            FBrunner.start();
        }




          g = new GPSTracker(this);
                locationManager =(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double d1 = location.getLatitude();
                double d2 = location.getLongitude();
                textView7.setText(String.valueOf(d1));
                textView8.setText(String.valueOf(d2));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }
    public  void onResume(){
        super.onResume();
        start();
    }
    public  void onPause(){
        super.onPause();
        stop();
    }
    public void start(){
        if(mediaRecorder==null){
            mediaRecorder = new MediaRecorder();

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);



            mediaRecorder.setOutputFile("/dev/null");
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public double getAmplitude(){
        if (mediaRecorder!=null) return mediaRecorder.getMaxAmplitude();
        else return 0;
    }
    public double getAmplitudeEMA(){
        double amp = getAmplitude();
        EMA = amp*EMA_Filter+(1-EMA_Filter)*EMA;
        return EMA;
    }
    public void stop(){
        if (mediaRecorder!=null){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
    public double SoundDB(){
        return  20 * Math.log10(getAmplitudeEMA() / 6.02);
    }
   /****************************************************/
    //left to add getlatitude() and get Longitude method and Update method
   //how to open a new activity from an existing one.//done

    private  void UpdateTextView(){
        double noise = SoundDB();
        String res = String.format("%.02f",noise);
        if(res.equals("-Infinity")){
            noise = 0;
        }
        avgSound+=noise;
        textView12.setText(res+"Db");
        textView7.setText(String.valueOf(g.getLatitude()));
        textView8.setText(String.valueOf(g.getLongitude()));
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        textView11.setText(dateFormat.format(date).toString());
    }

    private void updateFirebase(){
        DatabaseReference dbRef = mainRef.child("User");
        Map <String,User> mymap = new HashMap<String,User>();
        myTempUser.setName(uname);
        myTempUser.setTimestamp(textView11.getText().toString());
        myTempUser.setLatitude(textView7.getText().toString());
        myTempUser.setLongitude(textView8.getText().toString());
        myTempUser.setLoudness(String.format("%.02f",avgSound/20.0));
        avgSound = 0.0;
        String x = myTempUser.getTimestamp().replaceAll("[^0-9]", "");
        mymap.put(x,myTempUser);
        dbRef.push().setValue(mymap);
        Toast.makeText(getApplicationContext(),"Stored in firebase",Toast.LENGTH_SHORT).show();

    }

}
