package com.soo.nememo.common;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.soo.nememo.MainActivity;
import com.soo.nememo.R;
import com.soo.nememo.db.DBLoader;
import com.soo.nememo.item.MemoGroup;

public class Dialog {

    private Context context;
    private DBLoader dbLoader;

    public Dialog(Context context) {

        this.context = context;
        this.dbLoader = new DBLoader(context);
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void addDialog() {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final android.app.Dialog dlg = new android.app.Dialog(context);



        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.add_dialog);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        //final EditText message = (EditText) dlg.findViewById(R.id.mesgase);
        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);

        final RadioGroup rg = (RadioGroup) dlg.findViewById(R.id.memoGroup);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addViewPager(Settings.TYPE_TEXT);
                //pagerAdapter.notifyDataSetChanged();

                MainActivity activity = (MainActivity)context;


                TextView titleView = (TextView) dlg.findViewById(R.id.memoGroupTitle);

                /*
                if (txtView.getVisibility() == View.VISIBLE)
                    txtView.setVisibility(View.INVISIBLE);
                else
                    txtView.(View.VISIBLE);
*/

                if (rg.getVisibility() == View.VISIBLE){
                    ((TextView)dlg.findViewById(R.id.title)).setText("노트제목입력");
                    rg.setVisibility(View.GONE);
                    titleView.setVisibility(View.VISIBLE);


                    //제목으로 포커스
                    titleView.requestFocus();

                    //키보드 보이게 하는 부분
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput((TextView)dlg.findViewById(R.id.title), 0);
                    //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


                }
                else{
                    int type = 1;

                    int id = rg.getCheckedRadioButtonId();

                    switch (id){
                        case R.id.type1 :{
                            type = Settings.TYPE_TEXT;
                            break;
                        }
                        case R.id.type2 :{
                            type = Settings.TYPE_TODO;
                            break;
                        }
                        case R.id.type3 :{
                            type = Settings.TYPE_SITE;
                            break;
                        }
                        case R.id.type4 :{
                            type = Settings.TYPE_ACCOUNT;
                            break;
                        }



                    }


                    MemoGroup memoGroup = dbLoader.insertGroup(titleView.getText().toString(), type);
                    activity.addViewPager(memoGroup);
                    dlg.dismiss();
                }



/*
                dbLoader.insertGroup("타이틀!!", type);

                activity.addViewPager(type);


                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
                */
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
    }
}
