package com.example.App.GraphicsEngine.Utils;

public class Vector3 {

    private double xyz[] = new double[3];

    public Vector3() {
        xyz[0] = 0;
        xyz[1] = 0;
        xyz[2] = 0;
    }

    public Vector3(double x, double y, double z) {
        xyz[0] = x;
        xyz[1] = y;
        xyz[2] = z;
    }

    public Vector3(double[] array) {
        if (array.length != 3)
            throw new RuntimeException("Must create vector with 3 element array");

        xyz[0] = array[0];
        xyz[1] = array[1];
        xyz[2] = array[2];
    }

    public double[] array() {
        return xyz.clone();
    }

    public double length() {
        return (double) (Math.sqrt(xyz[0] * xyz[0] + xyz[1] * xyz[1] + xyz[2] * xyz[2]));
    }

    public Vector3 add(Vector3 rhs) {
        return new Vector3(
                xyz[0] + rhs.xyz[0],
                xyz[1] + rhs.xyz[1],
                xyz[2] + rhs.xyz[2]);
    }

    public Vector3 sub(Vector3 rhs) {
        return new Vector3(
                xyz[0] - rhs.xyz[0],
                xyz[1] - rhs.xyz[1],
                xyz[2] - rhs.xyz[2]);
    }

    public Vector3 neg() {
        return new Vector3(-xyz[0], -xyz[1], -xyz[2]);
    }

    public Vector3 mul(double c) {
        return new Vector3(c * xyz[0], c * xyz[1], c * xyz[2]);
    }

    public Vector3 div(double c) {
        return new Vector3(xyz[0] / c, xyz[1] / c, xyz[2] / c);
    }

    public double dot(Vector3 rhs) {
        return xyz[0] * rhs.xyz[0] +
                xyz[1] * rhs.xyz[1] +
                xyz[2] * rhs.xyz[2];
    }

    public Vector3 cross(Vector3 rhs) {
        return new Vector3(
                xyz[1] * rhs.xyz[2] - xyz[2] * rhs.xyz[1],
                xyz[0] * rhs.xyz[2] - xyz[2] * rhs.xyz[0],
                xyz[0] * rhs.xyz[1] - xyz[1] * rhs.xyz[0]
        );
    }

    public boolean equals(Object obj) {
        if (obj instanceof Vector3) {
            Vector3 rhs = (Vector3) obj;

            return xyz[0] == rhs.xyz[0] &&
                    xyz[1] == rhs.xyz[1] &&
                    xyz[2] == rhs.xyz[2];
        } else {
            return false;
        }

    }

    public double norm() {
        return (double) Math.sqrt(this.dot(this));
    }

    public Vector3 normalize() {
        return this.div(norm());
    }

    public void x(double value) {
        xyz[0] = value;
    }

    public void y(double value) {
        xyz[1] = value;
    }

    public void z(double value) {
        xyz[2] = value;
    }

    public double x() {
        return xyz[0];
    }

    public double y() {
        return xyz[1];
    }

    public double z() {
        return xyz[2];
    }

    public String toString() {
        return "( " + xyz[0] + " " + xyz[1] + " " + xyz[2] + " )";
    }

    public Vector3 vectorBetween(Point3f from, Point3f to) {
        return new Vector3(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ());
    }
}
