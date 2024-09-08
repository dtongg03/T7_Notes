package com.example.homelandernotes.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.homelandernotes.R;
import com.example.homelandernotes.entities.Task;
import com.example.homelandernotes.receivers.TaskAlarmReceiver;
import com.example.homelandernotes.repository.TaskRepository;

import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {
    private static final int REQUEST_ALARM_PERMISSION = 100; // Mã yêu cầu quyền cho báo thức
    private static final int REQUEST_NOTIFICATION_PERMISSION = 101; // Mã yêu cầu quyền thông báo

    private EditText titleEditText, descriptionEditText;
    private TextView dateTextView;
    private TimePicker startTimePicker, endTimePicker;
    private Button saveButton;
    private TaskRepository taskRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        titleEditText = findViewById(R.id.editTextTitle);
        descriptionEditText = findViewById(R.id.editTextDescription);
        dateTextView = findViewById(R.id.textViewDate);
        startTimePicker = findViewById(R.id.startTimePicker);
        endTimePicker = findViewById(R.id.endTimePicker);
        saveButton = findViewById(R.id.buttonSave);

        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());

        startTimePicker.setIs24HourView(true);
        endTimePicker.setIs24HourView(true);

        taskRepository = new TaskRepository(getApplication());

        dateTextView.setOnClickListener(v -> showDatePickerDialog());

        saveButton.setOnClickListener(view -> saveTask());
        // Kiểm tra và yêu cầu quyền thông báo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Yêu cầu quyền thông báo
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            } else {
                // Quyền đã được cấp, thực hiện hành động cần thiết
                setupSaveButton();
            }
        }

    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddTaskActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    dateTextView.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void saveTask() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String date = dateTextView.getText().toString().trim();

        int startHour = startTimePicker.getHour();
        int startMinute = startTimePicker.getMinute();
        int endHour = endTimePicker.getHour();
        int endMinute = endTimePicker.getMinute();

        if (title.isEmpty() || date.isEmpty()) {
            Toast.makeText(AddTaskActivity.this, "Vui lòng nhập tiêu đề và ngày", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(date);
        task.setTimestamp(System.currentTimeMillis());
        task.setStatus(Task.Status.valueOf(Task.Status.PENDING.name()));

        taskRepository.insert(task);

        // Kiểm tra và yêu cầu quyền thông báo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                setupSaveButton();
            }
        } else {
            setupSaveButton();
        }

        Calendar taskStartTime = Calendar.getInstance();
        taskStartTime.set(Calendar.HOUR_OF_DAY, startHour);
        taskStartTime.set(Calendar.MINUTE, startMinute);
        taskStartTime.set(Calendar.SECOND, 0);
        taskStartTime.set(Calendar.MILLISECOND, 0);

        long triggerAtMillis = taskStartTime.getTimeInMillis() - 30 * 60 * 1000; // 30 phút trước khi bắt đầu
        setTaskReminder(task, triggerAtMillis);

        Toast.makeText(AddTaskActivity.this, "Thêm công việc và đặt thông báo thành công", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void setupSaveButton() {
        // Ví dụ: Kích hoạt nút lưu hoặc thực hiện hành động khác
        saveButton.setEnabled(true); // Kích hoạt nút lưu nếu quyền đã được cấp

        // Hoặc cập nhật giao diện người dùng
        Toast.makeText(this, "Quyền thông báo đã được cấp, bạn có thể lưu công việc", Toast.LENGTH_SHORT).show();

        // Thực hiện các hành động khác cần thiết sau khi quyền được cấp
    }

    private void setTaskReminder(Task task, long triggerAtMillis) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED) {
            scheduleAlarm(task, triggerAtMillis);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WAKE_LOCK}, REQUEST_ALARM_PERMISSION);
        }
    }

    private void scheduleAlarm(Task task, long triggerAtMillis) {
        Intent intent = new Intent(this, TaskAlarmReceiver.class);
        intent.putExtra("task_title", task.getTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, task.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "Quyền không được cấp, không thể đặt thông báo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Quyền thông báo đã được cấp", Toast.LENGTH_SHORT).show();
                setupSaveButton(); // Setup save button or related actions
            } else {
                Toast.makeText(this, "Không thể gửi thông báo vì quyền chưa được cấp", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_ALARM_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Quyền báo thức đã được cấp", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Không thể đặt báo thức vì chưa được cấp quyền", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
