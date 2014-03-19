package com.stenstrom.TaskTracker;

/**
 * Created by ludste on 2014-03-03.
 */

public class DatabaseThread implements Runnable {
	public static String add = "addTask";
	public static String remove = "removeTask";
	public static String setDone = "setDone";
	public static String getTasks = "getUserTasks";
	public static String getAllTasks = "getAllTasks";
	public static String checkUserUniqueness = "testUserUniqueness";
	public static String checkMailUniqueness = "testMailUniqueness";
	
	public static final String METHOD = "method";
	public static final String STATUS = "status";
	public static final String DATA = "data";
	
	
	public static final String USER_ID = "userID";
	public static final String TASK_NAME = "name";
	public static final String START_TIME = "startDate";
	public static final String END_TIME = "endDate";
	public final String COMPLETED_WHOLE_TASK = "completionTime";
	public static final String FINNISH_TIME_LAST_POMODORO = "finnishTime";
	public static final String POMODOROS = "numOfPomodoros";
	public static final String IS_COLLABORATIVE = "collaborative";
	
    private static DatabaseThread testSingleton;
    private Thread runner;


    private DatabaseThread() {
        runner = new Thread(this, "DatabaseThread");
        runner.start();
    }



    public static DatabaseThread getInstance(){

        if(testSingleton ==null){
            testSingleton = new DatabaseThread();
        }
        return testSingleton;
    }



    @Override
    public void run() {
        System.out.println("Hello");
    }
}