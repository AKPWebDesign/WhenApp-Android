package tech.akpmakes.android.taskkeeper;

import android.app.Application;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.database.FirebaseDatabase;

public class WhenApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        String testLabSetting = Settings.System.getString(getContentResolver(), "firebase.test.lab");
        if ("true".equals(testLabSetting)) {
            // Kill the process if we're running in Test Lab. We don't like Test Lab at the moment.
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
