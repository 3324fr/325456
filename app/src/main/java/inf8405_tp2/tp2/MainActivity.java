package inf8405_tp2.tp2;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

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

}
