package inf8405_tp2.tp2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 422234 on 2017-02-28.
 */

public class Group {

    public String m_name_;
    public Manager m_manager_;
    private List<User> m_users;

    public  void Group(Manager manager, String name){
        this.m_manager_ = manager;
        this.m_name_ = name;
        this.m_users =  new ArrayList<User>();
    }

    public void setUsers(User user){
        this.m_users.clear();
        this.m_users.add(user);
    }

    public void addUsers(User user){
        this.m_users.add(user);
    }

    public List<User> getUsers(List<User> user){
        return this.m_users;
    }

    public void setUsers(List<User> users){
        this.m_users = users;
    }
}
