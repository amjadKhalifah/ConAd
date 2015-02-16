package conad.tum.conad;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amjad on 02-Feb-15.
 */
public class Results {
    private static Results instance;

    public static Results getInstance() {
        if (instance == null) {
            instance = new Results();
        }
        return instance;
    }

    public void clearResults() {

        instance = null;
    }

    private List<Long> responseTimes;
    private List<Boolean> memoryResults;
    private List<Integer> awarnessResults;
    private float responseTimeScore, memoryScore, awarenessScore;

    private Results() {
        this.responseTimes = new ArrayList<>();
        this.memoryResults = new ArrayList<>();
        this.awarnessResults = new ArrayList<>();

    }

    public List<Long> getResponseTimes() {
        return responseTimes;
    }

    public void setResponseTimes(List<Long> responseTimes) {
        this.responseTimes = responseTimes;
    }

    public void addResponseTime(Long responseTime) {
        this.responseTimes.add(responseTime);
    }

    public long getLastResponseResult() {
        return responseTimes.get(responseTimes.size() - 1);
    }

    public float getAverageResponseResult() {
        long sum = 0;
        int count = 0;
        float average;
        for (long result : responseTimes) {
            if (result != -1) {
                count++;
                sum += result;
            }
        }
        if (count > 0)
            average = sum / count;
        else
            average = 0;

        return average;
    }
    public float getMinimumResponseResult() {
        int count = 0;
        float minimum = responseTimes.get(0);
        for (long result : responseTimes) {
            if (result != -1) {
                if(result < minimum){
                  minimum = result;
                }
            }
        }
        return minimum;
    }

    public float getAverageMemoryResult() {
        int correctCount = 0;
        float average;
        for (Boolean result : memoryResults) {
            if (result) {
                correctCount++;
            }
        }
        if (correctCount > 0)
            average = correctCount / memoryResults.size();
        else
            average = 0;

        return average;
    }


    public float getAverageAwarenessResult() {
        float sum = 0;
        float average;
        for (int result : awarnessResults) {
            sum += (result / 3.00);
        }
        average = sum / awarnessResults.size();
        return average;
    }

    public List<Boolean> getMemoryResults() {
        return memoryResults;
    }

    public void setMemoryResults(List<Boolean> memoryResults) {
        this.memoryResults = memoryResults;
    }

    public void addMemoryResult(Boolean memoryResult) {
        this.memoryResults.add(memoryResult);
    }

    public List<Integer> getAwarnessResults() {
        return awarnessResults;
    }

    public void setAwarnessResults(List<Integer> awarnessResults) {
        this.awarnessResults = awarnessResults;
    }

    public void addAwarnessResult(Integer awarnessResult) {
        this.awarnessResults.add(awarnessResult);
    }


    public float calculateResponseTimeScore(int standardAverage) {
        float responseTimeScore = 0;
        float userDeviation = getAverageResponseResult() - standardAverage;
        Log.d("ConAd", "user Deviation "+ userDeviation);
        if (userDeviation > 0) {
            if (userDeviation >= standardAverage){
                responseTimeScore = 10;
            }else{
            responseTimeScore = (1 - ((float)userDeviation / standardAverage)) * 100;
            }
        } else {
            responseTimeScore = 100;
        }
        Log.d("ConAd", "Response time score"+ responseTimeScore );
        this.responseTimeScore = responseTimeScore;
        return responseTimeScore;
    }


    public float calculateMemoryScore() {
        this.memoryScore = getAverageMemoryResult() * 100;
        Log.d("ConAd", "memoryScore"+ memoryScore );
        return this.memoryScore;

    }

    public float calculateAwarenessScore() {
        this.awarenessScore = getAverageAwarenessResult() * 100;
        Log.d("ConAd", "awarenessScore"+ awarenessScore );
        return this.awarenessScore;

    }

    public float getResponseTimeScore() {
        return responseTimeScore;
    }

    public float getMemoryScore() {
        return memoryScore;
    }

    public float getAwarenessScore() {
        return awarenessScore;
    }

    public float getFinalScore(int standardAverage, int responseWeight, int memoryWeight, int awarenessWeight) {

        float finalScore = 0;
        finalScore = calculateResponseTimeScore(standardAverage) * ((float)responseWeight/100) + calculateMemoryScore() * ((float)memoryWeight /100)
                + calculateAwarenessScore() * ((float)awarenessWeight/100);


        return finalScore;

    }

    public String getLevel (float score){

        if (score>= 90){
            return "Excellent";
        }
        if (score>= 80){
            return "Very Good";
        }
        if (score>= 70){
            return "Fair";
        }
        if (score>= 60){
            return "Below Average";
        }
        else{
            return "Require Attention";
        }

    }


}
