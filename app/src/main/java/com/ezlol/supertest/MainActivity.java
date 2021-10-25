package com.ezlol.supertest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static JSONObject test;
    private static Map<Integer, JSONObject> tests = new HashMap<>();

    Button button, reloadBtn, randomTestBtn;
    ProgressBar progressBar;
    LinearLayout contentLayout, loadingLayout;
    TextView loadingLabel;
    Spinner testSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contentLayout = findViewById(R.id.contentLayout);
        loadingLayout = findViewById(R.id.loadingLayout);
        button = findViewById(R.id.startTest);
        reloadBtn = findViewById(R.id.reloadBtn);
        randomTestBtn = findViewById(R.id.randomTestBtn);
        progressBar = findViewById(R.id.progressBar);
        loadingLabel = findViewById(R.id.loadingLabel);
        testSelect = findViewById(R.id.testSelect);

        button.setOnClickListener(this);
        reloadBtn.setOnClickListener(this);
        randomTestBtn.setOnClickListener(this);

        loadingLayout.setVisibility(View.VISIBLE);

        GetTest t = new GetTest();
        t.execute();
    }

    @Override
    public void onClick(View view){
        switch(view.getId()) {
            case R.id.startTest: {
                JSONObject test = tests.get(testSelect.getSelectedItemPosition());
                EditText username_edittext = findViewById(R.id.editTextTextPersonName);
                Editable username = username_edittext.getText();
                if (username_edittext.length() > 0) {
                    Intent intent = new Intent(this, MainActivity2.class);
                    intent.putExtra("username", username.toString().trim());
                    intent.putExtra("test", test.toString());
                    startActivity(intent);
                }
                break;
            }
            case R.id.reloadBtn: {
                (new GetTest()).execute();
                break;
            }
            case R.id.randomTestBtn:{
                JSONObject test = tests.get((int) (Math.random() * tests.size()));
                EditText username_edittext = findViewById(R.id.editTextTextPersonName);
                Editable username = username_edittext.getText();
                if (username_edittext.length() > 0) {
                    Intent intent = new Intent(this, MainActivity2.class);
                    intent.putExtra("username", username.toString().trim());
                    intent.putExtra("test", test.toString());
                    startActivity(intent);
                }
                break;
            }
        }
    }

    class GetTest extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            button.setEnabled(false);
            reloadBtn.setEnabled(false);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            return AppAPI.getTests();
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            progressBar.setVisibility(View.GONE);
            try {
                if(json != null) {
                    Log.e("JSON!!!", JSON.encode(json) + JSON.isSuccess(json) + " " + json.has("response"));
                    if (JSON.isSuccess(json) && json.has("response")) {
                        JSONArray rTests = json.getJSONArray("response");
                        Log.e("!!!", "AAA");

                        ArrayList<String> testNames = new ArrayList<>();
                        JSONObject test;
                        for(int i = 0; i < rTests.length(); i++){
                            test = rTests.getJSONObject(i);
                            testNames.add(i, test.getString("name"));
                            tests.put(i, test);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, testNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        testSelect.setAdapter(adapter);

                        button.setEnabled(true);
                        loadingLayout.setVisibility(View.GONE);
                        contentLayout.setVisibility(View.VISIBLE);
                        return;
                    }
                }
            } catch (JSONException e) {
                Log.e("ERROR", e.toString());
            }
            reloadBtn.setEnabled(true);
            reloadBtn.setVisibility(View.VISIBLE);
            loadingLabel.setText(R.string.server_connection_error);
            Toast.makeText(getApplicationContext(), R.string.server_connection_error, Toast.LENGTH_LONG).show();
        }
    }
}