package com.FinalProject.events;

// As with Color, these events are pure data types, so like we'll forego encapsulation here
public class KeyEvent extends Event {
	// Types
	public static final int PRESS = 0;
	public static final int RELEASE = 1;
	
	public int type;
	public String key;
	
	public KeyEvent(int type, String key) {
		this.type = type;
		this.key = key;
	}

	public int getType() {
		return Event.KEY;
	}
}
