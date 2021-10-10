package com.ezlol.supertest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    static String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        MainActivity.username = username;
    }

    public void btnClick(View view){
        EditText username_edittext = (EditText) findViewById(R.id.editTextTextPersonName);
        Editable username = username_edittext.getText();
        if(username_edittext.length() > 0){
            MainActivity.setUsername(username.toString());
            Intent intent = new Intent(this, MainActivity2.class);
            intent.putExtra("username", username);
            intent.putExtra("question_counter", 1);
            startActivity(intent);
        }
    }
}