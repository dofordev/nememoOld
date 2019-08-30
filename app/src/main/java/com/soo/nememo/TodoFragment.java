package com.soo.nememo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.soo.nememo.adapter.ItemTouchHelperCallback;
import com.soo.nememo.adapter.ListAdapter;
import com.soo.nememo.adapter.TodoListAdapter;
import com.soo.nememo.db.DBLoader;
import com.soo.nememo.item.NoteItem;
import com.soo.nememo.item.TodoItem;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jongsoo on 2018-09-18
 */

public class TodoFragment extends Fragment implements TodoListAdapter.OnStartDragListener{

    private Context mContext;
    private RecyclerView mRecyclerView;
    private TodoListAdapter mListAdapter;
    private ArrayList<TodoItem> mArrayList;
    private ItemTouchHelper mItemTouchHelper;
    private Long groupId;

    private DBLoader dbLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        groupId = getArguments().getLong("groupId");
        View view = inflater.inflate(R.layout.todo_fragment, container, false);
        mContext = getActivity();
        mArrayList = new ArrayList<TodoItem>();

        final DBLoader dbLoader = new DBLoader(mContext);
        ArrayList<TodoItem> mArrayList = dbLoader.selectTodo(groupId);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.todoListWrap);



        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext.getApplicationContext()));
        mRecyclerView.setHasFixedSize(true);


        mListAdapter = new TodoListAdapter(mContext.getApplicationContext(),this, R.layout.todo_list_item);

        ItemTouchHelperCallback mCallback = new ItemTouchHelperCallback(mListAdapter);
        mItemTouchHelper = new ItemTouchHelper(mCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(mListAdapter);
        mListAdapter.setList(mArrayList);



        final EditText todoText = (EditText)view.findViewById(R.id.todoText);

        final Button saveBtn = (Button)view.findViewById(R.id.todoSaveBtn);


        //체크리스트 저장
        saveBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                //키보드 숨기기
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(todoText.getWindowToken() , 0);




                TodoItem todoItem = new TodoItem();
                todoItem.setContents(todoText.getText().toString());
                todoItem.setGroupId(groupId);
                todoItem.setChecked(0);
                todoItem.setDate(new Date());
                Long id = dbLoader.insertTodo(todoItem);
                todoItem.setId(id);
                mListAdapter.refreshList(groupId);


                todoText.setText("");
                todoText.clearFocus();

                Toast.makeText(mContext, "저장", Toast.LENGTH_SHORT).show();



            }
        });

        /*
        final EditText edit = (EditText)view.findViewById(R.id.searchText);

        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // 입력되는 텍스트에 변화가 있을 때

            }
            @Override
            public void afterTextChanged(Editable arg0) {
                Log.e("aaaaaaaaaaaaaaaaaa" , "text === " + edit.getText().toString());
                // 입력이 끝났을 때
                mListAdapter.filter(edit.getText().toString());

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // 입력하기 전에

            }
        });
*/



        return view;
    }


    @Override
    public void onStartDrag(TodoListAdapter.MyViewholder holder) {

        mItemTouchHelper.startDrag(holder);
    }
}
