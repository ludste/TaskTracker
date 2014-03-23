package com.stenstrom.TaskTracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SingleTask extends Activity {
    HashMap<String, String> contactMap;
    String collaborators;
    int userID;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userID = getSharedPreferences(getString(R.string.preference_key_file), 0).getInt(
                Constants.USER_ID, -1);
        try {

            setContentView(R.layout.single_task);

            String taskName;
            String endTime;
            String pomodoros;
            String completedPom;
            String isCollab;
            String completedByMe;
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                contactMap = (HashMap<String, String>) extras.get(Constants.CONTACT_MAP);
                taskName = contactMap.get(Constants.TASK_NAME);// extras.getString(Constants.TASK_NAME);
                endTime = contactMap.get(Constants.END_TIME);
                pomodoros = contactMap.get(Constants.NUM_OF_POMODOROS);
                completedPom = contactMap.get(Constants.NUM_COMPLETED_POMODOROS);
                isCollab = contactMap.get(Constants.IS_COLLABORATIVE);
                completedByMe = contactMap.get(Constants.OWN_POMODOROS);

                TextView nameView = (TextView) findViewById(R.id.task_name_s);
                TextView endView = (TextView) findViewById(R.id.end_s);
                TextView pomView = (TextView) findViewById(R.id.pomodoros_s);
                TextView completedView = (TextView) findViewById(R.id.pomodoros_comp_tot);
                TextView completedByMeView = (TextView) findViewById(R.id.pomodoros_comp_me);
                // TextView collabView =
                // (TextView)findViewById(R.id.collaborate);

                nameView.setText(taskName);
                endView.setText(endTime);
                pomView.setText(pomodoros);
                completedView.setText(completedPom);
                completedByMeView.setText(completedByMe);
                if (isCollab.equals("1")) {
                    ImageView i = (ImageView) findViewById(R.id.collab_image);
                    i.setVisibility(View.VISIBLE);

                    String taskID = contactMap.get(Constants.TASK_ID_DB);
                    SetWithDB dbConn = new SetWithDB(Constants.getCollab, taskID, null);
                    dbConn.execute().get();
                    ((TextView) findViewById(R.id.single_task_TV_shared_text)).setVisibility(View.VISIBLE);
                    TextView collab = (TextView) findViewById(R.id.single_task_TV_shared);
                    collab.setText(collaborators);
                    collab.setVisibility(View.VISIBLE);
//					TextView t = new TextView(this);
//					t.setText("these are the collaborators: " + collaborators);
//					LinearLayout singleView = (LinearLayout) findViewById(R.id.single_task);
//					singleView.addView(t);

                }


            }

        } catch (Exception e) {
            Log.e("Error", "Error in: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void registerPomodoro(View view) {
        SetWithDB dbConn = new SetWithDB(Constants.updatePomodoro,
                contactMap.get(Constants.TASK_ID_DB), Integer.toString(userID));
        dbConn.execute();
        Intent intent = new Intent(SingleTask.this, ListTasks.class);
        startActivity(intent);

    }

    public void setDone(View view) {
        SetWithDB dbConn = new SetWithDB(Constants.setDone, contactMap.get(Constants.TASK_ID_DB),
                Integer.toString(userID));
        dbConn.execute();
        Intent intent = new Intent(SingleTask.this, ListTasks.class);
        startActivity(intent);

    }

    public void remove(View view) {
        SetWithDB dbConn = new SetWithDB(Constants.remove, contactMap.get(Constants.TASK_ID_DB),
                Integer.toString(userID));
        dbConn.execute();
        Intent intent = new Intent(SingleTask.this, ListTasks.class);
        startActivity(intent);

    }

    public void startPomodoro(View view) {
        long time = 1000 * 7;
        toggleButtonsClickable(false);
        startTimer(time, Constants.TIME_POMODORO);
    }

    public void startLongBreak(View view) {
        long time = 1000 * 60 * 10;
        toggleButtonsClickable(false);

        startTimer(time, Constants.TIME_LONG);
    }

    public void startShortBreak(View view) {

        long time = 1000 * 60 * 5;
        toggleButtonsClickable(false);

        startTimer(time, Constants.TIME_SHORT);
    }

    public void startTimer(long time, final String type) {
        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.blackberry_gentle);
        final long millisGoal = System.currentTimeMillis() + time;
        final TextView timer = (TextView) findViewById(R.id.single_task_TV_pomodoro_clock);
        Handler timerHandler = new Handler();
        Runnable timerRunnable = new Runnable() {

            @Override
            public void run() {
                long millis = millisGoal - System.currentTimeMillis();
                if (millis <= 5000 && !mp.isPlaying()) {
                    mp.start();
                }
                if (millis <= 0) {
                    if (type.equals(Constants.TIME_POMODORO)) {
                        mp.stop();
                        registerPomodoro(null);
                    }
                    return;
                }
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                timer.setText(String.format("%d:%02d", minutes, seconds));

                timer.postDelayed(this, 500);
            }
        };
        timerHandler.postDelayed(timerRunnable, 0);

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

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SingleTask.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Constants.METHOD, method));
            nameValuePairs.add(new BasicNameValuePair(Constants.TASK_ID_DB, taskID));
            nameValuePairs.add(new BasicNameValuePair(Constants.USER_ID_DB, userID));
            ServiceHandler serviceHandler = new ServiceHandler();
            String jsonStr = serviceHandler
                    .makeServiceCall(url, ServiceHandler.GET, nameValuePairs);
            if (method.equals(Constants.getCollab)) {
                try {
                    JSONObject all = new JSONObject(jsonStr);
                    collaborators = all.getString("data");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (jsonStr.contains(method))
                return true;
            return false;

        }

        protected void onPostExecute(Boolean result) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (!method.equals(Constants.getCollab)) {
                if (result) {
                    new AlertDialog.Builder(SingleTask.this).setMessage("It has now been updated")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                } else {
                    System.err.println("Show warning dialog");
                    new AlertDialog.Builder(SingleTask.this)
                            .setMessage("Error in connection with database, please try again")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }
            }
        }

    }


    public void toggleButtonsClickable(boolean clickable) {
        Button pomodoro = (Button) findViewById(R.id.single_task_B_start_pomodoro);
        Button shortTime = (Button) findViewById(R.id.single_task_B_short_break);
        Button longTime = (Button) findViewById(R.id.single_task_B_long_break);
        pomodoro.setClickable(clickable);
        shortTime.setClickable(clickable);
        longTime.setClickable(clickable);
    }
}
