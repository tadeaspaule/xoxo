package com.example.tadeas.xoxo;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class GameActivity extends AppCompatActivity {

    private HashMap<Integer,Double> loadValues(Context context){

        HashMap<Integer,Double> moveValues = new HashMap<>();
        int count = 0;
        String valuesInString;
        int move = 111111111;
        double value = 0.1;
        int[] positionsOfBreaks = new int[5891];

        String data = "";
        StringBuffer stringBuffer = new StringBuffer();
        InputStream inputStream = context.getResources().openRawResource(R.raw.movevalues);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        if (inputStream!=null){

            try {

                while ((data = reader.readLine()) != null){

                    stringBuffer.append(data);

                }

                valuesInString = stringBuffer.toString();

                for (int c = 0; c < valuesInString.length(); c++){
                    if (valuesInString.charAt(c) == '#'){
                        positionsOfBreaks[count] = c;
                        count++;
                    }
                }

                String valueString = "";
                String temp = "";

                for (int hashIndex = 0; hashIndex < positionsOfBreaks.length; hashIndex++){

                    if (hashIndex + 1 < positionsOfBreaks.length){

                        move = Integer.parseInt(valuesInString.substring(positionsOfBreaks[hashIndex]+1,
                                positionsOfBreaks[hashIndex] + 10));
                        value = Double.parseDouble(valuesInString.substring(positionsOfBreaks[hashIndex]+11,
                                positionsOfBreaks[hashIndex+1]));
                        moveValues.put(move,value);

                    }

                }

                inputStream.close();


            }
            catch (Exception e){

            }

        }

        return moveValues;

    }

    private Integer[] getHashmapKeys(HashMap<Integer,Double> hashmap){
        Set<Integer> temp = hashmap.keySet();
        return temp.toArray(new Integer[temp.size()]);
    }

    private Double[] getHashmapValues(HashMap<Integer,Double> hashmap){
        Set<Integer> temp = hashmap.keySet();
        return hashmap.values().toArray(new Double[temp.size()]);
    }


    private HashMap<Integer,Double> getMyPossibleMoves(int state){
        ArrayList<Integer> emptyTiles = new ArrayList<Integer>();
        char[] stateTiles = String.valueOf(state).toCharArray();
        String stateString = String.valueOf(state);
        for (int c = 0; c < stateTiles.length; c++){
            if (stateTiles[c]=='1'){
                emptyTiles.add(c);
            }
        }
        HashMap<Integer,Double> options = new HashMap<Integer,Double>();
        for (int spaceIndex = 0; spaceIndex < emptyTiles.size(); spaceIndex++){
            int freeSpace = emptyTiles.get(spaceIndex);
            int availableState = Integer.parseInt(stateString.substring(0,freeSpace) + "3" + stateString.substring(freeSpace+1));
            options.put(availableState, values.get(availableState));
        }
        return options;
    }


    private int pickMove(int state){

        HashMap<Integer,Double> possibleMovesHashMap = getMyPossibleMoves(state);

        Integer[] possibleMoves = getHashmapKeys(getMyPossibleMoves(state));
        Double[] possibleMovesValues = getHashmapValues(getMyPossibleMoves(state));
        double maxValue = possibleMovesValues[0];
        for (double value : possibleMovesValues){
            if (value > maxValue){
                maxValue = value;
            }
        }
        ArrayList<Integer> bestChoices = new ArrayList<Integer>();

        for (int move : possibleMoves){
            if (possibleMovesHashMap.get(move) >= maxValue){
                bestChoices.add(move);
            }
            if (isWin(move)){
                return move;
            }
        }

        return bestChoices.get(0);

    }


    private boolean isWin(int state){
        char[] s = String.valueOf(state).toCharArray();
        if (s[0]==s[1] & s[0]==s[2] & s[0]=='3'){return true;}
        if (s[3]==s[4] & s[3]==s[5] & s[3]=='3'){return true;}
        if (s[6]==s[7] & s[6]==s[8] & s[6]=='3'){return true;}
        if (s[0]==s[3] & s[0]==s[6] & s[0]=='3'){return true;}
        if (s[1]==s[4] & s[1]==s[7] & s[1]=='3'){return true;}
        if (s[2]==s[5] & s[2]==s[8] & s[2]=='3'){return true;}
        if (s[0]==s[4] & s[0]==s[8] & s[0]=='3'){return true;}
        if (s[2]==s[4] & s[2]==s[6] & s[2]=='3'){return true;}
        return false;

    }


    private int flipState(int state){
        return Integer.parseInt(String.valueOf(state).replace('2','m').replace('3','2').replace('m','3'));
    }


    private int getStateReward(int state){  // working
        if (isWin(state)){ return 1; } // XOXO won
        if (isWin(flipState(state))) { return 2; }  // player won
        String stateString = String.valueOf(state);
        if (stateString.length() - stateString.replace("1","").length() == 0){
            return 3;  // draw
        }
        return 0;
    }

    private void goDark(){
        chooseSymbolLayout.setBackgroundColor(Color.BLACK);
        chooseSymbolTextView.setTextColor(Color.WHITE);
        chooseX.setImageResource(R.drawable.x_white);
        chooseO.setImageResource(R.drawable.o_white);
        gameBackground.setBackgroundColor(Color.BLACK);
        for (ImageView tile : tiles){
            tile.setBackgroundColor(Color.BLACK);
        }
    }

    ViewFlipper flipper;
    RelativeLayout chooseSymbolLayout;
    TextView chooseSymbolTextView;
    ImageView chooseX;
    ImageView chooseO;
    RelativeLayout gameBackground;
    ImageView[] tiles = new ImageView[9];



    boolean isdark;
    boolean xoxofirst = false;
    boolean againstxoxo;
    char currentSymbol = '2';
    char startSymbol = '2';
    int playerSymbol;
    char playerSymbolChar;
    int opponentSymbol;
    char opponentSymbolChar;
    int emptyTile;
    int gamestate = 111111111;
    String gamestateString = "111111111";
    HashMap<Integer,Double> values;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        values = loadValues(this);

        flipper = (ViewFlipper) findViewById(R.id.game_flipper);
        chooseSymbolLayout = (RelativeLayout) findViewById(R.id.choose_symbol_layout);
        chooseSymbolTextView = (TextView) findViewById(R.id.choose_symbol_textview);
        chooseX = (ImageView) findViewById(R.id.chooseX);
        chooseO = (ImageView) findViewById(R.id.chooseO);
        gameBackground = (RelativeLayout) findViewById(R.id.game_background);
        tiles[0] = (ImageView) findViewById(R.id.tile0);
        tiles[1] = (ImageView) findViewById(R.id.tile1);
        tiles[2] = (ImageView) findViewById(R.id.tile2);
        tiles[3] = (ImageView) findViewById(R.id.tile3);
        tiles[4] = (ImageView) findViewById(R.id.tile4);
        tiles[5] = (ImageView) findViewById(R.id.tile5);
        tiles[6] = (ImageView) findViewById(R.id.tile6);
        tiles[7] = (ImageView) findViewById(R.id.tile7);
        tiles[8] = (ImageView) findViewById(R.id.tile8);

        isdark = getIntent().getBooleanExtra("isdark",false);
        if(isdark){
            goDark();
            emptyTile = R.drawable.empty_black;
            playerSymbol = R.drawable.x_white;
            opponentSymbol = R.drawable.o_white;
            findViewById(R.id.gamemode_choice_background).setBackgroundColor(Color.BLACK);

        }
        else {
            emptyTile = R.drawable.empty_white;
            for (ImageView tile : tiles){
                tile.setBackgroundColor(Color.WHITE);
            }
            playerSymbol = R.drawable.x_black;
            opponentSymbol = R.drawable.o_black;
        }


        updateBoard();

        try{
            getSupportActionBar().hide();
        }
        catch (Exception e){}
    }

    public void humanVShuman(View view){
        againstxoxo = false;
        flipper.showNext();
        flipper.showNext();
    }

    public void humanVSpc(View view){
        againstxoxo = true;
        flipper.showNext();
    }

    public void pickedX(View view){
        if (isdark){
            playerSymbol = R.drawable.x_white;
            opponentSymbol = R.drawable.o_white;
        }
        else {
            playerSymbol = R.drawable.x_black;
            opponentSymbol = R.drawable.o_black;
        }
        flipper.showNext();
    }

    public void pickedO(View view){
        if (isdark){
            playerSymbol = R.drawable.o_white;
            opponentSymbol = R.drawable.x_white;
        }
        else {
            playerSymbol = R.drawable.o_black;
            opponentSymbol = R.drawable.x_black;
        }
        flipper.showNext();
    }

    private void updateBoard(){
        int stateReward = getStateReward(gamestate);
        for (int c = 0; c < 9; c++){

            if (gamestateString.charAt(c) == '2'){
                tiles[c].setImageResource(playerSymbol);
                tiles[c].setEnabled(false);
            }
            else if (gamestateString.charAt(c) == '3'){
                tiles[c].setImageResource(opponentSymbol);
                tiles[c].setEnabled(false);
            }
            else{
                tiles[c].setImageResource(emptyTile);
                tiles[c].setEnabled(true);
            }
        }

        if (stateReward > 0){
            xoxofirst = !xoxofirst;
            gameEnds(stateReward);
        }
    }

    private void resetBoard(){
        gamestate = 111111111;
        gamestateString = "111111111";
        updateBoard();
        if (xoxofirst & againstxoxo){
            waitForMove(200);
        }
        if(!againstxoxo){
            if (startSymbol == '3'){ startSymbol = '2'; }
            else { startSymbol = '3'; }
            currentSymbol = startSymbol;
        }

    }

    private void gameEnds(int result){
        for (ImageView tile: tiles){
            tile.setEnabled(false);
        }
        LinearLayout resultScreen = (LinearLayout) findViewById(R.id.result_screen);
        resultScreen.bringToFront();
        TextView resultText = (TextView) findViewById(R.id.result_textview);
        String[] resultStrings = {"ERROR", "O WON", "X WON", "DRAW"};
        if (againstxoxo){
            resultStrings[1] = "XOXO WON";
            resultStrings[2] = "YOU WON";
        }
        resultText.setText(resultStrings[result]);
        TextView playAnother = (TextView) findViewById(R.id.play_another_textview);
        playAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout secondRow = (LinearLayout) findViewById(R.id.second_row);
                secondRow.bringToFront();
                LinearLayout firstRow = (LinearLayout) findViewById(R.id.first_row);
                firstRow.bringToFront();
                LinearLayout thirdRow = (LinearLayout) findViewById(R.id.third_row);
                thirdRow.bringToFront();
                resetBoard();
            }
        });;
        TextView mainMenu = (TextView) findViewById(R.id.main_menu_textview);
        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    public void waitForMove(int waitMilis){
        for (ImageView tile : tiles){
            tile.setEnabled(false);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                XOXOplays();
            }
        },waitMilis);
    }

    public void XOXOplays(){
        gamestate = pickMove(gamestate);
        gamestateString = String.valueOf(gamestate);
        updateBoard();
    }

    public void clicked0(View view){
        tiles[0].setEnabled(false);
        gamestateString = currentSymbol + gamestateString.substring(1);
        gamestate = Integer.parseInt(gamestateString);
        updateBoard();
        if (againstxoxo) {
            if (getStateReward(gamestate) < 1) {
                waitForMove(500);
            }
        }
        else{
            if (currentSymbol == '3'){ currentSymbol = '2'; }
            else { currentSymbol = '3'; }
        }
    }

    public void clicked1(View view){
        tiles[1].setEnabled(false);
        gamestateString = gamestateString.substring(0,1) + currentSymbol + gamestateString.substring(2);
        gamestate = Integer.parseInt(gamestateString);
        updateBoard();
        if (againstxoxo) {
            if (getStateReward(gamestate) < 1) {
                waitForMove(500);
            }
        }
        else{
            if (currentSymbol == '3'){ currentSymbol = '2'; }
            else { currentSymbol = '3'; }
        }
    }

    public void clicked2(View view){
        tiles[2].setEnabled(false);
        gamestateString = gamestateString.substring(0,2) + currentSymbol + gamestateString.substring(3);
        gamestate = Integer.parseInt(gamestateString);
        updateBoard();
        if (againstxoxo) {
            if (getStateReward(gamestate) < 1) {
                waitForMove(500);
            }
        }
        else{
            if (currentSymbol == '3'){ currentSymbol = '2'; }
            else { currentSymbol = '3'; }
        }
    }

    public void clicked3(View view){
        tiles[3].setEnabled(false);
        gamestateString = gamestateString.substring(0,3) + currentSymbol + gamestateString.substring(4);
        gamestate = Integer.parseInt(gamestateString);
        updateBoard();
        if (againstxoxo) {
            if (getStateReward(gamestate) < 1) {
                waitForMove(500);
            }
        }
        else{
            if (currentSymbol == '3'){ currentSymbol = '2'; }
            else { currentSymbol = '3'; }
        }
    }

    public void clicked4(View view){
        tiles[4].setEnabled(false);
        gamestateString = gamestateString.substring(0,4) + currentSymbol + gamestateString.substring(5);
        gamestate = Integer.parseInt(gamestateString);
        updateBoard();
        if (againstxoxo) {
            if (getStateReward(gamestate) < 1) {
                waitForMove(500);
            }
        }
        else{
            if (currentSymbol == '3'){ currentSymbol = '2'; }
            else { currentSymbol = '3'; }
        }
    }

    public void clicked5(View view){
        tiles[5].setEnabled(false);
        gamestateString = gamestateString.substring(0,5) + currentSymbol + gamestateString.substring(6);
        gamestate = Integer.parseInt(gamestateString);
        updateBoard();
        if (againstxoxo) {
            if (getStateReward(gamestate) < 1) {
                waitForMove(500);
            }
        }
        else{
            if (currentSymbol == '3'){ currentSymbol = '2'; }
            else { currentSymbol = '3'; }
        }
    }

    public void clicked6(View view){
        tiles[6].setEnabled(false);
        gamestateString = gamestateString.substring(0,6) + currentSymbol + gamestateString.substring(7);
        gamestate = Integer.parseInt(gamestateString);
        updateBoard();
        if (againstxoxo) {
            if (getStateReward(gamestate) < 1) {
                waitForMove(500);
            }
        }
        else{
            if (currentSymbol == '3'){ currentSymbol = '2'; }
            else { currentSymbol = '3'; }
        }
    }

    public void clicked7(View view){
        tiles[7].setEnabled(false);
        gamestateString = gamestateString.substring(0,7) + currentSymbol + gamestateString.substring(8);
        gamestate = Integer.parseInt(gamestateString);
        updateBoard();
        if (againstxoxo) {
            if (getStateReward(gamestate) < 1) {
                waitForMove(500);
            }
        }
        else{
            if (currentSymbol == '3'){ currentSymbol = '2'; }
            else { currentSymbol = '3'; }
        }
    }

    public void clicked8(View view){
        tiles[8].setEnabled(false);
        gamestateString = gamestateString.substring(0,8) + currentSymbol;
        gamestate = Integer.parseInt(gamestateString);
        updateBoard();
        if (againstxoxo) {
            if (getStateReward(gamestate) < 1) {
                waitForMove(500);
            }
        }
        else{
            if (currentSymbol == '3'){ currentSymbol = '2'; }
            else { currentSymbol = '3'; }
        }
    }



}
