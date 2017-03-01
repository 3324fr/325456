package inf8405_tp2.tp2;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private UserFragment mUserFragment;

    public final static String TAG_RETAINED_USER = "inf8405_tp2.tp2.UserFragment";


    private RelativeLayout m_CurrentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_CurrentLayout = (RelativeLayout) findViewById(R.id.activity_main);
        EditText editTextPrice = (EditText) findViewById(R.id.et_username);
        editTextPrice.addTextChangedListener(new GenericTextWatcher(editTextPrice));

        // find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        mUserFragment = (UserFragment) fm.findFragmentByTag(TAG_RETAINED_USER);

        // create the fragment and data the first time
        if (mUserFragment == null) {
            // add the fragment
            mUserFragment = new UserFragment();
            fm.beginTransaction().add(mUserFragment, TAG_RETAINED_USER).commit();

            mUserFragment.setData(new User(new Profile("User Name", "Group Name")));
        }


    }
    @Override
    public void onResume(){
        super.onResume();
        ImageView mImageView = (ImageView) findViewById(R.id.picture);
        mImageView.setImageBitmap( mUserFragment.getData().profile_.picture_);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mUserFragment.getData().profile_.picture_ = imageBitmap;
            mUserFragment.getData().profile_.save(getApplicationContext());





        }
    }//onActivityResult

    private void picture() {

    }

    public void OnClickConfirm(View view) {

        EditText editText_userName = ((EditText) findViewById(R.id.et_username));
        EditText editText_group = ((EditText) findViewById(R.id.et_groupname));
        String m_UserName = editText_userName.getText().toString();
        String m_GroupName = editText_group.getText().toString();

        Toast.makeText(this, m_UserName + m_GroupName, Toast.LENGTH_SHORT).show();

        Profile profile = Profile.get(getApplicationContext(), m_UserName, m_GroupName);

        if (profile == null) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

            mUserFragment.setData(new User(new Profile(m_UserName, m_GroupName)));
            picture();
        } else {
            mUserFragment.setData(new User(profile));
        }
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
