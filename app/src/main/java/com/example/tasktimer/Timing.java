package com.example.tasktimer;
* Simple Timing object.
* Sets its start time when created and calculates how long since creation.
* when setDuration is called



import android.util.Log;

import java.io.Serializable;
import java.util.Date;

class Timing implements Serializable {
    public static final long serialVersionId = 20161120L;
    public static final String TAG = Timing.class.getSimpleName();

    private long m_Id;
    private Task mTask;
    private long mStartTime;
    private long mDuration;

    public Timing(Task task) {
        mTask = task;
        // Initialise the start time to now and the duration to zero for a new object.
        Date currentTime = new Date();
        mStartTime = currentTime.getTime() / 1000; // we are only tracking whole seconds, not milliseconds
        mDuration = 0;
    }

 public static long getSerialVersionId() {
        return serialVersionId;
    }

    public static String getTAG() {
        return TAG;
    }



   long getId() {
        return m_Id;
    }

    void setId(long id) {
        m_Id = id;
    }

    long getDuration() {
        return mDuration;
    }

    void setDuration() {
       // Calculate the duration from mStartTime to dateTime
        Date currentTime = new Date();
        mDuration = (currentTime.getTime() / 1000) - mStartTime; //working in seconds, not milliseconds
        Log.d(TAG, mTask.getid() +  " - Start time: " + mStartTime + " | Duration: " + mDuration);
    }

    Task getTask() {
        return mTask;
    }

    void setTask(Task task) {
        mTask = task;
    }

    long getStartTime() {
        return mStartTime;
    }

    void setStartTime(long startTime) {
        mStartTime = startTime;
    }
}
