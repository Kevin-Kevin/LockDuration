package com.gzhu.kevin.locktime.View.Viewpager2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gzhu.kevin.locktime.DAO.InfoDatabaseSingleInstance;
import com.gzhu.kevin.locktime.DAO.ReceivedBroadcastInfo;
import com.gzhu.kevin.locktime.R;
import com.gzhu.kevin.locktime.View.Viewpager2.RecyclerView.MainRecyclerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class DataListFragment extends Fragment {
  List<MainRecyclerViewAdapter.ItemData> itemDataList;
  RecyclerView recyclerView;
  public DataListFragment(){
    super(R.layout.main_data_list_fragment);
  }
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view=super.onCreateView(inflater, container, savedInstanceState);
     recyclerView = view.findViewById(R.id.main_data_list_recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    // new GetDatabaseRecordThread().start();
    return view;
  }

}
