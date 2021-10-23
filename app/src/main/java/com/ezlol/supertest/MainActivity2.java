package com.ezlol.supertest;

import static java.lang.String.valueOf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {
    int questionCounter = 0;
    private int points = 0;
    private JSONObject test;
    private JSONArray questions;
    private final List<String> answers = new ArrayList<String>();

    TextView welcomeText, questionName, questionContent;
    Button answerBtn;

    ConstraintLayout answerLayout;
    CheckBox checkBox1, checkBox2, checkBox3;
    EditText answerInput;
    RadioGroup radioGroup;
    RadioButton radioButton1, radioButton2, radioButton3, radioButton4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        welcomeText = (TextView) findViewById(R.id.welcomeText);
        questionName = (TextView) findViewById(R.id.questionName);
        questionContent = (TextView) findViewById(R.id.questionContent);
        answerBtn = (Button) findViewById(R.id.answerBtn);

        answerLayout = (ConstraintLayout) findViewById(R.id.answerLayout);
        checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
        checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        checkBox3 = (CheckBox) findViewById(R.id.checkBox3);
        answerInput = (EditText) findViewById(R.id.answerInput);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
        radioButton4 = (RadioButton) findViewById(R.id.radioButton4);

        Intent intent = getIntent();

        String json = intent.getStringExtra("test");
        try {
            this.test = new JSONObject(json);
            this.questions = test.getJSONArray("questions");
        } catch (JSONException e) {
            Log.e("MainActivity2", "Error while parsing json TEST.");
            finish();
            return;
        }
        answerBtn.setOnClickListener(this);

        nextQuestion();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == answerBtn.getId()) {
            nextQuestion();
        }
    }

    private void nextQuestion(){
        for(int i = 0; i < answerLayout.getChildCount(); i++){
            answerLayout.getChildAt(i).setVisibility(View.INVISIBLE);
        }
        if(questionCounter > 0) {
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
                answers.add(questionCounter - 1, answer);
            } catch (Exception e) {
                Log.e("FATAL ERROR!", e.toString());
            }
            if (questionCounter == questions.length()) {
                // END
                Log.e("EZLOL", "END!");
                Log.e("ANSWERS", answers.toString());

                endTest();
                return;
            }
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
        JSONObject currentQuestion, currentQuestionContent;
        String answer;
        final int points = 0;
        try {
            for (int i = 0; i < questions.length(); i++) {
                answer = answers.get(i);
                currentQuestion = questions.getJSONObject(i);

                JSONObject finalCurrentQuestion = currentQuestion;
                String finalAnswer = answer;
                class Task extends AsyncTask<Void, Void, Boolean> {
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        try {
                            return AppAPI.checkAnswer(finalCurrentQuestion.getInt("id"), finalAnswer);
                        } catch (JSONException e) {
                            return false;
                        }
                    }

                    @Override
                    protected void onPostExecute(Boolean b) {
                        super.onPostExecute(b);
                    }
                }
            }
        }catch (JSONException ignored){}
    }
}