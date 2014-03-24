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

import java.util.ArrayList;
import java.util.Calendar;

public class NewTask extends Activity implements DatePickerDialog.OnDateSetListener {
    int userID;
    Button buttonChangeDate;
    final Calendar c = Calendar.getInstance();
    int yearSelected;
    int monthSelected;
    int daySelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userID = getSharedPreferences(getString(R.string.preference_key_file), 0).getInt(Constants.USER_ID, -1);
        setContentView(R.layout.new_task);

        // If not selected, year, month and day will be today's date
        yearSelected = c.get(Calendar.YEAR);
        monthSelected = c.get(Calendar.MONTH);
        daySelected = c.get(Calendar.DAY_OF_MONTH);

        buttonChangeDate = (Button) findViewById(R.id.new_task_B_change_date);
        buttonChangeDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(v);
            }

        });

    }

    public void showDateDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    /*
     * Called when button "Send" is clicked.
     */
    public void sendToBackend(View view) {
    	
        EditText editName = (EditText) findViewById(R.id.new_task_ET_name);
        EditText editPomodoros = (EditText) findViewById(R.id.new_task_ET_pomodoros);
        String taskName = editName.getText().toString();
        String pomodoros = editPomodoros.getText().toString();
        String collaborators = ((EditText) findViewById(R.id.new_task_ET_choose_collab)).getText().toString();//Comma separated list
        
        if (taskName.isEmpty()) {
            new AlertDialog.Builder(NewTask.this)
                    .setMessage(getString(R.string.invalidTaskname))
                    .setNeutralButton("OK", null).show();
            return;
        }
        if (pomodoros.isEmpty() || Integer.parseInt(pomodoros)< 1) {
            new AlertDialog.Builder(NewTask.this)
                    .setMessage(getString(R.string.invalidPomodoros))
                    .setNeutralButton("OK", null).show();
            return;
        }
        if (!isOKCollab(collaborators)) {
            new AlertDialog.Builder(NewTask.this)
                    .setMessage(R.string.need_comma_separation)
                    .setNeutralButton("OK", null).show();
            return;
        }
        int isCollaborative = (collaborators.equals("") ? 0 : 1);
        String date = yearSelected + "-" + (monthSelected + 1) + "-" + daySelected;
        AddTask newTask = new AddTask(userID, pomodoros, taskName, isCollaborative, date, collaborators, Constants.add);
        newTask.execute();
    }

    private boolean isOKCollab(String collaborators) {
        if (collaborators.equals("")) {
            return true;
        }
        String[] collabList = collaborators.split(",");
        for (String collaborator : collabList) {
            if (!isAlphanumeric(collaborator)) {
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
            return jsonStr.contains("true");

        }

        @Override
        protected void onPostExecute(Boolean isSuccessful) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (isSuccessful) {
                Intent intent = new Intent(NewTask.this, ListTasks.class);
                startActivity(intent);
                finish();
            } else {
                new AlertDialog.Builder(NewTask.this)
                        .setMessage(getString(R.string.no_db_conn))
                        .setNeutralButton("OK", null).show();
            }

        }

    }
}
