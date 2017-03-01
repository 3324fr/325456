package inf8405_tp2.tp2;

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

    public final static String USER = "inf8405_tp2.tp2.User";


    private RelativeLayout m_CurrentLayout;
    static private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_CurrentLayout = (RelativeLayout) findViewById(R.id.activity_main);
        EditText editTextPrice = (EditText) findViewById(R.id.et_username);
        editTextPrice.addTextChangedListener(new GenericTextWatcher(editTextPrice));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            user.profile_.picture_ = imageBitmap;
            user.profile_.save(getApplicationContext());

            ImageView mImageView = (ImageView) findViewById(R.id.picture);
            mImageView.setImageBitmap(user.profile_.picture_);

        }
    }//onActivityResult

    private void picture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void OnClickConfirm(View view) {

        EditText editText_userName = ((EditText) findViewById(R.id.et_username));
        EditText editText_group = ((EditText) findViewById(R.id.et_groupname));
        String m_UserName = editText_userName.getText().toString();
        String m_GroupName = editText_group.getText().toString();

        Toast.makeText(this, m_UserName + m_GroupName, Toast.LENGTH_SHORT).show();

        Profile profile = Profile.get(getApplicationContext(), m_UserName, m_GroupName);

        if (profile == null) {
            user.profile_ = new Profile(m_UserName, m_GroupName);
            picture();
        } else {
            user.profile_ = profile;
            ImageView mImageView = (ImageView) findViewById(R.id.picture);
            mImageView.setImageBitmap(user.profile_.picture_);
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
