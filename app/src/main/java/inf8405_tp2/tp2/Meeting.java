package inf8405_tp2.tp2;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.List;

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

    @Exclude
    final static String PROPERTY_PARTICIPANTS = "participants";

    @Exclude
    final static String PROPERTY_MAYBE = "maybe";

    @Exclude
    final static String PROPERTY_DECLINE = "decline";

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

    @PropertyName(PROPERTY_PARTICIPANTS)
    public List<User> m_participants;

    @PropertyName(PROPERTY_MAYBE)
    public List<User> m_maybe;

    @PropertyName(PROPERTY_DECLINE)
    public List<User> m_decline;

    public Meeting(){
        m_participants = new ArrayList<>();
        m_maybe = new ArrayList<>();
        m_decline = new ArrayList<>();
    }

    public Meeting(Place place){
        this.m_place = place;
        m_participants = new ArrayList<>();
        m_maybe = new ArrayList<>();
        m_decline = new ArrayList<>();
    }
}
