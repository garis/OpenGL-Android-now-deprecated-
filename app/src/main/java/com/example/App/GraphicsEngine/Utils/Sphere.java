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

    public Point3f getPosition() {
        return center;
    }

    public void setPosition(Point3f point) {
        center = point;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float Radius) {
        radius = Radius;
    }
}
