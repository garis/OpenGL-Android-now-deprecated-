package com.example.App.GraphicsEngine.Utils;

public class BoundingSphere extends Sphere {

    public BoundingSphere() {
        this.setPosition(new Point3f());
        this.setRadius(0);
    }

    public BoundingSphere(Point3f pos, float rad) {
        this.setPosition(pos);
        this.setRadius(rad);
    }

    public boolean intersects(BoundingSphere sphere, Ray3f ray) {
        return distanceBetween(sphere.getPosition(), ray) < sphere.getRadius();
    }

    public float distanceBetween(Point3f point, Ray3f ray) {
        Vector3f p1ToPoint = new Vector3f().vectorBetween(ray.getPoint(), point);
        Vector3f p2ToPoint = new Vector3f().vectorBetween(ray.getPoint().translate(ray.getVector()), point);

        // The length of the cross product gives the area of an imaginary
        // parallelogram having the two vectors as sides. A parallelogram can be
        // thought of as consisting of two triangles, so this is the same as
        // twice the area of the triangle defined by the two vectors.
        // http://en.wikipedia.org/wiki/Cross_product#Geometric_meaning

        float areaOfTriangleTimesTwo = p1ToPoint.cross(p2ToPoint).length();
        float lengthOfBase = ray.getVector().length();

        // The area of a triangle is also equal to (base * height) / 2. In
        // other words, the height is equal to (area * 2) / base. The height
        // of this triangle is the distance from the point to the ray.
        float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase;
        return distanceFromPointToRay;

    }
}
