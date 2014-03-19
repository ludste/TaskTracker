package com.stenstrom.TaskTracker;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class SingleTask extends Activity {
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			
			setContentView(R.layout.single_task);
			
			String taskName;
			String endTime;
			String pomodoros;
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				taskName = extras.getString(Constants.TASK_NAME);
				endTime = extras.getString(Constants.END_TIME);
				pomodoros = extras.getString(Constants.NUM_OF_POMODOROS);
			
			TextView nameView = (TextView) findViewById(R.id.task_name_s);
			TextView endView = (TextView) findViewById(R.id.end_s);
			TextView pomView = (TextView) findViewById(R.id.pomodoros_s);

			nameView.setText(taskName);
			endView.setText(endTime);
			pomView.setText(pomodoros);
			}

		}
		catch (Exception e) {
			Log.e("Error", "Error in: " + e.getMessage());
			e.printStackTrace();
		}
	}
	//TODO gör klart nedan
	public void registerPomodoro(View view){
//		new setPomodoro().execute();
	}
//	private class setPomodoro extends AsyncTask<Void, Void, Void> {
//		String url = "http://ludste.synology.me/TaskTracker/index.php";
//		@Override
//		protected Void doInBackground(Void... params) {
//
//			ServiceHandler serviceHandler = new ServiceHandler();
//			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//			nameValuePairs.add(new BasicNameValuePair(Constants.METHOD, Constants.UPDATE_POMODORO));
//		}
}
