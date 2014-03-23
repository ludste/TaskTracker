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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<HashMap<String, String>>{
	ArrayList<HashMap<String, String>> objects;
	public CustomAdapter(Context context, ArrayList<HashMap<String, String>> objects) {
		super(context, R.layout.list_item, objects);
		this.objects=objects;
//		this.sort(null);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try{
		HashMap<String, String> task = objects.get(position);
		String isCompleted = task.get(Constants.IS_COMPLETED);
		 if (convertView == null) {
	          convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);
	     }
		 
		 TextView taskNameView = (TextView) convertView.findViewById(R.id.task_name);
		 TextView endDateView = (TextView) convertView.findViewById(R.id.end);
		 TextView PomView = (TextView) convertView.findViewById(R.id.pomodoros);
//		 ProgressBar progressView = (ProgressBar) convertView.findViewById(R.id.list_item_progressBar);
//		 ImageView iconDone = (ImageView) convertView.findViewById(R.id.list_item_IM_done);
		 
//		 String pom = task.get(Constants.NUM_OF_POMODOROS);
//		 String comp = task.get(Constants.NUM_COMPLETED_POMODOROS);
//		 System.out.println("pom and comp " + pom + " " + comp);
//		 progressView.setMax(Integer.parseInt(pom));
//		 progressView.setProgress(Integer.parseInt(comp));
		 
		 taskNameView.setText(task.get(Constants.TASK_NAME));
		 endDateView.setText(task.get(Constants.END_TIME));
		 PomView.setText(task.get(Constants.NUM_OF_POMODOROS));
//		 if(isCompleted.equals("1")){
//			 iconDone.setVisibility(View.VISIBLE);
//			 convertView.setBackgroundColor(Color.GRAY);
//			 endDateView.setText(task.get(Constants.COMPLETED_WHOLE_TASK_DATE));
//			 ((TextView) convertView.findViewById(R.id.end_text)).setText(R.string.done_date);
//		 }
		}catch(Exception e){
			e.printStackTrace();
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
