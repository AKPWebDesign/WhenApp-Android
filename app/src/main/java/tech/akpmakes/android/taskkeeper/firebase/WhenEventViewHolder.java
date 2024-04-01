package tech.akpmakes.android.taskkeeper.firebase;

import android.os.Handler;
import android.os.SystemClock;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import tech.akpmakes.android.taskkeeper.R;

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
                handler.postDelayed( this, delay() );
            }
        }, delay());
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view, getAdapterPosition());
                return false;
            }
        });
    }

    private long delay() {
        return 1000 - (SystemClock.uptimeMillis() % 1000);
    }

    private void draw() {
        final long now = System.currentTimeMillis();
        String extra = "";
        if(this.when > now) {
            extra = "+";
        }
        this.whenText.setText(millisToShortDHMS(now - this.when, extra));
    }

    public void setName(String name) {
        this.name.setText(name);
        draw();
    }

    public CharSequence getName() {
        return this.name.getText();
    }

    public void setWhen(long when) {
        this.when = when;
        draw();
    }

    public long getWhen() {
        return this.when;
    }

    private static String millisToShortDHMS(long duration, String extra) {
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

        String res = "";
        if (days > 0) {
            res = "%1$d day" + (days > 1 ? "s" : "") + "\n";
        }
        res += "%2$02d:%3$02d:%4$02d";
        return extra + String.format(Locale.ENGLISH, res, days, hours, minutes, seconds);
    }

    private WhenEventViewHolder.ClickListener mClickListener;
    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
    public void setOnClickListener(WhenEventViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }
}
