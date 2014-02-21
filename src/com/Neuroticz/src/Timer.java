package com.Neuroticz.src;

public class Timer {
	long start;
	long end;
	public Timer() {}
	public void start() {
		start = System.currentTimeMillis();
	}
	public void end() {
		end = System.currentTimeMillis();
	}
//	public void reset() {
//		end = 0;
//		start = 0;
//	}
	public long getElapsedTimeMilis() {
		return end-start;
	}
}
