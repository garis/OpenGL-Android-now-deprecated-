package com.example.App.GraphicsEngine.Utils;

public class Vector3f {

    private float xyz[] = new float[3];

    public Vector3f() {
        xyz[0] = 0;
        xyz[1] = 0;
        xyz[2] = 0;
    }

    public Vector3f(float x, float y, float z) {
        xyz[0] = x;
        xyz[1] = y;
        xyz[2] = z;
    }

    public Vector3f(float[] array) {
        if (array.length != 3)
            throw new RuntimeException("Must create vector with 3 element array");

        xyz[0] = array[0];
        xyz[1] = array[1];
        xyz[2] = array[2];
    }

    public float[] Array() {
        return xyz.clone();
    }

    public float Length() {
        return (float) (Math.sqrt(xyz[0] * xyz[0] + xyz[1] * xyz[1] + xyz[2] * xyz[2]));
    }

    public Vector3f Add(Vector3f rhs) {
        return new Vector3f(
                xyz[0] + rhs.xyz[0],
                xyz[1] + rhs.xyz[1],
                xyz[2] + rhs.xyz[2]);
    }

    public Vector3f Sub(Vector3f rhs) {
        return new Vector3f(
                xyz[0] - rhs.xyz[0],
                xyz[1] - rhs.xyz[1],
                xyz[2] - rhs.xyz[2]);
    }

    public Vector3f Neg() {
        return new Vector3f(-xyz[0], -xyz[1], -xyz[2]);
    }

    public Vector3f Mul(float c) {
        return new Vector3f(c * xyz[0], c * xyz[1], c * xyz[2]);
    }

    public Vector3f Div(float c) {
        return new Vector3f(xyz[0] / c, xyz[1] / c, xyz[2] / c);
    }

    public float Dot(Vector3f rhs) {
        return xyz[0] * rhs.xyz[0] +
                xyz[1] * rhs.xyz[1] +
                xyz[2] * rhs.xyz[2];
    }

    public Vector3f Cross(Vector3f rhs) {
        return new Vector3f(
                xyz[1] * rhs.xyz[2] - xyz[2] * rhs.xyz[1],
                xyz[0] * rhs.xyz[2] - xyz[2] * rhs.xyz[0],
                xyz[0] * rhs.xyz[1] - xyz[1] * rhs.xyz[0]
        );
    }

    public boolean Equals(Object obj) {
        if (obj instanceof Vector3f) {
            Vector3f rhs = (Vector3f) obj;

            return xyz[0] == rhs.xyz[0] &&
                    xyz[1] == rhs.xyz[1] &&
                    xyz[2] == rhs.xyz[2];
        } else {
            return false;
        }

    }

    public float Norm() {
        return (float) Math.sqrt(this.Dot(this));
    }

    public Vector3f Normalize() {
        return this.Div(Norm());
    }

    public void X(float value) {
        xyz[0] = value;
    }

    public void Y(float value) {
        xyz[1] = value;
    }

    public void Z(float value) {
        xyz[2] = value;
    }

    public float X() {
        return xyz[0];
    }

    public float Y() {
        return xyz[1];
    }

    public float Z() {
        return xyz[2];
    }

    public String toString() {
        return "( " + xyz[0] + " " + xyz[1] + " " + xyz[2] + " )";
    }

    public Vector3f VectorBetween(Point3f from, Point3f to) {
        return new Vector3f(to.GetX() - from.GetX(), to.GetY() - from.GetY(), to.GetZ() - from.GetZ());
    }
}
