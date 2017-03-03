package inf8405_tp2.tp2;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by 422234 on 2017-02-17.
 */

public class User {

    public Profile m_profile;
    public Group m_group;

    public User(){}

    public User(Profile profile){
        this.m_profile = profile;
    }

    protected User(User user){
        this(user.m_profile);
    }

}
