package com.FinalProject.util;

// This is really just a pure data type so like writing out encapsulation code would really be boilerplate for no reason in my opinion.
public class Color {
	public int r;
	public int g;
	public int b;
	public int a;
	
	/*
	public Color(byte r, byte g, byte b, byte a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	*/
	
	// Trust
	public Color(int r, int g, int b, int a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public Color blend(Color other, double alpha) {
		return new Color(
			(int) (alpha * this.r + (1-alpha) * other.r),
			(int) (alpha * this.g + (1-alpha) * other.g),
			(int) (alpha * this.b + (1-alpha) * other.b),
			(int) (alpha * this.a + (1-alpha) * other.a)
		);
	}
	
	public String toString() {
		return "Color(" + this.r + ", " + this.g + ", " + this.b + ", " + this.a + ")";
	}
}
