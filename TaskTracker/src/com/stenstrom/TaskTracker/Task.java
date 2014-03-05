package com.stenstrom.TaskTracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Task {
	public String taskID;
	public String name;
	public String startDate;
	public String endDate;
	public String completionTime;
	public String numOfPomodoros;
	public String collaborative;
	
	public final static String DATE_FORMAT = "yyyy-MM-dd";
	public final static Date ZERO_DATE = new Date(0);
	
	public Task(){
		
	}
	public Task(String taskID, String name, String startDate, String endDate, String completionTime, 
			String numOfPomodoros, String collaborative) {
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.completionTime = completionTime;
		this.numOfPomodoros = numOfPomodoros;
		this.collaborative = collaborative;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getCompletionTime() {
		return completionTime;
	}
	public void setCompletionTime(String completionTime) {
		this.completionTime = completionTime;
	}
	public String getNumOfPomodoros() {
		return numOfPomodoros;
	}
	public void setNumOfPomodoros(String numOfPomodoros) {
		this.numOfPomodoros = numOfPomodoros;
	}
	public boolean isCollaborative(){
		if(collaborative.equals("1") || collaborative.equals("0")){
			return collaborative.equals("1");
		}
		else{
			System.err.println("Iscollaborative in Task is corrupt");
		}
		return false;
	}
	public boolean isCompleted(){
		Date date = new Date(0);
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			date = dateFormat.parse(completionTime);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return !date.equals(ZERO_DATE);
	}
	
}
