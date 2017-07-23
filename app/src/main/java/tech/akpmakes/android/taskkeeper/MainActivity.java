package tech.akpmakes.android.taskkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import tech.akpmakes.android.taskkeeper.tech.akpmakes.android.taskkeeper.models.WhenEvent;

public class MainActivity extends AppCompatActivity implements AddItemDialog.AddItemDialogListener {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mDBRef;
    private ArrayList<WhenEvent> events;
    private WhenEventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main);
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
                        Toast.makeText(MainActivity.this, "Server connection failed. You may experience problems saving data if this issue persists.",
                                Toast.LENGTH_SHORT).show();
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

        mDBRef = FirebaseDatabase.getInstance().getReference("events/" + user.getUid());
        events = new ArrayList<>();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, WhenEvent>> type = new GenericTypeIndicator<Map<String, WhenEvent>>() {};
                Map val = dataSnapshot.getValue(type);
                if(val == null || val.isEmpty()) {
                    events = new ArrayList<>();
                } else {
                    events = new ArrayList<>(val.values());
                }
                eventAdapter.clear();
                eventAdapter.addAll(events);
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Database Error: " + databaseError.getMessage());
            }
        };

        mDBRef.addValueEventListener(listener);

        eventAdapter = new WhenEventAdapter(this, events);

        ListView listView = (ListView) findViewById(R.id.events_list);
        listView.setAdapter(eventAdapter);
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
            case R.id.profile:
                startActivity(new Intent(this, ProfileActivity.class));
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
        mDBRef.push().setValue(new WhenEvent(name, new Date().getTime()));
    }
}
