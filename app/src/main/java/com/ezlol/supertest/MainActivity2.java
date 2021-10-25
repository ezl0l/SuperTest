package com.ezlol.supertest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {
    int questionCounter = 0;
    private String username;
    private JSONObject test;
    private JSONArray questions;
    private final Map<Integer, String> answers = new HashMap<>();

    TextView welcomeText, questionName, questionContent;
    Button answerBtn, backBtn;

    ConstraintLayout answerLayout;
    CheckBox checkBox1, checkBox2, checkBox3;
    EditText answerInput;
    RadioGroup radioGroup;
    RadioButton radioButton1, radioButton2, radioButton3, radioButton4;
    ProgressBar answersCheckBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        welcomeText = findViewById(R.id.welcomeText);
        questionName = findViewById(R.id.questionName);
        questionContent = findViewById(R.id.questionContent);
        answerBtn = findViewById(R.id.answerBtn);
        backBtn = findViewById(R.id.backBtn);

        answerLayout = findViewById(R.id.answerLayout);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        answerInput = findViewById(R.id.answerInput);
        radioGroup = findViewById(R.id.radioGroup);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        radioButton4 = findViewById(R.id.radioButton4);
        answersCheckBar = findViewById(R.id.answersCheckBar);

        Intent intent = getIntent();

        String json = intent.getStringExtra("test");
        this.username = intent.getStringExtra("username");
        try {
            this.test = new JSONObject(json);
            this.questions = test.getJSONArray("questions");
        } catch (JSONException e) {
            Log.e("MainActivity2", "Error while parsing json TEST.");
            finish();
            return;
        }
        answerBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        nextQuestion();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.answerBtn: {
                nextQuestion();
                break;
            }
            case R.id.backBtn:{
                questionCounter -= 2;
                nextQuestion();
                break;
            }
        }
    }

    private void nextQuestion(){
        for(int i = 0; i < answerLayout.getChildCount(); i++){
            answerLayout.getChildAt(i).setVisibility(View.INVISIBLE);
        }
        if(questionCounter > 0) {
            welcomeText.setVisibility(View.GONE);
            backBtn.setVisibility(View.VISIBLE);
            try {
                Log.e("ANSWERS", "START WRITE ANSWER!");
                JSONObject lastQuestion = questions.getJSONObject(questionCounter - 1);
                JSONObject content = lastQuestion.getJSONObject("content");
                String answer = "";
                switch (content.getString("type")) {
                    case "input": {
                        answer = answerInput.getText().toString();
                        break;
                    }
                    case "radio": {
                        int radioButtonID = radioGroup.getCheckedRadioButtonId();
                        View radioButton = radioGroup.findViewById(radioButtonID);
                        answer = String.valueOf(radioGroup.indexOfChild(radioButton));
                        break;
                    }
                    case "checkbox": {
                        if (checkBox1.isChecked())
                            answer += "0,";
                        if (checkBox2.isChecked())
                            answer += "1,";
                        if (checkBox3.isChecked())
                            answer += "2";
                        if (answer.endsWith(","))
                            answer = answer.substring(0, answer.length() - 1);
                        break;
                    }
                }
                Log.e("ANSWER", answer);
                answers.put(lastQuestion.getInt("id"), answer);
            } catch (Exception e) {
                Log.e("FATAL ERROR!", e.toString());
            }
            if(questionCounter + 1 == questions.length()){
                answerBtn.setText(R.string.finish);
            }

            if (questionCounter == questions.length()) {
                // END
                Log.e("EZLOL", "END!");
                Log.e("ANSWERS", answers.toString());

                endTest();
                return;
            }
        }else{
            backBtn.setVisibility(View.GONE);
            welcomeText.setText(String.format("%s, %s!", getString(R.string.sc_hi), username));
        }
        try {
            JSONObject currentQuestion = questions.getJSONObject(questionCounter);
            JSONObject content = currentQuestion.getJSONObject("content");
            questionName.setText(content.getString("content"));
            switch (content.getString("type")){
                case "input":{
                    answerInput.setVisibility(View.VISIBLE);
                    break;
                }
                case "radio":{
                    JSONArray variants = content.getJSONArray("variants");
                    radioButton1.setText(variants.getString(0));
                    radioButton2.setText(variants.getString(1));
                    radioButton3.setText(variants.getString(2));
                    radioButton4.setText(variants.getString(3));
                    radioGroup.setVisibility(View.VISIBLE);
                    break;
                }
                case "checkbox":{
                    JSONArray variants = content.getJSONArray("variants");
                    Log.e("VARIANTS", variants.toString());
                    checkBox1.setText(variants.getString(0));
                    checkBox2.setText(variants.getString(1));
                    checkBox3.setText(variants.getString(2));
                    checkBox1.setVisibility(View.VISIBLE);
                    checkBox2.setVisibility(View.VISIBLE);
                    checkBox3.setVisibility(View.VISIBLE);
                    break;
                }
            }
        } catch (JSONException ignored) {}

        questionCounter++;
    }

    private void endTest(){
        for(int i = 0; i < answerLayout.getChildCount(); i++){
            answerLayout.getChildAt(i).setVisibility(View.INVISIBLE);
        }
        backBtn.setVisibility(View.GONE);
        answerBtn.setVisibility(View.GONE);
        answersCheckBar.setVisibility(View.VISIBLE);
        ArrayList<Integer> questionsIDS = new ArrayList<>(this.answers.keySet());
        ArrayList<String> answersL = new ArrayList<>(this.answers.values());
        Log.e("QUESTIONSIDS", questionsIDS.toString());
        Log.e("ANSWERS!", answersL.toString());

        class Task extends AsyncTask<ArrayList, Void, ArrayList<Object>> {
            @Override
            protected ArrayList<Object> doInBackground(ArrayList ...lists) { //
                Log.e("LIST 0", lists[0].toString());
                Log.e("LIST 1", lists[1].toString());
                ArrayList<Object> l = new ArrayList<>();
                l.add(AppAPI.checkAnswers(lists[0], lists[1]));
                l.add(AppAPI.getGeneralStats(lists[0]));
                return l;
            }

            @Override
            protected void onPostExecute(ArrayList<Object> r) {
                super.onPostExecute(r);
                Map<Integer, Boolean> answersResults = (Map<Integer, Boolean>) r.get(0);
                Double avgResult = (Double) r.get(1);
                answersCheckBar.setVisibility(View.GONE);
                Log.e("ANSWERS RES", r.toString());
                int points = 0;
                for (Map.Entry<Integer, Boolean> e : answersResults.entrySet()) {
                    if(e.getValue())
                        points++;
                }
                welcomeText.setText(R.string.congratulations_result);
                questionName.setText(String.format("%s: %d - %d%%", getResources().getString(R.string.ur_points), points, (points * 100) / answersResults.size()));
                questionContent.setText(String.format("%s: %d%%", getResources().getString(R.string.avg_complet), (int) (avgResult * 100)));
                questionContent.setVisibility(View.VISIBLE);
                welcomeText.setVisibility(View.VISIBLE);
                questionName.setVisibility(View.VISIBLE);
            }
        }
        Task t = new Task();
        t.execute(questionsIDS, answersL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}