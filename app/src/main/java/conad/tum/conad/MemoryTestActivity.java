package conad.tum.conad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class MemoryTestActivity extends Activity {
    private static final int SPEECH_REQUEST = 0;
    private List<String> arrows = new ArrayList(Arrays.asList("UP","DOWN","LEFT","RIGHT"));
    private ImageView arrowImg;
    private TextView resultTxt, optionsTxt;
    private int index =0;
    boolean answered = false;


    /** Listener that displays the options menu when the touchpad is tapped. */
    private final GestureDetector.BaseListener mBaseListener = new GestureDetector.BaseListener() {
        @Override
        public boolean onGesture(Gesture gesture) {
            if (gesture == Gesture.TAP && answered) {
                mAudioManager.playSoundEffect(Sounds.TAP);
                openOptionsMenu();
                return true;
            } else {
                return false;
            }
        }
    };

    /** Audio manager used to play system sound effects. */
    private AudioManager mAudioManager;

    /** Gesture detector used to present the options menu. */
    private GestureDetector mGestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_test);
        arrowImg = (ImageView) findViewById(R.id.memoryArrow);
        resultTxt = (TextView) findViewById(R.id.resultTxt);
        optionsTxt = (TextView)findViewById(R.id.tip_tap_for_options);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener);
        Collections.shuffle(arrows);
        Log.d("ConAd", "the picked sequence is "+ arrows);
        displaySequence();

    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector.onMotionEvent(event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_response_result, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.repeat_test) {
            startActivity(new Intent(this, MemoryTestStartupActivity.class));
            finish();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.new_test) {
            startActivity(new Intent(this, AwarenessTestStartupActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void displaySequence() {
        arrowImg.postDelayed(new Runnable() {
            public void run() {
                if (index != -1) {
                    String direction = arrows.get(index);
                    arrowImg.setImageResource(getDrawable(direction));
                    shiftIndex();
                    arrowImg.postDelayed(this, getResources().getInteger(R.integer.memory_arrow_flip));
                } else {
                    // last sequence was displayed
                    displaySpeechRecognizer();
                    arrowImg.setImageDrawable(null);
                }

            }
        }, getResources().getInteger(R.integer.memory_arrow_flip));

    }


    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        startActivityForResult(intent, SPEECH_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            handleResult(spokenText);
            Log.d("ConAd", spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleResult(String spokenText) {
        String result ="";
        //TODO add some validation here and maybe offer user another try
        String[] spokenTokens = spokenText.split(" ");
        boolean correctAnswer = true;
        for (int i=0; i<spokenTokens.length;i++){
            if (!compare(spokenTokens[i], arrows.get(i))){
                correctAnswer = false;
                break;
            }

        }
        if (correctAnswer){
            result ="Correct sequence.";
        }else
        {
            result = "Wrong sequence.\n" +
                    "Sequence: "+arrows+".\n"+
                    "Answer  : "+"["+spokenText.toUpperCase()+"]";
        }
        Results.getInstance().addMemoryResult(correctAnswer);
        answered = true;
        optionsTxt.setVisibility(View.VISIBLE);
        resultTxt.setVisibility(View.VISIBLE);
        resultTxt.setText(result);


    }

    private boolean compare(String spokenToken, String originalToken) {
        if (originalToken.equalsIgnoreCase(spokenToken))
        {
            return true;
        }
        // check last 2 letters
        else if (originalToken.endsWith(spokenToken.substring(spokenToken.length()-2))){
            return true;
        }
        //TODO we can add more heuristics here
        return  false;
    }


    private void shiftIndex() {
        if (index + 1 != arrows.size()) {
            index++;
        } else
            index = -1;
    }
    private int getDrawable(String direction){

        if(direction.equals("UP")){
            return R.drawable.ic_arrow_up_50;
        } else  if(direction.equals("DOWN")){
            return R.drawable.ic_arrow_down_50;
        }else  if(direction.equals("LEFT")){
            return R.drawable.ic_arrow_left_50;
        }else  {
            return R.drawable.ic_arrow_right_50;
        }


    }





}
