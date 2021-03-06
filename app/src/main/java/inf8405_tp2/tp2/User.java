package inf8405_tp2.tp2;

import android.location.Location;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

/**
 * Created by 422234 on 2017-02-17.
 */
@IgnoreExtraProperties
public class User {

    @Exclude
    final static String PROPERTY_LOCATION = "Location";
    @Exclude
    final static String PROPERTY_VOTE = "Vote";
    @Exclude
    final static String PROPERTY_USERDATE = "Date";
    @Exclude
    final static String PROPERTY_USERTIME = "Time";

    public Profile m_profile;

    @PropertyName(PROPERTY_USERDATE)
    public String m_date;

    @PropertyName(PROPERTY_USERTIME)
    public String m_time;

    @PropertyName(PROPERTY_VOTE)
    public boolean m_vote;

    @PropertyName(PROPERTY_LOCATION)
    public SuperLocation m_CurrentLocation;

    public User(){
        this.m_profile = new Profile();
    }

    public User(Profile profile){
        this.m_profile = profile;
    }

    protected User(User user){
        this(user.m_profile);
    }
    @Exclude
    public SuperLocation getCurrentLocation() {
        return this.m_CurrentLocation;
    }
    @Exclude
    public void setCurrentLocation(Location loc){
        this.m_CurrentLocation = new SuperLocation(loc);
    }

    @Exclude
    public void setVote(boolean value){
        this.m_vote = value;
    }

    @Exclude
    public Boolean getVote(){
        return this.m_vote;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof User)) {
            return false;
        }
        User that = (User) other;
        // Custom equality check here.
        return this.m_profile.equals(that.m_profile);
    }

    @Override
    public int hashCode() {
        return m_profile.hashCode();
    }
}
