package com.example.App.GraphicsEngine.Utils;

public class Point3f {
    private float x, y, z;

    public Point3f() {
        x = 0;
        y = 0;
        z = 0;
    }

    public Point3f(float vX, float vY, float vZ) {
        x = vX;
        y = vY;
        z = vZ;
    }

    public float GetX() {
        return x;
    }

    public float GetY() {
        return y;
    }

    public float GetZ() {
        return z;
    }

    public String toString() {
        return "( " + x + " " + y + " " + z + " )";
    }

    public Point3f translate(Vector3f vector) {
        return new Point3f(x + vector.X(), y + vector.Y(), z + vector.Z());
    }

}
