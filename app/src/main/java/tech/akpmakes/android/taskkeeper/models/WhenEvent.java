package tech.akpmakes.android.taskkeeper.models;

import android.content.Intent;
import androidx.annotation.NonNull;

public class WhenEvent implements Comparable<WhenEvent> {
    public String name;
    public Long when;

    public WhenEvent(String name, long when) {
        this.name = name;
        this.when = when;
    }

    public WhenEvent(Intent data) {
        this.name = data.getStringExtra("whenName");
        this.when = data.getLongExtra("whenTime", 0);
    }

    public WhenEvent(){};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getWhen() {
        return when;
    }

    public void setWhen(long when) {
        this.when = when;
    }

    @Override
    public int compareTo(@NonNull WhenEvent o) {
        return when.compareTo(o.when);
    }
}
