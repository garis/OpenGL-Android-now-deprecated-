package com.example.App.GraphicsEngine.Utils;

public class Sphere {

    private Point3f center;
    private float radius;

    public Sphere() {
        center = new Point3f();
        radius = 0;
    }

    public Sphere(Point3f Center, float Radius) {
        center = Center;
        radius = Radius;
    }

    public void SetPosition(Point3f point) {
        center = point;
    }

    public Point3f GetPosition() {
        return center;
    }

    public void SetRadius(float Radius) {
        radius = Radius;
    }

    public float GetRadius() {
        return radius;
    }
}
