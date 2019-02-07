package com.soo.nememo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.soo.nememo.db.DBLoader;
import com.soo.nememo.item.NoteItem;

import java.util.Date;

public class TextDetailActivity extends AppCompatActivity {

    private Context mContext;
    private DBLoader db;
    private MenuItem deleteBtn;

    private String orgTitle = "";
    private String orgContens = "";
    private NoteItem noteItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_detail);

        mContext = this;
        db = new DBLoader(mContext);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //상세보기
        Intent intent = getIntent();
        noteItem = (NoteItem)intent.getSerializableExtra("item");


        if(noteItem.getId() == null){
            getSupportActionBar().setTitle("새메모");
            //findViewById(R.id.menu_back).setVisibility(View.INVISIBLE);
            //deleteBtn.setVisible(false);
        }
        else{
            getSupportActionBar().setTitle("메모수정");

            String title = noteItem.getTitle();
            String contents = noteItem.getContents();
            ((TextView)findViewById(R.id.detailTitle)).setText(title);
            ((TextView)findViewById(R.id.detailContents)).setText(contents);

            orgTitle = title;
            orgContens = contents;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu,menu);


        deleteBtn =  menu.findItem(R.id.menu_delete);
        //신규입력일때는 삭제버튼 숨김
        if(noteItem == null){
            deleteBtn.setVisible(false);
        }


        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                this.onBackPressed();
                return true;
            }
            case R.id.menu_delete:{


                AlertDialog.Builder alt_bld = new AlertDialog.Builder(mContext);
                alt_bld.setMessage("메모를 삭제 하시겠습니까?").setCancelable(false).setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("item",noteItem);
                        resultIntent.putExtra("mode","delete");
                        setResult(RESULT_OK,resultIntent);
                        finish();
                        Toast.makeText(mContext, "삭제 성공", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                AlertDialog alert = alt_bld.create();
                alert.show();



                return true;
            }
            case R.id.menu_save:{
                String title = ((TextView) findViewById(R.id.detailTitle)).getText().toString();
                String contents = ((TextView) findViewById(R.id.detailContents)).getText().toString();

                Intent resultIntent = new Intent();
                if(noteItem.getId() == null){
                    Long groupId = noteItem.getGroupId();
                    noteItem = new NoteItem();
                    noteItem.setGroupId(groupId);
                    noteItem.setTitle(title);
                    noteItem.setContents(contents);
                    noteItem.setDate(new Date());
                    Long id = db.insertMemo(noteItem);
                    noteItem.setId(id);

                    Toast.makeText(mContext, "저장 성공", Toast.LENGTH_SHORT).show();

                    resultIntent.putExtra("mode","insert");
                }
                else{
                    Toast.makeText(mContext, "수정 성공", Toast.LENGTH_SHORT).show();
                    noteItem.setContents(contents);
                    noteItem.setTitle(title);
                    db.update(noteItem);

                    resultIntent.putExtra("mode","update");
                }




                resultIntent.putExtra("item",noteItem);

                setResult(RESULT_OK,resultIntent);

                finish();
                return true;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        String title = ((TextView) findViewById(R.id.detailTitle)).getText().toString();
        String contents = ((TextView) findViewById(R.id.detailContents)).getText().toString();

        if( title.equals(orgTitle) && contents.equals(orgContens)){
            finish();
        }
        else{
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(mContext);
            alt_bld.setMessage("메모가 작성중입니다.\n정말로 닫으시겠습니까?").setCancelable(false).setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                }
            }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });


            AlertDialog alert = alt_bld.create();
            alert.show();
        }

    }

}
