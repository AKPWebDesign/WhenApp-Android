package tech.akpmakes.android.taskkeeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

public class PreferenceManager {
    private static final String TAG = "PreferenceManager";
    private SharedPreferences pref;
    private Context _context;

    int PRIVATE_MODE= 0;

    private static final String PREF_NAME = "when";
    private static final String FIRST_LAUNCH = "firstLaunch";
    private static final String FIRST_DAY = "firstDayOfWeek";

    public PreferenceManager(Context context) {
        this._context = context;
        this.pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    public void setFirstLaunch(boolean isFirstLaunch) {
        Log.d(TAG, "setting first launch: " + isFirstLaunch);
        this.pref.edit().putBoolean(FIRST_LAUNCH, isFirstLaunch).apply();
    }

    public boolean isFirstLaunch() {
        final boolean ret = this.pref.getBoolean(FIRST_LAUNCH, true);
        Log.d(TAG, "getting first launch: " + ret);
        return ret;
    }

    public void setFirstDayOfWeek(int day) {
        boolean invalid = 0 > day || day > Calendar.SATURDAY;
        Log.d(TAG, (invalid ? "invalid" : "setting") + " first day of week: " + day);
        if (invalid) return;
        this.pref.edit().putInt(FIRST_DAY, day);
    }

    public int getFirstDayOfWeek() {
        final int day = this.pref.getInt(FIRST_DAY, 0);
        Log.d(TAG, "setting first day of week: " + day);
        return day;
    }
}
