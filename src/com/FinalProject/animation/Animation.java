package com.FinalProject.animation;

import com.FinalProject.events.Event;
import com.FinalProject.events.KeyEvent;
import com.FinalProject.events.MouseEvent;
import com.FinalProject.util.Canvas;
import com.FinalProject.util.Color;

public class Animation {
	private Canvas canvas;

	private IntroAnimation intro;
	private SceneAnimation scene;
	
	public static final int FPS = 60;
	
	public Animation(Canvas canvas) {
		this.canvas = canvas;
		
		this.intro = new IntroAnimation(1 * Animation.FPS);
		this.scene = new SceneAnimation();
	}
	
	public void run() {
		while (this.canvas.isRunning()) {
			this.canvas.setDrawColor(Canvas.BG);
			
			this.canvas.clear();
			
			// System.out.println("Size: " + this.canvas.getWidth() + "x" + this.canvas.getHeight());
			
			for (Event e : this.canvas.getEvents()) {
				switch (e.getType()) {
				case Event.KEY:
					KeyEvent ke = (KeyEvent) e;
					// System.out.println("Key Event { type: " + ke.type + ", key: " + ke.key + " }");
					
					if (!this.intro.isFinished()) {
						this.intro.onKeyEvent(ke);
					} else {
						this.scene.onKeyEvent(ke);
					}
					break;
				case Event.MOUSE:
					MouseEvent me = (MouseEvent) e;
					// System.out.println("Key Event { type: " + me.type + ", x: " + me.x + ", y: " + me.y + " }");
					
					if (!this.intro.isFinished()) {
						this.intro.onMouseEvent(me);
					} else {
						this.scene.onMouseEvent(this.canvas, me);
					}
					break;
				}
			}
			
			if (!this.intro.isFinished()) {
				this.intro.render(this.canvas);
			} else {
				this.scene.render(this.canvas);
			}
			
			this.canvas.present();
			
			this.canvas.delay(1000 / Animation.FPS);
		}
	}
}
