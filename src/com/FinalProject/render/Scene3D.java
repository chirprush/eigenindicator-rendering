package com.FinalProject.render;

import java.util.ArrayList;

import com.FinalProject.math.Vector;
import com.FinalProject.util.Color;
import com.FinalProject.util.TriangleIndex;

public class Scene3D {
	private ArrayList<Vector> points;
	private ArrayList<TriangleIndex> mesh;
	
	public Scene3D() {
		this.points = new ArrayList<>();
		this.mesh = new ArrayList<>();
	}
	
	public ArrayList<Vector> getPoints() {
		return this.points;
	}
	
	public ArrayList<TriangleIndex> getMesh() {
		return this.mesh;
	}
	
	public int addPoint(Vector point) {
		this.points.add(point);
		// System.out.println("Point " + (this.points.size() - 1) + ": " + point);
		return this.points.size() - 1;
	}
	
	public void addTri(TriangleIndex tri) {
		// System.out.println("Triangle: " + tri.i + " " + tri.j + " " + tri.k);
		this.mesh.add(tri);
	}
	
	// Indices must correspond to vertices that are in some (counterclockwise or clockwise) order.
	public void addQuad(int i, int j, int k, int l, Color color) {
		this.addTri(new TriangleIndex(i, j, k, color));
		this.addTri(new TriangleIndex(k, l, i, color));
	}
	
	// Colors the triangles based on their magnitude from the origin
	public void addQuadColorBlend(int i, int j, int k, int l, Color base, Color blend, double max) {
		Vector a = this.points.get(i);
		Vector b = this.points.get(j);
		Vector c = this.points.get(k);
		Vector d = this.points.get(l);
		
		double blend1 = 1 - a.add(b).add(c).mult(1.0/3).length() / max;
		double blend2 = 1 - c.add(d).add(a).mult(1.0/3).length() / max;
		
		Color color1 = base.blend(blend, blend1);
		Color color2 = base.blend(blend, blend2);
		
		this.addTri(new TriangleIndex(i, j, k, color1));
		this.addTri(new TriangleIndex(k, l, i, color2));
	}
}
