package com.FinalProject.math;

public class Vector {
	private int n;
	private double[] data;
	
	public Vector(int n) {
		this.n = n;
		this.data = new double[n];
	}
	
	public Vector(double[] data) {
		this.n = data.length;
		this.data = data;
	}
	
	// Returns a 3D Vector given spherical 
	public static Vector fromSpherical(double polar, double azimuthal) {
		// See Camera.java for specification of signs and all that.
		
		// Hopefully this is correct :sob:
		double x = Math.cos(polar) * Math.cos(azimuthal);
		double y = -Math.sin(polar) * Math.cos(azimuthal);
		double z = -Math.sin(azimuthal);
		
		Vector result = new Vector(3);
		result.data[0] = x;
		result.data[1] = y;
		result.data[2] = z;
		
		return result;
	}
	
	public int getSize() {
		return this.n;
	}
	
	// Get the ith component
	public double comp(int i) {
		return this.data[i];
	}
	
	public Vector add(Vector other) {
		if (this.getSize() != other.getSize()) {
			throw new IllegalArgumentException("Cannot add different sized vectors");
		}
		
		Vector result = new Vector(this.getSize());
		
		for (int i = 0; i < this.getSize(); i++) {
			result.data[i] = this.data[i] + other.data[i];
		}
		
		return result;
	}
	
	public Vector sub(Vector other) {
		return this.add(other.mult(-1));
	}
	
	public Vector mult(double scalar) {
		Vector result = new Vector(this.getSize());
		
		for (int i = 0; i < this.getSize(); i++) {
			result.data[i] = this.data[i] * scalar;
		}
		
		return result;
	}
	
	public double length() {
		double total = 0;
		
		for (int i = 0; i < this.n; i++) {
			total += this.comp(i) * this.comp(i);
		}
		
		return Math.sqrt(total);
	}
	
	public Vector normalize() {
		return this.mult(1 / this.length());
	}
	
	public double dot(Vector other) {
		if (this.getSize() != other.getSize()) {
			throw new IllegalArgumentException("Cannot dot different sized vectors");
		}
		
		double sum = 0;
		
		for (int i = 0; i < this.n; i++) {
			sum += this.comp(i) * other.comp(i);
		}
		
		return sum;
	}
	
	public String toString() {
		String output = "(";
		
		for (int i = 0; i < this.n - 1; i++) {
			output += this.comp(i) + ", ";
		}
		
		output += this.comp(this.n - 1);
		output += ")";
		
		return output;
	}
}
