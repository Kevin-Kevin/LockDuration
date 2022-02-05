package com.gzhu.kevin.locktime.DAO;

import android.content.Context;

import androidx.room.Room;

public class InfoDatabaseSingleInstance {
  static public InfoDatabase db;
  static public InfoDAO infoDAO;
  static public void initDatabase(Context context) {
    InfoDatabaseSingleInstance.db = Room.databaseBuilder(context,
            InfoDatabase.class, "phone-lock-record-database").build();

    InfoDatabaseSingleInstance.infoDAO = InfoDatabaseSingleInstance.db.infoDAO();

  }
  static public void databaseClose(){
    db.close();
  }
}
