package inf8405_tp2.tp2;


import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private String m_TemporateUserName;
    public final static String EXTRA_USER_NAME = "inf8405_tp2.tp2.USER_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void picture(View view) {
        // Do something in response to button
        Intent intent = new Intent(MainActivity.this, TakePictureActivity.class);
        startActivity(intent);
    }

    public void OnClickConfirm(View view){
        m_TemporateUserName = ((EditText)findViewById(R.id.et_username)).getText().toString();
        Toast.makeText(this, m_TemporateUserName, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, TakePictureActivity.class);
        intent.putExtra(EXTRA_USER_NAME, m_TemporateUserName);
        startActivity(intent);

//        DatabaseHelper mDbHelper = new DatabaseHelper(getApplicationContext());
//        SQLiteDatabase db = mDbHelper.getReadableDatabase();
//        Profile profile = Profile.gourde(db,m_TemporateUserName,m_TemporateUserName);
//        byte[] imgbytes = profile.picture_;
//        Bitmap imageBitmap =  BitmapFactory.decodeByteArray(imgbytes, 0,
//                imgbytes.length);;
//        ImageView mImageView = (ImageView) findViewById(R.id.sadsad);
//        mImageView.setImageBitmap(imageBitmap);






    }

    public void OnClickGroup(View view) {
    }




    private class GenericTextWatcher implements TextWatcher {

        private View view;
        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
            switch(view.getId()){
                case R.id.et_username:
                    break;
            }
        }

        public void afterTextChanged(Editable editable)
        {
        }
    }
}
