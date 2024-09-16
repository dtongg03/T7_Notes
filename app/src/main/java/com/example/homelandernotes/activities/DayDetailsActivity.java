package com.example.homelandernotes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.homelandernotes.R;
import com.example.homelandernotes.adapters.TaskAdapter;
import com.example.homelandernotes.entities.Task;
import com.example.homelandernotes.viewmodel.TaskViewModel;

import java.util.List;

public class DayDetailsActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;
    private ListView taskListView;
    private TextView dateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        taskListView = findViewById(R.id.taskListView);
        dateTextView = findViewById(R.id.dateTextView);

        // Nhận ngày đã chọn từ Intent
        String selectedDate = getIntent().getStringExtra("selectedDate");
        dateTextView.setText("Ngày: " + selectedDate);

        // Khởi tạo ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Quan sát danh sách công việc của ngày đã chọn
        taskViewModel.getTasksByDate(selectedDate).observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                TaskAdapter adapter = new TaskAdapter(DayDetailsActivity.this, tasks);
                taskListView.setAdapter(adapter);
            }
        });

        // Thiết lập sự kiện khi nhấp vào mục công việc
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task selectedTask = (Task) parent.getItemAtPosition(position);
                Intent intent = new Intent(DayDetailsActivity.this, AddTaskActivity.class);
                intent.putExtra("TASK_ID", selectedTask.getId()); // Gửi ID của công việc để chỉnh sửa
                startActivityForResult(intent, REQUEST_CODE_EDIT_TASK);
            }
        });

        // Thiết lập sự kiện cho nút quay lại
        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());
    }

    private static final int REQUEST_CODE_EDIT_TASK = 2; // Mã yêu cầu để nhận kết quả từ AddTaskActivity
}
