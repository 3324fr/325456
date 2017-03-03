package inf8405_tp2.tp2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 422234 on 2017-02-28.
 */

public class Group {

    public String name_;
    public Manager manager_;
    private List<User> users_;

    public  void Group(Manager manager, String name){
        this.manager_ = manager;
        this.name_ = name;
        this.users_ =  new ArrayList<User>();
    }

    public void setUsers(User user){
        this.users_.clear();
        this.users_.add(user);
    }

    public void addUsers(User user){
        this.users_.add(user);
    }

    public List<User> getUsers(List<User> user){
        return this.users_;
    }

    public void setUsers(List<User> users){
        this.users_ = users;
    }
}
