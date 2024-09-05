package com.example.homelandernotes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.homelandernotes.R;
import com.example.homelandernotes.entities.Task;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {

    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, 0, tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
        }

        Task task = getItem(position);

        TextView titleTextView = convertView.findViewById(R.id.taskTitle);
        TextView timeTextView = convertView.findViewById(R.id.taskTime);

        titleTextView.setText(task.getTitle());
        timeTextView.setText(String.format("%02d:%02d - %02d:%02d",
                task.getStartHour(), task.getStartMinute(),
                task.getEndHour(), task.getEndMinute()));

        return convertView;
    }
}
