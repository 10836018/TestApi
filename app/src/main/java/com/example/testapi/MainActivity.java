package com.example.testapi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.os.AsyncTask;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private TextView mTxtAnswer;
    private Button mButton4;
    private EditText mEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTxtAnswer = findViewById(R.id.textView6);
        mButton4 = findViewById(R.id.button4);
        mEditText = findViewById(R.id.editText);

        mButton4.setOnClickListener(mButton2OnClick);
    }

    private View.OnClickListener mButton2OnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Question question = new Question();
            question.setUserQuestion(mEditText.getText().toString());

            AnswerPost(question);
        }
    };

    public void AnswerPost(Question question){
        String userQuestion=question.getUserQuestion();
        String path="https://flaskapi0415.herokuapp.com/";

        new MyPostTask().execute(userQuestion, path);//調方法
    }


    class MyPostTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            String question = params[0];
            String path = params[1];
            HttpURLConnection conn;

            JSONObject questionJSON = new JSONObject();
            try {
                questionJSON.put("answer", question);
                String content = String.valueOf(questionJSON);

                try {
                    URL url = new URL(path);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(8000);//超時時間
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);//允許對外傳輸數據
                    conn.setDoInput(true);
                    OutputStream os = conn.getOutputStream();//寫數據OutputStream
                    os.write(content.getBytes("UTF-8"));//把數據傳遞給伺服器
                    os.flush();
                    os.close();
                    Log.d("code", "code" + conn.getResponseCode());

                    System.out.println(conn.getResponseCode());

                    if (conn.getResponseCode() == 200) {//拿到返回结果
                        InputStream is = conn.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        String str = br.readLine();
                        JSONObject answerJSON = new JSONObject(str);
                        String ans = answerJSON.getString("answer");
                        //mTxtAnswer.setText(str);
                        return ans;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            Log.d("JSON",s);//打印伺服器返回標籤

            mTxtAnswer.setText(s);

            Log.d("JSON","驗證後");
        }

    }



}