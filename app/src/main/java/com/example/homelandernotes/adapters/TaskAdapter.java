package com.example.homelandernotes.adapters;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.homelandernotes.R;
import com.example.homelandernotes.entities.Task;
import com.example.homelandernotes.repository.TaskRepository;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {

    private TaskRepository taskRepository;

    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, 0, tasks);
        taskRepository = new TaskRepository((Application) context.getApplicationContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
        }

        Task task = getItem(position);

        TextView titleTextView = convertView.findViewById(R.id.taskTitle);
        TextView timeTextView = convertView.findViewById(R.id.taskTime);
        View taskStatusView = convertView.findViewById(R.id.taskStatusView);
        ImageButton completeButton = convertView.findViewById(R.id.completeButton);

        titleTextView.setText(task.getTitle());
        timeTextView.setText(task.getDueDate()); // Cập nhật theo định dạng thời gian của bạn

        // Đặt màu theo trạng thái
        int color;
        switch (task.getStatus()) {
            case COMPLETED:
                color = ContextCompat.getColor(getContext(), android.R.color.holo_green_light);
                break;
            case IN_PROGRESS:
                color = ContextCompat.getColor(getContext(), android.R.color.holo_orange_light);
                break;
            case PENDING:
            default:
                color = ContextCompat.getColor(getContext(), android.R.color.holo_red_light);
                break;
        }
        taskStatusView.setBackgroundColor(color);

        // Xử lý sự kiện khi nhấn nút "Hoàn thành"
        completeButton.setOnClickListener(v -> {
            if (task != null) {
                task.setStatus(Task.Status.COMPLETED); // Sử dụng enum Status
                taskRepository.update(task); // Cập nhật cơ sở dữ liệu
                notifyDataSetChanged(); // Cập nhật giao diện
            }
        });

        return convertView;
    }
}
