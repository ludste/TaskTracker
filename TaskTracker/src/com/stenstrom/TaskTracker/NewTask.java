package com.stenstrom.TaskTracker;

import android.app.*;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class NewTask extends Activity implements DatePickerDialog.OnDateSetListener {
    int userID;
    Button buttonChangeDate;
    final Calendar c = Calendar.getInstance();
    int yearSelected;
    int monthSelected;
    int daySelected;
    ArrayList<String> allUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allUsers = new ArrayList<String>();
        userID = getSharedPreferences(getString(R.string.preference_key_file), 0).getInt(Constants.USER_ID, -1);
        AddTask getUsers = new AddTask(0, null, null, 0, null, null, Constants.getAlUsers);
        getUsers.execute();
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
        String collaborators = ((EditText) findViewById(R.id.choose_collab)).getText().toString();//Comma separated list

        System.out.println("collaborators " + collaborators);
        if (!isOKCollab(collaborators)) {
            new AlertDialog.Builder(NewTask.this)
                    .setMessage("Your collaborator list need to be comma separated with alphanumeric")
                    .setNeutralButton("OK", null).show();
            return;
        }
        int isCollaborative = (collaborators.equals("") ? 0 : 1);
        String date = yearSelected + "-" + (monthSelected + 1) + "-" + daySelected;
        System.out.println("Button was clicked, with text " + taskName + " and pomodoros "
                + pomodoros + " at date " + date);
        AddTask newTask = new AddTask(userID, pomodoros, taskName, isCollaborative, date, collaborators, Constants.add);
        newTask.execute();
    }

    private boolean isOKCollab(String collaborators) {
        if (collaborators.equals("")) {
            return true;
        }
        String[] collabList = collaborators.split(",");
        for (int i = 0; i < collabList.length; i++) {
            if (!isAlphanumeric(collabList[i])) {
                return false;
            }
        }
        return true;
    }

    public boolean isAlphanumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
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
        int userID;
        String pomodoros;
        String taskName;
        int isCollaborative;
        String date;
        String collaborators;
        String method;

        public AddTask(int userID, String pomodoros, String taskName, int isCollaborative, String date, String collab, String method) {
            this.userID = userID;
            this.pomodoros = pomodoros;
            this.taskName = taskName;
            this.isCollaborative = isCollaborative;
            this.date = date;
            this.method = method;
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
            ServiceHandler serviceHandler = new ServiceHandler();
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Constants.METHOD, method));
            nameValuePairs.add(new BasicNameValuePair(Constants.USER_ID, Integer.toString(userID)));
            nameValuePairs
                    .add(new BasicNameValuePair(Constants.NUM_OF_POMODOROS, pomodoros));
            nameValuePairs.add(new BasicNameValuePair(Constants.TASK_NAME, taskName));
            nameValuePairs.add(new BasicNameValuePair(Constants.END_TIME, date));
            nameValuePairs.add(new BasicNameValuePair(Constants.IS_COLLABORATIVE, Integer
                    .toString(isCollaborative)));
            if (isCollaborative == 1) {
                nameValuePairs.add(new BasicNameValuePair(Constants.COLLAB, collaborators));
            }
            String jsonStr = serviceHandler
                    .makeServiceCall(Constants.SERVER_ADDRESS, ServiceHandler.GET, nameValuePairs);
            System.err.println("Jsonstr: " + jsonStr);
            if (method.equals(Constants.getAlUsers)){
                try {
                    JSONObject data = new JSONObject(jsonStr);
                String users = data.getString(Constants.DATA);
                    System.out.println(users);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

                if (jsonStr.contains("true")){
                return true;
                }
            //PRoblem, den vill inte konvertera till JSON objekt trots att jag g�r EXAKT som i allTasks
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
        protected void onPostExecute(Boolean isSuccessful) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (isSuccessful) {
                System.err.println("Now go to listtasks");
                Intent intent = new Intent(NewTask.this, ListTasks.class);
                startActivity(intent);
            } else {
                System.err.println("Show warning dialog");
                new AlertDialog.Builder(NewTask.this)
                        .setMessage("It was not possible to save your task, maybe you miss fields or the internet" +
                                "connection is corrupt. Please try again")
                        .setNeutralButton("OK", null).show();
            }

        }

    }
}
