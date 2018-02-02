package walidsodki.com.charrado;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

public class CharradoActivity extends AppCompatActivity {

    //creation of a bunch of variables that all are going to be used for the game
    private String[] words;
    int correct, passed, highscore;
    TextView charadeText, timerText, highscoreText;
    Button correctBtn, passBtn, playBtn;

    //MediaPlayer that will be used when the game is over
    MediaPlayer song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charrado);
        charadeText = findViewById(R.id.wordText);
        timerText = findViewById(R.id.secondsText);
        highscoreText = findViewById(R.id.highscoreText);
        words = getResources().getStringArray(R.array.words);
        correctBtn = findViewById(R.id.correctButton);
        passBtn = findViewById(R.id.passButton);
        playBtn = findViewById(R.id.playButton);

        //create the mediaplayer and attach the song.mp3 to it
        song = MediaPlayer.create(this, R.raw.song);

        //the game is not started so the correct/pass buttons are disabled
        disableButtons();

        //load the high score using SharedPreferences and then display it in the app
        loadInfo();
        highscoreText.setText("High score: " + highscore);

    }

    //creates a countdowntimer with 60000 ms(one minute) that updates the timer text every second
    //when the timer is up onFinish() is called that in turn calls other functions
    public void startGame(View v) {
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerText.setText("Time left: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timerText.setText("Time's up!");
                endGame();
            }
        }.start();

        enableButtons();
        nextWord();

        //resets the current score for each game, but not the high score
        correct = passed = 0;
    }

    public void endGame() {
        disableButtons();
        charadeText.setText("Correct guesses: " + correct + " / " + (correct + passed));

        //play a short song to make it known to everyone, especially the guessing player, that the game is over
        //the short soundfile is 100% royalty free and legal to use in educational as well as commercial products
        song.start();
        //call the Vibrator to make the phone vibrate for 1 second further indicating that the game is over
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(1000);

        if (correct > highscore) {
            highscore = correct;
            saveInfo();
            highscoreText.setText("High score: " + highscore);
            Toast.makeText(this, "New high score!", Toast.LENGTH_SHORT).show();
        }
    }

    //simply add 1 to either correct or passed, depending on the button
    //call the nextWord function to get a new word
    public void correctButton (View v) {
        nextWord();
        correct += 1;
    }

    public void passButton (View v) {
        nextWord();
        passed += 1;
    }


    //this function is called whenever the user guesses right or passes, this simply shows a new word with the help of the Random() function
    public void nextWord () {
        int randomIndex = new Random().nextInt(words.length);
        String randomName = words[randomIndex];
        charadeText.setText(randomName);
    }

    //disables the two game buttons and sets their alpha to 50% to further indicate that the game is over
    public void disableButtons() {
        correctBtn.setEnabled(false);
        correctBtn.setAlpha(0.5f);
        passBtn.setEnabled(false);
        passBtn.setAlpha(0.5f);

        //game is over but a new game has to be able to be started
        playBtn.setEnabled(true);
        playBtn.setAlpha(1f);
    }

    //enables the two game buttons and sets their alpha to 100%
    public void enableButtons() {
        correctBtn.setEnabled(true);
        correctBtn.setAlpha(1f);
        passBtn.setEnabled(true);
        passBtn.setAlpha(1f);

        //game is already running, no need for the play button
        playBtn.setEnabled(false);
        playBtn.setAlpha(0.5f);
    }


    //saving the high score with SharedPreferences and always displaying it gives the game a competetive edge
    private void saveInfo() {
        //mode_private makes the information private to this application, no need to make it public as it will only be used here
        SharedPreferences sharedPref = getSharedPreferences("theNumber", Context.MODE_PRIVATE);
        //creates the editor
        SharedPreferences.Editor editor = sharedPref.edit();
        //adds the numberKey Key with the assigned value highscore
        editor.putInt("numberKey", highscore);
        //apply() is used to actually commit the preferences, without this the save doesn't really take place (this can be seen a bit like .show() for Toast, you can create a Toast without actually displaying it.
        editor.apply();
    }

    private void loadInfo() {
        SharedPreferences sharedPref = getSharedPreferences("theNumber", Context.MODE_PRIVATE);
        //getInt("what it returns", "fail-safe, this is what is gotten if nothing is stored in the key")
        int savedHighscore = sharedPref.getInt("numberKey", 1);
        highscore = savedHighscore;
    }
}
