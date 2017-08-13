package tech.akpmakes.android.taskkeeper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import tech.akpmakes.android.taskkeeper.models.WhenEvent;

public class TaskViewActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    Calendar whenTime;
    TextView taskDate;
    TextView taskTime;
    String whenKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);
        final EditText taskName = findViewById(R.id.task_name_content);
        taskDate = findViewById(R.id.task_when_date);
        taskTime = findViewById(R.id.task_when_time);
        Button taskSave = findViewById(R.id.btn_save_task);

        whenTime = Calendar.getInstance();

        Intent i = getIntent();
        if (i.hasExtra("whenName")) {
            taskName.setText(i.getStringExtra("whenName"));
        }

        if (i.hasExtra("whenTime")) {
            whenTime.setTimeInMillis(i.getLongExtra("whenTime", 0));
        }

        if (i.hasExtra("whenKey")) {
            whenKey = i.getStringExtra("whenKey");
        }

        updateDateTimeUI();

        taskDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                if(whenTime != null) {
                    now = whenTime;
                }
                DatePickerDialog datePicker = DatePickerDialog.newInstance(
                        TaskViewActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
                datePicker.show(getFragmentManager(), "DatePickerDialog");
            }
        });

        taskTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                if(whenTime != null) {
                    now = whenTime;
                }
                TimePickerDialog timePicker = TimePickerDialog.newInstance(
                        TaskViewActivity.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        now.get(Calendar.SECOND),
                        false
                );
                timePicker.enableSeconds(true);
                timePicker.show(getFragmentManager(), "TimePickerDialog");
            }
        });

        taskSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = taskName.getText().toString();
                Long when = whenTime.getTimeInMillis();
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
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        whenTime.set(Calendar.YEAR, year);
        whenTime.set(Calendar.MONTH, monthOfYear);
        whenTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        updateDateTimeUI();
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        whenTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        whenTime.set(Calendar.MINUTE, minute);
        whenTime.set(Calendar.SECOND, second);

        updateDateTimeUI();
    }

    private void updateDateTimeUI() {
        DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG);
        DateFormat timeFormat = SimpleDateFormat.getTimeInstance();
        taskDate.setText(dateFormat.format(whenTime.getTime()));
        taskTime.setText(timeFormat.format(whenTime.getTime()));
    }
}
