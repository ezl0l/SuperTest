package com.ezlol.supertest;

import static java.lang.String.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {
    int questionCounter;
    private int points;
    private int maxPoints = 0;
    private final List<String> answers = new ArrayList<String>();
    private String lastQuestionType;
    private boolean isShowPoints = false;

    TextView welcomeText;
    TextView questionName;
    TextView questionContent;
    Button answerBtn;

    ConstraintLayout answerLayout;
    CheckBox checkBox1;
    CheckBox checkBox2;
    CheckBox checkBox3;
    EditText answerInput;
    RadioGroup radioGroup;
    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    RadioButton radioButton4;

    DBHelper dbHelper = new DBHelper(this);

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
      //  dbHelper.dropTable(db, "questions");
        Cursor s = db.rawQuery("SELECT * FROM sqlite_master WHERE name='questions' and type='table';", null);
        int cnt = s.getCount();
        if(cnt == 0){
            Toast.makeText(getApplicationContext(), "CREATED!", Toast.LENGTH_SHORT).show();
            db.execSQL("CREATE TABLE IF NOT EXISTS `questions` (`id` INTEGER primary key autoincrement, `type` TEXT, `question` TEXT, `variants` TEXT, `answer` TEXT, `cost` INTEGER);");
            ContentValues cv = new ContentValues();
            cv.put("type", "input");
            cv.put("question", "Внеядерная часть протоплазмы растительных и животных клеток. (10 букв)");
            cv.put("answer", "цитоплазма");
            cv.put("cost", 2);
            db.insert("questions", null, cv);
            cv.clear();

            cv.put("type", "checkboxes");
            cv.put("question", "Какие две планеты вращаются в обратном направлении от остальных?");
            cv.put("variants", "Уран,Плутон,Венера");
            cv.put("answer", "0,2");
            cv.put("cost", 1);
            db.insert("questions", null, cv);
            cv.clear();

            cv.put("type", "radio");
            cv.put("question", "К какой планете принадлежат спутники Оберон и Титания?");
            cv.put("variants", "Юпитер,Уран,Венера,Земля");
            cv.put("answer", "1");
            cv.put("cost", 1);
            db.insert("questions", null, cv);
            cv.clear();

            cv.put("type", "radio");
            cv.put("question", "Какой из вариантов лучше всего описывает атмосферу, окружающую Венеру?");
            cv.put("variants", "яркая и солнечная,холодная и снежная,холодная и влажная,горячая и ядовитая");
            cv.put("answer", "3");
            cv.put("cost", 2);
            db.insert("questions", null, cv);
            Log.i(this.getClass().getSimpleName(), "---CREATED!---");
        }
        s.close();
        s = db.rawQuery("SELECT * FROM questions;", null);
        s.moveToFirst();
        int id = s.getColumnIndex("id");
        Toast.makeText(getApplicationContext(), "" + s.getInt(id), Toast.LENGTH_SHORT).show();

        Intent intent = getIntent();

        String username = intent.getStringExtra("username");
        this.questionCounter = intent.getIntExtra("question_counter", 0);

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

        welcomeText.setText(format("Hi, %s! Voila first question:", MainActivity.username.trim()));
        nextQuestion(findViewById(R.id.answerBtn));
    }

    public void nextQuestion(View view) {
        if(isShowPoints){
            this.finish();
            return;
        }
        if(questionCounter > 1){
            if(welcomeText.getVisibility() != View.INVISIBLE)
                welcomeText.setVisibility(View.INVISIBLE);
            switch(this.lastQuestionType){
                case "input":{
                    answers.add(answerInput.getText().toString());
                    break;
                }
                case "checkboxes":{
                    String checkedCheckboxes = "";
                    if(checkBox1.isChecked())
                        checkedCheckboxes += "1,";
                    if(checkBox2.isChecked())
                        checkedCheckboxes += "2,";
                    if(checkBox3.isChecked())
                        checkedCheckboxes += "3";
                    if(checkedCheckboxes.endsWith(","))
                        checkedCheckboxes = checkedCheckboxes.substring(0, checkedCheckboxes.length() - 1);
                    Toast.makeText(getApplicationContext(), "check " + checkedCheckboxes, Toast.LENGTH_SHORT).show();
                    answers.add(checkedCheckboxes);
                    break;
                }
                case "radio":{
                    int radioButtonID = radioGroup.getCheckedRadioButtonId();
                    View radioButton = radioGroup.findViewById(radioButtonID);
                    answers.add(valueOf(radioGroup.indexOfChild(radioButton)));
                    break;
                }
            }
            //Toast.makeText(getApplicationContext(), answers.toString(), Toast.LENGTH_SHORT).show();
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query("questions", null, "id=" + questionCounter, null, null, null, null);
        if (c.moveToFirst()) {
            int typeColI = c.getColumnIndex("type");
            int qColI = c.getColumnIndex("question");
            int varColI = c.getColumnIndex("variants");
            int answerColI = c.getColumnIndex("answer");
            int costColI = c.getColumnIndex("cost");

            String type = c.getString(typeColI);
            String question = c.getString(qColI);
            String variants = c.getString(varColI);
            String answer = c.getString(answerColI);
            int cost = c.getInt(costColI);

            questionName.setText(question);
            questionContent.setText(String.format("%s: %d", getString(R.string.cost), cost));
            checkBox1.setVisibility(View.INVISIBLE);
            checkBox2.setVisibility(View.INVISIBLE);
            checkBox3.setVisibility(View.INVISIBLE);
            answerInput.setVisibility(View.INVISIBLE);
            radioGroup.setVisibility(View.INVISIBLE);
            checkBox1.setVisibility(View.INVISIBLE);
            checkBox2.setVisibility(View.INVISIBLE);
            checkBox3.setVisibility(View.INVISIBLE);
            switch(type){
                case "input":{
                    answerInput.setVisibility(View.VISIBLE);
                    break;
                }
                case "checkboxes":{
                    String[] variantsArr = variants.split(",");
                    checkBox1.setText(variantsArr[0]);
                    checkBox2.setText(variantsArr[1]);
                    checkBox3.setText(variantsArr[2]);
                    checkBox1.setVisibility(View.VISIBLE);
                    checkBox2.setVisibility(View.VISIBLE);
                    checkBox3.setVisibility(View.VISIBLE);
                    break;
                }
                case "radio":{
                    String[] variantsArr = variants.split(",");
                    radioGroup.clearCheck();
                    radioButton1.setText(variantsArr[0]);
                    radioButton2.setText(variantsArr[1]);
                    radioButton3.setText(variantsArr[2]);
                    radioButton4.setText(variantsArr[3]);
                    radioGroup.setVisibility(View.VISIBLE);
                    break;
                }
            }
            //questionContent.setText(answer);
            lastQuestionType = type;
        }else{
            answerLayout.setVisibility(View.INVISIBLE);
            questionContent.setVisibility(View.INVISIBLE);
            welcomeText.setText(R.string.congratulations_result);
            welcomeText.setVisibility(View.VISIBLE);
            String[] answersArr = answers.toArray(new String[answers.size()]);
            for(int i = 0; i < answersArr.length; i++){
                Toast.makeText(getApplicationContext(), answersArr[i] + i, Toast.LENGTH_SHORT).show();
                Cursor c0 = db.query("questions", null, "id=" + (i + 1) , null, null, null, null);
                if (c0.moveToFirst()) {
                    int typeColI = c0.getColumnIndex("type");
                    int qColI = c0.getColumnIndex("question");
                    int varColI = c0.getColumnIndex("variants");
                    int answerColI = c0.getColumnIndex("answer");
                    int costColI = c0.getColumnIndex("cost");

                    String type = c0.getString(typeColI);
                    String question = c0.getString(qColI);
                    String variants = c0.getString(varColI);
                    String answer = c0.getString(answerColI);
                    int cost = c0.getInt(costColI);
                    Toast.makeText(getApplicationContext(), answersArr[i].trim().toLowerCase() + " != " + answer, Toast.LENGTH_SHORT).show();
                    if(type.equals("checkboxes")){
                        String[] user_variants = answersArr[i].split(",");
                        String real_user_variants = "";
                        for(int i1 = 0; i1 < user_variants.length; i1++){
                            real_user_variants += valueOf(Integer.parseInt(user_variants[i1]) - 1) + ",";
                        }
                        if (answer.equals(real_user_variants.substring(0, real_user_variants.length() - 1)))
                            points += cost;
                    }else{
                        if (answer.equals(answersArr[i].trim().toLowerCase()))
                            points += cost;
                    }
                    maxPoints += cost;
                }else{
                    Toast.makeText(getApplicationContext(), "JOPA!" + i, Toast.LENGTH_SHORT).show();
                }
            }
            isShowPoints = true;
            questionName.setText(points + " points - " + (points * 100 / maxPoints) + "%");
            Toast.makeText(getApplicationContext(), maxPoints + " MAX", Toast.LENGTH_SHORT).show();
            answerBtn.setText(R.string.back);
        }
        //Toast.makeText(getApplicationContext(), questionCounter + " - " + DatabaseUtils.queryNumEntries(db, "questions"), Toast.LENGTH_SHORT).show();
        questionCounter++;
    }
}
