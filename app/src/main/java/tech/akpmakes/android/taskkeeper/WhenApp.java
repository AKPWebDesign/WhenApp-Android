package tech.akpmakes.android.taskkeeper;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.google.firebase.database.FirebaseDatabase;
import com.twitter.sdk.android.core.Twitter;

public class WhenApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Twitter.initialize(this);
    }
}
