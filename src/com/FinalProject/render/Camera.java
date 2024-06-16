package com.FinalProject.render;

import java.util.ArrayList;

import com.FinalProject.math.Matrix;
import com.FinalProject.math.Vector;
import com.FinalProject.util.Triangle;
import com.FinalProject.util.TriangleIndex;

public class Camera {
	private Vector pos;
	
	// Becase the z-axis denotes height, we want the polarAngle (determines the x,y direction) to go from [-pi, pi)
	// and we want the azimuthal angle to lie in [-pi/2, pi/2]
	
	// The polarAngle measures the clockwise angle from the x-axis.
	private double polarAngle;
	
	// The azimuthal angle measures the angle from the xy plane where a positive angle denotes moving toward the -z axis (so looking down)
	private double azimuthalAngle;
	
	private double planeDistance;
	
	// View plane options
	private double planeWidth;
	private double planeHeight;
	
	public Camera() {
		this.pos = new Vector(3);
		
		this.polarAngle = 0.0;
		this.azimuthalAngle = 0.0;
		
		this.planeDistance = 0.5;
		
		// These should probably definitely be set later
		this.planeWidth = 1.0;
		this.planeHeight = 1.0;
	}
	
	// Probably make dimensions proportional to like the window ratio
	public void setPlaneWidth(double width) {
		this.planeWidth = width;
	}
	
	public void setPlaneHeight(double height) {
		this.planeHeight = height;
	}
	
	public Vector getCameraPos() {
		return this.pos;
	}
	
	public Vector getCameraDir() {
		return Vector.fromSpherical(this.polarAngle, this.azimuthalAngle);
	}
	
	public Vector getPlaneCenter() {
		return this.getCameraPos().add(this.getCameraDir().mult(this.planeDistance));
	}
	
	public void move(Vector v) {
		this.pos = this.pos.add(v);
	}
	
	public void addPolar(double polar) {
		this.polarAngle += polar;
	}
	
	public void addAzimuthal(double azimuthal) {
		this.azimuthalAngle += azimuthal;
	}
	
	public Vector getUp() {
		return Vector.fromSpherical(this.polarAngle, this.azimuthalAngle - Math.PI / 2);
	}
	
	public Vector getDown() {
		return Vector.fromSpherical(this.polarAngle, this.azimuthalAngle + Math.PI / 2);
	}
	
	public Vector getRight() {
		return Vector.fromSpherical(this.polarAngle + Math.PI / 2, this.azimuthalAngle);
	}
	
	public Vector getLeft() {
		return Vector.fromSpherical(this.polarAngle - Math.PI / 2, this.azimuthalAngle);
	}
	
	public Vector getForward() {
		return this.getCameraDir();
	}
	
	public Vector getBackward() {
		return this.getCameraDir().mult(-1);
	}
	
	// Projects a point in space onto the viewing plane
	// Note: can return null in the case that the projected point does not lie in the viewing subspace
	// (that is, either the point can be projected onto the plane or it can be projected but it lies behind the camera)
	public Vector projectPoint(Vector point) {
		Vector q = this.getPlaneCenter();
		Vector n = this.getCameraDir();
		
		// Replace these with dot products later I suppose
		double numeratorSum = n.dot(q.sub(this.getCameraPos()));
		double denominatorSum = n.dot(point.sub(this.getCameraPos()));
		
		// Floating point equality is kinda sus but
		if (denominatorSum == 0) {
			return null;
		}
		
		double t = numeratorSum / denominatorSum;
		
		// If points are behind viewing space, we don't want to place them on the viewing plane
		if (t < 0) {
			return null;
		}
		
		Vector projected = this.getCameraPos().mult(1 - t).add(point.mult(t));
		Vector translatedProjected = projected.sub(q);
		
		// Hmmmmmmm if we add stuff to the angles it doesn't stay in the same bounds how do we want to address this?
		// I suppose it doesn't really matter though?
		Vector basisDown = Vector.fromSpherical(this.polarAngle, this.azimuthalAngle + Math.PI / 2);
		Vector basisRight = Vector.fromSpherical(this.polarAngle + Math.PI / 2, this.azimuthalAngle);

		// Do we really have to implement matrix inverses :sob:
		// Okay trust it's only like 2x2 inverses not 3x3 like I was worried about
		
		double[][] basisData = {
			{basisRight.comp(0), basisDown.comp(0)},
			{basisRight.comp(1), basisDown.comp(1)},
			{basisRight.comp(2), basisDown.comp(2)}
		};
		
		Matrix A = new Matrix(basisData);
		
		Matrix result = (A.transpose().mult(A)).inv().mult(A.transpose()).mult(translatedProjected);
		
		double xComponent = result.get(0, 0);
		double yComponent = result.get(1, 0);
		
		// Outside viewing plane
		if (Math.abs(xComponent) > this.planeWidth / 2 || Math.abs(yComponent) > this.planeHeight / 2) {
			return null;
		}
		
		// Map to [0, 1]^2
		double[] projectionData = {
			(xComponent + this.planeWidth / 2) / this.planeWidth,
			(yComponent + this.planeHeight / 2) / this.planeHeight
		};
		
		return new Vector(projectionData);
	}
	
	// Perhaps make a more general like abstract class for Scenes
	// so that I can generalize this to 4D?
	public ArrayList<Triangle> project(Scene3D scene) {
		Vector[] points = new Vector[scene.getPoints().size()];
		ArrayList<Vector> scenePoints = scene.getPoints();
		ArrayList<Triangle> tris = new ArrayList<>();
		
		for (int i = 0; i < scene.getPoints().size(); i++) {
			points[i] = this.projectPoint(scene.getPoints().get(i));
		}
		
		for (int i = 0; i < scene.getMesh().size(); i++) {
			TriangleIndex tri = scene.getMesh().get(i);
			
			if (points[tri.i] == null || points[tri.j] == null || points[tri.k] == null) {
				continue;
			}
			
			double distIndex = scenePoints.get(tri.i)
					.add(scenePoints.get(tri.j))
					.add(scenePoints.get(tri.k))
					.mult(1.0/3)
					.sub(this.getCameraPos())
					.length();
			
			tris.add(new Triangle(
				points[tri.i], points[tri.j], points[tri.k],
				distIndex, tri.getColor()
			));
			
		}
		
		return tris;
	}
	
	public void debug() {
		System.out.println("Camera pos: " + this.getCameraPos());
		System.out.println("Camera direction: " + this.getCameraDir());
		System.out.println("View plane width: " + this.planeWidth);
		System.out.println("View plane height: " + this.planeHeight);
	}
}
