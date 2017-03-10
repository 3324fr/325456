package inf8405_tp2.tp2;

import android.location.Location;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

/**
 * Created by 422234 on 2017-03-03.
 */

public class Meeting {

    @Exclude
    final static String PROPERTY_PLACE = "Place";

    private int chosenPlace;

    @PropertyName(PROPERTY_PLACE)
    public Place m_place;

    public Meeting(){}

    public Meeting(Place place){
        this.m_place = place;
    }


}
