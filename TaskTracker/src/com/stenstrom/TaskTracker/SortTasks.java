package com.stenstrom.TaskTracker;

import java.util.Comparator;
import java.util.HashMap;

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
