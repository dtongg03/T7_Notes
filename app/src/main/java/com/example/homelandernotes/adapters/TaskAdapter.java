package com.example.homelandernotes.adapters;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.homelandernotes.R;
import com.example.homelandernotes.activities.AddTaskActivity;
import com.example.homelandernotes.entities.Task;
import com.example.homelandernotes.repository.TaskRepository;
import com.example.homelandernotes.toast.ReminderBroadcastReceiver;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {

    private TaskRepository taskRepository;
    private static final int REQUEST_CODE = 1;

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
        timeTextView.setText(task.getDueDate());

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

        completeButton.setOnClickListener(v -> {
            if (task != null) {
                task.setStatus(Task.Status.COMPLETED);
                taskRepository.update(task);
                notifyDataSetChanged(); // Consider optimizing this if performance is an issue
            }
        });

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddTaskActivity.class);
            intent.putExtra("task", task); // Ensure Task implements Parcelable
            ((AppCompatActivity) getContext()).startActivityForResult(intent, REQUEST_CODE);
        });

        convertView.setOnLongClickListener(v -> {
            showPopupMenu(v, position);
            return true;
        });

        convertView.setTag(position);

        return convertView;
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.task_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.delete_task) {
                showConfirmDeleteDialog(position);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void showConfirmDeleteDialog(int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa công việc")
                .setMessage("Bạn có chắc chắn muốn xóa công việc này không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteTask(position))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteTask(int position) {
        Task task = getItem(position);

        if (task != null) {
            taskRepository.delete(task);
            remove(task);
            cancelReminder(task.getId());
            notifyDataSetChanged();
        }
    }

    private void cancelReminder(int taskId) {
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), ReminderBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), taskId, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}
