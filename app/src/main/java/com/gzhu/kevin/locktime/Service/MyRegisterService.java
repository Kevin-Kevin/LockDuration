package com.gzhu.kevin.locktime.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gzhu.kevin.locktime.BroadcastReceiver.PhoneLockReceiver;
import com.gzhu.kevin.locktime.DAO.InfoDatabaseSingleInstance;
import com.gzhu.kevin.locktime.Utils.NotificationHelper;

public class MyRegisterService extends Service {
  PhoneLockReceiver phoneLockReceiver;
  public MyRegisterService() {
  }
String TAG="MyRegisterService";
  @Override
  public void onCreate() {
    super.onCreate();
//    String pushString="null";
//
//    if (intent != null) {
//      pushString=intent.getAction();
//    }
    Log.d(TAG, "onCreate: "+Thread.currentThread().getName());
    InfoDatabaseSingleInstance.initDatabase(getApplicationContext());
    phoneLockReceiver = PhoneLockReceiver.getInstance();
    registerReceiver();
//    try {
//      unregisterReceiver(phoneLockReceiver);
//    }catch (IllegalArgumentException e){
//      phoneLockReceiver.isRegister=false;
//      NotificationHelper.pushNotification(getApplicationContext(),"启动失败,请打开自启动权限后",5*1000);
//      return super.onStartCommand(intent, flags, startId);
//    }
//      phoneLockReceiver.isRegister = true;

    NotificationHelper.pushNotification(getApplicationContext(),"服务运行中",5*1000);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return super.onStartCommand(intent, flags, startId);
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return new MyBinder(getApplicationContext());
  }
  private void registerReceiver() {
    try {
      unregisterReceiver(phoneLockReceiver);
    }catch (IllegalArgumentException e){
e.printStackTrace();
    }finally{

      IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
      //intentFilter.addAction(Intent.ACTION_SCREEN_ON);

      // screen locked broadcast receive
      intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
      // smart phone unlock broadcast receive
      intentFilter.addAction(Intent.ACTION_USER_PRESENT);

      this.registerReceiver(phoneLockReceiver, intentFilter);
    }


  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(phoneLockReceiver);
    InfoDatabaseSingleInstance.databaseClose();
  }

public class MyBinder extends Binder{
    Context context;

  public MyBinder(Context context) {
    this.context = context;
  }

  public void ShowFloatWindowNotification() {
    // 显示悬浮窗, 3s后移除
    WindowManager windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
    TextView textView = new TextView(context);

    final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    } else {
      layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
    }

    layoutParams.x = 300;
    layoutParams.y = 600;
    layoutParams.height = 100;
    layoutParams.width = 100;
    layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
    windowManager.addView(textView,layoutParams);
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    //windowManager.removeView(textView);
    Log.d(TAG, "ShowFloatWindowNotification: "+Thread.currentThread().getName());
    }
}

}