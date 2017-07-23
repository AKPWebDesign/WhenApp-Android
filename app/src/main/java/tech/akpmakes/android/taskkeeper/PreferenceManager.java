package tech.akpmakes.android.taskkeeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PreferenceManager {
    private static final String TAG = "PreferenceManager";
    private SharedPreferences pref;
    private Context _context;

    int PRIVATE_MODE= 0;

    private static final String PREF_NAME = "when";
    private static final String FIRST_LAUNCH = "firstLaunch";

    public PreferenceManager(Context context) {
        this._context = context;
        this.pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    public void setFirstLaunch(boolean isFirstLaunch) {
        Log.d(TAG, "setting first launch: " + isFirstLaunch);
        this.pref.edit().putBoolean(FIRST_LAUNCH, isFirstLaunch).apply();
    }

    public boolean isFirstLaunch() {
        Log.d(TAG, "getting first launch: " + this.pref.getBoolean(FIRST_LAUNCH, true));
        return this.pref.getBoolean(FIRST_LAUNCH, true);
    }
}
