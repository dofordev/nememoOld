package com.soo.nememo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.soo.nememo.R;
import com.soo.nememo.common.Settings;
import com.soo.nememo.db.DBLoader;
import com.soo.nememo.item.NoteItem;
import com.soo.nememo.item.TodoItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.MyViewholder> implements ItemTouchHelperCallback.OnItemMoveListener {


    private ArrayList<TodoItem> arrayList;


    private Context context;
    private OnStartDragListener mStartDragListener;
    private DBLoader dbLoader;
    //리스트 아이템 아이디
    private int listLayout;

    public interface OnStartDragListener{
        void onStartDrag(MyViewholder holder);
    }
    public class MyViewholder extends RecyclerView.ViewHolder{


        protected TextView contentsView;
        protected View listView;
        protected ImageButton checkImg;
        /*
        protected Button moveBtnView;

        */
        public MyViewholder(View view){
            super(view);

            this.contentsView = (TextView) view.findViewById(R.id.todoContents);
            this.listView = (View) view.findViewById(R.id.card_view);
            this.checkImg = (ImageButton) view.findViewById(R.id.checkImg);
            /*
            this.moveBtnView = (Button) view.findViewById(R.id.moveBtn);

            */
        }
    }




    public TodoListAdapter(Context context, OnStartDragListener startDragListener, int listLayout){
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
        TodoItem todoItem = arrayList.get(position);
        //holder.titleView.setText(noteItem.getTitle());
        holder.contentsView.setText(todoItem.getContents());


        if( todoItem.getChecked() == 0 ){
            //취소선 제거
            holder.contentsView.setPaintFlags(0);
            //체크 이미지 hide
            holder.checkImg.setVisibility(View.GONE);
        }
        else{ //확인상태
            //취소선 적용
            holder.contentsView.setPaintFlags(holder.contentsView.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            //체크 이미지 show
            holder.checkImg.setVisibility(View.VISIBLE);
        }

        //터치이동
        /*
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
*/
        holder.listView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                TodoItem todoItem = arrayList.get(holder.getLayoutPosition());
                if( todoItem.getChecked() == 1 ){   //확인상태
                    //취소선 제거
                    holder.contentsView.setPaintFlags(0);
                    //체크 이미지 hide
                    holder.checkImg.setVisibility(View.GONE);
                    //업데이트
                    todoItem.setChecked(0);
                    dbLoader.updateTodo(todoItem);

                }
                else{
                    //취소선 적용
                    holder.contentsView.setPaintFlags(holder.contentsView.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                    //체크 이미지 show
                    holder.checkImg.setVisibility(View.VISIBLE);
                    //업데이트
                    todoItem.setChecked(1);
                    dbLoader.updateTodo(todoItem);
                }



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




    }

    @Override
    public void onSelectedChanged() {
        dbLoader.updateSeq(arrayList);
    }

    public void setList(ArrayList<TodoItem> arrayList){
        this.arrayList = arrayList;
        //this.tmpList.addAll(arrayList);
        notifyDataSetChanged();
    }

    public void refreshList(Long groupId){

        ArrayList<TodoItem> dbArrayList = dbLoader.selectTodo(groupId);
        this.arrayList = dbArrayList;

        for(TodoItem item : dbArrayList){
            Log.i(Settings.LOG_TAG,"todoItem ==" + item.getContents());
        }

        notifyDataSetChanged();
    }
    public void addItem(TodoItem item){
        this.arrayList.add(0,item);
        notifyDataSetChanged();
    }

    public void deleteItem(NoteItem item){
        dbLoader.delete(item);
        ArrayList<TodoItem> dbArrayList = dbLoader.selectTodo(item.getGroupId());
        this.arrayList = dbArrayList;
        notifyDataSetChanged();
    }


   }
