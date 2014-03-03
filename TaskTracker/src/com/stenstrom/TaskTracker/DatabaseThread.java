package com.stenstrom.TaskTracker;

/**
 * Created by ludste on 2014-03-03.
 */
public class DatabaseThread implements Runnable {
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
