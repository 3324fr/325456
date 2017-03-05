package inf8405_tp2.tp2;

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
    private List<User> m_users;
    private List<Meeting> m_meetings;

    public void Group(){Group(new Manager(new User()), "");}

    public  void Group(Manager manager, String name){
        this.m_manager = manager;
        this.m_name = name;
        this.m_users =  new ArrayList<User>();
    }

    @Exclude
    public void setUsers(User user){
        this.m_users.clear();
        this.m_users.add(user);
    }

    @Exclude
    public void addUsers(User user){
        this.m_users.add(user);
    }

    @Exclude
    public List<User> getUsers(){
        return this.m_users;
    }

    @Exclude
    public void resetUsers(){
        this.m_users = new ArrayList<>();
    }

    @Exclude
    public void setUsers(List<User> users){
        this.m_users = users;
    }
}
