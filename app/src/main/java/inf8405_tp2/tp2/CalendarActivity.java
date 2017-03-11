package inf8405_tp2.tp2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import org.w3c.dom.Text;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {

    public static UserSingleton ourInstance;
    public static final String TIMEPICKER_TAG1 = "timepicker1";
    public static final String TIMEPICKER_TAG2 = "timePicker2";
    TimePicker timeStart;
    TimePicker timeEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
       // String userName = getIntent().getExtras().getString(MainActivity.EXTRA_USER);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle(R.string.settings);
        setSupportActionBar(myToolbar);
        ourInstance = UserSingleton.getInstance(getApplicationContext());
    }



    public void OnDateClicked(View view)
    {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker1");
        updateTextView();
    }

    private void updateTextView() {
        String date = ourInstance.getGroup().m_meeting.m_date == null ? "" : ourInstance.getGroup().m_meeting.m_date;
        String start = ourInstance.getGroup().m_meeting.m_startTime == null ? "" : ourInstance.getGroup().m_meeting.m_startTime;
        String end = ourInstance.getGroup().m_meeting.m_endTime == null ? "" : ourInstance.getGroup().m_meeting.m_endTime;
        TextView tv = (TextView)findViewById(R.id.calendar_dateOutput);
        String temp = "Date:" + date +"\n"+ "Start Time:" +  start +"\n"+ "End Time:" +  end;
        tv.setText(temp);
    }

    public void OnStartHourClicked(View view)
    {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), TIMEPICKER_TAG1);
        updateTextView();

    }
    public void OnEndHourClicked(View view)
    {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), TIMEPICKER_TAG2);
        updateTextView();
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            //Use the current time as the default values for the time picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            //Create and return a new instance of TimePickerDialog
            return new TimePickerDialog(getActivity(),this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        //onTimeSet() callback method
        public void onTimeSet(TimePicker view, int hourOfDay, int minute){
            Fragment frag = getActivity().getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG1);
            if(frag!=null){
                ourInstance.getGroup().m_meeting.m_startTime = hourOfDay+":"+minute;
            }
            frag = getActivity().getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG2);
            if(frag!=null){
                ourInstance.getGroup().m_meeting.m_endTime = hourOfDay+":"+minute;
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            ourInstance.getGroup().m_meeting.m_date = year +"-"+ month +"-"+ day;
        }
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

    public void onClickConfirm(View view){
        Group group = ourInstance.getGroup();
        DatabaseReference groupRef = ourInstance.getGroupref().child(group.m_name)
                .child(Group.PROPERTY_MEETING);
        groupRef.setValue(ourInstance.getGroup().m_meeting);
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}