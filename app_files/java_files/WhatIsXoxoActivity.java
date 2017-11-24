package com.example.tadeas.xoxo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class WhatIsXoxoActivity extends AppCompatActivity {

    RelativeLayout background;

    ViewFlipper whatisFlipper;

    TextView title;
    TextView text;
    TextView previous;
    TextView next;

    int currentPosition = 0;

    TextView table1;
    TextView table2;
    TextView table3;
    TextView table4;

    String[] titlesArray = {"What is XOXO", "Using reinforcement learning", "Layers of learning", "Source code"};

    String[] textsArray = new String[titlesArray.length];

    private void setupTexts() {
        textsArray[0] = "    XOXO is a machine learning algorithm that learned to be unbeatable in Tic Tac Toe.\n    " +
                "I made it because the field interests me, and I considered Tic Tac Toe, a very simple game in " +
                "which one can play in a way that makes loss impossible, a good proving ground to test my " +
                "understanding of the basic concepts.\n    It was written in Python with no external packages except " +
                "for random. The final move-value pairs were written into a text file this app uses to make its moves.";

        textsArray[1] = "    I made XOXO after reading about the basic concepts of reinforcement learning, and it " +
                "utilises some of them, namely iteration as a way of learning, and rewards.\n    How XOXO learns, " +
                "basically, is it records all the moves it makes during a game, and then after the game it adds a " +
                "part of the resulting reward to each move's value. The earlier in the game the move is, the smaller " +
                "part of the reward it gets, because a move early on that leads to a loss may not always lead to a " +
                "loss, but with later moves the correlation is much more certain.\n    Since losing gives a negative " +
                "reward, this adding mechanism also lowers the value of losing moves.";

        textsArray[2] = "    To 'master' Tic Tac Toe, XOXO goes through three phases:\n\n1. It assigns tentative " +
                "values to moves by projecting two moves into the future (the exact formula, along with a better " +
                "explanation, can be found in the source code)\n\n2. It plays against itself, but 49% of the time it " +
                "picks a random move. This is done so that it explores all possible moves and estimates their value" +
                ".\n\n3. It plays with 100% accuracy against completely random moves. This is done to correct a few " +
                "remaining incorrect values produced by the first and second layer.\n\n    The second and third layer " +
                "require only a couple thousand iterations, where the value database updates after every game. " +
                "Overall it takes only a few seconds to produce an unbeatable set of move-value pairs.";

        textsArray[3] = "    If you are interested in more than just this little overview, XOXO's source code with " +
                "explanatory comments can be found and freely used at github.com/tadeaspaule/...";
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whatis_layout);

        setupTexts();

        background = (RelativeLayout) findViewById(R.id.whatis_background);

        whatisFlipper = (ViewFlipper) findViewById(R.id.whatis_flipper);

        table1 = (TextView) findViewById(R.id.table1);
        table1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPosition = 0;
                updateTitleAndText();
                checkButtons();
                whatisFlipper.showNext();
            }
        });
        table2 = (TextView) findViewById(R.id.table2);
        table2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPosition = 1;
                updateTitleAndText();
                checkButtons();
                whatisFlipper.showNext();
            }
        });
        table3 = (TextView) findViewById(R.id.table3);
        table3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPosition = 2;
                updateTitleAndText();
                checkButtons();
                whatisFlipper.showNext();
            }
        });
        table4 = (TextView) findViewById(R.id.table4);
        table4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPosition = 3;
                updateTitleAndText();
                checkButtons();
                whatisFlipper.showNext();
            }
        });


        title = (TextView) findViewById(R.id.whatis_title);
        text = (TextView) findViewById(R.id.whatis_text);

        previous = (TextView) findViewById(R.id.whatis_previous);
        next = (TextView) findViewById(R.id.whatis_next);

        if (getIntent().getBooleanExtra("isdark",false)){
            background.setBackgroundColor(Color.BLACK);
            text.setTextColor(getResources().getColor(R.color.textColorDARKTHEME));
            title.setBackgroundColor(Color.rgb(15,15,15));
        }



        try{
            getSupportActionBar().hide();
        }
        catch (Exception e){}
    }

    public void previousText(View view){
        currentPosition -= 1;
        updateTitleAndText();
        checkButtons();
    }

    public void nextText(View view){
        currentPosition += 1;
        updateTitleAndText();
        checkButtons();
    }

    private void updateTitleAndText(){
        title.setText(titlesArray[currentPosition]);
        text.setText(textsArray[currentPosition]);
    }

    private void checkButtons(){
        if (currentPosition > 0){
            previous.setEnabled(true);
            previous.setVisibility(View.VISIBLE);
        }
        else{
            previous.setEnabled(false);
            previous.setVisibility(View.INVISIBLE);
        }
        if (currentPosition + 1 == titlesArray.length){
            next.setEnabled(false);
            next.setVisibility(View.INVISIBLE);
        }
        else{
            next.setEnabled(true);
            next.setVisibility(View.VISIBLE);
        }
    }

}
