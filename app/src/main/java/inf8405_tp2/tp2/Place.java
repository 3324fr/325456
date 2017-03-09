package inf8405_tp2.tp2;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by 422234 on 2017-03-04.
 */

@IgnoreExtraProperties
public class Place {

    public Place(){

    }

    public String m_name;
    public LatLng m_latlng;
    public int m_vote;

    @Exclude
    public byte[] image;

}
