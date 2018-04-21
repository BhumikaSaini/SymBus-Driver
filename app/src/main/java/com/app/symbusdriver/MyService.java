package com.app.symbusdriver;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.content.Intent.getIntent;

public class MyService extends Service {
    ScheduledExecutorService executorService;
    Runnable runnable;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    int id = 0;
    private double lattitude,longitude;
    String busNo;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private static final int REQUEST_CODE_PERMISSION=2;
    String mPermission= android.Manifest.permission.ACCESS_FINE_LOCATION;
    GPSTracker gps;
    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        busNo= intent.getStringExtra("busNo");

        executorService = new ScheduledThreadPoolExecutor(5);
        preferences = getSharedPreferences("my_pref",MODE_PRIVATE);

        id = preferences.getInt("not_id",0);
        gps=new GPSTracker(getApplicationContext()); //try now sir i have tried this it is also not working
        if(gps.CanGetLocation())
        {
            lattitude=gps.getLatitude();
            longitude=gps.getLongitude();

            runnable = new Runnable() {
                @Override
                public void run() {
                    new NotificationTask().execute();
                }
            };
            executorService.scheduleAtFixedRate(runnable,3,10, TimeUnit.SECONDS);
        }
        else
        {
            gps.showSettingsAlert();
        }


        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class NotificationTask extends AsyncTask<String,Integer,Integer> {

        String message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            id = preferences.getInt("not_id",0);
        }

        @Override
        protected Integer doInBackground(String... params) {

//            gps=new GPSTracker(getApplicationContext()); //try now sir i have tried this it is also not working
//            if(gps.CanGetLocation())
//            {
//                lattitude=gps.getLatitude();
//                longitude=gps.getLongitude();
//            }
//            else
//            {
//                gps.showSettingsAlert();
//            }

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference reference = firebaseDatabase.getReferenceFromUrl("https://symbus-jwt.firebaseio.com/");
            Query query = reference.child("Buses").orderByChild("bus_no").equalTo(busNo);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                    String key = nodeDataSnapshot.getKey();
                    String path = "/" + dataSnapshot.getKey() + "/" + key;
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("lattitude", lattitude);
                    reference.child(path).updateChildren(result);
                    result.put("longitude", longitude);
                    reference.child(path).updateChildren(result);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Logger.error(TAG, ">>> Error:" + "find onCancelled:" + databaseError);

                }
            });

            return -1;
        }

        @Override
        protected void onPostExecute(Integer i) {
            super.onPostExecute(i);
            }
        }
}
