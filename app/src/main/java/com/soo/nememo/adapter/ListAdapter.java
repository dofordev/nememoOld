package com.soo.nememo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.soo.nememo.R;
import com.soo.nememo.TextDetailActivity;
import com.soo.nememo.common.Settings;
import com.soo.nememo.db.DBLoader;
import com.soo.nememo.item.NoteItem;
import com.soo.nememo.item.TodoItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewholder> implements ItemTouchHelperCallback.OnItemMoveListener {


    private ArrayList<NoteItem> arrayList;
    private ArrayList<NoteItem> tmpList = new ArrayList<NoteItem>();


    private Context context;
    private OnStartDragListener mStartDragListener;
    private DBLoader dbLoader;
    //리스트 아이템 아이디
    private int listLayout;

    public interface OnStartDragListener{
        void onStartDrag(MyViewholder holder);
    }
    public class MyViewholder extends RecyclerView.ViewHolder{
        protected TextView titleView;
        protected TextView contentsView;
        protected Button moveBtnView;
        protected View listView;
        public MyViewholder(View view){
            super(view);
            this.titleView = (TextView) view.findViewById(R.id.textTitle);
            this.contentsView = (TextView) view.findViewById(R.id.textContents);
            this.moveBtnView = (Button) view.findViewById(R.id.moveBtn);
            this.listView = (View) view.findViewById(R.id.card_view);
        }
    }




    public ListAdapter(Context context, OnStartDragListener startDragListener,int listLayout){
        this.context = context;
        this.mStartDragListener = startDragListener;
        this.dbLoader = new DBLoader(context);
        this.listLayout = listLayout;
    }



    @Override
    public MyViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listLayout, parent,false);
       // RecyclerView.ViewHolder viewHolder = new RecyclerView.ViewHolder(view);
        MyViewholder viewHolder = new MyViewholder(view);
        return  viewHolder;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final MyViewholder holder, int position) {
        NoteItem noteItem = arrayList.get(position);
        holder.titleView.setText(noteItem.getTitle());
        holder.contentsView.setText(noteItem.getContents());


        //터치이동
        holder.moveBtnView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                Log.e("aaaaaaaaaaaaaa","터치!!" + MotionEventCompat.getActionMasked(event) + "==" + event.ACTION_DOWN);
                if(event.getActionMasked() == event.ACTION_DOWN){
                    mStartDragListener.onStartDrag(holder);
                }
                return false;

            }
        });

        holder.listView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Context context = view.getContext();
                Intent intent = new Intent(context, TextDetailActivity.class);
                intent.putExtra("item", arrayList.get(holder.getLayoutPosition()));
                ((Activity)context).startActivityForResult(intent,1);
            }
        });
    }

    @Override
    public int getItemCount() {

        return arrayList != null ? arrayList.size() : 0;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition){
        Collections.swap(arrayList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);


        for (NoteItem aa : arrayList){
            Log.e("aaaaaaaaaaaaaaaaaa=====",aa.getTitle());
        }


    }

    @Override
    public void onSelectedChanged() {
        dbLoader.updateSeq(arrayList);
    }

    public void setList(ArrayList<NoteItem> arrayList){
        this.arrayList = arrayList;
        this.tmpList.addAll(arrayList);
        notifyDataSetChanged();
    }

    public void refreshList(Long groupId){

        ArrayList<NoteItem> dbArrayList = dbLoader.selectMemo(groupId);
        this.arrayList = dbArrayList;

        for(NoteItem item : dbArrayList){
            Log.i(Settings.LOG_TAG,"noteItem ==" + item.getTitle());
        }
        notifyDataSetChanged();
    }
    public void addItem(NoteItem item){
        this.arrayList.add(0,item);
        notifyDataSetChanged();
    }

    public void deleteItem(NoteItem item){
        dbLoader.delete(item);
        ArrayList<NoteItem> dbArrayList = dbLoader.selectMemo(item.getGroupId());
        this.arrayList = dbArrayList;
        notifyDataSetChanged();
    }


    //텍스트유형 메모 검색 함수
    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        this.arrayList.clear();
        if (charText.length() == 0) {
            this.arrayList.addAll(tmpList);
        } else {
            for (NoteItem item : tmpList) {


                String title = item.getTitle();
                if (title.toLowerCase().contains(charText)) {
                    this.arrayList.add(item);
                }
                String contents = item.getContents();
                if (contents.toLowerCase().contains(charText)) {
                    this.arrayList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }


   }
