package com.ezlol.supertest;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AppAPI {
    private static final String serverURL = "http://85.12.218.165:8080/SuperTest/api/";

    public static String getServerURL() {
        return serverURL;
    }

    public static JSONObject getTest(int id){
        Response r = Requests.get(serverURL + "getTests.php?test_id=" + id);
        Log.e("EZLOL", r.toString());
        return r.json();
    }

    public static JSONObject getTests(int offset, int limit){
        Response r = Requests.get(serverURL + "getTests.php?offset=" + offset + "&limit=" + limit);
        Log.e("EZLOL", r.toString());
        return r.json();
    }

    public static JSONObject getTests(int limit){
        return getTests(0, limit);
    }

    public static JSONObject getTests(){
        return getTests(0, 10);
    }

    public static JSONObject getRandomTest(int numberOfQuestions){
        Response r = Requests.get(serverURL + "getTests.php?random=1&number_of_questions=" + numberOfQuestions);
        if(r != null) return r.json();
        return null;
    }

    public static JSONObject getRandomTest(){
        return getRandomTest(5);
    }

    public static boolean checkAnswer(int questionID, String answer){ // arrays must be like "1,2,3"
        Response r = Requests.get(serverURL + "checkAnswer.php?question_id=" + questionID + "&answer=" + answer);
        JSONObject json = r.json();
        try{
            if(JSON.isSuccess(json) && json.has("response")){
                JSONObject response = json.getJSONObject("response");
                if(response.has("match")){
                    return response.getBoolean("match");
                }
            }
        }catch (JSONException ignored){}
        return false;
    }

    public static Map<Integer, Boolean> checkAnswers(List questionIDS, List answers){
        String questionString = join(questionIDS, ";");
        String answerString = join(answers, ";");
        Log.e("strings", questionIDS + " " + answers);
        Response r = Requests.get(serverURL + "checkAnswers.php?question_ids=" + questionString + "&answers=" + answerString);
        JSONObject json = r.json();
        Map<Integer, Boolean> matches = new HashMap<>();
        try {
            if (JSON.isSuccess(json) && json.has("response")) {
                JSONObject response = json.getJSONObject("response");

                Iterator<String> keys = response.keys();
                String key;
                JSONObject match;
                while(keys.hasNext()){
                    key = keys.next();
                    match = response.getJSONObject(key);
                    if(JSON.isSuccess(match) && match.has("response")){
                        matches.put(Integer.parseInt(key), match.getJSONObject("response").getBoolean("match"));
                    }else
                        matches.put(Integer.parseInt(key), false);
                }
            }
        }catch (JSONException ignored){}
        return matches;
    }

    public static Double getGeneralStats(List questionIDS){
        String questionString = join(questionIDS, ";");
        Log.e("questionIDS", questionString);
        Response r = Requests.get(serverURL + "getStats.php?question_ids=" + questionString);
        JSONObject json = r.json();
        try {
            if (JSON.isSuccess(json) && json.has("response")) {
                JSONObject response = json.getJSONObject("response");
                if(response.has("avg")){
                    return response.getDouble("avg");
                }
            }
        }catch (JSONException ignored){}
        return null;
    }

    private static String join(List elements, String delimiter){
        String s = "";
        for(int i = 0; i < elements.size(); i++){
            s += elements.get(i) + delimiter;
        }
        return s.substring(0, s.length() - delimiter.length());
    }

    private static String join(List elements){
        return join(elements, ",");
    }
}
