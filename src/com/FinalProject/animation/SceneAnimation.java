package com.FinalProject.animation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import com.FinalProject.events.KeyEvent;
import com.FinalProject.events.MouseEvent;
import com.FinalProject.math.Matrix;
import com.FinalProject.math.Vector;
import com.FinalProject.render.Camera;
import com.FinalProject.render.Scene3D;
import com.FinalProject.util.Canvas;
import com.FinalProject.util.Color;
import com.FinalProject.util.Triangle;
import com.FinalProject.util.TriangleIndex;

public class SceneAnimation {
	private Scene3D scene;
	private Camera camera;
	
	private Matrix transform;
	
	private boolean drawMesh;
	
	private class Direction {
		public static final int UP = 0;
		public static final int DOWN = 1;
		public static final int RIGHT = 2;
		public static final int LEFT = 3;
		public static final int FORWARD = 4;
		public static final int BACK = 5;
		
		public static Vector toVector(Camera camera, int dir) {
			switch (dir) {
			case UP:
				return camera.getUp();
			case DOWN:
				return camera.getDown();
			case RIGHT:
				return camera.getRight();
			case LEFT:
				return camera.getLeft();
			case FORWARD:
				return camera.getForward();
			case BACK:
				return camera.getBackward();
			}
			return new Vector(3);
		}
	}
	
	private HashSet<Integer> cameraDirections;
	
	private static final double fogDist = 10.0;
	private static final double cameraSpeed = 2.0;
	
	private static final int numPoints = 30;
	private static final double pointSep = 2.0 / (numPoints - 1);
	
	public SceneAnimation() {
		this.scene = new Scene3D();
		this.camera = new Camera();
		
		double[] cameraPos = {-3, 0, 0};
		this.camera.move(new Vector(cameraPos));
		
		this.cameraDirections = new HashSet<>();
		
		double[][] transformData = new double[3][3];
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				transformData[i][j] = Math.random();
			}
		}
		
		this.transform = new Matrix(transformData);
		
		this.drawMesh = false;
		
		/*
		double d = 1;
		
		double[][] points = {
			{1 + d, 0, 0},
			{1 + d, -1, 2},
			{1 + d, -1, -1},
			{0.5 + d, 0.5, 0},
			{0.5 + d, -0.5, 1},
			{0.5 + d, -0.5, -2}
		};
		
		for (int i = 0; i < points.length; i++) {
			this.scene.addPoint(new Vector(points[i]));
		}
		
		this.scene.addTri(new TriangleIndex(0, 1, 2, new Color(255, 255, 255, 255)));
		this.scene.addTri(new TriangleIndex(3, 4, 5, new Color(0, 0, 255, 255)));
		*/
		
		this.prepScene();
	}
	
	public int toIndex(int x, int y, int z) {
		return x + numPoints * y + numPoints * numPoints * z;
	}
	
	// Creates a giant cube mesh and then normalizes all the vectors to create a sphere
	public void prepScene() {
		// We define the top left corner of the cube to be the origin perhaps
		// (that is, positive z, positive x, positive y)
		// and we work our way down to negative, negative, negative
		
		// The point in the top left corner in terms of 3D space is given by (1, 1, 1) I suppose
		
		// Perhaps a waste of vertices but like it's not fun to think about what indices correspond to what points
		// if we don't fill everything in and give it a basis.
		double maxMagnitude = 0.0;
		
		for (int z = 0; z < numPoints; z++) {
			for (int y = 0; y < numPoints; y++) {
				for (int x = 0; x < numPoints; x++) {
					double[] data = {1 - x * pointSep, 1 - y * pointSep, 1 - z * pointSep};
					Vector v = new Vector(data).normalize();					
					// Transform the vector here
					// ...
					Vector transformed = this.transform.mult(v).toVector();
					// v = transformed;
					v = v.mult(Math.pow(transformed.dot(v) / (transformed.length() * v.length()), 2));
					
					maxMagnitude = Math.max(maxMagnitude, v.length());
					this.scene.addPoint(v);
				}
			}
		}
		
		// So like the integer tuple (x, y, z) corresponds uniquely to the index x + numPoints * y + numPoints^2 * z
		
		// There are 6 faces we have to consider so we shall do the inner points and that luckily covers literally everything
				
		// We should expect 12 * (numPoints - 1)^2 triangles so hopefully that is the case
		// Looking at the loops and using the fact that a quadrilateral is decomposed into 2 triangles,
		// this checks out, which is great for my sanity
		
		Color baseColor = new Color(0xe0, 0x6c, 0x75, 0xff);
		Color blendColor = new Color(0xff, 0xff, 0xff, 0xff);
		
		for (int i = 0; i < numPoints - 1; i++) {
			for (int j = 0; j < numPoints - 1; j++) {
				for (int place = 0; place <= numPoints - 1; place += numPoints - 1) {					
					// Top and bottom
					this.scene.addQuadColorBlend(
						this.toIndex(i, j, place),
						this.toIndex(i+1, j, place),
						this.toIndex(i+1, j+1, place),
						this.toIndex(i, j+1, place),
						baseColor, blendColor, maxMagnitude
					);
					
					// Front and back faces
					this.scene.addQuadColorBlend(
						this.toIndex(place, i, j),
						this.toIndex(place, i+1, j),
						this.toIndex(place, i+1, j+1),
						this.toIndex(place, i, j+1),
						baseColor, blendColor, maxMagnitude
					);
					
					// Right and left faces
					this.scene.addQuadColorBlend(
						this.toIndex(i, place, j),
						this.toIndex(i+1, place, j),
						this.toIndex(i+1, place, j+1),
						this.toIndex(i, place, j+1),
						baseColor, blendColor, maxMagnitude
					);
				}				
			}
		}
		
		// Lesson learned: if I'm gonna do 4d, it's probably gonna be a cube and not a sphere bro
	}
	
	public void render(Canvas canvas) {		
		ArrayList<Triangle> tris = this.camera.project(this.scene);
		
		
		double aspectRatio = (double) canvas.getHeight() / canvas.getWidth();
		this.camera.setPlaneWidth(2.0);
		this.camera.setPlaneHeight(2.0 * aspectRatio);

		// this.camera.debug();
		
		// Sort reversed by distIndex
		Collections.sort(tris, (tri1, tri2) -> Double.compare(tri2.getDist(), tri1.getDist()));
		
		for (Triangle tri : tris) {
			double fogEffect = 1 - 2 / Math.PI * Math.atan(tri.getDist() / SceneAnimation.fogDist);
			// System.out.println("Fog: " + fogEffect);
			
			// SDL_RenderGeometery doesn't use the render draw color so erm
			// canvas.setDrawColor(tri.getColor().blend(Canvas.BG, fogEffect));
			
			canvas.drawTriangle(tri, tri.getColor().blend(Canvas.BG, fogEffect), this.drawMesh);
		}
		
		// Move camera according to velocity
		double dt = (double) 1 / Animation.FPS;
		Vector velocity = new Vector(3);
		
		for (Integer dir : this.cameraDirections) {
			velocity = velocity.add(Direction.toVector(this.camera, dir));
		}
		
		double length = velocity.length();
		
		if (length == 0) { return; }
		
		this.camera.move(velocity.mult(dt * SceneAnimation.cameraSpeed / length));
	}
	
	public void onMouseEvent(Canvas canvas, MouseEvent e) {
		if (e.type == MouseEvent.MOVE) {
			double dPolar = (double) e.dx / canvas.getWidth() * 2 * Math.PI;
			double dAzimuthal = (double) e.dy / canvas.getHeight() * Math.PI;
			
			this.camera.addPolar(dPolar);
			this.camera.addAzimuthal(dAzimuthal);
		}
	}
	
	public void onKeyEvent(KeyEvent e) {
		int dir = -1;
		switch (e.key) {
		case "W":
			dir = Direction.FORWARD;
			break;
		case "A":
			dir = Direction.LEFT;
			break;
		case "S":
			dir = Direction.BACK;
			break;
		case "D":
			dir = Direction.RIGHT;
			break;
		case "Space":
			dir = Direction.UP;
			break;
		case "Left Shift":
			dir = Direction.DOWN;
			break;
		case "M":
			if (e.type == KeyEvent.PRESS) { this.drawMesh = !this.drawMesh; }
			break;
		}
		
		if (e.type == KeyEvent.PRESS) {
			this.cameraDirections.add(dir);
		} else {
			this.cameraDirections.remove(dir);
		}
	}
}
