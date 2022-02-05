package com.gzhu.kevin.locktime.View.Viewpager2;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.gzhu.kevin.locktime.View.Viewpager2.AboutAppFragment;
import com.gzhu.kevin.locktime.View.Viewpager2.DataListFragment;
import com.gzhu.kevin.locktime.View.Viewpager2.SettingFragment;

import java.util.ArrayList;
import java.util.List;

public class MainViewPager2Adapater extends FragmentStateAdapter {
  List<Fragment> fragmentList = new ArrayList<>();

  public MainViewPager2Adapater(@NonNull FragmentActivity fragmentActivity) {
    super(fragmentActivity);
//    测试看下还会不会有 error：Invalid ID 0x00000001
    fragmentList.add(new SettingFragment());
    fragmentList.add(new DataListFragment());
    fragmentList.add(new AboutAppFragment());

  }
String TAG="kevin";
  @NonNull
  @Override
  public Fragment createFragment(int position) {
    Log.d(TAG, "createFragment: "+position);
    switch(position){
      case 0:
        return new SettingFragment();
      case 1:
        return new DataListFragment();
      case 2:
        return new AboutAppFragment();
    }
    Log.d(TAG, "createFragment: use one of list");
    return fragmentList.get(position);

  }

  @Override
  public int getItemCount() {
    return fragmentList.size();
  }
}
