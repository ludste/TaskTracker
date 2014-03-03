package com.stenstrom.TaskTracker;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    TextView resultView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        StrictMode.enableDefaults(); //STRICT MODE ENABLED



        resultView = (TextView) findViewById(R.id.ResultText);
        resultView.append("TEST \n");
        //parse json data
        try {
            String s = "";
            JSONArray jArray = getDataFromDatabase();

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json = jArray.getJSONObject(i);
                s = s +
                        "id : " + json.getInt("id") + "\n" +
                        "Name : " + json.getString("name") + "\n" +
                        "Start : " + json.getString("startDate") + "\n" +
                        "End : " + json.getString("endDate") + "\n" +
                        "Completion time : " + json.getString("completionTime") + "\n" +
                        "Num of Pomodoros : " + json.getString("numOfPomodoros") + "\n" +
                        "Collaborative : " + json.getInt("collaborative") + "\n\n";
            }

            resultView.setText(s);

        } catch (Exception e) {
            // TODO: handle exception
            Log.e("log_tag", "Error Parsing Data " + e.toString());
            resultView.append("Error Parsing Data " + e.toString() + "\n");
        }

    }


    public JSONArray getDataFromDatabase() {
        InputStream inputStream = null;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("taskName", "MatteC"));

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://ludste.synology.me/TaskTracker/index.php"); //Behöver speca index.php, hittar inte annars


            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));//Lägg till postvariabler
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();
            resultView.append("HTTP: OK" + "\n");
            Log.d("HTTP", "HTTP: OK");
        } catch (Exception e) {
            Log.e("HTTP", "Error in http connection " + e.toString());
            resultView.append("Error in http connection " + e.toString() + "\n");

        }
        //convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            assert inputStream != null;
            inputStream.close();

            return new JSONArray(stringBuilder.toString());
        } catch (Exception e) {
            Log.e("log_tag", "Error  converting result " + e.toString());
            resultView.append("Error  converting result " + e.toString() + "\n");
        }
        return null;
    }


}