package com.Neuroticz.src;

public class Timer {
    long start;
    long end;

    public Timer() {
    }
    /**
     * 
     */
    public void start() {
	start = System.currentTimeMillis();
    }
    /**
     * 
     */
    public void end() {
	end = System.currentTimeMillis();
    }

    /**
     * Determines the total time that has passed since the Timer was started
     * @return the total time passed in milliseconds
     */
    //TODO: Can we convert this to seconds/minutes/hours somewhere?
    public long getElapsedTimeMilis() {
	return end - start;
    }
}
