package inf8405_tp2.tp2;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public final static String TAG_RETAINED_USER = "inf8405_tp2.tp2.UserFragment";

    private DialogFragment m_userFragment;
    private Profile m_profile;
    private UserSingleton ourInstance;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ourInstance = UserSingleton.getInstance(getApplicationContext());

        // find the retained fragment on activity restarts
        // this fragment open a login dialog
        fm = getSupportFragmentManager();
        m_userFragment = (UserFragment) fm.findFragmentByTag(TAG_RETAINED_USER);
    }

    @Override
    public void onResume(){
        super.onResume();
        UserSingleton userS= UserSingleton.getInstance(getApplicationContext());

        // create the fragment and data the first time
        if (m_userFragment == null) {
            // add the fragment
            m_userFragment = new UserFragment();
            fm.beginTransaction().add(m_userFragment, TAG_RETAINED_USER).commit();
        }

        m_profile = userS.getUserProfile();
        picture();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            m_profile.m_picture = imageBitmap; // a profile contains a picture
            UserSingleton.getInstance(getApplicationContext()).setM_user(m_profile); // save profile in SQLite
            picture();
        }
    }//onActivityResult

    private void picture() {//Display picture
        ImageView mImageView = (ImageView) findViewById(R.id.picture);
        if(m_profile.m_picture != null){
            mImageView.setImageBitmap( m_profile.m_picture);
        }
    }

    public void OnClickConfirm(String username) {

        if(!username.isEmpty() && !username.matches("^-?\\d+$")) {
            UserSingleton userS = UserSingleton.getInstance(getApplicationContext());
            Toast.makeText(this, getString(R.string.hello)+ " " + username, Toast.LENGTH_SHORT).show();
            Profile profile = userS.login(username); //log user

            if (profile == null) { //check if the profile already exist or take a photo is not
                m_profile = new Profile();
                m_profile.m_name = username;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } else {
                m_profile = profile;
                picture();
            }
            m_userFragment.dismiss();
        }
    }
    public void setting(View view){
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent); //start setting activity
    }

    public void OnClickUsername(View view) {
        m_userFragment = new UserFragment(); //open fragment dialog in order to choose a new login
        fm.beginTransaction().add(m_userFragment, TAG_RETAINED_USER).commit();
    }

    public void OnClickGMap(View view) { // start the application
        EditText editText_group = ((EditText) findViewById(R.id.et_groupname));
        String groupName = editText_group.getText().toString();
        if(!groupName.isEmpty() && !groupName.matches("^-?\\d+$")) {
            ourInstance.addUser2Group(groupName);
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        }
    }

    public static class UserFragment extends DialogFragment  {
        static UserFragment newInstance() {
            return new UserFragment();
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.fragment_user, container, false);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_dropdown_item_1line, UserSingleton.getInstance(getContext()).getAllUsername());
            final AutoCompleteTextView textView = (AutoCompleteTextView)
                    v.findViewById(R.id.editText_username);
            textView.setAdapter(adapter);

            // Show soft keyboard automatically
            textView.requestFocus();

            // Watch for button clicks.
            Button button = (Button)v.findViewById(R.id.btn_username);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String username = textView.getText().toString();
                    // When button is clicked, call up to owning activity.
                    ((MainActivity)getActivity()).OnClickConfirm(username);
                }
            });

            this.getDialog().setCanceledOnTouchOutside(false);

            return v;
        }

        // this method is only called once for this fragment
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // retain this fragment
            setRetainInstance(true);
        }

        @Override
        public void onDestroyView()
        {
            Dialog dialog = getDialog();

            // Work around bug: http://code.google.com/p/android/issues/detail?id=17423
            if ((dialog != null) && getRetainInstance())
                dialog.setDismissMessage(null);

            super.onDestroyView();
        }
    }
}
