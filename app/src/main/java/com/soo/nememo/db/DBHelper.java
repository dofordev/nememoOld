package com.soo.nememo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


public class DBHelper extends SQLiteOpenHelper {



    public DBHelper(@Nullable Context context) {
        super(context, DBUtils.DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //메모 그룹 테이블
        String groupSql = "create table " + DBUtils.DB_GROUP_TABLE  + "("+ DBUtils.COL_ID +" integer primary key autoincrement, " + DBUtils.COL_SEQ + " integer, " + DBUtils.COL_TITLE + " text,"
                + DBUtils.COL_TYPE + " integer, " + DBUtils.COL_DATE + " text)";
        db.execSQL(groupSql);

        //텍스트 메모 리스트 테이블
        String memoSql = "create table " + DBUtils.DB_TABLE  + "("+ DBUtils.COL_ID +" integer primary key autoincrement, " + DBUtils.COL_GROUP_ID + " integer, " + DBUtils.COL_SEQ + " integer, " + DBUtils.COL_TITLE + " text,"
                + DBUtils.COL_CONTENTS + " text, " + DBUtils.COL_DATE + " text)";
        db.execSQL(memoSql);

        //체크리스트 리스트 테이블
        String todoSql = "create table " + DBUtils.DB_TODO_TABLE  + "("+ DBUtils.COL_ID +" integer primary key autoincrement, " + DBUtils.COL_GROUP_ID + " integer, " + DBUtils.COL_SEQ + " integer, "
                + DBUtils.COL_CONTENTS + " text, " + DBUtils.COL_CHECKED + " integer, " + DBUtils.COL_DATE + " text)";
        db.execSQL(todoSql);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
