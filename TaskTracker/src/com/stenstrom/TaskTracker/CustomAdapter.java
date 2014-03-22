package com.stenstrom.TaskTracker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<HashMap<String, String>>{
	ArrayList<HashMap<String, String>> objects;
	public CustomAdapter(Context context, ArrayList<HashMap<String, String>> objects) {
		super(context, R.layout.list_item, objects);
		this.objects=objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HashMap<String, String> task = objects.get(position);
		String isCompleted = task.get(Constants.IS_COMPLETED);
		 if (convertView == null) {
	          convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);
	     }
		 
		 TextView taskNameView = (TextView) convertView.findViewById(R.id.task_name);
		 TextView endDateView = (TextView) convertView.findViewById(R.id.end);
		 TextView PomView = (TextView) convertView.findViewById(R.id.pomodoros);
		 
		 taskNameView.setText(task.get(Constants.TASK_NAME));
		 endDateView.setText(task.get(Constants.END_TIME));
		 PomView.setText(task.get(Constants.NUM_OF_POMODOROS));
		 System.out.println("task name " + task.get(Constants.TASK_NAME) + " completed :"+ isCompleted);
		 convertView.setBackgroundColor(Color.BLACK);
		 if(isCompleted.equals("1")){
			 convertView.setBackgroundColor(Color.GRAY);
		 }
		 
		 return convertView;
		
	}
	
	@Override
	public void sort(Comparator<? super HashMap<String, String>> comparator) {
		super.sort(new SortTasks());
	}
	public class SortTasks implements Comparator<HashMap<String, String>>{

		@Override
		public int compare(HashMap<String, String> obj1, HashMap<String, String> obj2) {
			String iscompleted1 = obj1.get(Constants.IS_COMPLETED);
			String iscompleted2 = obj2.get(Constants.IS_COMPLETED);
			if(iscompleted1.equals(iscompleted2)){
				return 0;
			}
			if(iscompleted1.equals("0")){
				return -1;
			}
			return 1;
		}
		
	}

}
