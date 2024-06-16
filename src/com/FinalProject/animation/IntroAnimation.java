package com.FinalProject.animation;

import com.FinalProject.events.KeyEvent;
import com.FinalProject.events.MouseEvent;
import com.FinalProject.util.Canvas;
import com.FinalProject.util.Color;

public class IntroAnimation {
	private int fadeLength;
	private int fadeStart;
	private int frame;
	
	private int state;
	
	public final static int SHOW = 0;
	public final static int FADE = 1;
	public final static int DONE = 2;
	
	public IntroAnimation(int fadeLength) {
		this.fadeLength = fadeLength;
		
		this.frame = 0;
		this.fadeStart = frame;
		
		this.state = IntroAnimation.SHOW;
	}
	
	public void nextState() {
		if (this.state == IntroAnimation.DONE) { return; }
		
		this.state++;
		
		if (this.state == IntroAnimation.FADE) {
			this.fadeStart = this.frame;
		}
	}
	
	public boolean isFinished() {
		return this.state == IntroAnimation.DONE;
	}
	
	public void onKeyEvent(KeyEvent e) {
		// Probably not useful here but meh
	}
	
	public void onMouseEvent(MouseEvent e) {
		if (e.type == MouseEvent.LEFTCLICK) {
			this.nextState();
		}
	}
	
	private double f(double x, int a) {
		return Math.sin((double) a / 50) * Math.sin(2 * Math.PI * x);
	}
	
	public void render(Canvas canvas) {
		double alphaMultiplier = 1.0;
		
		if (this.state == IntroAnimation.FADE) {
			alphaMultiplier = 1.0 - (double) (this.frame - this.fadeStart) / this.fadeLength;
		}
		
		int segments = 100;
		double spacing = (double) canvas.getWidth() / segments;
		int[] xs = new int[segments + 1];
		int[] ys = new int[segments + 1];
		
		// Adapted from an animation I previously made in Javascript:
		// https://github.com/chirprush/animations/blob/master/index.js
		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 6; k++) {
				for (int i = 0; i <= segments; i++) {
					int canvasX = (int) (i * spacing);
					double funcX = ((double) canvasX / canvas.getWidth() - 0.5) * 2 + Math.PI * j;
					double funcY = 1 / Math.sqrt(k + 1) * f(funcX, 2 * this.frame + 20 * j);
					int canvasY = canvas.getHeight() / 2 - (int) (canvas.getHeight() / 5 * funcY);
					
					xs[i] = canvasX;
					ys[i] = canvasY;
				}
				
				canvas.setDrawColor(new Color(
					0xff, 0xff, 0xff,
					Math.max(0, (int) (alphaMultiplier * (255 - 50 * k)))
				));
				
				for (int i = 1; i <= segments; i++) {
					canvas.drawThickLine(xs[i-1] + 1, ys[i-1], xs[i], ys[i]);
				}
			}
		}
		
		if (this.state == IntroAnimation.FADE) {
			canvas.setDrawColor(new Color(
				255, 255, 255,
				(int) (alphaMultiplier * 127)
			));
			
			int loadingBarHeight = 10;
			
			int width = (int) ((double) (this.frame - this.fadeStart) / (this.fadeLength) * canvas.getWidth());
			
			canvas.drawRectangle(0, canvas.getHeight() - loadingBarHeight, width, loadingBarHeight);
		}
		
		this.frame++;
		
		if (this.state == IntroAnimation.FADE && this.frame > this.fadeStart + this.fadeLength) {
			this.nextState();
		}
	}
}
