package com.stenstrom.TaskTracker;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListTasks extends ListActivity {
    ArrayList<HashMap<String, String>> allTasks;
    int userID;
    ArrayAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userID = getSharedPreferences(getString(R.string.preference_key_file), 0).getInt(Constants.USER_ID, -1);
        if (userID == -1) {
            Intent signin = new Intent(this, UserValidation.class);
            startActivity(signin);
        }
        setContentView(R.layout.list_tasks);
        allTasks = new ArrayList<HashMap<String, String>>();

        listView = getListView();
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                try {
                    Intent in = new Intent(ListTasks.this, SingleTask.class);
                    in.putExtra(Constants.CONTACT_MAP, allTasks.get((int) id));
                    startActivity(in);
                } catch (Exception e) {
                    Log.e("Error", "Error in: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        new GetTasks().execute(userID);
        //Now alltasks is populated
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            sync(null);

        }
    }

    public void newTask(View view) {
        Intent intent = new Intent(ListTasks.this, NewTask.class);
        startActivity(intent);
    }

    public void signOut(View view) {
        Editor edit = getSharedPreferences(getString(R.string.preference_key_file), 0).edit();
        edit.clear();
        edit.apply();
        Intent intent = new Intent(this, UserValidation.class);
        startActivity(intent);
        finish();
    }

    public void sync(View view) {
        allTasks.clear();
        adapter.notifyDataSetInvalidated();
        new GetTasks().execute(userID);
    }


    private class GetTasks extends AsyncTask<Integer, Void, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ListTasks.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(Integer... arg0) {
            int userID = arg0[0];
            ServiceHandler serviceHandler = new ServiceHandler();
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Constants.METHOD, Constants.getTasks));
            nameValuePairs.add(new BasicNameValuePair(Constants.USER_ID, Integer.toString(userID)));
            String jsonStr = serviceHandler.makeServiceCall(Constants.SERVER_ADDRESS, ServiceHandler.GET, nameValuePairs);
            try {
                JSONObject allResultJson = new JSONObject(jsonStr);
                String statusCode = allResultJson.getString(Constants.STATUS);
                if (statusCode.equals(Constants.getTasks)) {
                    JSONArray jArray = allResultJson.getJSONArray(Constants.DATA);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json = jArray.getJSONObject(i);
                        //Read all values from JSON response and put in HashMap
                        allTasks.add(createTask(json));
                    }
                }
            } catch (Exception e) {
                Log.e("log_tag", "Error Parsing Data " + e.toString());
            }
            return jsonStr;
        }

        protected void onPostExecute(String jsonStr) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            String statusCode;
            try {
                JSONObject allResultJson = new JSONObject(jsonStr);
                statusCode = allResultJson.getString(Constants.STATUS);
                if (statusCode.equals(Constants.getTasks)) {
                    adapter = new CustomAdapter(ListTasks.this, allTasks);
                    setListAdapter(adapter);
                } else {
                    new AlertDialog.Builder(ListTasks.this).setMessage(R.string.failed_to_load_tasks)
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private HashMap<String, String> createTask(JSONObject json) throws JSONException {
        HashMap<String, String> task = new HashMap<String, String>();
        task.put(Constants.TASK_NAME, json.getString(Constants.TASK_NAME));
        task.put(Constants.TASK_ID_DB, json.getString(Constants.TASK_ID_DB));
        task.put(Constants.USER_ID_DB, json.getString(Constants.USER_ID_DB));
        task.put(Constants.END_TIME, json.getString(Constants.END_TIME));
        task.put(Constants.NUM_OF_POMODOROS, json.getString(Constants.NUM_OF_POMODOROS));
        task.put(Constants.IS_COLLABORATIVE, json.getString(Constants.IS_COLLABORATIVE));
        task.put(Constants.NUM_COMPLETED_POMODOROS, json.getString(Constants.NUM_COMPLETED_POMODOROS));
        task.put(Constants.OWN_POMODOROS, json.getString(Constants.OWN_POMODOROS));
        task.put(Constants.COMPLETED_WHOLE_TASK_DATE, json.getString(Constants.COMPLETED_WHOLE_TASK_DATE));
        task.put(Constants.IS_COMPLETED, json.getString(Constants.IS_COMPLETED));
        return task;
    }
}
