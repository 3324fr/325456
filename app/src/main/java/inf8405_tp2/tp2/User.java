package inf8405_tp2.tp2;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
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
    private Location m_CurrentLocation;

    public User(){}

    public User(Location loc){m_CurrentLocation = loc;}

    public User(Profile profile){
        this.m_profile = profile;
    }

    protected User(User user){
        this(user.m_profile);
    }

    @Exclude
    protected void updateLocation(Location loc){
        m_CurrentLocation = loc;
        Log.d("NewLoc", loc.getLatitude() + "");
    }
    @Exclude
    protected void updateOtherLocation(){

    }


    public Location getM_CurrentLocation() {
        return m_CurrentLocation;
    }
}
