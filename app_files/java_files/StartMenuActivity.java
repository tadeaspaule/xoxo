package com.example.tadeas.xoxo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StartMenuActivity extends AppCompatActivity {

    RelativeLayout startMenuBackground;
    ImageView toggleDarkImageView;
    TextView toggleDarkTextView;
    TextView challengeXOXOTextView;
    TextView moveAdvisorTextView;
    TextView whatIsXOXOTextView;
    TextView contactInfo;
    TextView contactEmail;

    boolean isdark = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startmenu_layout);

        startMenuBackground = (RelativeLayout) findViewById(R.id.startmenu_background);
        toggleDarkImageView = (ImageView) findViewById(R.id.toggle_dark_imageview);
        toggleDarkTextView = (TextView) findViewById(R.id.toggle_dark_tv);
        challengeXOXOTextView = (TextView) findViewById(R.id.challenge_xoxo_tv);
        moveAdvisorTextView = (TextView) findViewById(R.id.move_advisor_tv);
        whatIsXOXOTextView = (TextView) findViewById(R.id.what_is_xoxo_tv);
        contactInfo = (TextView) findViewById(R.id.contact_info_tv);
        contactEmail = (TextView) findViewById(R.id.contact_info_email);

        SharedPreferences sharedPrefs = getSharedPreferences("settings",MODE_PRIVATE);
        if (sharedPrefs.getBoolean("seenwelcome",false)){
            findViewById(R.id.welcomescreen).setVisibility(View.GONE);
        }
        isdark = !sharedPrefs.getBoolean("isdark",false);
        toggleDark(null);

        try{
            getSupportActionBar().hide();
        }
        catch (Exception e){}
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPrefs = getSharedPreferences("settings",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean("isdark",isdark);
        editor.commit();
    }

    public void toggleDark(View view){
        if (isdark){
            isdark = false;
            startMenuBackground.setBackgroundColor(Color.WHITE);
            toggleDarkImageView.setImageResource(R.drawable.bright_theme);
            toggleDarkTextView.setTextColor(getResources().getColor(R.color.textColorLIGHTTHEME));
            challengeXOXOTextView.setTextColor(getResources().getColor(R.color.textColorLIGHTTHEME));
            challengeXOXOTextView.setBackgroundResource(R.drawable.rounded_rect_bg);
            moveAdvisorTextView.setTextColor(getResources().getColor(R.color.textColorLIGHTTHEME));
            moveAdvisorTextView.setBackgroundResource(R.drawable.rounded_rect_bg);
            whatIsXOXOTextView.setTextColor(getResources().getColor(R.color.textColorLIGHTTHEME));
            whatIsXOXOTextView.setBackgroundResource(R.drawable.rounded_rect_bg);
            contactInfo.setTextColor(getResources().getColor(R.color.textColorLIGHTTHEME));
            contactEmail.setTextColor(getResources().getColor(R.color.textColorLIGHTTHEME));

        }
        else{
            isdark = true;
            startMenuBackground.setBackgroundColor(Color.BLACK);
            toggleDarkImageView.setImageResource(R.drawable.dark_theme);
            toggleDarkTextView.setTextColor(getResources().getColor(R.color.textColorDARKTHEME));
            challengeXOXOTextView.setTextColor(getResources().getColor(R.color.textColorDARKTHEME));
            challengeXOXOTextView.setBackgroundResource(R.drawable.rounded_rect_bg_dark);
            moveAdvisorTextView.setTextColor(getResources().getColor(R.color.textColorDARKTHEME));
            moveAdvisorTextView.setBackgroundResource(R.drawable.rounded_rect_bg_dark);
            whatIsXOXOTextView.setTextColor(getResources().getColor(R.color.textColorDARKTHEME));
            whatIsXOXOTextView.setBackgroundResource(R.drawable.rounded_rect_bg_dark);
            contactInfo.setTextColor(getResources().getColor(R.color.textColorDARKTHEME));
            contactEmail.setTextColor(getResources().getColor(R.color.textColorDARKTHEME));
        }
    }

    public void challengeXOXO(View view){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("isdark",isdark);
        startActivity(intent);
    }

    public void moveAdvisor(View view){
        Intent intent = new Intent(this, MoveAdvisorActivity.class);
        intent.putExtra("isdark",isdark);
        startActivity(intent);
    }

    public void whatIsXOXO(View view){
        Intent intent = new Intent(this, WhatIsXoxoActivity.class);
        intent.putExtra("isdark",isdark);
        startActivity(intent);
    }

    public void dismissWelcome(View view){
        findViewById(R.id.welcomescreen).setVisibility(View.GONE);
        SharedPreferences sharedPrefs = getSharedPreferences("settings",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean("seenwelcome",true);
        editor.commit();
    }


}
