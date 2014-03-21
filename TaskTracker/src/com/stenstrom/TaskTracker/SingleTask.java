package com.stenstrom.TaskTracker;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SingleTask extends Activity {
	HashMap<String, String> contactMap;
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {

			setContentView(R.layout.single_task);

			String taskName;
			String endTime;
			String pomodoros;
			String completedPom;
			String isCollab;
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				contactMap = (HashMap<String, String>) extras
						.get(Constants.CONTACT_MAP);
				taskName = contactMap.get(Constants.TASK_NAME);// extras.getString(Constants.TASK_NAME);
				endTime = contactMap.get(Constants.END_TIME);
				pomodoros = contactMap.get(Constants.NUM_OF_POMODOROS);
				completedPom = contactMap.get(Constants.NUM_COMPLETED_POMODOROS);
				isCollab = contactMap.get(Constants.IS_COLLABORATIVE);

				TextView nameView = (TextView) findViewById(R.id.task_name_s);
				TextView endView = (TextView) findViewById(R.id.end_s);
				TextView pomView = (TextView) findViewById(R.id.pomodoros_s);
				TextView completed = (TextView) findViewById(R.id.pomodoros_comp_tot);
				// TextView collabView =
				// (TextView)findViewById(R.id.collaborate);

				nameView.setText(taskName);
				endView.setText(endTime);
				pomView.setText(pomodoros);
				if (!completedPom.equals("null")) {
					completed.setText(completedPom);
				}
				else {
					completed.setText(R.string.no_pom_done);
				}
				if (isCollab.equals("1")) {
					ImageView i = (ImageView) findViewById(R.id.collab_image);
					i.setImageResource(R.drawable.shared_icon);
					// LinearLayout singleView =
					// (LinearLayout)findViewById(R.id.single_task);
					// singleView.addView(i);
					// TextView t = new TextView(this);
					// i
					// t.setText("This is shared");

				}

			}

		}
		catch (Exception e) {
			Log.e("Error", "Error in: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void registerPomodoro(View view) {
		SetWithDB dbConn = new SetWithDB(Constants.updatePomodoro,contactMap.get(Constants.TASK_ID_DB),contactMap.get(Constants.TASK_ID_DB));
		dbConn.execute();
	}

	public void setDone(View view) {
		SetWithDB dbConn = new SetWithDB(Constants.setDone,contactMap.get(Constants.TASK_ID_DB),contactMap.get(Constants.TASK_ID_DB));
		dbConn.execute();
	}

	private class SetWithDB extends AsyncTask<Void, Void, Boolean> {
		String url = "http://ludste.synology.me/TaskTracker/index.php";

		String method;
		String taskID;
		String userID;

		public SetWithDB(String method, String taskID, String userID) {
			this.method = method;
			this.taskID = taskID;
			this.userID = userID;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair(Constants.METHOD, method));
			nameValuePairs.add(new BasicNameValuePair(Constants.TASK_ID_DB,taskID));
			nameValuePairs.add(new BasicNameValuePair(Constants.USER_ID_DB, userID));
			ServiceHandler serviceHandler = new ServiceHandler();
			String jsonStr = serviceHandler.makeServiceCall(url, ServiceHandler.GET, nameValuePairs);
			if (jsonStr.contains("true"))
				return true;
			return false;

		}

		protected void onPostExecute(Boolean result) {
			if (result) {
				new AlertDialog.Builder(SingleTask.this)
				.setMessage(
						"Your pomodoro has now been updated")
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
			}
			else{
				System.err.println("Show warning dialog");
				new AlertDialog.Builder(SingleTask.this)
						.setMessage(
								"Error in connection with database, please try again")
						.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {

							}
						}).show();
			}

		}

	}
}
