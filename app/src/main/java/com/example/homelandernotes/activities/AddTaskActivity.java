package com.example.homelandernotes.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homelandernotes.R;
import com.example.homelandernotes.entities.Task;
import com.example.homelandernotes.repository.TaskRepository;
import com.example.homelandernotes.toast.ReminderBroadcastReceiver;

import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText;
    private TextView dateTextView;
    private TimePicker startTimePicker, endTimePicker;
    private ImageButton saveButton;
    private View deleteButton;
    private TaskRepository taskRepository;
    private Task taskToEdit;

    @SuppressLint("MissingInflatedId")
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
        deleteButton = findViewById(R.id.delete_task);

        // Khởi tạo nút quay lại
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());

        // Kiểm tra quyền và yêu cầu quyền lập lịch báo thức chính xác
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

        // Thiết lập chế độ TimePicker
        startTimePicker.setIs24HourView(true);
        endTimePicker.setIs24HourView(true);

        taskRepository = new TaskRepository(getApplication());

        // Hiển thị DatePickerDialog khi nhấn vào TextView
        dateTextView.setOnClickListener(v -> showDatePickerDialog());

        // Nhận đối tượng Task từ Intent nếu có
        taskToEdit = (Task) getIntent().getParcelableExtra("task");

        if (taskToEdit != null) {
            // Điền dữ liệu vào các thành phần UI
            titleEditText.setText(taskToEdit.getTitle());
            descriptionEditText.setText(taskToEdit.getDescription());
            dateTextView.setText(taskToEdit.getDueDate());

            String[] startTimeParts = taskToEdit.getStartTime().split(":");
            startTimePicker.setHour(Integer.parseInt(startTimeParts[0]));
            startTimePicker.setMinute(Integer.parseInt(startTimeParts[1]));

            String[] endTimeParts = taskToEdit.getEndTime().split(":");
            endTimePicker.setHour(Integer.parseInt(endTimeParts[0]));
            endTimePicker.setMinute(Integer.parseInt(endTimeParts[1]));

            // Hiển thị nút xóa khi đang chỉnh sửa công việc
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(v -> showConfirmDeleteDialog());
        } else {
            // Ẩn nút xóa khi tạo công việc mới
            deleteButton.setVisibility(View.GONE);
        }

        saveButton.setOnClickListener(view -> {
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

            if (startHour > endHour || (startHour == endHour && startMinute > endMinute)) {
                Toast.makeText(AddTaskActivity.this, "Thời gian bắt đầu phải trước thời gian kết thúc", Toast.LENGTH_SHORT).show();
                return;
            }

            if (taskToEdit == null) {
                // Tạo một công việc mới
                Task task = new Task();
                task.setTitle(title);
                task.setDescription(description);
                task.setDueDate(date);
                task.setStartTime(String.format("%02d:%02d", startHour, startMinute));
                task.setEndTime(String.format("%02d:%02d", endHour, endMinute));
                task.setTimestamp(System.currentTimeMillis());
                task.setStatus(Task.Status.PENDING);

                taskRepository.insert(task);
                scheduleReminder(task);
                Toast.makeText(AddTaskActivity.this, "Thêm công việc thành công", Toast.LENGTH_SHORT).show();
            } else {
                // Cập nhật công việc hiện tại
                taskToEdit.setTitle(title);
                taskToEdit.setDescription(description);
                taskToEdit.setDueDate(date);
                taskToEdit.setStartTime(String.format("%02d:%02d", startHour, startMinute));
                taskToEdit.setEndTime(String.format("%02d:%02d", endHour, endMinute));
                taskRepository.update(taskToEdit);
                scheduleReminder(taskToEdit); // Thiết lập nhắc nhở mới
                Toast.makeText(AddTaskActivity.this, "Cập nhật công việc thành công", Toast.LENGTH_SHORT).show();
            }

            // Trả kết quả cho Activity gọi
            setResult(RESULT_OK);
            finish();
        });

    }


    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddTaskActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    dateTextView.setText(formattedDate);
                },
                year, month, day);
        datePickerDialog.show();
    }

    @SuppressLint("MissingPermission")
    private void scheduleReminder(Task task) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(task.getDueDate().substring(0, 4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(task.getDueDate().substring(5, 7)) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(task.getDueDate().substring(8, 10)));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(task.getStartTime().split(":")[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(task.getStartTime().split(":")[1]));

        // Trừ đi 30 phút
        calendar.add(Calendar.MINUTE, -30);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderBroadcastReceiver.class);
        intent.putExtra("title", task.getTitle());
        intent.putExtra("description", task.getDescription());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, task.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                // Hiển thị màn hình yêu cầu quyền lập lịch báo thức chính xác
                Intent intent1 = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent1);
                Toast.makeText(this, "Vui lòng cấp quyền lập lịch báo thức chính xác", Toast.LENGTH_LONG).show();
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }


    private boolean canScheduleExactAlarms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            return alarmManager.canScheduleExactAlarms();
        }
        return true; // For API levels below S, this permission is not required
    }

    private void cancelReminder(int taskId) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, taskId, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    private void showConfirmDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa công việc")
                .setMessage("Bạn có chắc chắn muốn xóa công việc này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    taskRepository.delete(taskToEdit);
                    cancelReminder(taskToEdit.getId());
                    Toast.makeText(AddTaskActivity.this, "Xóa công việc thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .setNegativeButton("Không", null)
                .show();
    }
}
