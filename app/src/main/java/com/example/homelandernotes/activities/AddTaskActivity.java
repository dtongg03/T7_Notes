package com.example.homelandernotes.activities;

import android.app.DatePickerDialog;
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

import com.example.homelandernotes.R;
import com.example.homelandernotes.entities.Task;
import com.example.homelandernotes.repository.TaskRepository;

import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

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

        // Khởi tạo nút quay lại
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại màn hình trước đó
                onBackPressed();
            }
        });

        // Thiết lập chế độ TimePicker
        startTimePicker.setIs24HourView(true);
        endTimePicker.setIs24HourView(true);

        taskRepository = new TaskRepository(getApplication());

        // Hiển thị DatePickerDialog khi nhấn vào TextView
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                String date = dateTextView.getText().toString().trim();

                // Lấy giờ và phút từ TimePicker
                int startHour = startTimePicker.getHour();
                int startMinute = startTimePicker.getMinute();
                int endHour = endTimePicker.getHour();
                int endMinute = endTimePicker.getMinute();

                if (title.isEmpty() || date.isEmpty()) {
                    Toast.makeText(AddTaskActivity.this, "Vui lòng nhập tiêu đề và ngày", Toast.LENGTH_SHORT).show();
                    return;
                }

                Task task = new Task(date, title, description, startHour, startMinute, endHour, endMinute);
                taskRepository.insert(task);
                Toast.makeText(AddTaskActivity.this, "Thêm công việc thành công", Toast.LENGTH_SHORT).show();

                // Trả kết quả cho Activity gọi
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddTaskActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Định dạng ngày thành "năm-tháng-ngày"
                        String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        dateTextView.setText(selectedDate);
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }
}
