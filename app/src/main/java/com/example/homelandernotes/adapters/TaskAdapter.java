package com.example.homelandernotes.adapters;

import static com.example.homelandernotes.entities.Task.Status.COMPLETED;
import static com.example.homelandernotes.entities.Task.Status.IN_PROGRESS;
import static com.example.homelandernotes.entities.Task.Status.PENDING;

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

    // ViewHolder pattern to optimize view lookup
    private static class ViewHolder {
        TextView titleTextView;
        TextView timeTextView;
        View taskStatusView;
        ImageButton completeButton;
    }

    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, 0, tasks);
        taskRepository = new TaskRepository((Application) context.getApplicationContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
            viewHolder.titleTextView = convertView.findViewById(R.id.taskTitle);
            viewHolder.timeTextView = convertView.findViewById(R.id.taskTime);
            viewHolder.taskStatusView = convertView.findViewById(R.id.taskStatusView);
            viewHolder.completeButton = convertView.findViewById(R.id.completeButton);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Task task = getItem(position);

        if (task != null) {
            viewHolder.titleTextView.setText(task.getTitle());
            viewHolder.timeTextView.setText(task.getDueDate()); // Cập nhật theo định dạng thời gian của bạn

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
            viewHolder.taskStatusView.setBackgroundColor(color);

            // Xử lý sự kiện khi nhấn nút "Hoàn thành"
            viewHolder.completeButton.setOnClickListener(v -> {
                task.setStatus(COMPLETED); // Sử dụng enum Status
                taskRepository.update(task); // Cập nhật cơ sở dữ liệu
                notifyDataSetChanged(); // Cập nhật giao diện
            });
        }

        return convertView;
    }
}
