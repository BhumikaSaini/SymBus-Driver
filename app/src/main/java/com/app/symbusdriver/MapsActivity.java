package com.app.symbusdriver;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.app.symbusdriver.GPSTracker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ImageButton BMaptype, Blogout,Brepair,Bpetrol;
    private String busNo, destn, isRunning;
    private double destnLat, destnLong, lattitude, longitude;
    private int nextStop;
    GPSTracker gps;
    int PROXIMITY_RADIUS = 10000;
    GoogleApiClient.Builder mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationManager locationManager;
    FirebaseDatabase firebaseDatabase;
    LatLng SIT= new LatLng(18.5411726, 73.72813050000002);
    LatLng SBRoad= new LatLng(18.522351, 73.82915000000003);
    private DatabaseReference mBusesRef;
    private String pushKey="";
    private ArrayList<LatLng> path= new ArrayList<LatLng>();
    private DatabaseReference mStopsRef;
    private int stopCount=0;
    private ArrayList<Stop> mStops= new ArrayList<Stop>();
    private Location toPt= new Location("next stop");
    private Location curr= new Location("bus curr loc");
    private double oldDist;
    private Bus thisBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent i= getIntent();
        busNo= i.getStringExtra("busNo");
        destn= i.getStringExtra("destn");
        if(destn.equalsIgnoreCase("SIT"))
        {
            destnLat= SIT.latitude;
            destnLong= SIT.longitude;
            lattitude= SBRoad.latitude;
            longitude= SBRoad.longitude;
            nextStop= 1;
        }
        else if (destn.equalsIgnoreCase("SB Road"))
        {
            destnLat= SBRoad.latitude;
            destnLong= SBRoad.longitude;
            lattitude= SIT.latitude;
            longitude= SIT.longitude;
            nextStop= 4;
        }
        isRunning="true";
        curr.setLatitude(lattitude);
        curr.setLongitude(longitude);
        getStops();
        /*gps=new GPSTracker(MapsActivity.this);
        if(gps.CanGetLocation())
        {
            lattitude=gps.getLatitude();
            longitude=gps.getLongitude();
        }
        else
        {
            gps.showSettingsAlert();
        }*/
        //lattitude=20.5; longitude= 12.5;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        BMaptype = (ImageButton) findViewById(R.id.BMapType);
        Blogout = (ImageButton) findViewById(R.id.BMapLogout);
        Brepair = (ImageButton) findViewById(R.id.BRepairShop);
        Bpetrol = (ImageButton) findViewById(R.id.BPetrolPump);

        final GetnearbyPlaces g = new GetnearbyPlaces();
        final Object datatransfer[] = new Object[2];

        Brepair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* mMap.clear();//It will remove all the marksers from the map
                String repairshop = "repairshop";
                String url = geturl(lattitude, longitude, repairshop);

                datatransfer[0] = mMap;
                datatransfer[1] = url;


                g.execute(datatransfer);*/
                Toast.makeText(MapsActivity.this, "Nothing to show!!", Toast.LENGTH_LONG).show();
            }
        });

        Bpetrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              /*  mMap.clear();//It will remove all the marksers from the map
                String petrolpump = "petrolpump";
               String url = geturl(lattitude, longitude, petrolpump);

                datatransfer[0] = mMap;
                datatransfer[1] = url;


                g.execute(datatransfer);*/
                Toast.makeText(MapsActivity.this, "Nothing to show!!", Toast.LENGTH_LONG).show();


            }
        });
        Blogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning="false";
                /*FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                final DatabaseReference reference = firebaseDatabase.getReferenceFromUrl("https://symbus-jwt.firebaseio.com/");
                Query query = reference.child("Buses").orderByChild("bus_no").equalTo(busNo);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                        String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                        String path = "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("isRunning", isRunning);
                        reference.child(path).updateChildren(result);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                       // Logger.error(TAG, ">>> Error:" + "find onCancelled:" + databaseError);

                    }*/

                    finish();
                }});

                //Intent servIntent = new Intent(MapsActivity.this,MyService.class);
                //stopService(servIntent);




        Brepair.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MapsActivity.this, "Repair Shops", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        Blogout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MapsActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        BMaptype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });
        BMaptype.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MapsActivity.this, "Map Type", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        /*FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference reference = firebaseDatabase.getReferenceFromUrl("https://symbus-jwt.firebaseio.com/");
        Query query = reference.child("Buses").orderByChild("bus_no").equalTo(busNo);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                String key = nodeDataSnapshot.getKey();
                String path = "/" + dataSnapshot.getKey() + "/" + key;
                HashMap<String, Object> result = new HashMap<>();
                Bus thisBus= new Bus(busNo, lattitude, longitude,
                        nextStop, destnLat, destnLong, destn, isRunning);
                reference.child(path).setValue(thisBus);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Logger.error(TAG, ">>> Error:" + "find onCancelled:" + databaseError);

            }
        });*/

       /* Bus thisBus= new Bus(busNo, lattitude, longitude,
                nextStop, destnLat, destnLong, destn, isRunning);
        mBusesRef= FirebaseDatabase.getInstance().getReferenceFromUrl("https://symbus-jwt.firebaseio.com/").child("Buses");
        mBusesRef.push().setValue(thisBus);*/

        //Intent servIntent = new Intent(MapsActivity.this,MyService.class);
        //servIntent.putExtra("busNo", busNo);
        //startService(servIntent);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                DemoWriteToDB();
            }
        };
        Timer mTimer = new Timer();
        mTimer.schedule(timerTask, 0,5*1000);
    }

    public void getStops()
    {
        //fill the mStops array with data from DB
        mStopsRef= FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://symbus-jwt.firebaseio.com/")
                .child("Stops");
        ChildEventListener mStopsListener= new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Stop x= dataSnapshot.getValue(Stop.class);
                mStops.add(x);
                ++stopCount;
                if (x.getStopNo()==nextStop)
                {
                    toPt.setLatitude(x.getLattitude());
                    toPt.setLongitude(x.getLongitude());
                    oldDist= curr.distanceTo(toPt);
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                Stop x= dataSnapshot.getValue(Stop.class);
                for(int i=0; i<mStops.size(); i++)
                {
                    Stop temp= mStops.get(i);
                    if (temp.getStopNo()==x.getStopNo())
                        mStops.set(i, x);
                }
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                Stop x= dataSnapshot.getValue(Stop.class);
                for(int i=0; i<mStops.size(); i++)
                {
                    Stop temp= mStops.get(i);
                    if (temp.getStopNo()==x.getStopNo())
                    {
                        mStops.remove(x);
                        --stopCount;
                    }
                }
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        mStopsRef.addChildEventListener(mStopsListener);
    }

    private void DemoWriteToDB() {

        //get lat lang
        new RouteDirections().execute(lattitude, longitude,
                destnLat, destnLong, path, "false");
        int len= path.size();
        if (len>50)
        {
            lattitude= path.get(49).latitude;
            longitude= path.get(49).longitude;
            curr.setLatitude(lattitude);
            curr.setLongitude(longitude);
        }
        thisBus= new Bus(busNo, lattitude, longitude,
                nextStop, destnLat, destnLong, destn, isRunning);
        //get next stop
        monitorNextStop();
        //write to DB
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference reference = firebaseDatabase.getReferenceFromUrl("https://symbus-jwt.firebaseio.com/")
                                                .child("Buses");
        if(pushKey.equalsIgnoreCase(""))
            pushKey+=reference.push().getKey();
        reference.child(pushKey).setValue(thisBus);
        /*reference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                mutableData.setValue(thisBus);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d("Datasnapshot is ---> ", ""+dataSnapshot);
            }
        });*/

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mMap.setMyLocationEnabled(true);

        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(lattitude, longitude));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(25));

    }

    private String geturl(double latitude, double longitude, String nearbyplaces) {
        StringBuilder googlePlacerUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacerUrl.append("location=" + latitude + "," + longitude);  //Check this again if error comes
        googlePlacerUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacerUrl.append("&type=" + nearbyplaces);
        googlePlacerUrl.append("&sensor=true");
        googlePlacerUrl.append("&key=" + "AIzaSyDOUcPCPi5O5zOIAkqO9vX7vZ4Cbgxik3M");


        return googlePlacerUrl.toString();

    }

    private void monitorNextStop()
    {
        Stop nxt = null; //stores next stop object
        double newDist; //temp variable storing new dist between bus & next stop

        //Monitor next stop only if last stop is not crossed
        if(thisBus.getNextStop()<=stopCount && thisBus.getNextStop()>0)
        {
            //find the next stop object from array of stops
            for(int i=0; i<mStops.size(); i++)
            {
                Stop temp= mStops.get(i);
                if (temp.getStopNo()==thisBus.getNextStop())
                    nxt= temp;
            }

            //get location of next stop
            //toPt= new Location("next stop");
            toPt.setLatitude(nxt.getLattitude());
            toPt.setLongitude(nxt.getLongitude());

            //calculate distance between current location of bus & next stop
            newDist = curr.distanceTo(toPt);
            Log.d("Old dist bfr cmp ---> ", ""+oldDist);
            Log.d("New dist bfr cmp ---> ", ""+newDist);
            //if distance between them is more than
            //previous values (i.e., oldDist),
            //bus crossed stop.
            //Update the next stop.
            if(oldDist-newDist<0)
            {
                Log.d("TAG ---> ", "Stop needs to update");
                if(destn.equalsIgnoreCase("SIT"))
                {
                    thisBus.setNextStop(1+thisBus.getNextStop());
                    ++nextStop;
                }
                else
                {
                    thisBus.setNextStop(thisBus.getNextStop()-1);
                    --nextStop;
                }
                //Now, set initial distance between current location of bus
                //& the new nextStop. Assign this to "oldDist".

                //find the next stop object from array of stops
                for(int i=0; i<mStops.size(); i++)
                {
                    Stop temp= mStops.get(i);
                    if (temp.getStopNo()==thisBus.getNextStop())
                    {
                        toPt.setLatitude(temp.getLattitude());
                        toPt.setLongitude(temp.getLongitude());
                        oldDist = curr.distanceTo(toPt);
                    }
                }
                Log.d("TAG ---> ", "Updated next stop to "+thisBus.getBusNo());
            }
            //Else, set new dist between bus & stop
            else
            {
                Log.d("TAG ---> ", "Stop doesnt need to update");
                oldDist= newDist;
            }
        }
    }

}
