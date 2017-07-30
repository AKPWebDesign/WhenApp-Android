package tech.akpmakes.android.taskkeeper;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class WhenEventViewHolder extends RecyclerView.ViewHolder {
    private TextView name;
    private TextView whenText;
    private long when;
    public WhenEventViewHolder(View v) {
        super(v);
        name = v.findViewById(R.id.event_name);
        whenText = v.findViewById(R.id.event_time);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                draw();
                handler.postDelayed( this, 1000 );
            }
        }, 1000);
    }

    private void draw() {
        String extra = "";
        if(this.when > new Date().getTime()) {
            extra = "+";
        }
        this.whenText.setText(millisToShortDHMS(new Date().getTime() - this.when, extra));
    }

    public void setName(String name) {
        this.name.setText(name);
        draw();
    }

    public CharSequence getName() {
        return name.getText();
    }

    public void setWhen(long when) {
        this.when = when;
        draw();
    }

    private static String millisToShortDHMS(long duration, String extra) {
        String res;
        long days  = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(days);
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
