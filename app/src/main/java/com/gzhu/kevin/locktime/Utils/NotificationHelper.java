package com.gzhu.kevin.locktime.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.gzhu.kevin.locktime.MainActivity;
import com.gzhu.kevin.locktime.R;

public class NotificationHelper {
  static int id;

  static public boolean is_notification_float = true;
  static private String maxChannelId = null;
  static private String lowChannelId = null;
  static private String channelId = null;
  static private int priority = 0;

  public static void pushNotification(Context context, String string) {
    pushNotification(context,string,60*1000);
  }
  public static void pushNotification(Context context, String string, int afterMillisecondsDisappear) {
    is_notification_float=context.getSharedPreferences("share",Context.MODE_PRIVATE).getBoolean(ShareSharedPreferenceBean.share_notification_float,true);
    // create notification chennel
    int notificationPriority = NotificationManager.IMPORTANCE_MAX;
    if (maxChannelId == null) {
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        maxChannelId = createNotificationChannel(context, "my_max_channelID", "通知", NotificationManager.IMPORTANCE_MAX);
      }
    }
    if (lowChannelId == null) {
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        lowChannelId = createNotificationChannel(context, "my_low_channelID", "次要通知", NotificationManager.IMPORTANCE_LOW);
      }
    }

    channelId = is_notification_float ? maxChannelId :lowChannelId;
    // priority = is_notification_float ? NotificationCompat.PRIORITY_HIGH : NotificationCompat.PRIORITY_LOW;
    priority = NotificationCompat.PRIORITY_HIGH;
    Log.d("float priority ", String.valueOf(notificationPriority));

    // set the intent of click notification
    Intent intent = new Intent(context, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
  PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


    // Create the TaskStackBuilder and add the intent, which inflates the back stack
//    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//    stackBuilder.addNextIntentWithParentStack(intent);
//    // Get the PendingIntent containing the entire back stack
//    PendingIntent pendingIntent =
//            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


    // create notification
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId).setSmallIcon(R.mipmap.app_icon_round).setContentTitle("Lock Record").setContentText(string).setPriority(priority).setTimeoutAfter(afterMillisecondsDisappear).setContentIntent(pendingIntent).setAutoCancel(true);
    // push notification
    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
    notificationManagerCompat.notify(id++, builder.build());

  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  private static String createNotificationChannel(Context context, String channelID, String channelNAME, int level) {

    NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

    NotificationChannel channel = new NotificationChannel(channelID, channelNAME, level);
    channel.setSound(null, null);

    manager.createNotificationChannel(channel);
    return channelID;

  }
}
