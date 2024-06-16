package com.FinalProject;

import com.FinalProject.animation.Animation;
import com.FinalProject.util.Canvas;

public class Main {
	public static void main(String[] args) {
		Canvas canvas = new Canvas();
		Animation anim = new Animation(canvas);
		
		anim.run();
	}
}
