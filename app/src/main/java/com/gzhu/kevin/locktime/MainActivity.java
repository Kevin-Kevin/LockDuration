package com.gzhu.kevin.locktime;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.gzhu.kevin.locktime.BroadcastReceiver.PhoneLockReceiver;
import com.gzhu.kevin.locktime.DAO.InfoDAO;
import com.gzhu.kevin.locktime.DAO.InfoDatabase;
import com.gzhu.kevin.locktime.DAO.InfoDatabaseSingleInstance;
import com.gzhu.kevin.locktime.DAO.ReceivedBroadcastInfo;
import com.gzhu.kevin.locktime.Service.MyRegisterService;
import com.gzhu.kevin.locktime.Utils.ShareSharedPreferenceBean;
import com.gzhu.kevin.locktime.View.Viewpager2.DataListFragment;
import com.gzhu.kevin.locktime.View.Viewpager2.MainViewPager2Adapater;
import com.gzhu.kevin.locktime.View.Viewpager2.RecyclerView.MainRecyclerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

  String TAG = "mainActivity";
  SharedPreferences sharedPreferences;
  ViewPager2 viewPager2;
  TabLayout tabLayout;
  Switch notificationFloatSwitch;
  MainViewPager2Adapater mainViewPager2Adapater;
  Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate: ");
    setContentView(R.layout.activity_main);
    context = this;
    initView();
    InfoDatabaseSingleInstance.initDatabase(getApplicationContext());
  }

  List<MainRecyclerViewAdapter.ItemData> itemDataList;
  List<ReceivedBroadcastInfo> infoList;

  private void initView() {
    sharedPreferences = getSharedPreferences("share", MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    // 首次打开app
    if (sharedPreferences.getBoolean(ShareSharedPreferenceBean.share_is_first_start, true)) {
      editor.putBoolean(ShareSharedPreferenceBean.share_is_first_start, false);
      editor.putInt(ShareSharedPreferenceBean.share_after_how_many_seconds_show_notification, 0);
      editor.putString(ShareSharedPreferenceBean.share_welcome_words, "欢迎回来 : ");
      editor.apply();

    }

    viewPager2 = findViewById(R.id.main_view_pager2);
    tabLayout = findViewById(R.id.main_tab_layout);
//    notificationFloatSwitch = findViewById(R.id.main_notification_float_switch);
    mainViewPager2Adapater = new MainViewPager2Adapater(this);
    viewPager2.setAdapter(mainViewPager2Adapater);
    // tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

    new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
      switch (position) {
        case 0:
          tab.setText("锁定时间");
          break;
        case 1:
          tab.setText("今日锁定记录");
          break;
        case 2:
          tab.setText("关于");
          break;
        default:
          break;
      }

    }).attach();

    viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
      @Override
      public void onPageSelected(int position) {
        super.onPageSelected(position);
        Log.d(TAG, "onPageSelected: " + position);
        if (position == 1) {

          new GetDataOfRecyclerViewThread().start();
        }

      }


    });
//    new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
//      tab.setText("设置");
//    }).attach();
//    new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
//      tab.setText("关于");
//    }).attach();
//    new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
//      @Override
//      public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
//
//      }
//    }).attach();



  }



  class GetDataOfRecyclerViewThread extends Thread {
    @Override
    public void run() {


      RecyclerView recyclerView = findViewById(R.id.main_data_list_recycler_view);
      ProgressBar progressBar = findViewById(R.id.main_data_list_progress_bar);
      if(progressBar==null || recyclerView==null){
        new GetDataOfRecyclerViewThread().start();
        return;
      }
      try {
        progressBar.post(()->progressBar.setVisibility(View.VISIBLE));
        recyclerView.post(() -> {
          recyclerView.setVisibility(View.INVISIBLE);});
      } catch (NullPointerException e) {
        e.printStackTrace();
        Log.d(TAG, "run: recyclerview or progressbar is null");

      }

      itemDataList = new ArrayList<>();
      infoList = InfoDatabaseSingleInstance.infoDAO.getTodayRecord(getTodayStartTime());
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
      Drawable drawable;
      for (ReceivedBroadcastInfo info : infoList) {
        if (info.actionString.equals("解锁")) {
          drawable = getDrawable(R.drawable.unlock_green);
        } else {
          drawable = null;
        }
        itemDataList.add(new MainRecyclerViewAdapter.ItemData(info.actionString + " " + simpleDateFormat.format(new Date(info.time)), drawable));
      }
      Collections.reverse(itemDataList);
      try {
        recyclerView.post(() -> {
          recyclerView.setVisibility(View.VISIBLE);
          recyclerView.setAdapter(new MainRecyclerViewAdapter(itemDataList));
        });
        progressBar.post(()->progressBar.setVisibility(View.INVISIBLE));
      } catch (NullPointerException e) {
        e.printStackTrace();
        Log.d(TAG, "run: recyclerview or progressbar is null");
      }


    }

    public long getTodayStartTime() {
      //设置时区
      Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      return calendar.getTimeInMillis();
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.d(TAG, "onStart: ");
  }

  //TODO: 电池优化检测, 允许通知, 允许自启动
  @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume: ");
    // start service
    Intent intent = new Intent("自动开启服务", null, this, MyRegisterService.class);

    startService(intent);

    // refresh recycler view data
//    new GetDataOfRecyclerViewThread().start();

  }

  @Override
  protected void onPause() {
    super.onPause();
    Log.d(TAG, "onPause: ");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy: ");
    InfoDatabaseSingleInstance.db.close();

  }
}