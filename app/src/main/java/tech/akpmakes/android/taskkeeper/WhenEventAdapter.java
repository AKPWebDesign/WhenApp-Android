package tech.akpmakes.android.taskkeeper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import tech.akpmakes.android.taskkeeper.tech.akpmakes.android.taskkeeper.models.WhenEvent;

public class WhenEventAdapter extends ArrayAdapter<WhenEvent> {
    public WhenEventAdapter(@NonNull Context context, ArrayList<WhenEvent> list) {
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        WhenEvent evt = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_whenevent, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.event_name);
        TextView tvTime = (TextView) convertView.findViewById(R.id.event_time);
        // Populate the data into the template view using the data object
        tvName.setText(evt.name);
        tvTime.setText("" + evt.when);
        // Return the completed view to render on screen
        return convertView;
    }
}
