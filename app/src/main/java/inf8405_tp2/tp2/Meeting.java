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

    @Exclude
    final static String PROPERTY_INFO = "moreInfo";

    @Exclude
    final static String PROPERTY_DATE = "date";

    @Exclude
    final static String PROPERTY_STARTTIME = "startTime";

    @Exclude
    final static String PROPERTY_ENDTIME = "endTime";

    private int chosenPlace;

    @PropertyName(PROPERTY_PLACE)
    public Place m_place;

    @PropertyName(PROPERTY_INFO)
    public String m_info;

    @PropertyName(PROPERTY_DATE)
    public String m_date;

    @PropertyName(PROPERTY_STARTTIME)
    public String m_startTime;

    @PropertyName(PROPERTY_ENDTIME)
    public String m_endTime;


    public Meeting(){}

    public Meeting(Place place){
        this.m_place = place;
    }


}
