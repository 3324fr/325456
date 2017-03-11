package inf8405_tp2.tp2;

import android.location.Location;
import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    public HashMap<String,User> m_users;

    @PropertyName(PROPERTY_MEETING)
    public Meeting m_meeting;

    @PropertyName(PROPERTY_PLACES)
    public List<Place> m_places;

    public  Group(){

        this.m_users =  new HashMap<>();
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
        this.m_users = new HashMap<>();
        this.m_users.put(manager.m_profile.m_name,manager);
        this.m_meeting = null;
    }

    @Exclude
    public Boolean isManager(User user){
        return user == this.m_manager ?  true : false;
    }

    @Exclude
    public List<User> getUsers(){
        return new ArrayList<>(this.m_users.values());
    }

    @Exclude
    public void add(User user){
       this.m_users.put(user.m_profile.m_name,user);
    }

    @Exclude
    public void remove(User user){
        this.m_users.remove(user.m_profile.m_name);
    }

    @Exclude
    public Boolean userAllVoted(){
        Iterator it = this.m_users.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(((User)pair.getValue()).getVote() == false){
                return false;
            }
        }
        return true;
    }
}
