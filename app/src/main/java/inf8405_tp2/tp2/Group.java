package inf8405_tp2.tp2;

import android.location.Location;
import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 422234 on 2017-02-28.
 */

@IgnoreExtraProperties
public class Group {

    public String m_name;
    public Manager m_manager;
    public List<User> m_users;
    public List<Meeting> m_meetings;
    public List<Place> m_places;

    public  Group(){

        this.m_users =  new ArrayList<>();
        this.m_meetings = new ArrayList<>();
        this.m_places = new ArrayList<>();
    }

    public  Group(Manager manager, String name){
        this();
        if(name.isEmpty()){
            name = "default";
        }
        this.m_manager = manager;
        this.m_name = name;

    }

    @Exclude
    public Boolean isMember(User user){
        if(this.m_users.contains(user) || user == this.m_manager) {
            return true;
        }
        else{
            return false;
        }
    }


    @Exclude
    public Boolean isManager(User user){
        if(user == this.m_manager) {
            return false;
        }
        else{
            return true;
        }
    }

    @Exclude
    public List<User> getUsers(){
        List<User> tmp = this.m_users;
        tmp.add(m_manager);
        return tmp;
    }

    @Exclude
    public Boolean updateLoc(User user,Location loc){
        if(user == m_manager){
            this.m_manager.setCurrentLocation(loc);
            return true;
        }
        else if(m_users.contains(user)) {
            this.m_users.get(this.m_users.indexOf(user))
                    .setCurrentLocation(loc);
            return true;
        }
        return false;
    }

    @Exclude
    public Location getLocation(User user){
        if(user == this.m_manager){
            return this.m_manager.getCurrentLocation();
        }
        else if(m_users.contains(user)) {
            return this.m_users.get(this.m_users.indexOf(user)).getCurrentLocation();
        }
        return null;
    }

}
