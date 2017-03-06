package inf8405_tp2.tp2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import inf8405_tp2.tp2.user.Post;

/**
 * Created by 422234 on 2017-03-05.
 */
public class UserSingleton {

    private static DatabaseHelper m_sqLitehelper;
    private static FirebaseDatabase m_FirebaseDatabase;
    private static FirebaseStorage m_FirebaseStorage;
    private static DatabaseReference m_GroupRef;
    private static StorageReference m_UserPictureRef;

    private static Context m_Ctx;
    private static UserSingleton ourInstance;

    private  static User m_user;
    private  static Group m_group;
    private  static Boolean m_isLogin = false;

    public static UserSingleton getInstance(Context context) {

        if (ourInstance == null) {
            m_user = new User();
            m_sqLitehelper = new DatabaseHelper(context.getApplicationContext());
            m_FirebaseStorage = FirebaseStorage.getInstance();
            m_FirebaseDatabase = FirebaseDatabase.getInstance();
            m_UserPictureRef = m_FirebaseStorage.getReference("UserPic");
            m_GroupRef = m_FirebaseDatabase.getReference("Group's list");
            ourInstance = new UserSingleton(context.getApplicationContext());
        }
        return ourInstance;
    }

    private UserSingleton(Context context) {
        m_Ctx = context;
    }


    public Boolean isLogin(){
        return m_isLogin;
    }

    public String[] getAllUsername() {
        SQLiteDatabase db =  UserSingleton.m_sqLitehelper.getReadableDatabase();
        String[] list =  Profile.getAllUsername(db);
        db.close();
        return list;
    }

    public Profile getUserProfile(String username){
        SQLiteDatabase db =  UserSingleton.m_sqLitehelper.getReadableDatabase();
        Profile profile =  Profile.get(db,username);
        db.close();
        if(profile != null){
            UserSingleton.m_user.m_profile = profile;
            UserSingleton.m_isLogin = true;
        }
        return profile;
    }

    public Profile getUserProfile(){
        return   UserSingleton.m_user.m_profile;
    }

    public static User getM_user() {
        return m_user;
    }

    public static Group getM_group() {
        return m_group;
    }

    public void setM_user(Profile profile) {
        UserSingleton.m_isLogin = true;
        SQLiteDatabase db = m_sqLitehelper.getWritableDatabase();
        profile.save(db, m_UserPictureRef);
        db.close();
        UserSingleton.m_user.m_profile = profile;
    }

    public void setM_group(final String groupName) {
        final DatabaseReference groupRef =  m_GroupRef.child(groupName);
        // Attach a listener to read the data at our posts reference
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserSingleton.m_group = dataSnapshot.getValue(Group.class);

                if(UserSingleton.m_group == null){
                    Manager manager = new Manager(UserSingleton.m_user);
                    UserSingleton.m_group = new Group(manager, groupName);
                    m_user = manager;
                    groupRef.setValue(UserSingleton.m_group);
                }
                else {
                    if(UserSingleton.m_group.addUsers(UserSingleton.m_user)) {
                        groupRef.setValue(UserSingleton.m_group);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserSingleton.m_group = dataSnapshot.getValue(Group.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }
    public void setLocation(Location loc) {
        if(m_group.updateLoc(m_user,loc)){
        m_GroupRef.child(UserSingleton.m_group.m_name).setValue(UserSingleton.m_group);
        }
    }
}
