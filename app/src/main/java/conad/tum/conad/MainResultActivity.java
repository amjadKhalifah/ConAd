package conad.tum.conad;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

public class MainResultActivity extends Activity {

    private List<CardBuilder> mCards;
    private CardScrollView mCardScrollView;
    private ResultCardScrollAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createCards();

        mCardScrollView = new CardScrollView(this);
        mAdapter = new ResultCardScrollAdapter();
        mCardScrollView.setAdapter(mAdapter);
        mCardScrollView.activate();
        setContentView(mCardScrollView);
    }

    private void createCards() {
        float finalScore = Results.getInstance().getFinalScore(getResources().getInteger(R.integer.standard_average_response_time), getResources().getInteger(R.integer.response_time_weight)
        , getResources().getInteger(R.integer.memory_weight), getResources().getInteger(R.integer.awareness_weight));
        String level = Results.getInstance().getLevel(finalScore);
        Log.d("ConAd"," score "+ finalScore +" level: "+ level);

        mCards = new ArrayList<CardBuilder>();

        mCards.add(new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Thank you for using ConAd.\n" +
                        "Your final score: "+String.format("%.02f", finalScore)+" %.\n"+
                        "Your concentration level: "+level+".")
                .setFootnote("Swipe for details"));
        float responseTime = Results.getInstance().getResponseTime();
        float responseScore = Results.getInstance().getResponseTimeScore();
        float memoryScore = Results.getInstance().getMemoryScore();
        float awarnessScore = Results.getInstance().getAwarenessScore();

        mCards.add(new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Response time : "+String.format("%.02f", responseScore) + "% (" + responseTime +" ms)\n" +
                         "Short memory : "+String.format("%.02f", memoryScore)+"%\n"+
                         "Self awareness : "+String.format("%.02f", awarnessScore)+"%")
                .setFootnote("Swipe for details"));

        mCards.add(new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Recommendations based on your level:\n"+
                        getRecommendation(finalScore)  )
                .setFootnote("Swipe down to exit!"));

        Results.getInstance().clearResults();
    }

    private class ResultCardScrollAdapter extends CardScrollAdapter {

        @Override
        public int getPosition(Object item) {
            return mCards.indexOf(item);
        }

        @Override
        public int getCount() {
            return mCards.size();
        }

        @Override
        public Object getItem(int position) {
            return mCards.get(position);
        }

        @Override
        public int getViewTypeCount() {
            return CardBuilder.getViewTypeCount();
        }

        @Override
        public int getItemViewType(int position){
            return mCards.get(position).getItemViewType();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return mCards.get(position).getView(convertView, parent);
        }
    }

    public String getRecommendation(float score){

        if (score>= 90){
            return getResources().getString(R.string.level_A_recommendation) ;
        }
        if (score>= 80){
            return getResources().getString(R.string.level_B_recommendation) ;
        }
        if (score>= 70){
            return getResources().getString(R.string.level_C_recommendation) ;
        }
        if (score>= 60){
            return getResources().getString(R.string.level_D_recommendation) ;
        }
        else{
            return getResources().getString(R.string.level_E_recommendation) ;
        }

    }
}