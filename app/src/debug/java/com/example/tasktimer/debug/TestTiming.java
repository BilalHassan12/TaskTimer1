package com.example.tasktimer.debug;

import java.util.Date;

public class TestTiming {

    long taskId;
    long startTime;
    long duration;

    public TestTiming(long taskId, Date startTime, long duration) {
        this.taskId = taskId;
        //this.startTime = startTime / 1000; // store seconds, not milliseconds
        this.duration = duration;
    }

    public TestTiming(long taskId, long randomDate, long duration) {
    }
}
