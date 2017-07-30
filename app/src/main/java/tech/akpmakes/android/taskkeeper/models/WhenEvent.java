package tech.akpmakes.android.taskkeeper.models;

import android.support.annotation.NonNull;

public class WhenEvent implements Comparable<WhenEvent> {
    public String name;
    public Long when;

    public WhenEvent(String name, Long when) {
        this.name = name;
        this.when = when;
    }

    public WhenEvent(){};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getWhen() {
        return when;
    }

    public void setWhen(Long when) {
        this.when = when;
    }

    @Override
    public int compareTo(@NonNull WhenEvent o) {
        return o.when.compareTo(this.when);
    }
}
