package inf8405_tp2.tp2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class BatteryReceiver extends BroadcastReceiver {

    public static final String BATTERY_LEVEL = "inf8405_tp2.tp2.batteryLevel";
    public BatteryReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
        int percent = (level*100 / scale);
        // open battery activity
        Intent i = new Intent(context.getApplicationContext(),BatteryActivity.class);
        i.putExtra(BATTERY_LEVEL,percent);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
