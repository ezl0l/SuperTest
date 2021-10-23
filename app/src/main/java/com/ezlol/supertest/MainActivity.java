package com.ezlol.supertest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static JSONObject test;

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(this);

        class Task extends AsyncTask<Void, Void, JSONObject> {
            @Override
            protected JSONObject doInBackground(Void... voids) {
                return AppAPI.getRandomTest();
            }

            @Override
            protected void onPostExecute(JSONObject json) {
                super.onPostExecute(json);
                try {
                    Log.e("JSON!!!", json.toString() + JSON.isSuccess(json) + " " + json.has("response"));
                    if (JSON.isSuccess(json) && json.has("response")) {
                        MainActivity.test = json.getJSONObject("response");
                        Log.e("!!!", "AAA");
                        return;
                    }
                } catch (JSONException e) {
                    Log.e("ERROR", e.toString());
                }

                Toast.makeText(getApplicationContext(), "Server connection failed!", Toast.LENGTH_LONG).show();
            }
        }
        Task t = new Task();
        t.execute();
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.button) {
            EditText username_edittext = (EditText) findViewById(R.id.editTextTextPersonName);
            Editable username = username_edittext.getText();
            if (username_edittext.length() > 0) {
                Intent intent = new Intent(this, MainActivity2.class);
                intent.putExtra("username", username.toString());
                intent.putExtra("test", test.toString());
                startActivity(intent);
            }
        }
    }
}