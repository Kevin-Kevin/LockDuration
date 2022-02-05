package com.gzhu.kevin.locktime.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InfoDAO {
  @Query("SELECT * FROM ReceivedBroadcastInfo")
  List<ReceivedBroadcastInfo> getAll();

  @Query("SELECT * FROM ReceivedBroadcastInfo WHERE id > (:todayStartTime)")
  List<ReceivedBroadcastInfo> getTodayRecord(long todayStartTime);

  @Insert
  void insertAll(ReceivedBroadcastInfo... ReceivedBroadcastInfos);

  @Delete
  void delete(ReceivedBroadcastInfo ReceivedBroadcastInfo);
}
