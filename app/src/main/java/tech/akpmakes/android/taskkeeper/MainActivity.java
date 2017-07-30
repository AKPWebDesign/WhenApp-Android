package tech.akpmakes.android.taskkeeper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import tech.akpmakes.android.taskkeeper.models.WhenEvent;

public class MainActivity extends AppCompatActivity implements AddItemDialog.AddItemDialogListener {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private Query mDBQuery;
    private RecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.events_list);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setHapticFeedbackEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        setUpSwipe();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    private void setUpSwipe() {
        ItemTouchHelper.SimpleCallback mIthSc = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            ColorDrawable bgDelete;
            ColorDrawable bgRefresh;
            Drawable refreshIcon;
            Drawable deleteIcon;
            int iconMargin;
            boolean initiated = false;

            private void init() {
                bgDelete = new ColorDrawable(Color.parseColor("#f44336"));
                bgRefresh = new ColorDrawable(Color.parseColor("#263238"));
                refreshIcon = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_refresh);
                deleteIcon = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_delete);
                iconMargin = (int) MainActivity.this.getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                WhenEventViewHolder vh = (WhenEventViewHolder) viewHolder;
                DatabaseReference item =  mAdapter.getRef(vh.getAdapterPosition());
                if(direction == ItemTouchHelper.LEFT) {
                    item.removeValue();
                } else {
                    WhenEvent evt = new WhenEvent();
                    evt.setName(vh.getName().toString());
                    evt.setWhen(new Date().getTime());
                    item.setValue(evt);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                if (viewHolder.getAdapterPosition() == -1) {
                    return;
                }

                if (!initiated) {
                    init();
                }

                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;

                    if(dX > 0){
                        draw(refreshIcon, bgRefresh, itemView, c, (int) dX, true);
                    } else {
                        draw(deleteIcon, bgDelete, itemView, c, (int) dX, false);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            private void draw(Drawable icon, ColorDrawable bgColor, View itemView, Canvas c, int dX, boolean fromLeft) {
                bgColor.setBounds(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                if(fromLeft) {
                    bgColor.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + dX, itemView.getBottom());
                }
                bgColor.draw(c);

                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = icon.getIntrinsicWidth();
                int intrinsicHeight = icon.getIntrinsicWidth();

                int iconLeft = itemView.getRight() - iconMargin - intrinsicWidth;
                int iconRight = itemView.getRight() - iconMargin;
                int iconTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int iconBottom = iconTop + intrinsicHeight;

                if(fromLeft) {
                    iconLeft = itemView.getLeft() + iconMargin;
                    iconRight = itemView.getLeft() + iconMargin + intrinsicWidth;
                }
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                icon.draw(c);
            }
        };
        ItemTouchHelper mIth = new ItemTouchHelper(mIthSc);

        mIth.attachToRecyclerView(mRecyclerView);
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
        mDBQuery.getRef().push().setValue(new WhenEvent(name, new Date().getTime()));
    }
}
