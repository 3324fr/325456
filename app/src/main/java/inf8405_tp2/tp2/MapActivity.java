package inf8405_tp2.tp2;


import android.app.Fragment;
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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MapActivity extends FragmentActivity implements  OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
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
    private ValueEventListener valEventList;
    private Button m_btnVote;

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

        m_btnVote = (Button)findViewById(R.id.btn_vote_start);
        m_btnVote.getBackground().setAlpha(32);
        this.m_group = ourInstance.getGroup();

    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
        if(this.m_group != null ) {
            ourInstance.getGroupref().child(this.m_group.m_name).removeEventListener(valEventList);
            // Remove the listener you previously added
        }
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
        m_Map.setOnInfoWindowClickListener(this);
        // Request permission.
        ActivityCompat.requestPermissions(MapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                MY_LOCATION_REQUEST_CODE);
        map();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }

    public void map(){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            m_Map.setMyLocationEnabled(true);
            SetOnMapListener();

            // Acquire a reference to the system Location Manager
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


            // todo check GPS provider
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            setUserLocation(loc);

            // Define a listener that responds to location updates
            LocationListener locationListener = locationListener();
            //  minimum time interval between location updates, in milliseconds
            int minTime = Integer.parseInt(sharedPref.getString(getString(R.string.location_updateInterval_key), "3")) * 1000;
            // Register the listener with the Location Manager to receive location updates

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,100, 1,locationListener);
            try {
                valEventList = ourInstance.getGroupref().child(this.m_group.m_name)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Log.d(TAG, "onDataChange Fired: ============");
                                    final Group group = dataSnapshot.getValue(Group.class);
                                    if(group != null){
                                        m_group = group;
                                        // Only get lastest place for new marker. The other ones are supposedly already marked on Gmap
                                        m_Map.clear();
                                        CreateMarker(m_group);
                                        if(m_group.m_places.size() >= 3){
                                            m_btnVote.getBackground().setAlpha(255);
                                        }
                                    }
                                }
                                catch (Exception e) {//todo
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                                System.out.println("The read read failed: " + databaseError.getCode() + "============");
                            }
                        });

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        else {
            // Show rationale and request permission.
        }
    }

    public void CreateMarker(Group m_group){
        if(m_group.m_places.size() == 3){
            for(Place place : m_group.m_places){
                if(place != null){
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(place.m_loc.getLatitude(),place.m_loc.getLongitude()))
                            .title(place.m_name).snippet("Rating : " + place.m_vote);
                    m_Map.addMarker(marker);
                }
            }
        }
        for(User u : m_group.getUsers()){
            Location loc = u.getCurrentLocation();
            Profile p = u.m_profile;

            if(p != null && loc != null){
                // get the local profile whose contain a picture
                Profile localProfile = ourInstance.getUserProfile(p.m_name);
                if(localProfile != null)
                { // get the local user profile which have a picture
                    p = localProfile;
                }
                MarkerOptions marker = new MarkerOptions().position(new LatLng(loc.getLatitude(),loc.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromBitmap(p.m_picture))
                        .title(p.m_name);
                m_Map.addMarker(marker);
            }
        }
    }

    private void updateButtonTextField() {
        Button btn1 = (Button)findViewById(R.id.btn_place1);
        Button btn2 = (Button)findViewById(R.id.btn_place2);
        Button btn3 = (Button)findViewById(R.id.btn_place3);
        List<Button> listBtn = new ArrayList<>(Arrays.asList(btn1, btn2, btn3));
        for(int i = 0; i < listBtn.size(); ++i){
            listBtn.get(i).setText(m_group.m_places.get(i).m_name);
        }
        LinearLayout ll = (LinearLayout)findViewById(R.id.maps);
        ll.invalidate();
    }

    private SuperLocation GetLocationFromUser(String username){
        List<User> users = new ArrayList<>(m_group.getUsers());
        for(User user : users){
            if(user.m_profile.m_name.equals(username)){
                return user.getCurrentLocation();
            }
        }
        Log.d(TAG, "User and profile not found ===========");
        return null;
    }

    public LocationListener  locationListener(){
        return new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                setUserLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

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

        });/*
        m_Map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //View popUp = getLayoutInflater().inflate(R.layout.map_popup, map, false);
                return false;
            }
        });*/
    }

    /*
    public void onClickPlace(View view){
        RatingBar rb;
        switch (view.getId()){
            case (R.id.btn_place1):
                rb = (RatingBar)findViewById(R.id.ratingBar1);
                m_group.m_places.get(0).m_vote = (int)rb.getRating();
                break;
            case (R.id.btn_place2):
                rb = (RatingBar)findViewById(R.id.ratingBar2);
                m_group.m_places.get(1).m_vote = (int)rb.getRating();
                break;
            case (R.id.btn_place3):
                rb = (RatingBar)findViewById(R.id.ratingBar3);
                m_group.m_places.get(2).m_vote = (int)rb.getRating();
                break;
        }
    }*/

    public void OnClickVote(View view){
        View child = getLayoutInflater().inflate(R.layout.content_map, null);
        if(view.getId() == R.id.btn_vote_start){
            if(m_group.m_places.size() >=3 ){
                m_btnVote.setText(R.string.vote_en_cours);
                View frag = (View)findViewById(R.id.map);
                frag.setVisibility(View.INVISIBLE);
                LinearLayout item = (LinearLayout)findViewById(R.id.maps);
                ArrayList<View> viewList = new ArrayList<View>();
                item.addView(child, 0);
                updateButtonTextField();
            } else {
                final PopupWindow popUpWindow = new PopupWindow(this);
                popUpWindow.showAtLocation(((LinearLayout)findViewById(R.id.maps)), Gravity.CENTER, 0, 0);
                RelativeLayout containerLayout = new RelativeLayout(this);
                TextView msg = new TextView(this);
                msg.setText(R.string.trois_lieux);

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
                containerLayout.addView(msg, layoutParams);
                popUpWindow.setContentView(containerLayout);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // close your dialog
                        popUpWindow.dismiss();
                    }
                }, 3000);
            }
        }
        if(view.getId() == R.id.btn_vote_confirm){
            LinearLayout item = (LinearLayout)findViewById(R.id.maps);
            child = (LinearLayout)findViewById(R.id.map_content);
            SetRating();
            if(item.getChildAt(0) == child){
                //second attempt to remove inflated
                //child.setVisibility(View.GONE);
                item.removeViewAt(0);
            }
            Button btn = (Button)findViewById(R.id.btn_vote_start);
            item.removeView(btn);
            View frag = (View)findViewById(R.id.map);
            frag.setVisibility(View.VISIBLE);
            item.invalidate();
        }
    }

    private void SetRating() {
        RatingBar rb;
        rb = (RatingBar)findViewById(R.id.ratingBar1);
        m_group.m_places.get(0).m_vote =  (int)Math.floor(rb.getRating());
        rb = (RatingBar)findViewById(R.id.ratingBar2);
        m_group.m_places.get(1).m_vote = (int)Math.floor(rb.getRating());
        rb = (RatingBar)findViewById(R.id.ratingBar3);
        m_group.m_places.get(2).m_vote = (int)Math.floor(rb.getRating());
    }
}
