package tech.akpmakes.android.taskkeeper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import tech.akpmakes.android.taskkeeper.models.WhenEvent;

public class WhenEventAdapter extends ArrayAdapter<WhenEvent> {
    public WhenEventAdapter(@NonNull Context context, ArrayList<WhenEvent> list) {
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final WhenEvent evt = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_whenevent, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.event_name);
        final TextView tvTime = (TextView) convertView.findViewById(R.id.event_time);
        String extra = "";
        // Populate the data into the template view using the data object
        tvName.setText(evt.name);
        if(evt.when > new Date().getTime()) {
            extra = "+";
        }
        tvTime.setText(millisToShortDHMS(new Date().getTime() - evt.when, extra));
        // Return the completed view to render on screen
        return convertView;
    }

    private static String millisToShortDHMS(long duration, String extra) {
        String res;
        long days  = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
        days = Math.abs(days);
        hours = Math.abs(hours);
        minutes = Math.abs(minutes);
        seconds = Math.abs(seconds);
        if (days == 0) {
            res = String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds);
        }
        else if (days == 1) {
            res = String.format(Locale.ENGLISH, "%d day \r\n%02d:%02d:%02d", days, hours, minutes, seconds);
        }
        else {
            res = String.format(Locale.ENGLISH, "%d days \r\n%02d:%02d:%02d", days, hours, minutes, seconds);
        }
        return extra + res;
    }
}
