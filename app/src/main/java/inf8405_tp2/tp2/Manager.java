package inf8405_tp2.tp2;

import com.google.firebase.database.Exclude;

/**
 * Created by 422234 on 2017-02-17.
 */

public class Manager extends User {

    public Manager() {
        super();
    }
    public Manager(User user) {
        super(user);
    }

    @Exclude
    @Override
    public void accept(MapActivity activity){
        activity.visit(this);
    }


}
