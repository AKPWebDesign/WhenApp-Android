package tech.akpmakes.android.taskkeeper.firebase;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import tech.akpmakes.android.taskkeeper.MainActivity;
import tech.akpmakes.android.taskkeeper.R;
import tech.akpmakes.android.taskkeeper.TaskViewActivity;
import tech.akpmakes.android.taskkeeper.models.WhenEvent;

public final class WhenAdapter extends FirebaseRecyclerAdapter<WhenEvent, WhenEventViewHolder> {
    private final Activity activity;

    public WhenAdapter(Activity activity, FirebaseRecyclerOptions<WhenEvent> options) {
        super(options);
        this.activity = activity;
    }

    @NonNull
    @Override
    public WhenEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_whenevent, parent, false);

        WhenEventViewHolder viewHolder = new WhenEventViewHolder(view);
        viewHolder.setOnClickListener(new WhenEventViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                editTask(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }

            private void editTask(int position) {
                Intent i = new Intent(activity, TaskViewActivity.class);
                WhenEvent evt = WhenAdapter.this.getItem(position);
                i.putExtra("whenName", evt.getName());
                i.putExtra("whenTime", evt.getWhen());
                i.putExtra("whenKey", WhenAdapter.this.getRef(position).getKey());
                activity.startActivityForResult(i, MainActivity.WHEN_EVENT_REQUEST);
            }
        });

        return viewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull WhenEventViewHolder holder, int i, @NonNull WhenEvent evt) {
        holder.setName(evt.name);
        holder.setWhen(evt.when);
    }
}
