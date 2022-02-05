package com.gzhu.kevin.locktime.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gzhu.kevin.locktime.Service.MyRegisterService;

public class BootCompleteReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    String action="default";
    switch(intent.getAction()){
      case Intent.ACTION_BOOT_COMPLETED:
        action="boot_completed";
        break;
      case Intent.ACTION_LOCKED_BOOT_COMPLETED:
        action="locked_boot_completed";
        break;
    }
    // start service
    Intent serviceIntent = new Intent(action+", 服务开机启动",null,context, MyRegisterService.class);
    context.startService(serviceIntent);

  }
}
