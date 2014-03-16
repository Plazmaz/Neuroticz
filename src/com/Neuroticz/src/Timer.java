package com.Neuroticz.src;

public class Timer {
	long start;
	long end;

	/**
	 * Start the timer
	 */
	public void start() {
		start = System.currentTimeMillis();
	}

	/**
	 * End the timer
	 */
	public void end() {
		end = System.currentTimeMillis();
	}

	/**
	 * Determines the total time that has passed since the Timer was started
	 * 
	 * @return the total time passed in milliseconds
	 */
	public long getElapsedTimeMilis() {
		return end - start;
	}
}
