package inf8405_tp2.tp2;

import android.location.Location;

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

    public  Group(){
        this.m_users =  new ArrayList<>();
    }

    public  Group(Manager manager, String name){
        if(name.isEmpty()){
            name = "default";
        }
        this.m_manager = manager;
        this.m_name = name;
        this.m_users =  new ArrayList<>();
    }

    @Exclude
    public void setUsers(User user){
        this.m_users.clear();
        this.m_users.add(user);
    }

    @Exclude
    public Boolean addUsers(User user){
        if(this.m_users.contains(user)) {
            return false;
        }
        else{
            this.m_users.add(user);
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
            m_manager.setCurrentLocation(loc);
            return true;
        }
        else if(m_users.contains(user)) {
            m_users.get(m_users.indexOf(user))
                    .setCurrentLocation(loc);
            return true;
        }
        return false;
    }

    @Exclude
    public Location getLocation(User user){
        if(user == m_manager){
            return m_manager.getCurrentLocation();
        }
        else if(m_users.contains(user)) {
            return m_users.get(m_users.indexOf(user)).getCurrentLocation();
        }
        return null;
    }

}
