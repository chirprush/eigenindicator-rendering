package com.FinalProject.events;

public abstract class Event {
	// Event types
	public static final int MOUSE = 0;
	public static final int KEY = 1;
	
	public abstract int getType();
}
