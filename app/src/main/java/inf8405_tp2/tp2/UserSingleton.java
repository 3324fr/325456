package inf8405_tp2.tp2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 422234 on 2017-03-05.
 * This singleton contains login information
 */
public class UserSingleton {

    private static DatabaseHelper m_sqLitehelper;
    private static FirebaseDatabase m_FirebaseDatabase;
    private static FirebaseStorage m_FirebaseStorage;
    private static DatabaseReference m_GroupRef;
    private static StorageReference m_UserPictureRef;
    private static StorageReference m_PlacePictureRef;

    private static Context m_Ctx;
    private static UserSingleton ourInstance;

    private  static User m_user;
    private  static Group m_group;
    private AlertDialog m_alert;

    public static UserSingleton getInstance(Context context) {

        if (ourInstance == null) {
            m_user = new User();
            m_sqLitehelper = new DatabaseHelper(context.getApplicationContext());
            m_FirebaseStorage = FirebaseStorage.getInstance();
            FirebaseAuth.getInstance().signInAnonymously();
            m_FirebaseDatabase = FirebaseDatabase.getInstance();
            m_UserPictureRef = m_FirebaseStorage.getReference("UserPic");
            m_PlacePictureRef = m_FirebaseStorage.getReference("PlacePic");
            m_GroupRef = m_FirebaseDatabase.getReference("Group's list");
            ourInstance = new UserSingleton(context.getApplicationContext());
            m_group = new Group();
            Log.d("newUserSingle", "Re instantiation of ourInstance");
        }
        return ourInstance;
    }

    private UserSingleton(Context context) {
        m_Ctx = context;
    }

    public String[] getAllUsername() {
        SQLiteDatabase db_read =  UserSingleton.m_sqLitehelper.getReadableDatabase();
        final List<String> listSQLite =  Profile.getAllUsername(db_read);
        db_read.close();
        m_GroupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    try{
                        final Group group = postSnapshot.getValue(Group.class);
                        for (User user : group.getUsers()) {
                            if(user != null && user.m_profile != null){
                                final String profileName = user.m_profile.m_name;
                                if (!listSQLite.contains(profileName)) {
                                    m_UserPictureRef.child(profileName).getBytes(Long.MAX_VALUE)
                                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                @Override
                                                public void onSuccess(byte[] bytes) {
                                                    // Use the bytes to display the image
                                                    Bitmap bitmap = BitmapFactory
                                                            .decodeByteArray(bytes, 0, bytes.length);
                                                    Profile profile = new Profile(profileName, bitmap);
                                                    SQLiteDatabase db_write =
                                                            UserSingleton.m_sqLitehelper
                                                                    .getWritableDatabase();
                                                    profile.save(db_write);
                                                    db_write.close();
                                                }
                                            });
                                }
                            }
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                System.out.println("The getAllUsername read failed: " + databaseError.getCode());
            }
        });

        SQLiteDatabase db =  UserSingleton.m_sqLitehelper.getReadableDatabase();
        List<String> returnList =  Profile.getAllUsername(db);
        db.close();

        return returnList.toArray(new String[0]);
    }

    public Profile getUserProfile(String username){
        SQLiteDatabase db =  UserSingleton.m_sqLitehelper.getReadableDatabase();
        Profile profile =  Profile.get(db,username);
        db.close();
        return profile;
    }
    public Profile login(String userName){
        Profile profile = getUserProfile(userName);
        UserSingleton.m_user.m_profile = profile;

        return profile;
    }

    public List<Profile> getAllUserProfiles(){
        List<Profile> profiles = new ArrayList<>();
        for(User user : getGroup().getUsers()){
            profiles.add(getUserProfile(user.m_profile.m_name));
        }
        return profiles;
    }

    public Profile getUserProfile(){
        return   UserSingleton.m_user.m_profile;
    }

    public static User getUser() {
        return m_user;
    }

    public void setM_user(Profile profile) {
        SQLiteDatabase db = m_sqLitehelper.getWritableDatabase();
        profile.save(db, m_UserPictureRef);
        db.close();
        UserSingleton.m_user.m_profile = profile;
    }

    public void addUser2Group(final String groupName) {
        if(!groupName.isEmpty()) {
            final DatabaseReference groupRef = m_GroupRef.child(groupName);
            // Attach a listener to read the data at our posts reference
            groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
               // todo try catch

                    Group group = null;
                    try{group = dataSnapshot.getValue(Group.class);}
                    catch (Exception e) {//todo
                        e.printStackTrace();
                    }

                    if (group == null) {
                        Manager manager = new Manager(UserSingleton.m_user);
                        m_group = new Group(manager, groupName);
                        m_user = manager;
                        groupRef.setValue(m_group);
                    } else {
                        m_group  = group;
                        if ( m_group.m_manager != null && m_group.m_manager.equals(UserSingleton.m_user)) {
                            // promote user to manager
                            UserSingleton.m_user =  m_group.m_manager;
                        }
                        if (!m_group.m_users.containsKey(UserSingleton.m_user.m_profile.m_name))
                        {
                            m_group.add(m_user);
                            groupRef.child(Group.PROPERTY_USERS).child(UserSingleton.m_user.m_profile.m_name).setValue(m_user);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }
    }

    public String quitGroup(){
        if(m_group == null || m_group.m_name.isEmpty())
            return "";
        if(m_user.m_profile.m_name != null){
            m_group.remove(m_user);
            m_GroupRef.child(m_group.m_name).child(Group.PROPERTY_USERS)
                    .child(m_user.m_profile.m_name).removeValue();
        }
        return m_group.m_name;
    }

    public DatabaseReference getGroupref() {
        return this.m_GroupRef;
    }

    public StorageReference getPlaceImageStorage() {
        return this.m_PlacePictureRef;
    }
    public Group getGroup() {
        return this.m_group;
    }

    public void setGroup(Group grp) {
        this.m_group = grp;
    }

    public void createPlace(final Place place, String groupName) {

        String placeName = place.m_name;
        if(!groupName.isEmpty() && !placeName.isEmpty()) {
            final DatabaseReference groupRef = m_GroupRef.child(groupName);
            // Attach a listener to read the data at our group reference
            groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // todo try catch
                    Group group = dataSnapshot.getValue(Group.class);

                    if (group != null) {
                        group.m_places.add(place);
                        groupRef.setValue(group);
                        // Save image
                        if(place.image != null){
                            m_PlacePictureRef.child(place.m_name).putBytes(place.image);
                        }
                        m_group = group;
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }

    }

    public void updateCalendarWithMeeting(Context context){
        if(m_alert != null){
            m_alert.dismiss();
        }
        m_alert = new AlertDialog.Builder(context)
                .setTitle(R.string.mark_calendar)
                .setMessage(R.string.highlight_calendar)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        User user = m_group.m_users.get(ourInstance.getUser());
                        if(user!= null){
                            user.m_date = m_group.m_meeting.m_date;
                            user.m_time = m_group.m_meeting.m_startTime;
                        }
                        ourInstance.getUser().m_date = m_group.m_meeting.m_date;
                        ourInstance.getUser().m_time = m_group.m_meeting.m_startTime;
                        DatabaseReference groupRef = ourInstance.getGroupref().child(m_group.m_name)
                                .child(Group.PROPERTY_USERS).child(ourInstance.getUser().m_profile.m_name).child(User.PROPERTY_USERDATE);
                        groupRef.setValue(ourInstance.getUser().m_date);
                        groupRef = ourInstance.getGroupref().child(m_group.m_name)
                                .child(Group.PROPERTY_USERS).child(ourInstance.getUser().m_profile.m_name).child(User.PROPERTY_USERTIME);
                        groupRef.setValue(ourInstance.getUser().m_time);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}

