package inf8405_tp2.tp2;

import android.location.Location;
import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 422234 on 2017-02-28.
 */

@IgnoreExtraProperties
final public class Group {

    @Exclude
    final static String PROPERTY_USERS = "usersList";
    final static String PROPERTY_PLACES = "placeList";
    final static String PROPERTY_MEETING = "meeting";

    public String m_name = "Default Group";
    public Manager m_manager;
    @PropertyName(PROPERTY_USERS)
    public List<User> m_users;

    @PropertyName(PROPERTY_MEETING)
    public Meeting m_meeting;

    @PropertyName(PROPERTY_PLACES)
    public List<Place> m_places;

    public  Group(){

        this.m_users =  new ArrayList<>();
        this.m_places = new ArrayList<>();
        this.m_manager = new Manager();
        this.m_meeting = null;
    }
    public  Group(Manager manager, String name){
        this();
        if(name.isEmpty()){
            name = "default";
        }
        this.m_manager = manager;
        this.m_name = name;
        this.m_places = new ArrayList<>();
        this.m_users = new ArrayList<>();
        this.m_users.add(manager);
        this.m_meeting = null;
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
        return user == this.m_manager ?  true : false;
    }

    @Exclude
    public List<User> getUsers(){
        List<User> tmp = this.m_users;
        tmp.add(m_manager);
        return tmp;
    }
    @Exclude
    public Boolean updateLoc(User user,Location loc){
        if(loc != null) {
             if (m_users.contains(user)) {
                this.m_users.get(this.m_users.indexOf(user)).setCurrentLocation(loc);
                return true;
            }
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


    @Exclude
    public Boolean userAllVoted(){
        for(User user : this.m_users){
            if(user.getVote() == false){
                return false;
            }
        }
        return true;
    }
}
