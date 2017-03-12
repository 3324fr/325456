package inf8405_tp2.tp2;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class MapActivity extends AppCompatActivity implements  OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.InfoWindowAdapter,
        GoogleApiClient.ConnectionCallbacks, LocationListener,
        GoogleApiClient.OnConnectionFailedListener{
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
    private LocationManager locationManager;
    private ScheduledExecutorService scheduler;
    private LinearLayout m_layoutRoot;
    private final int GRAY_ALPHA = 32;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest m_LocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ourInstance = UserSingleton.getInstance(getApplicationContext());
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        m_layoutRoot = (LinearLayout)findViewById(R.id.maps);
        m_btnVote = (Button)findViewById(R.id.btn_vote_start);
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        m_GoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        // Create the LocationRequest object
        m_LocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


    }//onActivityResult

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Intent intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_agenda:
                // User chose the "agenda" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
        if(this.m_group != null ) {
            ourInstance.getGroupref().child(this.m_group.m_name).removeEventListener(valEventList);
            // Remove the listener you previously added
        }
    }
    // Use default InfoWindow frame
    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    // SOURCE: http://stackoverflow.com/questions/15090148/custom-info-window-adapter-with-custom-data-in-map-v2
    // Defines the contents of the InfoWindow
    @Override
    public View getInfoContents(Marker arg0) {
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        m_Map = googleMap;
        // Setting a custom info window adapter for the google map
        m_Map.setInfoWindowAdapter(this);
        //m_Map.setOnInfoWindowClickListener(this);
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
            try {
                updateMemberGroup();
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
                                        ParticipateEvent();
                                        User user = ourInstance.getUser();
                                        user.setVote(m_group.getUsers().get(m_group.getUsers().indexOf(user)).getVote());
                                        if(m_group.m_places.size() == 3){
                                            m_btnVote.getBackground().setAlpha(255);
                                        }
                                    }
                                }
                                catch (Exception e) {
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
        if(m_group == null){
            m_group = ourInstance.getGroup();
        }
    }

    private void updateMemberGroup() {
        this.m_group = ourInstance.getGroup();
        if(m_group.m_places.size() == 3 && m_group.m_meeting == null){
            m_btnVote.getBackground().setAlpha(255);
            if(m_group.userAllVoted()){
                UpdateButtonAfterVote(m_layoutRoot);
            }
        } else {
            m_btnVote.getBackground().setAlpha(GRAY_ALPHA);
        }
    }

    public void CreateMarker(final Group m_group){
        if(m_group.m_places.size() == 3 && m_group.m_meeting==null){
            for(Place place : m_group.m_places){
                if(place != null){
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(place.m_loc.getLatitude(),place.m_loc.getLongitude()))
                            .title(place.m_name).snippet("Rating : " + place.m_finalRating);
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
                        .title(p.m_name);
                if(p.m_picture != null){
                    marker.icon(BitmapDescriptorFactory.fromBitmap(p.m_picture));
                }
                m_Map.addMarker(marker);
            }
        }
        if(m_group.m_meeting != null){
            m_Map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                // Use default InfoWindow frame
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                // SOURCE: http://stackoverflow.com/questions/15090148/custom-info-window-adapter-with-custom-data-in-map-v2
                // Defines the contents of the InfoWindow
                @Override
                public View getInfoContents(Marker arg0) {

                    // Getting view from the layout file info_window_layout
                    View v = getLayoutInflater().inflate(R.layout.custom_infowind, null);
                    ((TextView) v.findViewById(R.id.tv_title)).setText(m_group.m_meeting.m_place.m_name);
                    ((TextView) v.findViewById(R.id.tv_rating)).setText("Rating:" + m_group.m_meeting.m_place.m_finalRating);
                    ((TextView) v.findViewById(R.id.tv_lat)).setText("Lat:" + m_group.m_meeting.m_place.m_loc.getLatitude());
                    ((TextView) v.findViewById(R.id.tv_lng)).setText("Long:" + m_group.m_meeting.m_place.m_loc.getLongitude());
                    ((TextView) v.findViewById(R.id.tv_date)).setText("Date:" + m_group.m_meeting.m_date+ "\t|");
                    ((TextView) v.findViewById(R.id.tv_start)).setText("Start Time:" + m_group.m_meeting.m_startTime+ "\t|");
                    ((TextView) v.findViewById(R.id.tv_end)).setText("End Time:" + m_group.m_meeting.m_endTime);
                    ((TextView) v.findViewById(R.id.tv_info)).setText("Infos:" + m_group.m_meeting.m_info);
                    ((TextView) v.findViewById(R.id.tv_participants)).setText("Go:" + getStringFromArray(m_group.m_meeting.m_participants) + "\t|");
                    ((TextView) v.findViewById(R.id.tv_maybe)).setText("Maybe:" + getStringFromArray(m_group.m_meeting.m_maybe) + "\t|");
                    ((TextView) v.findViewById(R.id.tv_decline)).setText("Declined:" + getStringFromArray(m_group.m_meeting.m_decline));
                    return v;
                }

                private String getStringFromArray(List<User> list) {
                    StringBuilder sb = new StringBuilder();
                    for (User user : list)
                        {
                        sb.append(user.m_profile.m_name);
                        sb.append("\t");
                    }
                    return sb.toString();
                }
            });
            Place place = m_group.m_meeting.m_place;
            if(place != null){
                MarkerOptions marker = new MarkerOptions().position(new LatLng(place.m_loc.getLatitude(),place.m_loc.getLongitude()))
                        .title("Meeting at " + place.m_name).snippet("Rating : " + place.m_finalRating + "\n");
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

    private void updateMeetingTextField() {
        Button btn1 = (Button)findViewById(R.id.btn_meeting1);
        Button btn2 = (Button)findViewById(R.id.btn_meeting2);
        Button btn3 = (Button)findViewById(R.id.btn_meeting3);
        List<Button> listBtn = new ArrayList<>(Arrays.asList(btn1, btn2, btn3));
        for(int i = 0; i < listBtn.size(); ++i){
            listBtn.get(i).setText(m_group.m_places.get(i).m_name);
        }
        TextView tv1 = (TextView)findViewById(R.id.textViewRating1);
        TextView tv2 = (TextView)findViewById(R.id.textViewRating2);
        TextView tv3 = (TextView)findViewById(R.id.textViewRating3);
        List<TextView> listTv = new ArrayList<>(Arrays.asList(tv1, tv2, tv3));
        for(int i = 0; i < listBtn.size(); ++i){
            listTv.get(i).setText(String.format("Rating : %d", m_group.m_places.get(i).m_finalRating));
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

    public void setUserLocation(final Location loc) {
        Group group =  this.m_group;
        User user = ourInstance.getUser();
        if ( user != null && group != null && !group.m_name.isEmpty() && group.m_users.containsValue(user)) {
            m_group.m_users.get(user.m_profile.m_name).setCurrentLocation(loc);
            m_group.m_users.put(user.m_profile.m_name, user);
            DatabaseReference groupRef = ourInstance.getGroupref().child(group.m_name)
                    .child(Group.PROPERTY_USERS).child(user.m_profile.m_name).child(User.PROPERTY_LOCATION);
            groupRef.setValue(loc);
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
                if(doubleCheckManager()){
                    if(m_group.m_places.size() < 3){
                        Intent i = new Intent(MapActivity.this, PlaceActivity.class);
                        i.putExtra(MESSAGE_LAT_LNG,latLng);
                        i.putExtra(MESSAGE_GROUP_NAME,m_group.m_name);
                        startActivity(i);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.not_manager, Toast.LENGTH_LONG);
                }
            }
        });
    }

    public void OnClickVote(View view){
        if(m_group.m_meeting != null){
            Toast.makeText(getApplicationContext(), R.string.event_exists_2, Toast.LENGTH_LONG).show();
            return;
        }
        if(!ourInstance.getUser().getVote()){
            View child = getLayoutInflater().inflate(R.layout.content_map, null);
            if(view.getId() == R.id.btn_vote_start){
                if(m_group.m_places.size() ==3 ){
                    m_btnVote.setText(R.string.vote_en_cours);
                    View frag = (View)findViewById(R.id.map);
                    frag.setVisibility(View.INVISIBLE);
                    LinearLayout item = (LinearLayout)findViewById(R.id.maps);
                    item.addView(child, 0);
                    updateButtonTextField();
                } else {
                    Toast.makeText(this, R.string.need_three_locs, Toast.LENGTH_SHORT).show();
                }
            }
            if(view.getId() == R.id.btn_vote_confirm){

                child = (LinearLayout)findViewById(R.id.map_content);
                SetRating();
                if(m_layoutRoot.getChildAt(0) == child){
                    //second attempt to remove inflated
                    //child.setVisibility(View.GONE);
                    m_layoutRoot.removeViewAt(0);
                }
                UpdateButtonAfterVote(m_layoutRoot);
                View frag = (View)findViewById(R.id.map);
                frag.setVisibility(View.VISIBLE);
                m_layoutRoot.invalidate();
                Group group =  this.m_group;
                User user = ourInstance.getUser();
                DatabaseReference groupRef = ourInstance.getGroupref().child(group.m_name)
                        .child(Group.PROPERTY_USERS).child(user.m_profile.m_name).child(User.PROPERTY_VOTE);
                groupRef.setValue(true);
                ourInstance.getUser().setVote(true);
            }
        } else {
            Toast.makeText(this, "You already voted!", Toast.LENGTH_SHORT).show();
        }
    }

    // EVENT CHOOSER IMPORTANT -- CREATE EVENT -- CREATEEVENT
    public void OnClickPlace(View view){
        m_Map.clear();
        Place place = null;
        switch (view.getId()){
            case R.id.btn_meeting1:
                place = m_group.m_places.get(0);
                break;
            case R.id.btn_meeting2:
                place = m_group.m_places.get(1);
                break;
            case R.id.btn_meeting3:
                place = m_group.m_places.get(2);
                break;
        }
        m_group.m_meeting = new Meeting(place);
        ourInstance.getGroup().m_meeting = m_group.m_meeting;
        LinearLayout child = (LinearLayout)findViewById(R.id.map_content_meeting);
        if(m_layoutRoot.getChildAt(0) == child){
            m_layoutRoot.removeViewAt(0);
        }
        View frag = (View)findViewById(R.id.map);
        frag.setVisibility(View.VISIBLE);
        m_layoutRoot.invalidate();
        setMeeting(m_group.m_meeting);
        // Start New intent for result calendar
        Intent intent = new Intent(MapActivity.this, CalendarActivity.class);
        startActivity(intent);
    }

    private void UpdateButtonAfterVote(LinearLayout item) {
        m_btnVote.setText(R.string.create_event);
        if(m_group.m_meeting == null){
            if(doubleCheckManager() ){
                m_btnVote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Creating Event", Toast.LENGTH_SHORT).show();
                        View child = getLayoutInflater().inflate(R.layout.content_map_meeting, null);
                        View frag = (View)findViewById(R.id.map);
                        frag.setVisibility(View.INVISIBLE);
                        LinearLayout item = (LinearLayout)findViewById(R.id.maps);
                        item.addView(child, 0);
                        updateMeetingTextField();
                        m_btnVote.setVisibility(View.INVISIBLE);
                    }
                });
                m_btnVote.setVisibility(View.VISIBLE);
            } else {
                m_btnVote.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), R.string.wait_event, Toast.LENGTH_SHORT).show();
                        m_btnVote.getBackground().setAlpha(GRAY_ALPHA);
                    }
                });
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.event_exists, Toast.LENGTH_SHORT).show();
        }
    }

    private void SetRating() {
        RatingBar rb;
        Integer[]rbs = {R.id.ratingBar1, R.id.ratingBar2, R.id.ratingBar3};
        for(int i = 0; i < rbs.length; ++i){
            rb = (RatingBar)findViewById(rbs[i]);
            m_group.m_places.get(i).m_rating.add(Math.floor(rb.getRating()));
        }
        updateAllRating();
        setPlaceRatings();
    }

    public void updateAllRating(){
        for(Place place : m_group.m_places){
            place.calculateRating();
        }
    }

    public void setPlaceRatings() {
        try{
            Group group =  m_group;
            for(Place place : group.m_places) {
                String placeNum = String.valueOf(group.m_places.indexOf(place));
                DatabaseReference groupRef = ourInstance.getGroupref().child(group.m_name)
                        .child(Group.PROPERTY_PLACES).child(placeNum).child(Place.PROPERTY_RATINGS);
                groupRef.setValue(place.m_rating);
            }
            for(Place place : group.m_places) {
                String placeNum = String.valueOf(group.m_places.indexOf(place));
                DatabaseReference groupRef = ourInstance.getGroupref().child(group.m_name)
                        .child(Group.PROPERTY_PLACES).child(placeNum).child(Place.PROPERTY_RATING);
                groupRef.setValue(place.m_finalRating);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setMeeting(Meeting meeting) {
        try{
            Group group =  m_group;
            DatabaseReference groupRef = ourInstance.getGroupref().child(group.m_name)
                    .child(Group.PROPERTY_MEETING);
            groupRef.setValue(meeting);
            Toast.makeText(getApplicationContext(), R.string.new_meeting, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean doubleCheckManager(){
        return (m_group.isManager(ourInstance.getUser()) || m_group.m_manager.equals(ourInstance.getUser()));
    }

    private void ParticipateEvent() {
        if(m_group.m_meeting != null){
            View child = getLayoutInflater().inflate(R.layout.content_map_participation, null);
            m_layoutRoot.removeView(m_btnVote);
            m_layoutRoot.addView(child);
        }
    }

    // Participation
    public void OnClickParticipate(View view){
        DatabaseReference groupRef = null;
        User user = ourInstance.getUser();
        boolean update = false;
        switch(view.getId()){
            case R.id.btn_participate:
                update |= addUserToList(m_group.m_meeting.m_participants, user);
                update |= removeUserToList(m_group.m_meeting.m_maybe, user);
                update |= removeUserToList(m_group.m_meeting.m_decline, user);

                break;
            case R.id.btn_maybe:
                update |= addUserToList(m_group.m_meeting.m_maybe, user);
                update |= removeUserToList(m_group.m_meeting.m_decline, user);
                update |= removeUserToList(m_group.m_meeting.m_participants, user);
                break;
            case R.id.btn_decline:
                update |= addUserToList(m_group.m_meeting.m_decline, user);
                update |= removeUserToList(m_group.m_meeting.m_maybe, user);
                update |= removeUserToList(m_group.m_meeting.m_participants, user);
                break;
        }
        if(update){
            groupRef = ourInstance.getGroupref().child(m_group.m_name).child(Group.PROPERTY_MEETING).child(Meeting.PROPERTY_PARTICIPANTS);
            groupRef.setValue(m_group.m_meeting.m_participants);
            groupRef = ourInstance.getGroupref().child(m_group.m_name).child(Group.PROPERTY_MEETING).child(Meeting.PROPERTY_MAYBE);
            groupRef.setValue(m_group.m_meeting.m_maybe);
            groupRef = ourInstance.getGroupref().child(m_group.m_name).child(Group.PROPERTY_MEETING).child(Meeting.PROPERTY_DECLINE);
            groupRef.setValue(m_group.m_meeting.m_decline);
        }

    }

    public boolean addUserToList(List<User> list, User user){
        if(!list.contains(user)){
            list.add(user);
            return true;
        }
        return false;
    }

    public boolean removeUserToList(List<User> list, User user){
        if(list.contains(user)){
            list.remove(user);
            return true;
        }
        return false;
    }
    //SOURCE http://developer.android.com/training/permissions/requesting.html
    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    protected void startLocationUpdates() {

    }

    private void getAndSetUserLocationWithPermission(){

        try{
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.


                //------------------------------------------------------------------------------
                ActivityCompat.requestPermissions(MapActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_LOCATION_REQUEST_CODE);

                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(m_GoogleApiClient, m_LocationRequest, this);
            /*Location location = LocationServices.FusedLocationApi.getLastLocation(m_GoogleApiClient);
            if (location == null) {
            }
            else {
                setUserLocation(location);
            }*/
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
    }

    //SOURCE: http://android-er.blogspot.ca/2016/04/requesting-permissions-of.html

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MapActivity.this,
                            R.string.permGranted,
                            Toast.LENGTH_LONG).show();
                    getAndSetUserLocationWithPermission();

                } else {
                    Toast.makeText(MapActivity.this,
                            R.string.deniedPerm,
                            Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_GoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (m_GoogleApiClient.isConnected()) {
            m_GoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        setUserLocation(location);
    }
}
