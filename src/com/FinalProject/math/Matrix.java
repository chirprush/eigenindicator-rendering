package com.FinalProject.math;

public class Matrix {
	private int rows;
	private int cols;
	
	private double[][] data;
	
	public Matrix(int r, int c) {
		this.rows = r;
		this.cols = c;
		
		this.data = new double[this.rows][this.cols];
	}
	
	public Matrix(double[][] data) {
		this.rows = data.length;
		this.cols = data[0].length;
		
		this.data = data;
	}
	
	public int getRows() {
		return this.rows;
	}
	
	public int getCols() {
		return this.cols;
	}
	
	public double get(int i, int j) {
		return this.data[i][j];
	}
	
	public static Matrix fromVector(Vector v) {
		Matrix result = new Matrix(v.getSize(), 1);
		
		for (int i = 0; i < v.getSize(); i++) {
			result.data[i][0] = v.comp(i);
		}
		
		return result;
	}
	
	public Vector toVector() {
		if (this.getCols() != 1) {
			throw new IllegalArgumentException("Invalid matrix size for conversion");
		}
		
		double[] data = new double[this.getRows()];
		
		for (int i = 0; i < this.getRows(); i++) {
			data[i] = this.get(i, 0);
		}
		
		return new Vector(data);
	}
	
	public Matrix mult(double scalar) {
		Matrix result = new Matrix(this.getRows(), this.getCols());
		
		for (int i = 0; i < this.getRows(); i++) {
			for (int j = 0; j < this.getCols(); j++) {
				result.data[i][j] = scalar * this.data[i][j];
			}
		}
		
		return result;
	}
	
	public Matrix mult(Matrix other) {
		if (this.getCols() != other.getRows()) {
			throw new IllegalArgumentException("Cannot multiply matrix of size " + this.getRows() + "x" + this.getCols() + " with matrix of size " + other.getRows() + "x" + other.getCols());
		}
		
		Matrix result = new Matrix(this.getRows(), other.getCols());
		
		for (int i = 0; i < this.getRows(); i++) {
			for (int j = 0; j < other.getCols(); j++) {
				double part = 0;
				
				for (int k = 0; k < this.getCols(); k++) {
					part += this.data[i][k] * other.data[k][j];
				}
				
				result.data[i][j] = part;
			}
		}
		
		return result;
	}
	
	public Matrix mult(Vector other) {
		return this.mult(Matrix.fromVector(other));
	}
	
	public Matrix transpose() {
		Matrix result = new Matrix(this.getCols(), this.getRows());
		
		for (int i = 0; i < this.getRows(); i++) {
			for (int j = 0; j < this.getCols(); j++) {
				result.data[j][i] = this.data[i][j];
			}
		}
		
		return result;
	}
	
	// I only need to invert 2x2 matrices so that's the only subset I'll implement this for really
	public Matrix inv() {
		if (this.getRows() != 2 || this.getCols() != 2) {
			throw new IllegalArgumentException("Matrix inversion not implemented for size " + this.getRows() + "x" + this.getCols());
		}
		
		double det = this.data[0][0] * this.data[1][1] - this.data[0][1] * this.data[1][0];
		
		Matrix result = new Matrix(2, 2);
		
		result.data[0][0] =  this.data[1][1];
		result.data[0][1] = -this.data[0][1];
		result.data[1][0] = -this.data[1][0];
		result.data[1][1] =  this.data[0][0];
		
		result = result.mult(1 / det);
		
		return result;
	}
	
	public String toString() {
		String output = "[\n";
		
		for (int i = 0; i < this.getRows(); i++) {
			for (int j = 0; j < this.getCols(); j++) {
				output += this.get(i, j) + " ";
			}
			output += "\n";
		}
		
		output += "]";
				
		return output;
	}
}
