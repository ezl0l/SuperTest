package com.ezlol.supertest;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
        Log.e("EZLOL", r.toString());
        return r.json();
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
}
