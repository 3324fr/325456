package inf8405_tp2.tp2;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView dateDisplay;
    private TextView helloDisplay;
    private TimePicker tP;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        String userName = getIntent().getExtras().getString(MainActivity.EXTRA_USER);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calendarView = (CalendarView) findViewById(R.id.calendarView);

        helloDisplay = (TextView) findViewById(R.id.hello);
        helloDisplay.setText(userName);

        tP = (TimePicker) this.findViewById(R.id.time_picker1);
        tP.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // TODO:
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                // TODO:
            }
        });
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
}