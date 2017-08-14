package tech.akpmakes.android.taskkeeper.firebase;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import tech.akpmakes.android.taskkeeper.MainActivity;
import tech.akpmakes.android.taskkeeper.R;
import tech.akpmakes.android.taskkeeper.TaskViewActivity;
import tech.akpmakes.android.taskkeeper.models.WhenEvent;

public final class WhenAdapter extends FirebaseRecyclerAdapter<WhenEvent, WhenEventViewHolder> {
    private final Activity activity;

    public WhenAdapter(Activity activity, Query query) {
        super(WhenEvent.class, R.layout.item_whenevent, WhenEventViewHolder.class, query);
        this.activity = activity;
    }

    @Override
    protected void populateViewHolder(WhenEventViewHolder holder, WhenEvent evt, int pos) {
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
                WhenEvent evt = WhenAdapter.this.getItem(position);
                i.putExtra("whenName", evt.getName());
                i.putExtra("whenTime", evt.getWhen());
                i.putExtra("whenKey", WhenAdapter.this.getRef(position).getKey());
                activity.startActivityForResult(i, MainActivity.WHEN_EVENT_REQUEST);
            }
        });
        return viewHolder;
    }
}
