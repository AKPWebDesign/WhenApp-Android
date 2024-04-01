package tech.akpmakes.android.taskkeeper.util;

import android.content.Context;
import android.icu.util.Calendar;

import tech.akpmakes.android.taskkeeper.R;

public enum DaysOfWeek {
    DEFAULT(R.string._default),
    SUNDAY(R.string.day_1),
    MONDAY(R.string.day_2),
    TUESDAY(R.string.day_3),
    WEDNESDAY(R.string.day_4),
    THURSDAY(R.string.day_5),
    FRIDAY(R.string.day_6),
    SATURDAY(R.string.day_7);

    final int index;

    DaysOfWeek(int dayString) {
        index = dayString;
    }

    public String getName(Context context) {
        return context.getString(index);
    }

    public static DaysOfWeek get(int day) {
        return values()[day];
    }
}
