package conad.tum.conad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import javax.xml.transform.Result;


public class ResponseResultActivity extends Activity {


    /** Listener that displays the options menu when the touchpad is tapped. */
    private final GestureDetector.BaseListener mBaseListener = new GestureDetector.BaseListener() {
        @Override
        public boolean onGesture(Gesture gesture) {
            if (gesture == Gesture.TAP) {
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
        setContentView(R.layout.activity_response_result);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener);
        String result = formatResponseResult();
        TextView resultTextView = (TextView)findViewById(R.id.resultTxt);
        resultTextView.setText(result);
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
            startActivity(new Intent(this, TestStartupActivity.class));
            finish();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.new_test) {
            startActivity(new Intent(this, MemoryTestStartupActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String formatResponseResult(){
        String result ="";
        long lastResult = Results.getInstance().getLastResponseResult();
        if (lastResult ==-1){
            result = "You didn't respond in time.\n";
        }else{
            result = "Response time "+ lastResult+" ms.\n";
        }
        float average = Results.getInstance().getAverageResponseResult();
        result += "Your overall average is "+ average+" ms";

        startActivity(new Intent(this, MemoryTestStartupActivity.class));
        finish();

        return result;
    }


}
