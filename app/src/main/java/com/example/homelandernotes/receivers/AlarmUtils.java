package com.example.homelandernotes.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


public class AlarmUtils {

    public static void setTaskAlarm(Context context, String taskTitle, int pendingTasks) {
        Intent intent = new Intent(context, TaskAlarmReceiver.class);
        intent.setAction("com.example.homelandernotes.ACTION_TASK_ALARM");
        intent.putExtra("pending_tasks", pendingTasks); // Số lượng công việc chưa hoàn thiện

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent); // Gửi Intent ngay lập tức
    }
}
