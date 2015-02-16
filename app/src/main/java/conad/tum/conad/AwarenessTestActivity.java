package conad.tum.conad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.polysfactory.headgesturedetector.HeadGestureDetector;
import com.polysfactory.headgesturedetector.OnHeadGestureListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class AwarenessTestActivity extends Activity implements OnHeadGestureListener {
    private HeadGestureDetector mHeadGestureDetector;
    private List<String> commands = new ArrayList(Arrays.asList("nod", "left", "right"));
    private TextView commandsTxt, optionsTxt;
    private int index = 0;
    private String lastCommand;
    private boolean finished = false;
    /** Audio manager used to play system sound effects. */
    private AudioManager mAudioManager;

    /** Gesture detector used to present the options menu. */
    private GestureDetector mGestureDetector;
    // count of correct answers
    private int result=0;

    /** Listener that displays the options menu when the touchpad is tapped. */
    private final GestureDetector.BaseListener mBaseListener = new GestureDetector.BaseListener() {
        @Override
        public boolean onGesture(Gesture gesture) {
            if (gesture == Gesture.TAP && finished) {
                mAudioManager.playSoundEffect(Sounds.TAP);
                openOptionsMenu();
                return true;
            } else {
                return false;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awareness_test);
        commandsTxt = (TextView) findViewById(R.id.commandTxt);
        optionsTxt = (TextView) findViewById(R.id.tip_tap_for_options);
        mHeadGestureDetector = new HeadGestureDetector(this);
        mHeadGestureDetector.setOnHeadGestureListener(this);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener);
        Collections.shuffle(commands);
        displayNextCommand();
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector.onMotionEvent(event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_awarnes_result, menu);

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
            startActivity(new Intent(this, TestStartupActivity.class));
            finish();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.show_result) {
            startActivity(new Intent(this, MainResultActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNod() {
        Log.d("ConAd", "nod");
        // stop detector
        mHeadGestureDetector.stop();
        handleResult(lastCommand.equals("nod"),"nod");
    }



    @Override
    public void onShakeToLeft() {
        Log.d("ConAd", "left");
        // stop detector
        mHeadGestureDetector.stop();
        handleResult(lastCommand.equals("left"),"left");
    }

    @Override
    public void onShakeToRight() {
        Log.d("ConAd", "right");
        // stop detector
        mHeadGestureDetector.stop();
        handleResult(lastCommand.equals("right"),"right");
    }
    private void handleResult(boolean correct, String command) {

        String text ="";
        if (correct){
            text = "Cool ";
            result++;
//            Log.d("ConAd", "result "+result);
        }else{
            text ="mmm Let us try something else.";
        }
        if (index!=-1){
            text+= getString(R.string.awareness_test_instruction);
        }

        displayNextCommand();
        commandsTxt.setText(text);

        if(finished){
            startActivity(new Intent(this, MainResultActivity.class));
            finish();
        }


    }

    private void showResult() {
       String result ="";
        //result = "Your score in this test is "+ String.format("%.02f", ((this.result/3.00)*100)) +"%.";
        optionsTxt.setVisibility(View.VISIBLE);
        commandsTxt.setText(result);

    }

    private void displayNextCommand() {
        commandsTxt.postDelayed(new Runnable() {
            public void run() {
                if (index != -1) {
                    mHeadGestureDetector.start();
                    lastCommand = commands.get(index);
                    if (lastCommand.equals("nod")) {
                        commandsTxt.setText("Nod");
                    } else {
                        commandsTxt.setText("Turn " + lastCommand );
                    }
                    shiftIndex();
                } else {
                    finished = true;
                    Results.getInstance().addAwarnessResult(result);
//                    Log.d("ConAd", "awarness result"+ result);
//                   showResult();

                }

            }
        }, 4000);
        // 4 sec so that user can get back to her position

    }




    private void shiftIndex() {
        if (index + 1 != commands.size()) {
            index++;
        } else
            index = -1;
    }
}
