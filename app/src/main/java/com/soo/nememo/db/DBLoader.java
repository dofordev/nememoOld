package com.soo.nememo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.soo.nememo.common.Settings;
import com.soo.nememo.item.MemoGroup;
import com.soo.nememo.item.NoteItem;
import com.soo.nememo.item.TodoItem;

import java.util.ArrayList;
import java.util.Date;

public class DBLoader {

    private Context mContext;
    private SQLiteDatabase db;

    public DBLoader(Context context){
        mContext = context;
        db =  new DBHelper(context).getWritableDatabase();
    }
    public MemoGroup insertGroup(String title, int type){
        String sql = "select max(seq)  from " +DBUtils.DB_GROUP_TABLE+ ";";
        Cursor result = db.rawQuery(sql, null);
        Long seq = Long.parseLong("0");

        if(result.moveToFirst()){
            do {
                seq  = result.getLong(0);
                seq += 1;
            } while (result.moveToNext());

        }
        result.close();

        ContentValues values = new ContentValues();
        values.put(DBUtils.COL_SEQ, seq);
        values.put(DBUtils.COL_TITLE, title);
        values.put(DBUtils.COL_TYPE, type);
        Long id = db.insert(DBUtils.DB_GROUP_TABLE, null, values);
        MemoGroup returnItem = new MemoGroup(id, seq, title, type, new Date());


        return returnItem;
    }
    public ArrayList<MemoGroup> selectGroup(){


        String sql = "select * from " +DBUtils.DB_GROUP_TABLE+ " order by "+ DBUtils.COL_SEQ +";";
        Cursor result = db.rawQuery(sql, null);

        ArrayList<MemoGroup> resultArr = new ArrayList<>();
        // result(Cursor 객체)가 비어 있으면 false 리턴
        if(result.moveToFirst()){

            do {
                Long id = result.getLong(0);
                Long seq = result.getLong(1);
                String title = result.getString(2);
                int type = result.getInt(3);

                MemoGroup item = new MemoGroup(id,seq, title,type,new Date());
                item.setSeq(seq);
                resultArr.add(item);
            } while (result.moveToNext());


        }
        result.close();


        return resultArr;

    }

    public Long insertMemo(NoteItem item){//String title, String note, String date){

        String sql = "select min(seq)  from " +DBUtils.DB_TABLE+ ";";
        Cursor result = db.rawQuery(sql, null);
        Long seq = Long.parseLong("0");

        if(result.moveToFirst()){
            do {
                seq  = result.getLong(0);
                seq -= 1;
            } while (result.moveToNext());

        }
        result.close();

        ContentValues values = new ContentValues();
        values.put(DBUtils.COL_SEQ, seq);
        values.put(DBUtils.COL_GROUP_ID, item.getGroupId());
        values.put(DBUtils.COL_TITLE, item.getTitle());
        values.put(DBUtils.COL_CONTENTS, item.getContents());
        values.put(DBUtils.COL_DATE, item.getDate());
        Long id = db.insert(DBUtils.DB_TABLE, null, values);
        return id;
    }
    public int update(NoteItem item){
        ContentValues values = new ContentValues();
        values.put(DBUtils.COL_SEQ, item.getSeq());
        values.put(DBUtils.COL_TITLE, item.getTitle());
        values.put(DBUtils.COL_CONTENTS, item.getContents());
        //values.put(DBUtils.COL_DATE, item.getDate());
        int id = db.update(DBUtils.DB_TABLE, values,  DBUtils.COL_ID + "=" + item.getId(), null);
        return id;
    }

    public void updateSeq(ArrayList arrayList){

        for (int i=0 ; i < arrayList.size() ; i++){
            String ClassName = arrayList.get(i).getClass().getName();
            if("NoteItem".equals(ClassName)){
                NoteItem item = (NoteItem)arrayList.get(i);
                ContentValues values = new ContentValues();
                values.put(DBUtils.COL_SEQ, i);
                int id = db.update(DBUtils.DB_TABLE, values, DBUtils.COL_ID+ "=" + item.getId(),null);
            }
            else if("TodoItem".equals(ClassName)){
                TodoItem item = (TodoItem)arrayList.get(i);
                ContentValues values = new ContentValues();
                values.put(DBUtils.COL_SEQ, i);
                int id = db.update(DBUtils.DB_TODO_TABLE, values, DBUtils.COL_ID+ "=" + item.getId(),null);
            }




        }

    }

    public void delete(NoteItem item){
        db.delete(DBUtils.DB_TABLE,DBUtils.COL_ID + "=" + item.getId() , null);
    }

    public NoteItem selectOne(Long id){

        NoteItem resultItem = new NoteItem();

        String sql = "select * from " +DBUtils.DB_TABLE+ " where "+ DBUtils.COL_ID + "=" + id + ";";
        Cursor result = db.rawQuery(sql, null);

        ArrayList<NoteItem> resultArr = new ArrayList<>();
        // result(Cursor 객체)가 비어 있으면 false 리턴
        if(result.moveToFirst()){
            Long id2 = result.getLong(0);
            Long groupId = result.getLong(1);
            Long seq = result.getLong(2);
            String title = result.getString(3);
            String contents = result.getString(4);

            resultItem = new NoteItem(id2,groupId, title,contents,new Date());
            resultItem.setId(id2);
            resultItem.setTitle(title);
            resultItem.setContents(contents);
            resultItem.setSeq(seq);
        }
        return resultItem;
    }

    public ArrayList<NoteItem> selectMemo(Long groupId){


        String sql = "select * from " +DBUtils.DB_TABLE+ " where " +DBUtils.COL_GROUP_ID+ "=" + groupId + " order by "+ DBUtils.COL_SEQ +";";
        Cursor result = db.rawQuery(sql, null);

        ArrayList<NoteItem> resultArr = new ArrayList<>();
        // result(Cursor 객체)가 비어 있으면 false 리턴
        if(result.moveToFirst()){

            do {
                Long id = result.getLong(0);
                Long group_Id = result.getLong(1);
                Long seq = result.getLong(2);
                String title = result.getString(3);
                String contents = result.getString(4);

                NoteItem item = new NoteItem(id,group_Id,title,contents,new Date());
                item.setSeq(seq);
                resultArr.add(item);
            } while (result.moveToNext());


        }
        result.close();


        return resultArr;



    }


    /**
     * 체크리스트 조회
     */
    public ArrayList<TodoItem> selectTodo(Long groupId){

        String sql = "select * from " +DBUtils.DB_TODO_TABLE+ " where " +DBUtils.COL_GROUP_ID+ "=" + groupId + " order by "+ DBUtils.COL_SEQ +";";
        Cursor result = db.rawQuery(sql, null);

        ArrayList<TodoItem> resultArr = new ArrayList<>();
        // result(Cursor 객체)가 비어 있으면 false 리턴
        if(result.moveToFirst()){

            do {
                Long id = result.getLong(0);
                Long group_Id = result.getLong(1);
                Long seq = result.getLong(2);
                String contents = result.getString(3);
                int checked = result.getInt(4);

                TodoItem item = new TodoItem(id,group_Id,contents,checked,new Date());
                item.setSeq(seq);
                resultArr.add(item);
            } while (result.moveToNext());

        }
        result.close();
        return resultArr;
    }

    /**
     * 체크리스트 저장
     */
    public Long insertTodo(TodoItem item){

        String sql = "select min(seq)  from " +DBUtils.DB_TODO_TABLE+ ";";
        Cursor result = db.rawQuery(sql, null);
        Long seq = Long.parseLong("0");

        if(result.moveToFirst()){
            do {
                seq  = result.getLong(0);
                seq -= 1;
            } while (result.moveToNext());

        }
        result.close();

        ContentValues values = new ContentValues();
        values.put(DBUtils.COL_SEQ, seq);
        values.put(DBUtils.COL_GROUP_ID, item.getGroupId());
        values.put(DBUtils.COL_CONTENTS, item.getContents());
        values.put(DBUtils.COL_CHECKED, item.getChecked());
        values.put(DBUtils.COL_DATE, item.getDate());
        Long id = db.insert(DBUtils.DB_TODO_TABLE, null, values);
        return id;
    }

    public int updateTodo(TodoItem item){
        ContentValues values = new ContentValues();
        values.put(DBUtils.COL_SEQ, item.getSeq());
        values.put(DBUtils.COL_CONTENTS, item.getContents());
        values.put(DBUtils.COL_CHECKED, item.getChecked());
        //values.put(DBUtils.COL_DATE, item.getDate());
        int id = db.update(DBUtils.DB_TODO_TABLE, values,  DBUtils.COL_ID + "=" + item.getId(), null);
        return id;
    }

}
