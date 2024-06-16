package com.FinalProject.util;

public class TriangleIndex {
	public int i;
	public int j;
	public int k;
	
	private Color color;
	
	public TriangleIndex(int i, int j, int k, Color color) {
		this.i = i;
		this.j = j;
		this.k = k;
		
		this.color = color;
	}
	
	public Color getColor() {
		return this.color;
	}
}
