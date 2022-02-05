package com.gzhu.kevin.locktime.View.Viewpager2;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.POWER_SERVICE;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.gzhu.kevin.locktime.BroadcastReceiver.PhoneLockReceiver;
import com.gzhu.kevin.locktime.R;
import com.gzhu.kevin.locktime.Service.MyRegisterService;
import com.gzhu.kevin.locktime.SetFloatWindowLocationActivity;
import com.gzhu.kevin.locktime.Utils.NotificationHelper;
import com.gzhu.kevin.locktime.Utils.ShareSharedPreferenceBean;
import com.gzhu.kevin.locktime.databinding.MainSettingFragmentBinding;

import java.net.URL;

public class SettingFragment extends Fragment {
  private MainSettingFragmentBinding binding;
  Button button;

  SharedPreferences sharedPreferences;
  Switch mainSwitch;

  public SettingFragment() {
    super(R.layout.main_setting_fragment);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override

  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    binding = MainSettingFragmentBinding.inflate(inflater, container, false);
    View view = binding.getRoot();
    initView(binding);
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    refreshSwitchState(binding);
  }

  void refreshSwitchState(MainSettingFragmentBinding bingding) {
    binding.showNotificationSwitch.setChecked(isServiceRunning("MyRegisterService"));

    boolean hasIgnored;
    PowerManager powerManager = (PowerManager) getContext().getSystemService(POWER_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      hasIgnored = powerManager.isIgnoringBatteryOptimizations(getContext().getPackageName());
    } else {
      hasIgnored=true;
    }

    binding.batteryOptimizationSwitch.setChecked(hasIgnored);

    boolean haveOverlayPermisson;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      haveOverlayPermisson=Settings.canDrawOverlays(getContext());
    }else {
      haveOverlayPermisson=true;
    }
    binding.overlayPermissionSwitch.setChecked(haveOverlayPermisson);


  }
//  hide soft keyboard
  public  void hideKeyboard(View v) {
    InputMethodManager manager = ((InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE));
    if (manager != null)
      manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    v.clearFocus();
  }

  void initView(MainSettingFragmentBinding binding) {
// set whether turn on service
    binding.showNotificationSwitch.setChecked(isServiceRunning("MyRegisterService"));
    binding.showNotificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (isChecked) {
        getContext().startService(new Intent(getContext(), MyRegisterService.class));
      } else {
        getContext().stopService(new Intent(getContext(), MyRegisterService.class));
      }
    });


    // set after How Many Time Show EditText
    sharedPreferences = getActivity().getSharedPreferences("share", MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    int after = sharedPreferences.getInt(ShareSharedPreferenceBean.share_after_how_many_seconds_show_notification, 0);

    binding.afterHowManyTimeShowEditText.setText(String.valueOf(after));
    binding.afterHowManyTimeShowEditText.addTextChangedListener(new main_text_watcher());
    binding.afterHowManyTimeShowEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId== EditorInfo.IME_ACTION_DONE){
          hideKeyboard(v);
        return true;
        }
        return  false;
      }
    });

// set ignoring battery optimization switch
    boolean hasIgnored;
    PowerManager powerManager = (PowerManager) getContext().getSystemService(POWER_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      hasIgnored = powerManager.isIgnoringBatteryOptimizations(getContext().getPackageName());
    } else {
      hasIgnored=true;
    }
    Log.d(TAG, "initView: ignore battery optimization  ="+hasIgnored);
    binding.batteryOptimizationSwitch.setChecked(hasIgnored);
    binding.batteryOptimizationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (isChecked) {
        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:"+getContext().getPackageName()));
        startActivity(intent);

      }else {
      }
    });

// set auto start permission
    binding.selfTurnOnRelativeLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Snackbar.make(v,"请开启应用自启权限, 否则应用无法正常工作",3000).show();
      }
    });
// set overlay permission switch
    boolean haveOverlayPermisson;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      haveOverlayPermisson=Settings.canDrawOverlays(getContext());
    }else {
      haveOverlayPermisson=true;
    }
    binding.overlayPermissionSwitch.setChecked(haveOverlayPermisson);
    binding.overlayPermissionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (isChecked) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


          Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//          intent.setData(Uri.parse("package:" + getContext().getPackageName()));
          startActivity(intent);
        }
      }
    });
// set float window location
    binding.setFloatWindowLocationRelativeLayout.setOnClickListener(v -> {
      Intent intent= new Intent(getContext(), SetFloatWindowLocationActivity.class);
      getContext().startActivity(intent);
    });




//  mainSwitch = view.findViewById(R.id.main_notification_float_switch);

    // set button listener
//  button.setOnClickListener((v) -> {
//    getContext().startService(new Intent("手动开启服务", null,getContext(),MyRegisterService.class));
//  });



//    mainSwitch.setChecked(sharedPreferences.getBoolean(ShareSharedPreferenceBean.share_notification_float, NotificationHelper.is_notification_float));

    // set main switch listener
//  mainSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//    NotificationHelper.is_notification_float = isChecked;
//    editor.putBoolean(ShareSharedPreferenceBean.share_notification_float,NotificationHelper.is_notification_float).apply();
//    Log.d("notification is float?" ,String.valueOf(NotificationHelper.is_notification_float));
//  });
//  Switch floatWindowPermission=view.findViewById(R.id.overlay_permission_switch);
//
//  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//    floatWindowPermission.setChecked(Settings.canDrawOverlays(getContext()));
//  } else{
//    floatWindowPermission.setChecked(true);
//  }
//  floatWindowPermission.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked)->{
//    String TAG = "kevin";
//    Log.d(TAG, "onCreateView: "+isChecked);
//    if(isChecked){
//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//        if (!Settings.canDrawOverlays(getContext())) {
//          Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//          intent.setData(Uri.parse("package:" + getContext().getPackageName()));
//          startActivity(intent);
//        }
//      }
//    }
//  });

  }

  String TAG = "kevin";
  private boolean isServiceRunning(String servicePackageName) {
    ActivityManager manager = (ActivityManager) getContext().getSystemService(ACTIVITY_SERVICE);

    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (servicePackageName.equals(service.service.getClassName())) {
        Log.d(TAG, "isServiceRunning: "+servicePackageName+" equals");
        return true;
      }
    }
    Log.d(TAG, "isServiceRunning: "+servicePackageName+" not equals");
    return false;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d("fragment", "onDestroy");
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  class main_text_watcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {

      Integer AInt;
      try {
        AInt = Integer.parseInt(s.toString());
      } catch (NumberFormatException e) {
        AInt = 0;
      }
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putInt(ShareSharedPreferenceBean.share_after_how_many_seconds_show_notification, AInt);
      editor.apply();
      Log.d("share", String.valueOf(sharedPreferences.getInt("after_how_many_seconds_show_notification", 0)));


    }
  }

}
