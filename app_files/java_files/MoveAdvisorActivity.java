package com.example.tadeas.xoxo;

import android.content.Context;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MoveAdvisorActivity extends AppCompatActivity {

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


    private int isValid(String state){ // 3 for full board, 4 too much O, 5 too much X, 1 x won, 2 o won, 6 both won
        int stateInt = Integer.parseInt(state);
        if (!state.contains("1")){
            return 3;
        }
        int countX = state.length() - state.replace("3","").length();
        int countO = state.length() - state.replace("2","").length();
        if (countX - countO < 0){
            return 4;
        }
        if (countX - countO > 1){
            return 5;
        }
        if (isWin(stateInt) & isWin(flipState(stateInt))){
            return 6;
        }
        if (isWin(stateInt)){
            return 1;
        }
        if (isWin(flipState(stateInt))){
            return 2;
        }
        return 0;
    }

    TextView moveAdviceTV;

    boolean xSelected = true;
    HashMap<Integer,Double> values;
    ImageView[] tiles_ma = new ImageView[9];
    int gamestate = 111111111;
    String gamestateString = "111111111";

    LinearLayout firstrowMA;
    LinearLayout secondrowMA;
    LinearLayout thirdrowMA;

    RelativeLayout moveAdvisorBackground;
    ImageView xMA;
    int xPicked;
    int xGrey = R.drawable.x_grey;
    ImageView oMA;
    int oPicked;
    int oGrey = R.drawable.o_grey;
    int emptyResource;

    TextView yourSymbolIs;

    LinearLayout errorBox;
    TextView errorTitle;
    TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moveadvisor_layout);

        moveAdvisorBackground = (RelativeLayout) findViewById(R.id.moveadvisor_background);
        moveAdvisorBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.help_box).setVisibility(View.GONE);
            }
        });
        yourSymbolIs = (TextView) findViewById(R.id.your_symbol_is_TV);
        yourSymbolIs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.help_box).setVisibility(View.GONE);
            }
        });
        moveAdviceTV = (TextView) findViewById(R.id.move_advice_tv);
        firstrowMA = (LinearLayout) findViewById(R.id.first_row_ma);
        secondrowMA = (LinearLayout) findViewById(R.id.second_row_ma);
        thirdrowMA = (LinearLayout) findViewById(R.id.third_row_ma);
        tiles_ma[0] = (ImageView) findViewById(R.id.tile0_ma);
        tiles_ma[1] = (ImageView) findViewById(R.id.tile1_ma);
        tiles_ma[2] = (ImageView) findViewById(R.id.tile2_ma);
        tiles_ma[3] = (ImageView) findViewById(R.id.tile3_ma);
        tiles_ma[4] = (ImageView) findViewById(R.id.tile4_ma);
        tiles_ma[5] = (ImageView) findViewById(R.id.tile5_ma);
        tiles_ma[6] = (ImageView) findViewById(R.id.tile6_ma);
        tiles_ma[7] = (ImageView) findViewById(R.id.tile7_ma);
        tiles_ma[8] = (ImageView) findViewById(R.id.tile8_ma);
        xMA = (ImageView) findViewById(R.id.x_ma);
        oMA = (ImageView) findViewById(R.id.o_ma);
        errorBox = (LinearLayout) findViewById(R.id.error_message_box);
        errorTitle = (TextView) findViewById(R.id.error_title);
        errorMessage = (TextView) findViewById(R.id.error_message_tv);

        if (getIntent().getBooleanExtra("isdark",false)){
            errorTitle.setTextColor(getResources().getColor(R.color.textColorDARKTHEME));
            errorMessage.setTextColor(getResources().getColor(R.color.textColorDARKTHEME));
            errorBox.setBackground(getResources().getDrawable(R.drawable.rounded_rect_bg_dark));
            moveAdvisorBackground.setBackgroundColor(Color.BLACK);
            moveAdviceTV.setTextColor(getResources().getColor(R.color.textColorDARKTHEME));
            yourSymbolIs.setTextColor(getResources().getColor(R.color.textColorDARKTHEME));
            findViewById(R.id.help_box).setBackground(getResources().getDrawable(R.drawable.rounded_rect_bg_dark));
            TextView goldExplained = (TextView)findViewById(R.id.goldexplained);
            goldExplained.setTextColor(Color.WHITE);
            TextView silverExplained = (TextView)findViewById(R.id.silverexplained);
            silverExplained.setTextColor(Color.WHITE);
            TextView bronzeExplained = (TextView)findViewById(R.id.bronzeexplained);
            bronzeExplained.setTextColor(Color.WHITE);
            TextView clearBoardButton = (TextView)findViewById(R.id.clearboardbutton);
            clearBoardButton.setTextColor(Color.WHITE);
            clearBoardButton.setBackground(getResources().getDrawable(R.drawable.rounded_rect_bg_dark));
            for (ImageView tile : tiles_ma){
                tile.setBackgroundColor(Color.BLACK);
            }
            xPicked = R.drawable.x_white;
            oPicked = R.drawable.o_white;
            emptyResource = R.drawable.empty_black;
        }
        else {
            for (ImageView tile : tiles_ma){
                tile.setBackgroundColor(Color.WHITE);
            }
            xPicked = R.drawable.x_black;
            oPicked = R.drawable.o_black;
            emptyResource = R.drawable.empty_white;
        }

        xMA.setImageResource(xPicked);
        oMA.setImageResource(oGrey);

        values = loadValues(this);

        try{
            getSupportActionBar().hide();
        }
        catch (Exception e){}
    }

    private void updateBoard(){
        for (int c = 0; c < 9; c++){

            if (gamestateString.charAt(c) == '2'){
                tiles_ma[c].setImageResource(oPicked);
            }
            else if (gamestateString.charAt(c) == '3'){
                tiles_ma[c].setImageResource(xPicked);
            }
            else{
                tiles_ma[c].setImageResource(emptyResource);
            }
        }

    }

    public void clicked0_ma (View view){
        RelativeLayout helpbox = (RelativeLayout)findViewById(R.id.help_box);
        if (helpbox.getVisibility() == View.VISIBLE){
            helpbox.setVisibility(View.GONE);
        }
        else if (findViewById(R.id.error_message_box).getVisibility()==View.VISIBLE){
            findViewById(R.id.error_message_box).setVisibility(View.GONE);
        }
        else {
            if (xSelected) {
                if (gamestateString.charAt(0) == '3') {
                    gamestateString = "1" + gamestateString.substring(1);
                } else {
                    gamestateString = "3" + gamestateString.substring(1);
                }
            } else {
                if (gamestateString.charAt(0) == '2') {
                    gamestateString = "1" + gamestateString.substring(1);
                } else {
                    gamestateString = "2" + gamestateString.substring(1);
                }
            }
            gamestate = Integer.parseInt(gamestateString);
            updateBoard();
        }
    }

    public void clicked1_ma (View view){
        RelativeLayout helpbox = (RelativeLayout)findViewById(R.id.help_box);
        if (helpbox.getVisibility() == View.VISIBLE){
            helpbox.setVisibility(View.GONE);
        }
        else if (findViewById(R.id.error_message_box).getVisibility()==View.VISIBLE){
            findViewById(R.id.error_message_box).setVisibility(View.GONE);
        }
        else {
            if (xSelected) {
                if (gamestateString.charAt(1) == '3') {
                    gamestateString = gamestateString.substring(0, 1) + "1" + gamestateString.substring(2);
                } else {
                    gamestateString = gamestateString.substring(0, 1) + "3" + gamestateString.substring(2);
                }
            } else {
                if (gamestateString.charAt(1) == '2') {
                    gamestateString = gamestateString.substring(0, 1) + "1" + gamestateString.substring(2);
                } else {
                    gamestateString = gamestateString.substring(0, 1) + "2" + gamestateString.substring(2);
                }
            }
            gamestate = Integer.parseInt(gamestateString);
            updateBoard();
        }
    }

    public void clicked2_ma (View view){
        RelativeLayout helpbox = (RelativeLayout)findViewById(R.id.help_box);
        if (helpbox.getVisibility() == View.VISIBLE){
            helpbox.setVisibility(View.GONE);
        }
        else if (findViewById(R.id.error_message_box).getVisibility()==View.VISIBLE){
            findViewById(R.id.error_message_box).setVisibility(View.GONE);
        }
        else {
            if (xSelected) {
                if (gamestateString.charAt(2) == '3') {
                    gamestateString = gamestateString.substring(0, 2) + "1" + gamestateString.substring(3);
                } else {
                    gamestateString = gamestateString.substring(0, 2) + "3" + gamestateString.substring(3);
                }
            } else {
                if (gamestateString.charAt(2) == '2') {
                    gamestateString = gamestateString.substring(0, 2) + "1" + gamestateString.substring(3);
                } else {
                    gamestateString = gamestateString.substring(0, 2) + "2" + gamestateString.substring(3);
                }
            }
            gamestate = Integer.parseInt(gamestateString);
            updateBoard();
        }
    }

    public void clicked3_ma (View view){
        RelativeLayout helpbox = (RelativeLayout)findViewById(R.id.help_box);
        if (helpbox.getVisibility() == View.VISIBLE){
            helpbox.setVisibility(View.GONE);
        }
        else if (findViewById(R.id.error_message_box).getVisibility()==View.VISIBLE){
            findViewById(R.id.error_message_box).setVisibility(View.GONE);
        }
        else {
            if (xSelected) {
                if (gamestateString.charAt(3) == '3') {
                    gamestateString = gamestateString.substring(0, 3) + "1" + gamestateString.substring(4);
                } else {
                    gamestateString = gamestateString.substring(0, 3) + "3" + gamestateString.substring(4);
                }
            } else {
                if (gamestateString.charAt(3) == '2') {
                    gamestateString = gamestateString.substring(0, 3) + "1" + gamestateString.substring(4);
                } else {
                    gamestateString = gamestateString.substring(0, 3) + "2" + gamestateString.substring(4);
                }
            }
            gamestate = Integer.parseInt(gamestateString);
            updateBoard();
        }
    }

    public void clicked4_ma (View view){
        RelativeLayout helpbox = (RelativeLayout)findViewById(R.id.help_box);
        if (helpbox.getVisibility() == View.VISIBLE){
            helpbox.setVisibility(View.GONE);
        }
        else if (findViewById(R.id.error_message_box).getVisibility()==View.VISIBLE){
            findViewById(R.id.error_message_box).setVisibility(View.GONE);
        }
        else {
            if (xSelected) {
                if (gamestateString.charAt(4) == '3') {
                    gamestateString = gamestateString.substring(0, 4) + "1" + gamestateString.substring(5);
                } else {
                    gamestateString = gamestateString.substring(0, 4) + "3" + gamestateString.substring(5);
                }
            } else {
                if (gamestateString.charAt(4) == '2') {
                    gamestateString = gamestateString.substring(0, 4) + "1" + gamestateString.substring(5);
                } else {
                    gamestateString = gamestateString.substring(0, 4) + "2" + gamestateString.substring(5);
                }
            }
            gamestate = Integer.parseInt(gamestateString);
            updateBoard();
        }
    }

    public void clicked5_ma (View view){
        RelativeLayout helpbox = (RelativeLayout)findViewById(R.id.help_box);
        if (helpbox.getVisibility() == View.VISIBLE){
            helpbox.setVisibility(View.GONE);
        }
        else if (findViewById(R.id.error_message_box).getVisibility()==View.VISIBLE){
            findViewById(R.id.error_message_box).setVisibility(View.GONE);
        }
        else {
            if (xSelected) {
                if (gamestateString.charAt(5) == '3') {
                    gamestateString = gamestateString.substring(0, 5) + "1" + gamestateString.substring(6);
                } else {
                    gamestateString = gamestateString.substring(0, 5) + "3" + gamestateString.substring(6);
                }
            } else {
                if (gamestateString.charAt(5) == '2') {
                    gamestateString = gamestateString.substring(0, 5) + "1" + gamestateString.substring(6);
                } else {
                    gamestateString = gamestateString.substring(0, 5) + "2" + gamestateString.substring(6);
                }
            }
            gamestate = Integer.parseInt(gamestateString);
            updateBoard();
        }
    }

    public void clicked6_ma (View view){
        RelativeLayout helpbox = (RelativeLayout)findViewById(R.id.help_box);
        if (helpbox.getVisibility() == View.VISIBLE){
            helpbox.setVisibility(View.GONE);
        }
        else if (findViewById(R.id.error_message_box).getVisibility()==View.VISIBLE){
            findViewById(R.id.error_message_box).setVisibility(View.GONE);
        }
        else {
            if (xSelected) {
                if (gamestateString.charAt(6) == '3') {
                    gamestateString = gamestateString.substring(0, 6) + "1" + gamestateString.substring(7);
                } else {
                    gamestateString = gamestateString.substring(0, 6) + "3" + gamestateString.substring(7);
                }
            } else {
                if (gamestateString.charAt(6) == '2') {
                    gamestateString = gamestateString.substring(0, 6) + "1" + gamestateString.substring(7);
                } else {
                    gamestateString = gamestateString.substring(0, 6) + "2" + gamestateString.substring(7);
                }
            }
            gamestate = Integer.parseInt(gamestateString);
            updateBoard();
        }
    }

    public void clicked7_ma (View view){
        RelativeLayout helpbox = (RelativeLayout)findViewById(R.id.help_box);
        if (helpbox.getVisibility() == View.VISIBLE){
            helpbox.setVisibility(View.GONE);
        }
        else if (findViewById(R.id.error_message_box).getVisibility()==View.VISIBLE){
            findViewById(R.id.error_message_box).setVisibility(View.GONE);
        }
        else {
            if (xSelected) {
                if (gamestateString.charAt(7) == '3') {
                    gamestateString = gamestateString.substring(0, 7) + "1" + gamestateString.substring(8);
                } else {
                    gamestateString = gamestateString.substring(0, 7) + "3" + gamestateString.substring(8);
                }
            } else {
                if (gamestateString.charAt(7) == '2') {
                    gamestateString = gamestateString.substring(0, 7) + "1" + gamestateString.substring(8);
                } else {
                    gamestateString = gamestateString.substring(0, 7) + "2" + gamestateString.substring(8);
                }
            }
            gamestate = Integer.parseInt(gamestateString);
            updateBoard();
        }
    }

    public void clicked8_ma (View view){
        RelativeLayout helpbox = (RelativeLayout)findViewById(R.id.help_box);
        if (helpbox.getVisibility() == View.VISIBLE){
            helpbox.setVisibility(View.GONE);
        }
        else if (findViewById(R.id.error_message_box).getVisibility()==View.VISIBLE){
            findViewById(R.id.error_message_box).setVisibility(View.GONE);
        }
        else {
            if (xSelected) {
                if (gamestateString.charAt(8) == '3') {
                    gamestateString = gamestateString.substring(0, 8) + "1";
                } else {
                    gamestateString = gamestateString.substring(0, 8) + "3";
                }
            } else {
                if (gamestateString.charAt(8) == '2') {
                    gamestateString = gamestateString.substring(0, 8) + "1";
                } else {
                    gamestateString = gamestateString.substring(0, 8) + "2";
                }
            }
            gamestate = Integer.parseInt(gamestateString);
            updateBoard();
        }
    }

    public void selectX(View view){
        xSelected = true;
        xMA.setImageResource(xPicked);
        oMA.setImageResource(oGrey);
    }

    public void selectO(View view){
        xSelected = false;
        xMA.setImageResource(xGrey);
        oMA.setImageResource(oPicked);
    }

    public void getAdvice(View view){
        RelativeLayout helpbox = (RelativeLayout)findViewById(R.id.help_box);
        if (helpbox.getVisibility() == View.VISIBLE){
            helpbox.setVisibility(View.GONE);
        }
        else if (findViewById(R.id.error_message_box).getVisibility()==View.VISIBLE){
            findViewById(R.id.error_message_box).setVisibility(View.GONE);
        }
        else {
            int currentStatus = isValid(gamestateString);
            if (currentStatus == 0) {
                HashMap<Integer, Double> options = getMyPossibleMoves(flipState(gamestate));
                Double[] optionsValues = getHashmapValues(options);
                Integer[] optionsMoves = getHashmapKeys(options);
                ArrayList<Integer> goldMoves = new ArrayList<>();
                ArrayList<Integer> silverMoves = new ArrayList<>();
                ArrayList<Integer> bronzeMoves = new ArrayList<>();

                double goldValue = optionsValues[0];
                for (Double value : optionsValues) {
                    if (value > goldValue) {
                        goldValue = value;
                    }
                }
                for (int move : optionsMoves) {
                    if (options.get(move) == goldValue) {
                        goldMoves.add(move);
                    }
                }

                double silverValue = 0;
                for (Double value : optionsValues) {
                    if (value > silverValue & value < goldValue) {
                        silverValue = value;
                    }
                }
                for (int move : optionsMoves) {
                    if (options.get(move) == silverValue) {
                        silverMoves.add(move);
                    }
                }

                double bronzeValue = 0;
                for (Double value : optionsValues) {
                    if (value > bronzeValue & value < silverValue) {
                        bronzeValue = value;
                    }
                }
                for (int move : optionsMoves) {
                    if (options.get(move) == bronzeValue) {
                        bronzeMoves.add(move);
                    }
                }

                int counter = 0;
                for (int move : goldMoves) {
                    if (counter < 3) {
                        String moveString = String.valueOf(move);
                        for (int c = 0; c < 9; c++) {
                            if (moveString.charAt(c) != String.valueOf(flipState(gamestate)).charAt(c)) {
                                tiles_ma[c].setImageResource(R.drawable.o_gold);
                            }
                        }
                    }
                    counter++;

                }
                if (counter < 3) {
                    for (int move : silverMoves) {
                        if (counter < 3) {
                            String moveString = String.valueOf(move);
                            for (int c = 0; c < 9; c++) {
                                if (moveString.charAt(c) != String.valueOf(flipState(gamestate)).charAt(c)) {
                                    tiles_ma[c].setImageResource(R.drawable.o_silver);
                                }
                            }
                        }
                        counter++;

                    }
                }
                if (counter < 3) {
                    for (int move : bronzeMoves) {
                        if (counter < 3) {
                            String moveString = String.valueOf(move);
                            for (int c = 0; c < 9; c++) {
                                if (moveString.charAt(c) != String.valueOf(flipState(gamestate)).charAt(c)) {
                                    tiles_ma[c].setImageResource(R.drawable.o_bronze);
                                }
                            }
                        }
                        counter++;

                    }
                }

            } else {
                errorBox.setVisibility(View.VISIBLE);
                if (currentStatus == 1) {
                    errorMessage.setText("X already won this board.");
                }
                if (currentStatus == 2) {
                    errorMessage.setText("O already won this board.");
                }
                if (currentStatus == 3) {
                    errorMessage.setText("The board is already full.");
                }
                if (currentStatus == 4) {
                    errorMessage.setText("Too many O symbols on the board.");
                }
                if (currentStatus == 5) {
                    errorMessage.setText("Too many X symbols on the board.");
                }
                if (currentStatus == 6) {
                    errorMessage.setText("X and O both won this board, which is impossible.");
                }
                errorMessage.append("\n\n(click anywhere to dismiss)");
            }
        }
    }

    public void showHelp(View view){
        if (findViewById(R.id.error_message_box).getVisibility()==View.VISIBLE){
            findViewById(R.id.error_message_box).setVisibility(View.GONE);
        }
        else {
            RelativeLayout helpbox = (RelativeLayout) findViewById(R.id.help_box);
            if (helpbox.getVisibility() == View.GONE) {
                helpbox.setVisibility(View.VISIBLE);
            } else {
                helpbox.setVisibility(View.GONE);
            }
        }
    }

    public void clearBoard(View view){
        RelativeLayout helpbox = (RelativeLayout)findViewById(R.id.help_box);
        if (helpbox.getVisibility() == View.VISIBLE){
            helpbox.setVisibility(View.GONE);
        }
        else if (findViewById(R.id.error_message_box).getVisibility()==View.VISIBLE){
            findViewById(R.id.error_message_box).setVisibility(View.GONE);
        }
        else {
            gamestateString = "111111111";
            gamestate = 111111111;
            updateBoard();
        }
    }
}
