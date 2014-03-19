package com.stenstrom.TaskTracker;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ListTasks extends ListActivity{
	ArrayList<HashMap<String,String>> allTasks;
	int userID =1; //TODO change this to look at userfile
	@Override
    protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        setContentView(R.layout.list_tasks);
        allTasks = new ArrayList<HashMap<String, String>>();
        ListView listView = getListView();
        
       
        
        listView.setOnItemClickListener(new OnItemClickListener() {
        			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                int position, long id){
				try{
        	String name = ((TextView) view.findViewById(R.id.task_name)).getText().toString();
        	String end = ((TextView) view.findViewById(R.id.end)).getText().toString();
        	String pomo = ((TextView) view.findViewById(R.id.pomodoros)).getText().toString();
        	Intent in = new Intent(ListTasks.this, SingleTask.class);
        	in.putExtra(Constants.TASK_NAME, name);
        	in.putExtra(Constants.END_TIME, end);
        	in.putExtra(Constants.NUM_OF_POMODOROS, pomo);
        	startActivity(in);
				}
				catch(Exception e){
					Log.e("Error", "Error in: " +e.getMessage());
					e.printStackTrace();
				}
			}
        });
        
	new GetTasks().execute(userID);
	//Now alltasks is populated
	}
	public void sendMessage(View view){
		Intent intent = new Intent(ListTasks.this, NewTask.class);
		System.out.println("go to new ");
		startActivity(intent);
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
					JSONObject json = jArray.getJSONObject(i);
//					Task task;
//					task = new Task(json.getString(Constants.TASK_ID_DB),
//							json.getString(Constants.TASK_NAME),
//							json.getString(Constants.START_TIME),
//							json.getString(Constants.END_TIME),
//							json.getString(Constants.COMPLETED_WHOLE_TASK),
//							json.getString(Constants.NUM_OF_POMODOROS),
//							json.getString(Constants.IS_COLLABORATIVE));
					HashMap<String, String> task = new HashMap<String, String>();
					task.put(Constants.TASK_NAME, json.getString(Constants.TASK_NAME));
					task.put(Constants.END_TIME, json.getString(Constants.END_TIME));
					task.put(Constants.NUM_OF_POMODOROS, json.getString(Constants.NUM_OF_POMODOROS));
					Boolean isCompleted = json.getString(Constants.NUM_COMPLETED_POMODOROS).equals(
							json.getString(Constants.NUM_OF_POMODOROS));
					String completedText = (isCompleted?"completed": "not completed");
					task.put(Constants.IS_COMPLETED, completedText);
					
					allTasks.add(task);
				}
			}
			System.err.println("All tasks added");
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("log_tag", "Error Parsing Data " + e.toString());
		}
		return jsonStr;
	}
	
	protected void onPostExecute(String jsonStr){
		String statusCode;
		TextView statusCodeView = (TextView) findViewById(R.id.Tasks);
		try {
			JSONObject allResultJson = new JSONObject(jsonStr);
			statusCode = allResultJson.getString("status");
			if (statusCode.equals(Constants.getTasks)) {
			//TODO show "could not reach task database" if not ok
			ListAdapter adapter = new SimpleAdapter(ListTasks.this, allTasks, 
					R.layout.list_item, 
					new String[]{
					Constants.TASK_NAME, Constants.END_TIME, Constants.NUM_OF_POMODOROS, Constants.IS_COMPLETED
			}, new int[]{
				R.id.task_name, R.id.end, R.id.pomodoros, R.id.is_completed	
			});
			setListAdapter(adapter);
			}
			else{
				statusCodeView.setText("Could not get info from db, error: "
								+ statusCode);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			statusCodeView.append("Problem with " + e.getMessage());
		}
	}
}
}
