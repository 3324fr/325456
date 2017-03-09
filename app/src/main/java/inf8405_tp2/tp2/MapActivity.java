package inf8405_tp2.tp2;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MapActivity extends FragmentActivity implements  OnMapReadyCallback {
    public static final String MESSAGE_LAT_LNG = "inf8405_tp2.tp2.LatLng";
    public static final String MESSAGE_GROUP_NAME = "inf8405_tp2.tp2.groupName";

    private static GoogleMap m_Map;
    private static GoogleApiClient m_GoogleApiClient;
    private static UiSettings mapSettings;
    private final static int MY_LOCATION_REQUEST_CODE = 1;
    private static final String TAG = "LocationActivity";
    private static final String TAG_ADD_PLACE = "inf8405_tp2.tp2.addPlaceFragment";

    private static Group m_group;
    private static UserSingleton ourInstance;
    private static SharedPreferences sharedPref;
    private static LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ourInstance = UserSingleton.getInstance(getApplicationContext());
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == android.Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                // todo : Permission was denied. Display an error message.
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        m_Map = googleMap;

        // Request permission.
        ActivityCompat.requestPermissions(MapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                MY_LOCATION_REQUEST_CODE);
        map();

    }

    public void map(){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            m_Map.setMyLocationEnabled(true);
            SetOnMapListener();



            this.m_group = ourInstance.getGroup();

            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            setUserLocation(loc);


            // Define a listener that responds to location updates
            LocationListener locationListener = locationListener();
            //  minimum time interval between location updates, in milliseconds
            int minTime = Integer.parseInt(sharedPref.getString(getString(R.string.location_updateInterval_key),"3"))*1000;
            // Register the listener with the Location Manager to receive location updates

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, 0, locationListener);

            ourInstance.getGroupref().child(this.m_group.m_name)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                final Group group = dataSnapshot.getValue(Group.class);
                                m_group = group;
                            } catch (Exception e) {//todo

                            }

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            System.out.println("The read read failed: " + databaseError.getCode());
                        }
                    });}
        else {
            // Show rationale and request permission.
        }

    }

    public LocationListener  locationListener(){
        return new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                setUserLocation(location);

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
    }


    public void setUserLocation(final Location loc) {
        if( this.m_group != null){

                String groupName = this.m_group.m_name;
                if (!groupName.isEmpty()) {
                    if (this.m_group.updateLoc(ourInstance.getUser(), loc)) {
                        ourInstance.getGroupref().child(groupName).setValue(this.m_group);
                    }
                }
            }
    }

    public static void quitGroup(String groupeName) {
        //// TODO: 2017-03-08
        // Remove the listener you previously added
        // locationManager.removeUpdates(locationListener);
    }
    public void visit(User user) {
        //// TODO: 2017-03-08

    }
    public void visit(Manager manager) {
        //// TODO: 2017-03-08

    }

    private void SetOnMapListener(){
        m_Map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                if(m_group.m_places.size() < 3){
                    Intent i = new Intent(MapActivity.this, PlaceActivity.class);
                    i.putExtra(MESSAGE_LAT_LNG,latLng);
                    i.putExtra(MESSAGE_GROUP_NAME,m_group.m_name);
                    startActivity(i);

                }

            }

        });
    }








}
