package com.stenstrom.TaskTracker;

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class NewTask extends Activity implements DatePickerDialog.OnDateSetListener {
	String url = "http://ludste.synology.me/TaskTracker/index.php";
	Button buttonChangeDate;
	final Calendar c = Calendar.getInstance();
	int yearSelected;
	int monthSelected;
	int daySelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_task);
		
		// If not selected, year, month and day will be today's date
		yearSelected = c.get(Calendar.YEAR);
		monthSelected = c.get(Calendar.MONTH);
		daySelected = c.get(Calendar.DAY_OF_MONTH);

		buttonChangeDate = (Button) findViewById(R.id.button_change_date);
		buttonChangeDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDateDialog(v);
			}

		});

	}

	private void showDateDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

	/*
	 * Called when button "Send" is clicked.
	 */
	public void sendToBackend(View view) {
		int userID = 1; // TODO kolla från cookie
		int isCollaborative = 0;
		EditText editName = (EditText) findViewById(R.id.name_edit);
		EditText editPomodoros = (EditText) findViewById(R.id.pomodoro_edit);
		String taskName = editName.getText().toString();
		String pomodoros = editPomodoros.getText().toString();
		String date = yearSelected + "-" + (monthSelected + 1) + "-" + daySelected;
		System.out.println("Button was clicked, with text " + taskName + " and pomodoros "
				+ pomodoros + " at date " + date);
		AddTask newTask = new AddTask(userID, pomodoros, taskName, isCollaborative, date);
		newTask.execute();
	}

	/*
	 * Called on date set in DatePickerFragment. Change text on buttonChangeDate
	 * and saves date into class variables
	 */
	@Override
	public void onDateSet(DatePicker datePicker, int year, int month, int day) {
		yearSelected = year;
		monthSelected = month;
		daySelected = day;
		String dateDelimiter = "-";
		StringBuilder dateString = new StringBuilder();
		dateString.append(getString(R.string.task_end)).append(" ").append(day)
				.append(dateDelimiter).append(month + 1).append(dateDelimiter).append(year);
		buttonChangeDate.setText(dateString);
		buttonChangeDate.setTextColor(Color.BLACK); // TODO find better way to
													// change color
	}

	private class AddTask extends AsyncTask<Void, Void, Boolean> {
		String url = "http://ludste.synology.me/TaskTracker/index.php";

		int userID;
		String pomodoros;
		String taskName;
		int isCollaborative;
		String date;

		public AddTask(int userID, String pomodoros, String taskName, int isCollaborative, String date) {
			this.userID = userID;
			this.pomodoros = pomodoros;
			this.taskName = taskName;
			this.isCollaborative = isCollaborative;
			this.date = date;
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			// TODO do a post to backend
			ServiceHandler serviceHandler = new ServiceHandler();
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair(Constants.METHOD, Constants.add));
			nameValuePairs.add(new BasicNameValuePair(Constants.USER_ID, Integer.toString(userID)));
			nameValuePairs
					.add(new BasicNameValuePair(Constants.NUM_OF_POMODOROS, pomodoros));
			nameValuePairs.add(new BasicNameValuePair(Constants.TASK_NAME, taskName));
			nameValuePairs.add(new BasicNameValuePair(Constants.END_TIME, date));
			nameValuePairs.add(new BasicNameValuePair(Constants.IS_COLLABORATIVE, Integer
					.toString(isCollaborative)));
			String jsonStr = serviceHandler
					.makeServiceCall(url, ServiceHandler.GET, nameValuePairs);
			System.err.println("Jsonstr: "+jsonStr);
			
			if(jsonStr.contains("true"))
				return true;
		//PRoblem, den vill inte konvertera till JSON objekt trots att jag gör EXAKT som i allTasks
//			try {
//				JSONObject allResultJson = new JSONObject(jsonStr);
//				String statusCode = allResultJson.getString("status");
//				String data = allResultJson.getString("data");
//				if (statusCode.equals(Constants.add) && data.equals("true")) {
//					return true;
//				}
//			}
//			catch (Exception e) {
//				Log.e("log_tag", "Error Parsing Data " + e.toString());
//				e.printStackTrace();
//			}
			
			return false;//Might want more exhaustive error messages?
			
		}
		@Override
		protected void onPostExecute(Boolean isSuccessful){
			if(isSuccessful){
			System.err.println("Now go to listtasks");
				Intent intent = new Intent(NewTask.this, ListTasks.class);
				startActivity(intent);
			}
			else{
				System.err.println("Show warning dialog");
				new AlertDialog.Builder(NewTask.this)
				.setMessage("It was not possible to save your task, maybe you miss fields or the internet" +
						"connection is corrupt. Please try again")
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            
		        }
		     }).show();
			}
			
		}
		
	}
}
