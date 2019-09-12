package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class LoadingScreenActivity extends AppCompatActivity {

    private LinearLayout bar1;
    private LinearLayout bar2;
    private LinearLayout bar3;
    private LinearLayout bar4;
    private LinearLayout bar5;
    private LinearLayout bar6;

    private Animation bar1_in_anim;
    private Animation bar2_in_anim;
    private Animation bar3_in_anim;
    private Animation bar4_in_anim;
    private Animation bar5_in_anim;
    private Animation bar6_in_anim;

    private Animation bar1_out_anim;
    private Animation bar2_out_anim;
    private Animation bar3_out_anim;
    private Animation bar4_out_anim;
    private Animation bar5_out_anim;
    private Animation bar6_out_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        loadBars();
        setAnimation();
        animate();
    }

    void changeActivity(){
        Intent intent = new Intent(LoadingScreenActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    void loadBars(){
        bar1 = (LinearLayout) findViewById(R.id.bar1);
        bar2 = (LinearLayout) findViewById(R.id.bar2);
        bar3 = (LinearLayout) findViewById(R.id.bar3);
        bar4 = (LinearLayout) findViewById(R.id.bar4);
        bar5 = (LinearLayout) findViewById(R.id.bar5);
        bar6 = (LinearLayout) findViewById(R.id.bar6);
    }

    void setAnimation(){
        bar1_in_anim = AnimationUtils.loadAnimation(this, R.anim.bar1_in_anim);
        bar2_in_anim = AnimationUtils.loadAnimation(this, R.anim.bar2_in_anim);
        bar3_in_anim = AnimationUtils.loadAnimation(this, R.anim.bar3_in_anim);
        bar4_in_anim = AnimationUtils.loadAnimation(this, R.anim.bar4_in_anim);
        bar5_in_anim = AnimationUtils.loadAnimation(this, R.anim.bar5_in_anim);
        bar6_in_anim = AnimationUtils.loadAnimation(this, R.anim.bar6_in_anim);

        bar1_out_anim = AnimationUtils.loadAnimation(this, R.anim.bar1_out_anim);
        bar2_out_anim = AnimationUtils.loadAnimation(this, R.anim.bar2_out_anim);
        bar3_out_anim = AnimationUtils.loadAnimation(this, R.anim.bar3_out_anim);
        bar4_out_anim = AnimationUtils.loadAnimation(this, R.anim.bar4_out_anim);
        bar5_out_anim = AnimationUtils.loadAnimation(this, R.anim.bar5_out_anim);
        bar6_out_anim = AnimationUtils.loadAnimation(this, R.anim.bar6_out_anim);
    }

    public void animate(){
        bar1.startAnimation(bar1_in_anim);
        bar2.startAnimation(bar2_in_anim);
        bar3.startAnimation(bar3_in_anim);
        bar4.startAnimation(bar4_in_anim);
        bar5.startAnimation(bar5_in_anim);
        bar6.startAnimation(bar6_in_anim);


        new CountDownTimer(1600, 200) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {



                new CountDownTimer(100, 20){

                    int i;
                    @Override
                    public void onTick(long l) {
                        if(i%2==0) {
                            bar1.setVisibility(View.INVISIBLE);
                            bar2.setVisibility(View.INVISIBLE);
                            bar3.setVisibility(View.INVISIBLE);
                            bar4.setVisibility(View.INVISIBLE);
                            bar5.setVisibility(View.INVISIBLE);
                            bar6.setVisibility(View.INVISIBLE);
                        }
                        else{
                            bar1.setVisibility(View.VISIBLE);
                            bar2.setVisibility(View.VISIBLE);
                            bar3.setVisibility(View.VISIBLE);
                            bar4.setVisibility(View.VISIBLE);
                            bar5.setVisibility(View.VISIBLE);
                            bar6.setVisibility(View.VISIBLE);
                        }
                        i++;
                    }

                    @Override
                    public void onFinish() {
                        bar1.setVisibility(View.VISIBLE);
                        bar2.setVisibility(View.VISIBLE);
                        bar3.setVisibility(View.VISIBLE);
                        bar4.setVisibility(View.VISIBLE);
                        bar5.setVisibility(View.VISIBLE);
                        bar6.setVisibility(View.VISIBLE);

                        bar1.startAnimation(bar1_out_anim);
                        bar2.startAnimation(bar2_out_anim);
                        bar3.startAnimation(bar3_out_anim);
                        bar4.startAnimation(bar4_out_anim);
                        bar5.startAnimation(bar5_out_anim);
                        bar6.startAnimation(bar6_out_anim);


                        bar1.setVisibility(View.INVISIBLE);
                        bar2.setVisibility(View.INVISIBLE);
                        bar3.setVisibility(View.INVISIBLE);
                        bar4.setVisibility(View.INVISIBLE);
                        bar5.setVisibility(View.INVISIBLE);
                        bar6.setVisibility(View.INVISIBLE);
                    }
                }.start();

                new CountDownTimer(1600, 200){

                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        if(bar1_out_anim.hasEnded())
                            changeActivity();
                    }
                }.start();
            }
        }.start();
    }
}
