package inf8405_tp2.tp2;

import android.location.Location;
import android.location.LocationManager;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by 422234 on 2017-03-06.
 */

@IgnoreExtraProperties
public class SuperLocation extends Location {
    protected SuperLocation(){
        super(LocationManager.GPS_PROVIDER);
    }
}
