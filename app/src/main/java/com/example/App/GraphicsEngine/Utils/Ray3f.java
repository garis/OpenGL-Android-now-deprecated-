package com.example.App.GraphicsEngine.Utils;

public class Ray3f {

    private Point3f point;
    private Vector3 vector;

    public Ray3f() {
        point = new Point3f();
        vector = new Vector3();
    }

    public Ray3f(Point3f ValuePoint, Vector3 ValueVector) {
        point = ValuePoint;
        vector = ValueVector;
    }

    public String toString() {
        return ("Point: " + point.toString() + " direction: " + vector.toString());
    }

    public Point3f getPoint() {
        return point;
    }

    public Vector3 getVector() {
        return vector;
    }
}
