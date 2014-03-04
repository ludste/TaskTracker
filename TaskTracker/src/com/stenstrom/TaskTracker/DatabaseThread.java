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
