package tech.akpmakes.android.taskkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.Date;

import io.fabric.sdk.android.Fabric;
import tech.akpmakes.android.taskkeeper.models.WhenEvent;

public class MainActivity extends AppCompatActivity implements AddItemDialog.AddItemDialogListener {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseRemoteConfig mRemoteConfig;
    private Query mDBQuery;
    private RecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        mAuth = FirebaseAuth.getInstance();

        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mRemoteConfig.setConfigSettings(remoteConfigSettings);
        mRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        long cacheExpiration = 43200; // 12 hours, to match Firebase best practices.
        if(mRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mRemoteConfig.fetch(cacheExpiration)
            .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mRemoteConfig.activateFetched();
                    }
                    applyRemoteConfig();
                }
            });

        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.events_list);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setHapticFeedbackEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        ItemTouchHelper mIth = new ItemTouchHelper(new SwipeHandler(this));
        mIth.attachToRecyclerView(mRecyclerView);
    }

    private void applyRemoteConfig() {
        // NOOP for now.
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInAnonymously:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInAnonymously:failure", task.getException());
                        Snackbar.make(findViewById(android.R.id.content), "Server connection failed. You may experience problems saving data if this issue persists.",
                                Snackbar.LENGTH_LONG).show();
                        updateUI(null);
                    }
                    }
                });
        } else {
            updateUI(currentUser);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    private void updateUI(FirebaseUser user) {
        if(user == null) {
            return; // TODO: display an error
        }

        mDBQuery = FirebaseDatabase.getInstance().getReference("events/" + user.getUid()).orderByChild("when");
        mAdapter = new FirebaseRecyclerAdapter<WhenEvent, WhenEventViewHolder>(
                WhenEvent.class,
                R.layout.item_whenevent,
                WhenEventViewHolder.class,
                mDBQuery) {
            @Override
            public void populateViewHolder(WhenEventViewHolder holder, WhenEvent evt, int position) {
                holder.setName(evt.getName());
                holder.setWhen(evt.getWhen());
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.add_item:
                addItem();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addItem() {
        DialogFragment addItemFragment = new AddItemDialog();
        addItemFragment.show(getSupportFragmentManager(), "addItem");
    }

    @Override
    public void onValue(String name) {
        if(mDBQuery != null) {
            mDBQuery.getRef().push().setValue(new WhenEvent(name, new Date().getTime()));
            Snackbar.make(findViewById(android.R.id.content), "Event saved successfully!",
                    Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Your event could not be saved. Please try again.",
                    Snackbar.LENGTH_LONG).show();
        }
    }

    FirebaseRecyclerAdapter getAdapter() {
        return mAdapter;
    }
}
