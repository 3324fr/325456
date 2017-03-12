package inf8405_tp2.tp2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle(R.string.settings);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SettingsFragment())
                .commit();
        updateCalendar();
    }

    private void updateCalendar() {
        CalendarView calendarView = (CalendarView)findViewById(R.id.simpleCalendarView);
        int day = 0;
        int month = 0;
        int year = 0;
        int hour = 0;
        int min = 0;
        try{
            User user = UserSingleton.getInstance(getApplicationContext()).getUser();
            if(user instanceof Manager){
                Group group = UserSingleton.getInstance(getApplicationContext()).getGroup();
                user = group.m_users.get(user.m_profile.m_name);
            }
            String parts1[] = user.m_date.split("-");
            year = Integer.parseInt(parts1[0]);
            month = Integer.parseInt(parts1[1]);
            day = Integer.parseInt(parts1[2]);
            String parts2[] = user.m_time.split(":");
            hour = Integer.parseInt(parts2[0]);
            min = Integer.parseInt(parts2[1]);
        }
        catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        //Month: 0-11
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, min);
        calendarView.setDate(calendar.getTimeInMillis(), true, true);
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

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }

    }

    public void onClickQuitGroup(View view){
        new AlertDialog.Builder(this)
                .setTitle(R.string.attention)
                .setMessage(R.string.quir)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String groupname = UserSingleton.getInstance(getApplicationContext()).quitGroup();
                        Toast.makeText(getApplicationContext(), getString(R.string.quir)+ " " + groupname, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PreferencesActivity.this, MainActivity.class);
                        NavUtils.navigateUpTo(PreferencesActivity.this,intent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
}
