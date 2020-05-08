package com.example.testapi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

        /**
         * 192.168.0.100为电脑ipv4地址
         * Demo_Database为eclipse中的工程名
         * TestServlet为要提交到的servlet名
         */
        String path="https://flaskapi0415.herokuapp.com/";
        new MyPostTask().execute(userQuestion, path);//调方法
    }


    class MyPostTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            String answer = params[0];
            String path = params[1];
            HttpURLConnection conn = null;

            JSONObject questionJSON = new JSONObject();
            try {
                questionJSON.put("answer", answer);
                String content = String.valueOf(questionJSON);

                try {
                    URL url = new URL(path);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(8000);//超时时间
                    /**
                     * conn.setRequestProperty("Content-Length",s.length()+"");
                     * 这条语句可能会导致报java.net.ProtocolException: exceeded content-length limit of 26 bytes 错误
                     */
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);//允许对外输出数据
                    conn.setDoInput(true);
                    OutputStream os = conn.getOutputStream();//写数据OutputStream
                    os.write(content.getBytes());//把数据传递给服务器了
                    os.flush();
                    os.close();
                    Log.d("code", "code" + conn.getResponseCode());

                    System.out.println(conn.getResponseCode());

                    if (conn.getResponseCode() == 200) {//拿到返回结果
                        InputStream is = conn.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        String str = br.readLine();
                        return str;
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
            Log.d("JSON",s);//打印服务器返回标签
            //flag=true;
            switch (s){
                //判断返回的状态码，并把对应的说明显示在UI
                case "100":
                    mTxtAnswer.setText(s);
                    break;
                case "200":
                    mTxtAnswer.setText(s);
                    break;
            }
            Log.d("JSON","验证后");
        }

    }



}





