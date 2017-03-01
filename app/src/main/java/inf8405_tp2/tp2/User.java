package inf8405_tp2.tp2;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by 422234 on 2017-02-17.
 */

public class User {

    public User(){}

    public User(Profile profile){
        this.profile_ = profile;
    }

    public Profile profile_;
    public Group group_;

}
