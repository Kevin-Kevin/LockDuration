package com.gzhu.kevin.locktime.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.ipsec.ike.TunnelModeChildSessionParams;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.view.GravityCompat;

import com.gzhu.kevin.locktime.DAO.InfoDatabaseSingleInstance;
import com.gzhu.kevin.locktime.DAO.ReceivedBroadcastInfo;
import com.gzhu.kevin.locktime.R;
import com.gzhu.kevin.locktime.Service.MyRegisterService;
import com.gzhu.kevin.locktime.Utils.ShareSharedPreferenceBean;

import io.reactivex.internal.util.ListAddBiConsumer;

public class PhoneLockReceiver extends BroadcastReceiver {
  int id = 0;
  long lastLockTimeMillis;
  long durationSeconds = 0;
  boolean isFirstScreenOff = true;
  SharedPreferences sharedPreferences;

  private MyRegisterService.MyBinder myBinder;
  TextView textView;

  private final static PhoneLockReceiver PHONE_LOCK_RECEIVER_INSTANCE = new PhoneLockReceiver();


  public static PhoneLockReceiver getInstance() {
    return PHONE_LOCK_RECEIVER_INSTANCE;
  }

  private PhoneLockReceiver() {
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    StringBuilder stringBuilder = new StringBuilder();

    switch (intent.getAction()) {
      case Intent.ACTION_USER_PRESENT:

        System.out.println("receive user_present " + intent.getType() + intent.getData() + intent.getScheme());

        long unlockTimeMillis = System.currentTimeMillis();
        sharedPreferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);

        lastLockTimeMillis = sharedPreferences.getLong("last_lock_millis", 0);
        // 写入数据库
        new InsertDatabaseThread(unlockTimeMillis, lastLockTimeMillis).start();

        durationSeconds = (unlockTimeMillis - lastLockTimeMillis) / 1000;
        if (durationSeconds < 0) {
          stringBuilder.append("last lock millis is negative 上次锁屏时间是负数");
          break;
        }

        int setSeconds = sharedPreferences.getInt("after_how_many_seconds_show_notification", 0);

        if (durationSeconds < setSeconds) {
          break;
        } else {
          secondsToHoursMinutesSeconds(context, durationSeconds, stringBuilder);
//          通知改成悬浮窗
//          NotificationHelper.pushNotification(context, stringBuilder.toString());
          //        new ShowFloatWindowNotificationThread(context).start();
          ShowFloatWindowNotification(context, stringBuilder.toString());
          Log.d(TAG, "onReceive: " + Thread.currentThread().getName());
        }

        System.out.println(durationSeconds);
        System.out.println(stringBuilder);
        System.out.println("receive over");
        isFirstScreenOff = true;
        break;
      case Intent.ACTION_SCREEN_OFF:
        System.out.println("receive screen_off");
        if (isFirstScreenOff) {
          isFirstScreenOff = false;
          lastLockTimeMillis = System.currentTimeMillis();
          // 写入sharepreference
          sharedPreferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);
          sharedPreferences.edit().putLong("last_lock_millis", lastLockTimeMillis).apply();

        } else {

        }
        break;
      case Intent.ACTION_SCREEN_ON:
        System.out.println("receive screen_on");
        break;

    }


  }

  private void secondsToHoursMinutesSeconds(Context context, long sec, StringBuilder stringBuilder) {
    long day = sec / (60 * 60 * 24);
    sec %= 60 * 60 * 24;
    long h = sec / 3600;
    sec %= 3600;
    long m = sec / 60;
    sec %= 60;
    long s = sec; //不足60的就是秒，够60就是分
    stringBuilder.append(context.getSharedPreferences("share", Context.MODE_PRIVATE).getString(ShareSharedPreferenceBean.share_welcome_words, "欢迎回来 : "));
    if (day != 0) {
      stringBuilder.append(day).append("天 ");
    }
    if (h != 0) {
      stringBuilder.append(h).append("时 ");
    }
    if (m != 0) {
      stringBuilder.append(m).append("分 ");
    }
    stringBuilder.append(s).append("秒");

  }

  private void ShowFloatWindowNotification(Context context, String string) {
    // if version greater than M , check whether get user permission
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (Settings.canDrawOverlays(context)) {
        realShowFloatWindow(context, string);
      } else {
        Toast.makeText(context, "lockTime : 未开启悬浮窗权限", Toast.LENGTH_SHORT).show();
      }
    } else {
      realShowFloatWindow(context, string);
    }

  }

  private void realShowFloatWindow(Context context, String string) {
    // 显示悬浮窗, 3s后移除
    WindowManager windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
    int displayHeight = windowManager.getDefaultDisplay().getHeight();
    int displayWidth = windowManager.getDefaultDisplay().getWidth();


    // 原来没有textview, 新建一个
    // 原来有textview, 立刻不显示, 新建一个, 之前的会有对应的线程remove它
    if (textView != null) {
      if (textView.isAttachedToWindow()) {
        textView.setVisibility(View.INVISIBLE);
      }
    }
    textView = new TextView(context);

//    LinearLayoutCompat linearLayoutCompat = new LinearLayoutCompat(context);

//    textView.setBackgroundColor(context.getResources().getColor(R.color.ios_system_blue));
    textView.setTextColor(Color.WHITE);
    textView.setText(string);
//    textView.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//    textView.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
//    textView.setX(displayWidth/2-textView.getMeasuredWidth());
//    textView.setY(displayHeight/2-textView.getMeasuredHeight());

    textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
    textView.setPadding(10, 10, 10, 10);
    textView.setBackground(context.getResources().getDrawable(R.drawable.float_window_background));

//    linearLayoutCompat.setOrientation(LinearLayoutCompat.VERTICAL);
//    linearLayoutCompat.setGravity(Gravity.CENTER_HORIZONTAL);


    final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    } else {
      layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
    }

    layoutParams.x = 0;
    layoutParams.y = -(displayHeight / 2);
    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;

    layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    windowManager.addView(textView, layoutParams);
    new RemoveFloatWindowThread(textView, windowManager).start();
//    try {
//      Thread.sleep(3000);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
//    windowManager.removeView(textView);
//    Log.d(TAG, "ShowFloatWindowNotification: "+Thread.currentThread().getName());
  }

  class RemoveFloatWindowThread extends Thread {
    View floatWindowView;
    WindowManager windowManager;

    public RemoveFloatWindowThread(View floatWindowView, WindowManager windowManager) {
      this.floatWindowView = floatWindowView;
      this.windowManager = windowManager;
    }

    @Override
    public void run() {
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      floatWindowView.post(() -> {
        try {
          windowManager.removeView(floatWindowView);
        } catch (Exception e) {
          e.printStackTrace();
          Log.d(TAG, "run: remove textview exception");
        }
      });
    }

  }

  class InsertDatabaseThread extends Thread {
    long screenOffTime;
    long unlockTime;

    public InsertDatabaseThread(long unlockTime, long screenOffTime) {
      this.unlockTime = unlockTime;
      this.screenOffTime = screenOffTime;
    }

    @Override
    public void run() {
      InfoDatabaseSingleInstance.infoDAO.insertAll(
              new ReceivedBroadcastInfo(screenOffTime, screenOffTime, "锁定"),
              new ReceivedBroadcastInfo(unlockTime, unlockTime, "解锁")
      );
    }
  }

  String TAG = "PhoneLockReceiver";

}


