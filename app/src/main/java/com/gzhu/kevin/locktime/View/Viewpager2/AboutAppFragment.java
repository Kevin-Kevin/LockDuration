package com.gzhu.kevin.locktime.View.Viewpager2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gzhu.kevin.locktime.R;

public class AboutAppFragment extends Fragment {
  @Override

  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.main_about_app_fragment, container, false);
    return view;
  }
}
