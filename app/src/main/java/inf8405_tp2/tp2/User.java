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
    public double m_longitude;
    public double m_latitude;


    public User(){
        this.m_profile = new Profile();


    }


    public User(Profile profile){
        this.m_profile = profile;
    }

    protected User(User user){
        this(user.m_profile);
    }

    @Exclude
    protected void updateLocation(Location loc){
        this.m_longitude = loc.getLongitude();
        this.m_latitude = loc.getLatitude();

        Log.d("NewLoc", loc.getLatitude() + "");
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof User)) {
            return false;
        }
        User that = (User) other;

        // Custom equality check here.
        return this.m_profile.m_name.equals(that.m_profile.m_name);
    }


    public Location getM_CurrentLocation() {
        Location temp = new Location(LocationManager.GPS_PROVIDER);
        temp.setLatitude(m_latitude);
        temp.setLongitude(m_longitude);
        return temp;
    }
}
