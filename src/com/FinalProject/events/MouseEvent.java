package com.FinalProject.events;

// As with Color, these events are pure data types, so like we'll forego encapsulation here
public class MouseEvent extends Event {
	// Types
	public static final int MOVE = 0;
	public static final int LEFTCLICK = 1;
	
	public int type;
	public int x;
	public int y;
	
	public int dx;
	public int dy;
	
	public MouseEvent(int type, int x, int y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}

	public int getType() {
		return Event.MOUSE;
	}
}
