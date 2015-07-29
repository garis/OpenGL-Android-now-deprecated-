package com.example.App.GraphicsEngine.Utils;

public class BoundingSphere extends Sphere {

    public BoundingSphere() {
        this.SetPosition(new Point3f());
        this.SetRadius(0);
    }

    public BoundingSphere(Point3f pos, float rad) {
        this.SetPosition(pos);
        this.SetRadius(rad);
    }

    public boolean intersects(BoundingSphere sphere, Ray3f ray) {
        return DistanceBetween(sphere.GetPosition(), ray) < sphere.GetRadius();
    }

    public float DistanceBetween(Point3f point, Ray3f ray) {
        Vector3f p1ToPoint = new Vector3f().VectorBetween(ray.GetPoint(), point);
        Vector3f p2ToPoint = new Vector3f().VectorBetween(ray.GetPoint().translate(ray.GetVector()), point);

        // The length of the cross product gives the area of an imaginary
        // parallelogram having the two vectors as sides. A parallelogram can be
        // thought of as consisting of two triangles, so this is the same as
        // twice the area of the triangle defined by the two vectors.
        // http://en.wikipedia.org/wiki/Cross_product#Geometric_meaning

        float areaOfTriangleTimesTwo = p1ToPoint.Cross(p2ToPoint).Length();
        float lengthOfBase = ray.GetVector().Length();

        // The area of a triangle is also equal to (base * height) / 2. In
        // other words, the height is equal to (area * 2) / base. The height
        // of this triangle is the distance from the point to the ray.
        float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase;
        return distanceFromPointToRay;

    }
}
