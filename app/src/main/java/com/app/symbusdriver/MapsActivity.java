package com.app.symbusdriver;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
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
    private String pushKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent i= getIntent();
        busNo= i.getStringExtra("busNo");
        destn= i.getStringExtra("destn");
        gps=new GPSTracker(MapsActivity.this);
        if(gps.CanGetLocation())
        {
            lattitude=gps.getLatitude();
            longitude=gps.getLongitude();
        }
        else
        {
            gps.showSettingsAlert();
        }
        //lattitude=20.5; longitude= 12.5;
        if(destn.equalsIgnoreCase("SIT"))
        {
            destnLat= SIT.latitude;
            destnLong= SIT.longitude;
        }
        else if (destn.equalsIgnoreCase("SBRoad"))
        {
            destnLat= SBRoad.latitude;
            destnLong= SBRoad.longitude;
        }
        isRunning="true";
        nextStop= 1;
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
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
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

                    }
                });

                Intent servIntent = new Intent(MapsActivity.this,MyService.class);
                stopService(servIntent);

                finish();
            }
        });
        Bpetrol.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MapsActivity.this, "Petrol Pumps", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

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
                Bus thisBus= new Bus(busNo, lattitude, longitude,
                        nextStop, destnLat, destnLong, destn, isRunning);
                reference.child(path).setValue(thisBus);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Logger.error(TAG, ">>> Error:" + "find onCancelled:" + databaseError);

            }
        });

       /* Bus thisBus= new Bus(busNo, lattitude, longitude,
                nextStop, destnLat, destnLong, destn, isRunning);
        mBusesRef= FirebaseDatabase.getInstance().getReferenceFromUrl("https://symbus-jwt.firebaseio.com/").child("Buses");
        mBusesRef.push().setValue(thisBus);*/

        Intent servIntent = new Intent(MapsActivity.this,MyService.class);
        servIntent.putExtra("busNo", busNo);
        startService(servIntent);
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
}
