package tech.akpmakes.android.taskkeeper.tech.akpmakes.android.taskkeeper.models;

import android.support.annotation.NonNull;

public class WhenEvent implements Comparable<WhenEvent> {
    public String name;
    public Long when;

    public WhenEvent(String name, Long when) {
        this.name = name;
        this.when = when;
    }

    public WhenEvent(){};

    @Override
    public int compareTo(@NonNull WhenEvent o) {
        return o.when.compareTo(this.when);
    }
}
