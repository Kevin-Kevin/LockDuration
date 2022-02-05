package com.gzhu.kevin.locktime.DAO;

import androidx.room.Database;
import androidx.room.RoomDatabase;
@Database(entities = {ReceivedBroadcastInfo.class}, version = 1, exportSchema = false)
public abstract class InfoDatabase extends RoomDatabase {
  public abstract InfoDAO infoDAO();
}
