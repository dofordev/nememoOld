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
import android.widget.EditText;

import com.soo.nememo.adapter.ItemTouchHelperCallback;
import com.soo.nememo.adapter.ListAdapter;
import com.soo.nememo.common.Settings;
import com.soo.nememo.db.DBLoader;
import com.soo.nememo.item.NoteItem;

import java.util.ArrayList;

/**
 * Created by Jongsoo on 2018-09-18
 */

public class TextFragment extends Fragment implements ListAdapter.OnStartDragListener{

    private Context mContext;
    private RecyclerView mRecyclerView;
    private ListAdapter mListAdapter;
    private ArrayList<NoteItem> mArrayList;
    private ItemTouchHelper mItemTouchHelper;
    private Long groupId;

    private DBLoader dbLoader;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        groupId = getArguments().getLong("groupId");
        View view = inflater.inflate(R.layout.text_fragment, container, false);
        mContext = getActivity();
        mArrayList = new ArrayList<NoteItem>();

        DBLoader dbLoader = new DBLoader(mContext);

        ArrayList<NoteItem> mArrayList = dbLoader.selectMemo(groupId);





        mRecyclerView = (RecyclerView) view.findViewById(R.id.listWrap);



        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext.getApplicationContext()));
        mRecyclerView.setHasFixedSize(true);


        mListAdapter = new ListAdapter(mContext.getApplicationContext(),this, R.layout.text_list_item);

        ItemTouchHelperCallback mCallback = new ItemTouchHelperCallback(mListAdapter);
        mItemTouchHelper = new ItemTouchHelper(mCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(mListAdapter);
        mListAdapter.setList(mArrayList);


        ((MainActivity)getActivity()).setFragmentRefreshListener(new MainActivity.FragmentRefreshListener() {
            @Override
            public void onRefresh(Long gId) {
                // Refresh  Fragment
                Log.e(Settings.LOG_TAG , "groupId === " + gId);
                mListAdapter.refreshList(gId);
            }
            @Override
            public void addItem(NoteItem item) {
                mListAdapter.addItem(item);
            }
            @Override
            public void deleteItem(NoteItem item) {
                mListAdapter.deleteItem(item);
            }

        });

        final EditText edit = (EditText)view.findViewById(R.id.searchText);

        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // 입력되는 텍스트에 변화가 있을 때

            }
            @Override
            public void afterTextChanged(Editable arg0) {
                Log.e(Settings.LOG_TAG , "text === " + edit.getText().toString());
                // 입력이 끝났을 때
                mListAdapter.filter(edit.getText().toString());

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // 입력하기 전에

            }
        });




        return view;
    }


    @Override
    public void onStartDrag(ListAdapter.MyViewholder holder) {

        mItemTouchHelper.startDrag(holder);
    }
}
