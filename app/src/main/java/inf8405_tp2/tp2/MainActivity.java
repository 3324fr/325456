package inf8405_tp2.tp2;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import inf8405_tp2.tp2.user.NewPostActivity;
import inf8405_tp2.tp2.user.Post;


public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private UserFragment m_UserFragment;

    private FirebaseDatabase m_FirebaseDatabase;

    public final static String TAG_RETAINED_USER = "inf8405_tp2.tp2.UserFragment";

    private DatabaseReference m_Database;
    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";
    private static final String m_TestUserId = "steakUser";
    private static final String m_TestTitle = "steakTitle";
    private static final String m_TestBody = "steakBody";

    private RelativeLayout m_CurrentLayout;

    private String m_UserName;
    private String m_GroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_CurrentLayout = (RelativeLayout) findViewById(R.id.activity_main);
        EditText editTextPrice = (EditText) findViewById(R.id.et_username);
        editTextPrice.addTextChangedListener(new GenericTextWatcher(editTextPrice));

        // find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        m_UserFragment = (UserFragment) fm.findFragmentByTag(TAG_RETAINED_USER);

        // create the fragment and data the first time
        if (m_UserFragment == null) {
            // add the fragment
            m_UserFragment = new UserFragment();
            fm.beginTransaction().add(m_UserFragment, TAG_RETAINED_USER).commit();

            m_UserFragment.setData(new User(new Profile("User Name", "Group Name")));
        }

        m_FirebaseDatabase = FirebaseDatabase.getInstance();

    }
    @Override
    public void onResume(){
        super.onResume();
        ImageView mImageView = (ImageView) findViewById(R.id.picture);
        mImageView.setImageBitmap( m_UserFragment.getData().profile_.picture_);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            m_UserFragment.getData().profile_.picture_ = imageBitmap;
            m_UserFragment.getData().profile_.save(getApplicationContext());

        }
    }//onActivityResult

    private void picture() {

    }

    public void OnClickConfirm(View view) {

        EditText editText_userName = ((EditText) findViewById(R.id.et_username));
        EditText editText_group = ((EditText) findViewById(R.id.et_groupname));
        m_UserName = editText_userName.getText().toString();
        m_GroupName = editText_group.getText().toString();

        Toast.makeText(this, m_UserName + m_GroupName, Toast.LENGTH_SHORT).show();

        Profile profile = Profile.get(getApplicationContext(), m_UserName, m_GroupName);

        if (profile == null) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

            m_UserFragment.setData(new User(new Profile(m_UserName, m_GroupName)));
            picture();
        } else {
            m_UserFragment.setData(new User(profile));
        }
    }

    public void OnClickLogin(View view) {

        EditText editText_userName = ((EditText) findViewById(R.id.et_username));
        EditText editText_group = ((EditText) findViewById(R.id.et_groupname));
        String m_UserName = editText_userName.getText().toString();
        String m_GroupName = editText_group.getText().toString();

        Toast.makeText(this, m_UserName + m_GroupName, Toast.LENGTH_SHORT).show();

        Profile profile = Profile.get(getApplicationContext(), m_UserName, m_GroupName);

        //m_Database = FirebaseDatabase.getInstance().getReference();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("22222gfdgdf");
    }

    private void submitPost() {
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        //final String userId = getUid();
        m_Database.child("users").child("").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + m_TestUserId + " is unexpectedly null");
                            Toast.makeText(MainActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(m_TestUserId, m_UserName, m_TestTitle, m_TestBody);
                        }

                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
        // [END single_value_read]
    }

    private void writeNewPost(String userId, String username, String title, String body) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = m_Database.child("posts").push().getKey();
        Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        m_Database.updateChildren(childUpdates);
    }

    public void OnClickGroup(View view) {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }

    private class GenericTextWatcher implements TextWatcher {

        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (view.getId()) {
                case R.id.et_username:
                    break;
                case R.id.et_groupname:
                    break;
            }
        }
        public void afterTextChanged(Editable editable) {
        }
    }

}
