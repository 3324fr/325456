package inf8405_tp2.tp2;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapActivity extends FragmentActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback {

    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private static final int REQUEST_LOCATION = 2;
    private static final float DEFAULT_ZOOM_STARTUP = 4.0f;
    private UserSingleton ourInstance;
    private final String TAG_RETAINED_USER = "inf8405_tp2.tp2.UserFragment";
    private Button m_btnFusedLocation;
    private TextView m_tvLocation;
    private LocationRequest m_LocationRequest;
    private GoogleApiClient m_GoogleApiClient;
    private Location m_CurrentLocation;
    private String m_LastUpdateTime;
    private GoogleMap m_Map;
    private String m_lat;
    private String m_lng;
    private User m_currentUser;
    private Group m_Group;

    protected void createLocationRequest() {
        m_LocationRequest = new LocationRequest();
        m_LocationRequest.setInterval(INTERVAL);
        m_LocationRequest.setFastestInterval(FASTEST_INTERVAL);
        m_LocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate ...............................");
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        m_GoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setContentView(R.layout.maps);
        m_tvLocation = (TextView) findViewById(R.id.tvLocation);

        m_btnFusedLocation = (Button) findViewById(R.id.btnShowLocation);
        m_btnFusedLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                updateUI(true);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ourInstance = UserSingleton.getInstance(getApplicationContext());
        Intent intent = getIntent();

        //m_currentUser = intent.getStringExtra("username");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        m_GoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        m_GoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + m_GoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + m_GoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {

        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                m_GoogleApiClient, m_LocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        m_CurrentLocation = location;
        m_LastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
        if(m_currentUser!=null){
            m_currentUser.setCurrentLocation(m_CurrentLocation);
        }
    }

    private void updateUI(){
        updateUI(false);
    }

    private void updateUI(boolean onStartUp) {
        Log.d(TAG, "UI update initiated .............");
        if (null != m_CurrentLocation) {
            m_lat = String.valueOf(m_CurrentLocation.getLatitude());
            m_lng = String.valueOf(m_CurrentLocation.getLongitude());
            m_tvLocation.setText("At Time: " + m_LastUpdateTime + "\n" +
                    "Latitude: " + m_lat + "\n" +
                    "Longitude: " + m_lng + "\n" +
                    "Accuracy: " + m_CurrentLocation.getAccuracy() + "\n" +
                    "Provider: " + m_CurrentLocation.getProvider());
            if(m_lat != null && m_lng != null){
                LatLng localLoc = new LatLng(Double.parseDouble(m_lat), Double.parseDouble(m_lng));
                m_Map.addMarker(new MarkerOptions().position(localLoc).title("Marker in local"));
                if(onStartUp){
                    float zoomLevel = DEFAULT_ZOOM_STARTUP;
                    m_Map.moveCamera( CameraUpdateFactory.newLatLngZoom(localLoc, zoomLevel) );
                    onStartUp = false;
                }
                m_Map.moveCamera(CameraUpdateFactory.newLatLng(localLoc));
                ourInstance.setUserLocation(m_CurrentLocation);
                showOtherUser();
                showEventInfo();

            }
        } else {
            Log.d(TAG, "location is null ...............");
        }
    }

    private void showEventInfo() {
        //TODO SHOW EACH EVENT WITH A DIFFERENT MARKER
    }

    private void showOtherUser() {
        try{
            List<User> arrayUser = new ArrayList<>(getOtherUserInfo());
            for(User user : arrayUser){
                Location loc = user.getCurrentLocation();
                LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                m_Map.addMarker(new MarkerOptions().position(latLng).title(user.m_profile.m_name));
            }

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private List<User> getOtherUserInfo() {
        List<User> arrayUser = new ArrayList<>();
        //TODO GET DB
        //m_Group.getUsers()
        //UserSingleton.getInstance(getApplicationContext()).getAllUsername()
        //TODO TEST
        return arrayUser;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                m_GoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onResume() {
        super.onResume();

        // Assume thisActivity is the current activity
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            // permission has been granted, continue as usual
            Location myLocation =
                    LocationServices.FusedLocationApi.getLastLocation(m_GoogleApiClient);
        }
        if (m_GoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

                    // We can now safely use the API we requested access to
                    Location myLocation = LocationServices.FusedLocationApi.getLastLocation(m_GoogleApiClient);
                }
            } else {
                // Permission was denied or request was cancelled
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        m_Map = googleMap;
        updateUI();
        // Add a marker in Sydney and move the camera
        if(m_lat != null && m_lng != null){
            LatLng localLoc = new LatLng(Double.parseDouble(m_lat), Double.parseDouble(m_lng));
            m_Map.addMarker(new MarkerOptions().position(localLoc).title("Marker in local"));
            m_Map.moveCamera(CameraUpdateFactory.newLatLng(localLoc));
        }

        m_Map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick (LatLng latLng) {

                Button btn = (Button)findViewById(R.id.btnInflateNewMenu);

                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MapActivity.this, btn);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(MapActivity.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                popup.show();//showing popup menu

                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(latLng.latitude, latLng.longitude)).title("New Marker");

                m_Map.addMarker(marker);



                System.out.println(latLng.latitude+"---"+ latLng.longitude);



            }
        });


    }

    public void OnMenuClick(View view){


        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(MapActivity.this, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(MapActivity.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        popup.show();//showing popup menu
    }

}