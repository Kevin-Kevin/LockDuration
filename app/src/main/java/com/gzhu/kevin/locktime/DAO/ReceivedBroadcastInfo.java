package com.gzhu.kevin.locktime.DAO;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ReceivedBroadcastInfo {
  volatile static public long realID=0;
  @PrimaryKey
   public long id;

  public ReceivedBroadcastInfo(long id, long time, String actionString) {
    this.id = id;
    this.time = time;
    this.actionString = actionString;
  }

  @ColumnInfo(name="time")
  public long time;
  @ColumnInfo(name="action")
  public String actionString;
}
