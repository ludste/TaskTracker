package com.stenstrom.TaskTracker;

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
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
	int userID;
	Button buttonChangeDate;
	final Calendar c = Calendar.getInstance();
	int yearSelected;
	int monthSelected;
	int daySelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userID=getSharedPreferences(getString(R.string.preference_key_file), 0).getInt(Constants.USER_ID, -1);
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

		EditText editName = (EditText) findViewById(R.id.name_edit);
		EditText editPomodoros = (EditText) findViewById(R.id.pomodoro_edit);
		String taskName = editName.getText().toString();
		String pomodoros = editPomodoros.getText().toString();
		String collaborators = ((EditText)findViewById(R.id.choose_collab)).getText().toString();//Comma separated list
		
		System.out.println("collaborators " + collaborators);
		if(!isOKCollab(collaborators)){
			new AlertDialog.Builder(NewTask.this)
			.setMessage(getString(R.string.invalidCollabFormat))
			.setNeutralButton("OK", null).show();
			return;
		}
		int isCollaborative = (collaborators.equals("")? 0 : 1);
		String date = yearSelected + "-" + (monthSelected + 1) + "-" + daySelected;
		System.out.println("Button was clicked, with text " + taskName + " and pomodoros "
				+ pomodoros + " at date " + date);
		AddTask newTask = new AddTask(userID, pomodoros, taskName, isCollaborative, date, collaborators);
		newTask.execute();
	}
	
	private boolean isOKCollab(String collaborators){
		if(collaborators.equals("")){
			return true;
		}
		String[] collabList = collaborators.split(",");
		for (int i = 0; i < collabList.length; i++) {
			String collaborator = (collabList[i]).trim();
			if(!isAlphanumeric(collaborator)){
				return false;
			}
		}
		return true;
	}
	public boolean isAlphanumeric(String str) {
        for (int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isDigit(c) && !Character.isLetter(c))
                return false;
        }

        return true;
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
		String collaborators;

		public AddTask(int userID, String pomodoros, String taskName, int isCollaborative, String date, String collab) {
			this.userID = userID;
			this.pomodoros = pomodoros;
			this.taskName = taskName;
			this.isCollaborative = isCollaborative;
			this.date = date;
			collaborators = collab;
		}
		private ProgressDialog pDialog;
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        // Showing progress dialog
	        pDialog = new ProgressDialog(NewTask.this);
	        pDialog.setMessage("Please wait...");
	        pDialog.setCancelable(false);
	        pDialog.show();

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
			if(isCollaborative ==1){
				nameValuePairs.add(new BasicNameValuePair(Constants.COLLAB, collaborators));
			}
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
			if (pDialog.isShowing())
	            pDialog.dismiss();
			if(isSuccessful){
			System.err.println("Now go to listtasks");
				Intent intent = new Intent(NewTask.this, ListTasks.class);
				startActivity(intent);
			}
			else{
				System.err.println("Show warning dialog");
				new AlertDialog.Builder(NewTask.this)
				.setMessage(getString(R.string.no_db_conn))
				.setNeutralButton("OK", null).show();
			}
			
		}
		
	}
}
