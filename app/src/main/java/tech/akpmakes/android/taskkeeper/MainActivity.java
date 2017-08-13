package tech.akpmakes.android.taskkeeper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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

import io.fabric.sdk.android.Fabric;
import tech.akpmakes.android.taskkeeper.models.WhenEvent;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    private static final String TAG = "MainActivity";
    private static final int WHEN_EVENT_REQUEST = 6900;
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
        mAuth.addAuthStateListener(this);

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

    public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
        updateUI(auth.getCurrentUser());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAuth();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    private void updateAuth() {
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
                                Snackbar.make(findViewById(android.R.id.content), R.string.sign_in_failure,
                                        Snackbar.LENGTH_LONG).show();
                                updateUI(null);
                            }
                        }
                    });
        } else {
            updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser user) {
        if(user == null) {
            return; // TODO: display an error
        }

        final Activity activity = this;
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
            @Override
            public WhenEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                WhenEventViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                viewHolder.setOnClickListener(new WhenEventViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {}

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Intent i = new Intent(activity, TaskViewActivity.class);
                        WhenEvent evt = (WhenEvent) mAdapter.getItem(position);
                        i.putExtra("whenName", evt.getName());
                        i.putExtra("whenTime", evt.getWhen());
                        i.putExtra("whenKey", mAdapter.getRef(position).getKey());
                        startActivityForResult(i, WHEN_EVENT_REQUEST);
                    }
                });
                return viewHolder;
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
                return super.onOptionsItemSelected(item);
            case R.id.add_item:
                startActivityForResult(new Intent(this, TaskViewActivity.class), WHEN_EVENT_REQUEST);
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == WHEN_EVENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                WhenEvent evt = new WhenEvent(
                        data.getStringExtra("whenName"),
                        data.getLongExtra("whenTime", 0)
                );

                if (mDBQuery != null) {
                    if(data.hasExtra("whenKey")) {
                        mDBQuery.getRef().child(data.getStringExtra("whenKey")).setValue(evt);
                    } else {
                        mDBQuery.getRef().push().setValue(evt);
                    }

                    Snackbar.make(findViewById(android.R.id.content), "Event saved successfully!",
                            Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Your event could not be saved. Please try again.",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    FirebaseRecyclerAdapter getAdapter() {
        return mAdapter;
    }
}
