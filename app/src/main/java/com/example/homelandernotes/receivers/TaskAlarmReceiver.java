package com.example.homelandernotes.receivers;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.homelandernotes.R;
import com.example.homelandernotes.activities.MainActivity;

import me.leolin.shortcutbadger.ShortcutBadger;

public class TaskAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String taskTitle = intent.getStringExtra("task_title");
        int pendingTasks = intent.getIntExtra("pending_tasks", 5); // Số lượng công việc chưa hoàn thành

        // Kiểm tra nếu taskTitle null hoặc rỗng thì không tạo thông báo
        if (taskTitle == null || taskTitle.isEmpty()) {
            return;  // Không gửi thông báo nếu không có tiêu đề công việc
        }

        createNotification(context, taskTitle, pendingTasks);

        // Cập nhật badge count
        ShortcutBadger.applyCount(context, pendingTasks);
        Log.d("TaskAlarmReceiver", "Pending tasks count: " + pendingTasks);

    }


    private void createNotification(Context context, String taskTitle, int pendingTasks) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "task_channel",
                    "Task Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Thông báo về công việc sắp đến");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Tạo thông báo với thông tin công việc
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "task_channel")
                .setSmallIcon(R.drawable.ic_check) // Đảm bảo có icon tương ứng
                .setContentTitle("Sắp đến công việc!")
                .setContentText("Công việc: " + taskTitle + " sắp diễn ra.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent) // Liên kết PendingIntent
                .setAutoCancel(true) // Thông báo tự động biến mất khi người dùng nhấn vào
                .setNumber(pendingTasks); // Hiển thị số lượng công việc chưa hoàn thành trên thông báo

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Kiểm tra quyền POST_NOTIFICATIONS trên Android 13 trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Nếu chưa có quyền, bỏ qua không gửi thông báo
                return;
            }
        }

        // Hiển thị thông báo
        notificationManager.notify(1, builder.build());
    }
}
