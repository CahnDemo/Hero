package com.yujie.hero.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yujie.hero.HeroApplication;
import com.yujie.hero.I;
import com.yujie.hero.R;
import com.yujie.hero.bean.Result;
import com.yujie.hero.bean.UserBean;
import com.yujie.hero.bean.WordContentBean;
import com.yujie.hero.db.DataHelper;
import com.yujie.hero.utils.MCountDownTimer;
import com.yujie.hero.utils.OkHttpUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GameActivity extends AppCompatActivity {
    public static final String TAG = GameActivity.class.getSimpleName();
    private Context mContext;
    @Bind(R.id.timer)
    TextView timer;
    @Bind(R.id.point)
    TextView point;
    @Bind(R.id.currentSpeed)
    TextView currentSpeed;
    @Bind(R.id.sporter)
    TextView sporter;
    @Bind(R.id.course)
    TextView course;
    @Bind(R.id.word_content)
    TextView wordContent;
    @Bind(R.id.edit_content)
    EditText editContent;
    private MyCountTimer mc;
    boolean isBegan = false;
    Dialog dialog;
    String contentTxt;
    String course_simple_name ;
    String time;
    int keyCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        mContext = this;
        initTitle();
        initEditListener();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * listen the editTextview's change,if you input first,the game will start,
     * then if your word that inputed is same to textview,the order will append
     * to edittext,else delete and brief error.(the char's position must be same
     * to textview's word's position)
     */
    private void initEditListener() {
        editContent.addTextChangedListener(new TextWatcher() {
            /**
             * when keypress,game start
             * @param s
             * @param start
             * @param count
             * @param after
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(!isBegan){
                    mc.start();
                    isBegan = true;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            /**
             * if the content's length > 0,and the same position char is identical,
             * allow the char append to edittext,else delete and brief error to user interface
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    int pos = s.length() - 1;
                    char c = s.charAt(pos);
                    if (c!=contentTxt.charAt(pos)) {
                        s.delete(pos,pos+1);
                        Toast.makeText(GameActivity.this,"input error!",Toast.LENGTH_SHORT).show();
                    }
                    keyCount++;
                    if (s.length()==contentTxt.length()){
                        initWordContent(course_simple_name);
                        editContent.setText("");
                    }
                }
            }
        });
    }

    /**
     * get the words from local database and format to a string
     * @param course_simple_name
     */
    private void initWordContent(String course_simple_name) {
        ArrayList<WordContentBean> words = new DataHelper(this).getWords(course_simple_name);
        Log.e(TAG, "initWordContent: "+words.size() );
        StringBuilder sb = new StringBuilder();
        for (WordContentBean bean:words){
            sb.append(bean.getWord()+" ");
        }
        contentTxt = sb.substring(0,sb.length()-1);
        wordContent.setText(contentTxt);
    }

    /**
     * init the title,and init the timer,the wordContent TextView will get the words from local database,
     * then add it to interface
     */
    private void initTitle() {
        String action_code = getIntent().getStringExtra("action_code");
        if (action_code!=null){
            course_simple_name = action_code.substring(0,1);
            time = action_code.substring(1,action_code.length());
        }
        Log.e(TAG, "initTitle: "+time );
        mc = new MyCountTimer((Long.parseLong(time)) * 60 * 1000,1000);
        timer.setText((Integer.parseInt(time)*60)+"");
        point.setText(HeroApplication.getInstance().getCurrentUser().getTop_grade()+"");
        sporter.setText(HeroApplication.getInstance().getCurrentUser().getUser_name());
        course.setText(HeroApplication.getInstance().getCurrentTestCourse());
        initWordContent(course_simple_name);
    }


    /**
     * 倒计时计时器
     */
    class MyCountTimer extends MCountDownTimer {
        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * when the timer tick,reckon the speed and show it on textview
         * @param millisUntilFinished
         */
        @Override
        public void onTick(long millisUntilFinished) {
            /**
             * here is a important problem,when the activity is finished,but the timer not,
             * it is always run,so when you go into the gameview again,the app will cash,
             * so I add a lock,if current activity is finished,the timer is also killed;
             */
            if (GameActivity.this.isFinishing()){
                mc.cancel();
            }else {
                long l = millisUntilFinished / 1000;
                timer.setText(""+l);
                currentSpeed.setText((int)(((float)keyCount/((Integer.parseInt(time)*60)-l))*60)+"");
            }
        }

        /**
         * when the timer is finish,end the contest,and brief the user about the grade,
         * if currentSpeed is better than currentuser's best grade,update the top grade,
         * but no matter whether the result is better, will be upload to server
         */
        @Override
        public void onFinish() {

            isBegan = false;
            editContent.setEnabled(false);
            final String speed = currentSpeed.getText().toString();
            final UserBean currentUser = HeroApplication.getInstance().getCurrentUser();
            Log.e(TAG, "onFinish: "+currentUser.getTop_grade() );
            if (Integer.parseInt(speed)>currentUser.getTop_grade()){
                dialog = new AlertDialog.Builder(mContext)
                        .setTitle("result")
                        .setMessage("your current speed is "+speed+", you win yourself!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editContent.setEnabled(true);
                                editContent.setText("");
                                keyCount = 0;
                                timer.setText(""+Integer.parseInt(time)*60);
                                currentUser.setTop_grade(Integer.parseInt(speed));
                                new DataHelper(mContext).updateUser(currentUser,1);
                                uploadGrade(currentUser,speed);
                                finish();
                            }
                        })
                        .create();
                dialog.setCancelable(false);
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_SEARCH)
                        {
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }
                });
                dialog.show();
            }else {
                dialog = new AlertDialog.Builder(mContext)
                        .setTitle("result")
                        .setMessage("your current speed is "+speed+", you should try again to challenge yourself!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editContent.setEnabled(true);
                                keyCount = 0;
                                timer.setText(""+Integer.parseInt(time)*60);
                                finish();
                            }
                        })
                        .create();
                dialog.setCancelable(false);
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_SEARCH)
                        {
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }
                });
                dialog.show();
            }
            addGradeToServer(currentUser,speed);
        }


    }

    /**
     * upload the exercise grade to server
     * @param currentUser
     * @param speed
     */
    private void addGradeToServer(UserBean currentUser, String speed) {
        Date nowTime = new Date(System.currentTimeMillis());
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = sdFormatter.format(nowTime);
        OkHttpUtils<Result> utils = new OkHttpUtils<>();
        utils.url(HeroApplication.SERVER_ROOT)
                .addParam(I.REQUEST,I.Request.REQUEST_ADD_EXERCISE_GRADE)
                .addParam(I.Exercise.GRADE,speed)
                .addParam(I.Exercise.EXE_TIME,nowDate)
                .addParam(I.Exercise.COURSE_ID,course_simple_name)
                .addParam(I.Exercise.USER_NAME,currentUser.getUser_name())
                .addParam(I.Exercise.B_CLASS,currentUser.getB_class()+"")
                .addParam(I.Exercise.START_TIME,currentUser.getUid().substring(1,6))
                .post()
                .targetClass(Result.class)
                .execute(new OkHttpUtils.OnCompleteListener<Result>() {
                    @Override
                    public void onSuccess(Result result) {
                        if (result!=null & result.isFlag()){
                            Toast.makeText(GameActivity.this,"the grade is uploaded...",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                            Toast.makeText(GameActivity.this,"upload faild...please try again later...",Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * upload the best grade to server
     * @param currentUser
     * @param speed
     */
    private void uploadGrade(UserBean currentUser, String speed) {
        OkHttpUtils<Result> utils = new OkHttpUtils();
        utils.url(HeroApplication.SERVER_ROOT)
                .addParam(I.REQUEST,I.Request.REQUEST_UPDATE_BEST_GRADE)
                .addParam(I.User.UID,currentUser.getUid())
                .addParam(I.User.TOP_GRADE,speed+"")
                .post()
                .targetClass(Result.class)
                .execute(new OkHttpUtils.OnCompleteListener<Result>() {
                    @Override
                    public void onSuccess(Result result) {
                        if (result!=null & result.isFlag()){
                            Toast.makeText(GameActivity.this,"you top_grade have updated!",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(GameActivity.this,"No Internet connected....pleast try again later",Toast.LENGTH_LONG).show();
                    }
                });
    }

}
