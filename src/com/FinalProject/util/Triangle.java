package com.FinalProject.util;

import java.util.ArrayList;

import com.FinalProject.math.Vector;

// 2D Triangles meant for rendering
public class Triangle {
	private Vector a;
	private Vector b;
	private Vector c;
	
	private double distIndex;
	
	private Color color;
	
	// TODO: constructor
	public Triangle(Vector a, Vector b, Vector c, double distIndex, Color color) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.distIndex = distIndex;
		this.color = color;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public double getDist() {
		return this.distIndex;
	}
	
	public ArrayList<Vector> getVertices() {
		ArrayList<Vector> vertices = new ArrayList<>();
		
		vertices.add(a);
		vertices.add(b);
		vertices.add(c);
		
		return vertices;
	}
}
