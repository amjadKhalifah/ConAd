package conad.tum.conad;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class ColorResponseTimeTestActivity extends Activity {

    public  List<Integer> colorsCodes = new ArrayList(Arrays.asList(Color.WHITE, Color.BLUE, Color.RED, Color.GREEN));
    int index = 0;
    long colorStartTime = -1;


    // colors sent from other activities
    private String pickedColor;
    private int pickedColorInt;
    private RelativeLayout colorPad;
    private boolean answered = false;

    /**
     * Listener that displays the options menu when the touchpad is tapped.
     */
    private final GestureDetector.BaseListener mBaseListener = new GestureDetector.BaseListener() {
        @Override
        public boolean onGesture(Gesture gesture) {
            Long gestureTime = System.currentTimeMillis();
            if(!answered) {// prevent double answers
                long responseTime;
                if (colorStartTime != -1) { // correct color was displayed, and this event is after it
                    responseTime = gestureTime - colorStartTime;
                } else { // wrong answer
                    responseTime = -1;
                }
                answered =true;
                registerResult(responseTime);
            }
            return true;

        }
    };
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_response_time_test);
        mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener);
        Bundle b = getIntent().getExtras();
        colorPad = (RelativeLayout) findViewById(R.id.color_layout);
        pickedColor = "" + b.get("pickedColor");
        pickedColorInt = getColor(pickedColor);
        Collections.shuffle(colorsCodes);
        displayColors();
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector.onMotionEvent(event);
    }

    private int getColor(String pickedColor) {

        if (pickedColor.equals("blue")) {
            return Color.BLUE;
        } else if (pickedColor.equals("red")) {
            return Color.RED;
        } else if (pickedColor.equals("green")) {
            return Color.GREEN;
        } else {
            return Color.WHITE;
        }
    }

    private void displayColors() {
        this.colorPad.postDelayed(new Runnable() {
            public void run() {
                if (index != -1) {
                    int colorCode = colorsCodes.get(index);
                    colorPad.setBackgroundColor(colorCode);
                    if (colorCode == pickedColorInt) {// starting to display the correct color
                        colorStartTime = System.currentTimeMillis();
                        Log.d("ConAd", "displaying color");
                    }
//                currentDisplayedColor = colorCode;

                    // for next color display
                    shiftIndex();
                    colorPad.postDelayed(this, getResources().getInteger(R.integer.response_color_flip));
                } else {
//                    registerResult(-1);
                }

            }
        }, getResources().getInteger(R.integer.response_color_flip));

    }


    private void shiftIndex() {
        if (index + 1 != colorsCodes.size()) {
            index++;
        } else
            index = -1;
    }

    private void registerResult(long result) {
        if (result!= -1){
            result = result - getResources().getInteger(R.integer.estimated_display_lag)- getResources().getInteger(R.integer.estimated_input_lag);
        }

        Log.d("ConAd", "registering result  " + result);
        Results.getInstance().addResponseTime(result);
        finish();
        startActivity(new Intent(this, ResponseResultActivity.class));

    }
}
