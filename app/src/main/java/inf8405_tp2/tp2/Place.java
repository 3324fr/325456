package inf8405_tp2.tp2;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 422234 on 2017-03-04.
 */

@IgnoreExtraProperties
public class Place {

    public Place(){
    }

    @Exclude
    final static String PROPERTY_RATING = "Rating";

    public String m_name;
    public SuperLocation m_loc;
    public List<Double> m_rating = new ArrayList<>();

    @PropertyName(PROPERTY_RATING)
    public int m_finalRating;

    @Exclude
    public void calculateRating(){
        double temp = 0;
        for(Double rate : m_rating){
            temp+=rate;
        }
        m_finalRating = (int)temp / m_rating.size();
    }

    @Exclude
    public byte[] image;

}
