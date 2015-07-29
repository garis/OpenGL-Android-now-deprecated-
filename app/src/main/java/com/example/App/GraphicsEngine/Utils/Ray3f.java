package com.example.App.GraphicsEngine.Utils;

public class Ray3f {

    private Point3f point;
    private Vector3f vector;

    public Ray3f() {
        point = new Point3f();
        vector = new Vector3f();
    }

    public Ray3f(Point3f ValuePoint, Vector3f ValueVector) {
        point = ValuePoint;
        vector = ValueVector;
    }

    public String toString() {
        return ("Point: " + point.toString() + " direction: " + vector.toString());
    }

    public Point3f GetPoint() {
        return point;
    }

    public Vector3f GetVector() {
        return vector;
    }
}
