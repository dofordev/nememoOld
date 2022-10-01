package com.soo.nememo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.soo.nememo.common.Dialog;
import com.soo.nememo.common.Settings;
import com.soo.nememo.common.Utils;
import com.soo.nememo.db.DBLoader;
import com.soo.nememo.item.MemoGroup;
import com.soo.nememo.item.NoteItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    private TabPagerAdapter pagerAdapter;
    private SharedPreferences prefs;

    private DBLoader dbLoader;

    private long backPressedTime = 0;
    private Context mContext;

    //그룹 아이디 리스트
    private ArrayList<MemoGroup> groupList = new ArrayList<MemoGroup>();
    //현재탭
    private int tabIndexaaa;



    private FragmentRefreshListener fragmentRefreshListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setContentView(R.layout.activity_main);
        mContext = this;

        //플로팅 버튼 이벤트
        fab = (FloatingActionButton) findViewById(R.id.fab);

        dbLoader = new DBLoader(this);

        // Adding Toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        prefs = getSharedPreferences("PrefName", MODE_PRIVATE);

        // Initializing the TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        //디비에서 메모 그룹값 가져와서 뿌리기
        ArrayList<MemoGroup> memoList =  dbLoader.selectGroup();

        for (MemoGroup item : memoList){
            addViewPager(item);
        }

        if(memoList.size() > 0){
            toggleFab();
        }




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(mContext, TextDetailActivity.class);

                NoteItem item = new NoteItem();
                item.setGroupId(groupList.get(tabIndex).getId());

                System.out.println("========저장시===" + groupList.get(tabIndex).getId());

                intent.putExtra("item", item);
                //requestCode 1 : text타입 상세
                startActivityForResult(intent,Settings.TEXT_DETAIL_CODE);



/*
                tabLayout.addTab(tabLayout.newTab().setText( "탭" + (tabLayout.getTabCount() + 1) ));
                initViewPager();

                pagerAdapter.notifyDataSetChanged();
                */
                /*
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */
            }
        });






        // URL 설정.
        //String url = "http://www.pocaskenm.kr/api/saving_check_complite.php";

        // AsyncTask를 통해 HttpURLConnection 수행.
        //NetworkTask networkTask = new NetworkTask(url, null);
        //networkTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            //메모 그룹 생성
            case R.id.menu_add:{




                // 커스텀 다이얼로그를 생성한다. 사용자가 만든 클래스이다.
                Dialog Dialog = new Dialog(MainActivity.this);

                //노트 추가 다이얼로그 호출
                Dialog.addDialog();




                return true;
            }


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Settings.TEXT_DETAIL_CODE && resultCode == RESULT_OK) {


            NoteItem item = (NoteItem)data.getSerializableExtra("item");

            String mode = data.getExtras().getString("mode");
            if(getFragmentRefreshListener()!=null) {
                if(mode.equals("insert")){

                    Log.i(Settings.LOG_TAG,"저장후===tabIndex = " + tabIndex + "=groupId=" + groupList.get(tabIndex).getId());
                    getFragmentRefreshListener().onRefresh(groupList.get(tabIndex).getId());
                    //getFragmentRefreshListener().addItem(item);
                }

                else if(mode.equals("update")){
                    getFragmentRefreshListener().onRefresh(groupList.get(tabIndex).getId());
                }
                else if(mode.equals("delete")){
                    getFragmentRefreshListener().deleteItem(item);
                }
            }



        }
    }


    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && Settings.FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(this, "'뒤로'버튼 한번 더 누르면시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }


    }



    public void addViewPager(MemoGroup groupItem){


        String title = groupItem.getTitle();
        Long groupId = groupItem.getId();

        Log.e(Settings.LOG_TAG,"addViewPager" + groupId);

        groupList.add(groupItem);


        tabLayout.addTab(tabLayout.newTab().setText( title ));

        // Initializing ViewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        // Creating TabPagerAdapter adapter
        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount()){

            @Override
            public Fragment getItem(int position) {
                // Returning the current tabs

                MemoGroup curGroup = groupList.get(position);
                Bundle bundle = new Bundle(1); // 파라미터는 전달할 데이터 개수
                Log.i(Settings.LOG_TAG, "curGroup "+ curGroup.getType() +"= " + curGroup.getId());
                bundle.putLong("groupId", curGroup.getId() ); // key , value
                switch (curGroup.getType()) {
                    case 1:
                        TextFragment tabFragment1 = new TextFragment();
                        tabFragment1.setArguments(bundle);
                        return tabFragment1;
                    case 2:
                        TodoFragment tabFragment2 = new TodoFragment();
                        tabFragment2.setArguments(bundle);
                        return tabFragment2;
                    /*
                    case 3:
                        TextFragment tabFragment3 = new TextFragment();
                        tabFragment3.setArguments(bundle);
                        return tabFragment3;
                    case 4:
                        TextFragment tabFragment4 = new TextFragment();
                        tabFragment4.setArguments(bundle);
                        return tabFragment4;
                     */
                    default:
                        return null;
                }


            }
        };




        viewPager.setAdapter(pagerAdapter);

        //데이터 가져오기

        tabIndex = prefs.getInt("tabIndex", 0);


        viewPager.setCurrentItem(tabIndex);



        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // Set TabSelectedListener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                //데이터 저장하기
                SharedPreferences.Editor editor = prefs.edit();

                Log.i(Settings.LOG_TAG,"=============tab.getPosition()==" + tab.getPosition());
                editor.putInt("tabIndex", tab.getPosition());
                tabIndex = tab.getPosition();
                editor.commit();


                Log.i(Settings.LOG_TAG,"=============groupId==" + groupList.get(tabIndex).getId());

                viewPager.setCurrentItem(tab.getPosition());

                toggleFab();
/*
                int type = groupList.get(tabIndex).getType();

                //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                switch (type){
                    case 1 :    //텍스트타입
                        fab.show();
                        break;
                    case 2 :    //체크리스트타입
                        fab.hide();
                        break;
                    default :
                        fab.hide();
                        break;

                }
                */
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tabIndex = tab.getPosition();
                int type = groupList.get(tabIndex).getType();
                System.out.println("=============onTabReselected==" + type);
            }




        });
    }

    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    public interface FragmentRefreshListener{
        void onRefresh(Long groupId);
        void addItem(NoteItem item);
        void deleteItem(NoteItem item);
    }

    public void toggleFab(){
        int type = groupList.get(tabIndex).getType();
        switch (type){
            case 1 :    //텍스트타입
                fab.show();
                break;
            case 2 :    //체크리스트타입
                fab.hide();
                break;
            default :
                fab.hide();
                break;

        }
    }





    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result=""; // 요청 결과를 저장할 변수.
  //          RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
    //        result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
           // tv_outPut.setText(s);
        }
    }
}
