package tech.akpmakes.android.taskkeeper;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import tech.akpmakes.android.taskkeeper.models.WhenEvent;

public class TaskViewActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    final Calendar whenTime = Calendar.getInstance();
    EditText taskName;
    TextView taskDate;
    TextView taskTime;
    CheckBox useCurrentTime;
    String whenKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);
        taskName = findViewById(R.id.task_name_content);
        taskDate = findViewById(R.id.task_when_date);
        taskTime = findViewById(R.id.task_when_time);
        useCurrentTime = findViewById(R.id.useCurrentTime);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(useCurrentTime.isChecked()) {
                    updateTime();
                }
                handler.postDelayed( this, delay() );
            }
        }, delay());

        Intent i = getIntent();
        if (i.hasExtra("whenName")) {
            taskName.setText(i.getStringExtra("whenName"));
        }

        if (i.hasExtra("whenTime")) {
            useCurrentTime.setChecked(false);
            whenTime.setTimeInMillis(i.getLongExtra("whenTime", 0));
        }

        if (i.hasExtra("whenKey")) {
            whenKey = i.getStringExtra("whenKey");
        }

        updateDateTimeUI();

        taskDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = whenTime;
                DatePickerDialog datePicker = new DatePickerDialog(
                        TaskViewActivity.this,
                        TaskViewActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                datePicker.show();
            }
        });

        taskTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = whenTime;
                TimePickerDialog timePicker = new TimePickerDialog(
                        TaskViewActivity.this,
                        TaskViewActivity.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        android.text.format.DateFormat.is24HourFormat(TaskViewActivity.this)
                );
                timePicker.show();
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private long delay() {
        return 1000 - (SystemClock.uptimeMillis() % 1000);
    }

    private void saveTask() {
        if(useCurrentTime.isChecked()) {
            updateTime();
        }
        String name = taskName.getText().toString();
        long when = whenTime.getTimeInMillis();
        if (name.length() == 0) {
            taskName.setError("Task name is required!");
            return;
        }
        WhenEvent evt = new WhenEvent(name, when);
        Intent i = new Intent();
        i.putExtra("whenName", evt.getName());
        i.putExtra("whenTime", evt.getWhen());
        if(whenKey != null) {
            i.putExtra("whenKey", whenKey);
        }
        setResult(Activity.RESULT_OK, i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_task:
                saveTask();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        whenTime.set(Calendar.YEAR, year);
        whenTime.set(Calendar.MONTH, monthOfYear);
        whenTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        useCurrentTime.setChecked(false);

        updateDateTimeUI();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        whenTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        whenTime.set(Calendar.MINUTE, minute);
        whenTime.set(Calendar.SECOND, 0);

        useCurrentTime.setChecked(false);

        updateDateTimeUI();
    }

    private void updateTime() {
        whenTime.setTimeInMillis(System.currentTimeMillis());
        updateDateTimeUI();
    }

    private void updateDateTimeUI() {
        DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG);
        DateFormat timeFormat = SimpleDateFormat.getTimeInstance();
        taskDate.setText(dateFormat.format(whenTime.getTime()));
        taskTime.setText(timeFormat.format(whenTime.getTime()));
    }
}
