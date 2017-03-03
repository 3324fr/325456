package inf8405_tp2.tp2;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by 422234 on 2017-02-17.
 */

public class User {

    public Profile m_profile;
    public Group m_group;
    private Location m_CurrentLocation;
    private ArrayList<User> m_ListUser;

    public User(){}

    public User(Profile profile){
        this.m_profile = profile;
    }

    protected User(User user){
        this(user.m_profile);
    }

    protected void updateLocation(Location loc){
        m_CurrentLocation = loc;
        Log.d("NewLoc", loc.getLatitude() + "");
    }

    protected void updateOtherLocation(){

    }


    public Location getM_CurrentLocation() {
        return m_CurrentLocation;
    }
}
