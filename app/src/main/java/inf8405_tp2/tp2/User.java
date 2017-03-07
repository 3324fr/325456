package inf8405_tp2.tp2;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Created by 422234 on 2017-02-17.
 */
@IgnoreExtraProperties
public class User {

    public Profile m_profile;

    public SuperLocation m_CurrentLocation;

    public User(){
        this.m_profile = new Profile();
        this.m_CurrentLocation = new SuperLocation();
    }

    public User(Profile profile){
        this();
        this.m_profile = profile;

    }

    protected User(User user){
        this(user.m_profile);
    }
    @Exclude
    public Location getCurrentLocation() {
        return m_CurrentLocation;
    }
    @Exclude
    public void setCurrentLocation(Location loc){
        this.m_CurrentLocation = (SuperLocation) loc;

        Log.d("NewLoc", loc.getLatitude() + "");
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof User)) {
            return false;
        }
        User that = (User) other;

        // Custom equality check here.
        return this.m_profile.equals(that.m_profile);
    }

}
