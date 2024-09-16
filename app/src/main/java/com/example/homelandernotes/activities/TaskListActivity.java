package com.example.homelandernotes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homelandernotes.R;

public class TaskListActivity extends AppCompatActivity {

    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        calendarView = findViewById(R.id.calendarView);
        ImageButton buttonAddTask = findViewById(R.id.buttonAddTask);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Chuyển đến Activity mới với ngày được chọn
                Intent intent = new Intent(TaskListActivity.this, DayDetailsActivity.class);
                String selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
                intent.putExtra("selectedDate", selectedDate);
                startActivity(intent);


            }
        });

        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskListActivity.this, AddTaskActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_TASK);
            }
        });
    }

    private static final int REQUEST_CODE_ADD_TASK = 1; // Mã yêu cầu để nhận kết quả từ AddTaskActivity
}
