package com.yujie.hero.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.TextView;

import com.yujie.hero.HeroApplication;
import com.yujie.hero.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GameActivity extends AppCompatActivity {
    public static final String TAG = GameActivity.class.getSimpleName();
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
    TextView editContent;
    private MyCountTimer mc;

    String course_simple_name ;
    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        initTitle();
    }

    private void initTitle() {
        String action_code = getIntent().getStringExtra("action_code");
        if (action_code!=null){
            course_simple_name = action_code.substring(0,1);
            time = action_code.substring(1,2);
        }
        mc = new MyCountTimer(Long.parseLong(time)*60*1000,1000);
        point.setText(HeroApplication.getInstance().getCurrentUser().getTop_grade()+"");
        sporter.setText(HeroApplication.getInstance().getCurrentUser().getUser_name());
        course.setText(HeroApplication.getInstance().getCurrentTestCourse());
    }


    /**
     * 倒计时计时器
     */
    class MyCountTimer extends CountDownTimer{
        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            timer.setText(""+millisUntilFinished/1000);
        }

        @Override
        public void onFinish() {

        }
    }

}
