package inf8405_tp2.tp2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PlaceActivity extends AppCompatActivity {

    final static int PICK_IMAGE = 7;
    private LatLng m_latLng;
    private String m_groupName;
    private String mTmpGalleryPicturePath;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        this.m_latLng = getIntent().getParcelableExtra(MapActivity.MESSAGE_LAT_LNG);
        this.m_groupName = getIntent().getStringExtra(MapActivity.MESSAGE_GROUP_NAME);

        TextView textView =  (TextView) findViewById(R.id.add_place_location);
        textView.setText( "Latitude: " + this.m_latLng.latitude + "\n" +
                "Longitude: " + this.m_latLng.longitude + "\n" );

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle(R.string.settings);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onClickImage(View view){
        if(!((EditText)findViewById(R.id.editText_place_name)).getText().toString().isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            intent.setType("image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("scale", true);
            intent.putExtra("outputX", 256);
            intent.putExtra("outputY", 256);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, PICK_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {

            Place place= new Place();

            Uri selectedImage = data.getData();
            if(selectedImage!= null){
                try{
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    // todo test save place
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap converetdImage = getResizedBitmap(imageBitmap, 500);
                    converetdImage.compress(Bitmap.CompressFormat.PNG, 0, stream);
                    place.image = stream.toByteArray();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                //ImageView mImageView = (ImageView) findViewById(R.id.imageView_place);
                //mImageView.setImageBitmap(  imageBitmap);


            }
            place.m_name = ((EditText)findViewById(R.id.editText_place_name)).getText().toString();

            place.m_finalRating = 0;
            Location loc = new Location(place.m_name);
            loc.setLatitude(m_latLng.latitude);
            loc.setLongitude(m_latLng.longitude);
            place.m_loc = new SuperLocation(loc);
            UserSingleton.getInstance(getApplicationContext()).createPlace(place, m_groupName);
            Intent i = new Intent(PlaceActivity.this, MapActivity.class);
            startActivity(i);
        }
    }//onActivityResult

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
