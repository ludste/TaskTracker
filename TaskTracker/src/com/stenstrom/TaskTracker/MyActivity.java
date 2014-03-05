package com.stenstrom.TaskTracker;

import android.app.Activity;
import android.os.AsyncTask;
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
import java.util.HashMap;
import java.util.List;

public class MyActivity extends Activity {
	/**
	 * Called when the activity is first created.
	 */
	TextView resultView;
	ArrayList<Task> allTasks = new ArrayList<Task>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		resultView = (TextView) findViewById(R.id.Tasks);
		int userID = 1;
		
		new GetTasks().execute(userID);
		//Now alltasks is populated

	}


	private class GetTasks extends AsyncTask<Integer, Void, String>{
		String url = "http://ludste.synology.me/TaskTracker/index.php";
		
		@Override
		protected String doInBackground(Integer... arg0) {
			int userID = arg0[0];
			ServiceHandler serviceHandler = new ServiceHandler();
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair(Constants.METHOD, Constants.getTasks));
			nameValuePairs.add(new BasicNameValuePair(Constants.USER_ID, Integer.toString(userID)));
			String jsonStr = serviceHandler.makeServiceCall(url, ServiceHandler.GET, nameValuePairs);
			try {
				JSONObject allResultJson = new JSONObject(jsonStr);
				String statusCode = allResultJson.getString("status");
				if (statusCode.equals(Constants.getTasks)) {
					JSONArray jArray = allResultJson.getJSONArray("data");					
					for (int i = 0; i < jArray.length(); i++) {
						Task task;
						JSONObject json = jArray.getJSONObject(i);
						task = new Task(json.getString(Constants.TASK_ID_DB),
								json.getString(Constants.TASK_NAME),
								json.getString(Constants.START_TIME),
								json.getString(Constants.END_TIME),
								json.getString(Constants.COMPLETED_WHOLE_TASK),
								json.getString(Constants.NUM_OF_POMODOROS),
								json.getString(Constants.IS_COLLABORATIVE));
						
						allTasks.add(task);
					}
				}
				System.err.println("All tasks added");
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("log_tag", "Error Parsing Data " + e.toString());
				resultView.append("Error Parsing Data " + e.toString() + "\n");
			}
			return jsonStr;
		}
		
		protected void onPostExecute(String jsonStr){
			String statusCode;
			try {
				JSONObject allResultJson = new JSONObject(jsonStr);
				String s = "";
				statusCode = allResultJson.getString("status");
				if (statusCode.equals(Constants.getTasks)) {
					JSONArray jArray = allResultJson.getJSONArray("data");					
					for (int i = 0; i < jArray.length(); i++) {
						JSONObject json = jArray.getJSONObject(i);						
						s = s + "id : " + json.getInt("id") + "\n" + "Name : "
								+ json.getString("name") + "\n" + "Start : "
								+ json.getString("start_date") + "\n" + "End : "
								+ json.getString("end_date") + "\n"
								+ "Completion time : "
								+ json.getString(Constants.COMPLETED_WHOLE_TASK) + "\n"
								+ "Num of Pomodoros : "
								+ json.getString(Constants.NUM_OF_POMODOROS) + "\n"
								+ "Collaborative : " + json.getInt("collaborative")
								+ "\n\n";
					}

					resultView.append("\n"+s);
				} else {
					resultView.setText("Could not get info from db, error: "
							+ statusCode);
				}
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("log_tag", "Error Parsing Data " + e.toString());
				resultView.append("Error Parsing Data " + e.toString() + "\n");
			}
		}
	}
	//Behöver inte denna funk längre, men finns kvar för ev referensbehov
//	public JSONObject getDataFromDatabase(int userID) {
//		InputStream inputStream = null;
//		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//		nameValuePairs.add(new BasicNameValuePair("method", Constants.getTasks));
//		nameValuePairs.add(new BasicNameValuePair("userID", Integer.toString(userID)));
//
//		try {
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpPost httppost = new HttpPost("http://ludste.synology.me/TaskTracker/index.php");
//			// String url =
//			// "http://ludste.synology.me/TaskTracker/?method=getUserTasks&userID="
//			// + userID;
//			// System.out.println(url);
//			// HttpGet httppost = new HttpGet(url);
//
//			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));// LÃ¤gg
//																			// till
//																			// postvariabler
//			HttpResponse response = httpclient.execute(httppost);
//			HttpEntity entity = response.getEntity();
//			inputStream = entity.getContent();
//			resultView.append("HTTP: OK" + "\n");
//			Log.d("HTTP", "HTTP: OK");
//		}
//		catch (Exception e) {
//			Log.e("HTTP", "Error in http connection " + e.toString());
//			resultView.append("Error in http connection " + e.toString() + "\n");
//
//		}
//		// convert response to string
//		try {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
//			StringBuilder stringBuilder = new StringBuilder();
//			String line;
//			while ((line = reader.readLine()) != null) {
//				stringBuilder.append(line).append("\n");
//			}
//			assert inputStream != null;
//			inputStream.close();
//			System.out.println("Response from server: " + stringBuilder.toString());
//			return new JSONObject(stringBuilder.toString());
//		}
//		catch (Exception e) {
//			Log.e("log_tag", "Error  converting result " + e.toString());
//			resultView.append("Error  converting result " + e.toString() + "\n");
//		}
//		return null;
//	}
}