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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
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
		StrictMode.enableDefaults(); // STRICT MODE ENABLED

		resultView = (TextView) findViewById(R.id.ResultText);
		resultView.append("TEST \n");
		

		try {
			String s = "";
			int userID = 1;
			JSONObject allResultJson = getDataFromDatabase(userID);
			String stat = allResultJson.getString("status");
//			System.out.println("Status: "+stat);
			if (stat.equals(DatabaseThread.getTasks)) {
				JSONArray jArray = allResultJson.getJSONArray("data");
//				System.out.println("Data: " + jArray);
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json = jArray.getJSONObject(i);
					s = s + "id : " + json.getInt("id") + "\n" + "Name : "
							+ json.getString("name") + "\n" + "Start : "
							+ json.getString("startDate") + "\n" + "End : "
							+ json.getString("endDate") + "\n"
							+ "Completion time : "
							+ json.getString("completionTime") + "\n"
							+ "Num of Pomodoros : "
							+ json.getString("numOfPomodoros") + "\n"
							+ "Collaborative : " + json.getInt("collaborative")
							+ "\n\n";
				}

				resultView.setText(s);
			} else {
				resultView.setText("Could not get info from db, error: "
						+ stat);
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("log_tag", "Error Parsing Data " + e.toString());
			resultView.append("Error Parsing Data " + e.toString() + "\n");
		}

	}

	public JSONObject getDataFromDatabase(int userID) {
		InputStream inputStream = null;
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("method",
				DatabaseThread.getTasks));
		nameValuePairs.add(new BasicNameValuePair("userID", Integer
				.toString(userID)));

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://ludste.synology.me/TaskTracker/index.php"); // Behöver
																		// speca
																		// index.php,
																		// hittar
																		// inte
																		// annars
			// String url =
			// "http://ludste.synology.me/TaskTracker/?method=getUserTasks&userID="
			// + userID;
			// System.out.println(url);
			// HttpGet httppost = new HttpGet(url);

			 httppost.setEntity(new
			 UrlEncodedFormEntity(nameValuePairs));//Lägg till postvariabler
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
			resultView.append("HTTP: OK" + "\n");
			Log.d("HTTP", "HTTP: OK");
		} catch (Exception e) {
			Log.e("HTTP", "Error in http connection " + e.toString());
			resultView
					.append("Error in http connection " + e.toString() + "\n");

		}
		// convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"), 8);
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
			assert inputStream != null;
			inputStream.close();
			System.out.println("Response from server: "
					+ stringBuilder.toString());
			return new JSONObject(stringBuilder.toString());
		} catch (Exception e) {
			Log.e("log_tag", "Error  converting result " + e.toString());
			resultView
					.append("Error  converting result " + e.toString() + "\n");
		}
		return null;
	}

}