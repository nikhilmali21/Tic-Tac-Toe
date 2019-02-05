package nikhil.tictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.valdesekamdem.library.mdtoast.MDToast;


public class HomeActivity extends Activity implements View.OnClickListener{

    private Button[][] buttons = new Button[3][3];

    private boolean player1Turn =true;

    private int roundCount;
    private int roundno=1;

    private int player1Points;
    private int player2Points;
    private int drawpoints;

    private TextView textViewPlayer1;
    private TextView textViewPlayer2;
    private TextView textViewdraw;
    private TextView textViewPlayer1p;
    private TextView textViewPlayer2p;
    private TextView textViewdp;
    private TextView textViewturn;
    private TextView textviewround;

    Button buttonReset;

    private AdView mAdView;
    private InterstitialAd mRoundWinInterstitial;
    private InterstitialAd mMatchWinInterstitial;
    private InterstitialAd mExitInterstitial;

    private SoundPool soundPool;
    private int xsound,osound,allsound,winsound,championsound,drawroundsound,drawmatchsound;

    private MDToast warnToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        warnToast = MDToast.makeText(HomeActivity.this,"Reset The Game",MDToast.LENGTH_LONG,MDToast.TYPE_WARNING);

        //Initialization

        textViewPlayer1 = findViewById(R.id.text_view_p1);
        textViewPlayer2 = findViewById(R.id.text_view_p2);
        textViewdraw=findViewById(R.id.text_view_draw);
        textViewPlayer1p=findViewById(R.id.text_view_p1p);
        textViewPlayer2p=findViewById(R.id.text_view_p2p);
        textViewdp=findViewById(R.id.text_view_dp);
        textViewturn=findViewById(R.id.text_view_turn);
        textviewround=findViewById(R.id.text_view_round);

        buttonReset = findViewById(R.id.button_reset);

        //Banner Ads

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //Interstitial Ads

        mRoundWinInterstitial=new InterstitialAd(this);
        mRoundWinInterstitial.setAdUnitId("ca-app-pub-9169779934207622/5666986778");
        AdRequest request=new AdRequest.Builder().build();
        mRoundWinInterstitial.loadAd(request);

        mMatchWinInterstitial=new InterstitialAd(this);
        mMatchWinInterstitial.setAdUnitId("ca-app-pub-9169779934207622/5666986778");
        AdRequest matchrequest=new AdRequest.Builder().build();
        mMatchWinInterstitial.loadAd(matchrequest);

        mExitInterstitial=new InterstitialAd(this);
        mExitInterstitial.setAdUnitId("ca-app-pub-9169779934207622/6405353372");
        AdRequest exitrequest=new AdRequest.Builder().build();
        mExitInterstitial.loadAd(exitrequest);

        //soundpool Intialization

       if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
           AudioAttributes audioAttributes=new AudioAttributes.Builder()
                   .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                   .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();

           soundPool=new SoundPool.Builder()
                   .setMaxStreams(3).setAudioAttributes(audioAttributes).build();

       }else {
           soundPool=new SoundPool(3, AudioManager.STREAM_MUSIC,0);
       }

       xsound=soundPool.load(this,R.raw.xbuttonsound,1);
       osound=soundPool.load(this,R.raw.obuttonsound,1);
       allsound=soundPool.load(this,R.raw.allbuttonsound,1);
       winsound=soundPool.load(this,R.raw.roundwinsound,1);
       championsound=soundPool.load(this,R.raw.championsound,1);
       drawroundsound=soundPool.load(this,R.raw.drawroundsound,1);
       drawmatchsound=soundPool.load(this,R.raw.drawmatchsound,1);


       // X or O Button Initialization

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);

            }
        }

        // Reset Game Button click Listner

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
                soundPool.play(allsound,1,1,0,0,1);
            }
        });
    }
    // Quit click logic

    @Override
    public void onBackPressed() {
        if (mExitInterstitial.isLoaded()){
            mExitInterstitial.show();
        }
        final AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("Are you sure you want to Quit ?");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton("Yes",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    // X or O Button click logic

    @Override
    public void onClick(View v) {
        if (!((Button) v).getText().toString().equals("")) {
            return;
        }

        if (player1Turn) {
            soundPool.play(xsound,1,1,0,0,1);
            ((Button) v).setText("X");
            textViewturn.setText("Turn O-player");
            textViewturn.setTextColor(Color.parseColor("#cf0707"));
            ((Button) v).setTextColor(Color.parseColor("#0c108d"));

        } else {
            soundPool.play(osound,1,1,0,0,1);
            ((Button) v).setText("O");
            textViewturn.setText("Turn X-player");
            textViewturn.setTextColor(Color.parseColor("#0c108d"));
            ((Button) v).setTextColor(Color.parseColor("#cf0707"));

        }

        roundCount++;

        // check for win

        if (checkForWin()) {
            if (player1Turn) {
                player1Wins();
            } else {
                player2Wins();
            }
        } else if (roundCount == 9) {
            draw();
        } else {
            player1Turn = !player1Turn;
        }

    }

    private boolean checkForWin() {
        String[][] field = new String[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1])
                    && field[i][0].equals(field[i][2])
                    && !field[i][0].equals("")) {
                return true;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals(field[1][i])
                    && field[0][i].equals(field[2][i])
                    && !field[0][i].equals("")) {
                return true;
            }
        }

        if (field[0][0].equals(field[1][1])
                && field[0][0].equals(field[2][2])
                && !field[0][0].equals("")) {
            return true;
        }

        if (field[0][2].equals(field[1][1])
                && field[0][2].equals(field[2][0])
                && !field[0][2].equals("")) {
            return true;
        }

        return false;
    }

    // player 1 wins logic

    private void player1Wins() {
        disableBoard();
        player1Points++;
        roundno++;
        soundPool.play(winsound,1,1,0,0,1);
        playerxRoundToast();
        updatePointsText();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resetBoard();
                enableBoard();
            }
        }, 2000);

        if (mRoundWinInterstitial.isLoaded()){
            mRoundWinInterstitial.show();
        }

    }

    // player 2 wins logic

    private void player2Wins() {
        disableBoard();
        player2Points++;
        roundno++;
        soundPool.play(winsound,1,1,0,0,1);
        playeroRoundToast();
        updatePointsText();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resetBoard();
                enableBoard();
            }
        }, 2000);

        if (mRoundWinInterstitial.isLoaded()){
            mRoundWinInterstitial.show();
        }
    }

    // draw match logic

    private void draw() {
        disableBoard();
        drawpoints++;
        roundno++;
        updatePointsText();
        soundPool.play(drawroundsound,1,1,0,0,1);
        drawRoundToast();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resetBoard();
                enableBoard();

            }
        }, 2000);

        if (mRoundWinInterstitial.isLoaded()){
            mRoundWinInterstitial.show();
        }
    }

    // point table update

    private void updatePointsText() {
        textViewPlayer1.setText("X Wins");
        textViewPlayer2.setText("O Wins");
        textViewdraw.setText("Draw");
        textViewPlayer1p.setText(""+player1Points);
        textViewPlayer2p.setText(""+player2Points);
        textViewdp.setText(""+drawpoints);
        textviewround.setText("Round:"+roundno);
        if (roundno>10){
            textviewround.setText("Round:Final");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (roundno==12){
                    champion();
                }
            }
        }, 2001);



    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }
        textViewturn.setText("Turn X-player");
        textViewturn.setTextColor(Color.parseColor("#0c108d"));
        roundCount = 0;
        player1Turn = true;
    }

    private void resetGame(){
        player1Points=0;
        player2Points=0;
        drawpoints=0;
        roundno=1;
        textViewturn.setText("Turn X-player");
        updatePointsText();
        resetBoard();

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void disableBoard(){

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(false);
            }
        }

    }

    private void enableBoard(){

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(true);
            }
        }
    }

    public void share(View v){
        soundPool.play(allsound,1,1,0,0,1);
        Intent share =new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT,"Play Amazing TicTacToe game on mobile,Download TicTacToe:= https://play.google.com/store/apps/details?id=nikhil.tictactoe");
        share.setType("text/plain");
        startActivity(share);

    }

    public void rate(View v){
        soundPool.play(allsound,1,1,0,0,1);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                ("https://play.google.com/store/apps/details?id=nikhil.tictactoe")));
    }



    public void champion(){

        if (mMatchWinInterstitial.isLoaded()){
            mMatchWinInterstitial.show();
        }

        if (player1Points>player2Points) {

            disableBoard();
            playerxMatchToast();
            soundPool.play(championsound,1,1,0,0,1);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    disableBoard();
                    warnToast.show();
                }
            }, 3000);



        }else if (player1Points<player2Points){

            disableBoard();
            playeroMatchToast();
            soundPool.play(championsound,1,1,0,0,1);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    disableBoard();
                    warnToast.show();
                }
            }, 3000);

        }else {

            disableBoard();
            drawMatchToast();
            soundPool.play(drawmatchsound,1,1,0,0,1);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    disableBoard();
                    warnToast.show();
                }
            }, 3000);
        }
    }

    private void playerxRoundToast(){

        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.playerx_toast_item,null);
        Toast toast=new Toast(this);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }

    private void playeroRoundToast(){

        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.playero_toast_item,null);
        Toast toast=new Toast(this);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();

    }

    private void drawRoundToast(){

        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.draw_toast_item,null);
        Toast toast=new Toast(this);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();

    }

    private void playerxMatchToast(){

        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.playerx_match_toast_item,null);
        Toast toast=new Toast(this);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER,0,0);
        toast.show();
    }

    private void playeroMatchToast(){

        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.playero_match_toast_item,null);
        Toast toast=new Toast(this);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER,0,0);
        toast.show();
    }

    private void drawMatchToast(){

        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.draw_match_toast_item,null);
        Toast toast=new Toast(this);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.anthem_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.privacypolicy:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                        ("https://sites.google.com/view/tictactoenm")));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

