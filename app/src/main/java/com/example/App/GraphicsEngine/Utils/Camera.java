package com.example.App.GraphicsEngine.Utils;

import android.opengl.Matrix;

public class Camera {

    private float _cameraX;
    private float _cameraY;
    private float _cameraZ;
    private float _cameraLAX;
    private float _cameraLAY;
    private float _cameraLAZ;

    private float ScreenWidth;
    private float ScreenHeight;

    private float[] ProjectionMatrix;
    private float[] InvertedProjectionMatrix;

    //Model View Projection Matrix
    private float[] MVPMatrix;

    private float[] ViewMatrix;

    //for rayCasting
    private Ray3f ray;

    public Camera() {
        _cameraX = 0;
        _cameraY = 0;
        _cameraZ = 0;
        _cameraLAX = 0;
        _cameraLAY = 0;
        _cameraLAZ = 0;

        ProjectionMatrix = new float[16];
        InvertedProjectionMatrix = new float[16];
        MVPMatrix = new float[16];
        ViewMatrix = new float[16];

        ScreenWidth = 0;
        ScreenHeight = 0;
    }

    public float GetScreenWidth() {
        return ScreenWidth;
    }

    public float GetScreenHeight() {
        return ScreenHeight;
    }

    public void SetCameraLookAt(Vector3f lookAt) {
        _cameraLAX = lookAt.X();
        _cameraLAY = lookAt.Y();
        _cameraLAZ = lookAt.Z();
        SetViewMatrix();
    }

    public void SetCameraPosition(Vector3f position) {
        _cameraX = position.X();
        _cameraY = position.Y();
        _cameraZ = position.Z();
        SetViewMatrix();
    }

    public Vector3f GetCameraPosition() {
        return new Vector3f(_cameraX, _cameraY, _cameraZ);
    }

    public void SetProjectionMatrix(float screenWidth, float screenHeight, float ZNear, float ZFar) {
        ScreenWidth = screenWidth;
        ScreenHeight = screenHeight;
        float ratio = ScreenWidth / ScreenHeight;
        Matrix.frustumM(ProjectionMatrix, 0, -ratio, ratio, -1, 1, ZNear, ZFar);
    }

    private void SetViewMatrix() {
        Matrix.setLookAtM(ViewMatrix, 0, _cameraX, _cameraY, _cameraZ, _cameraLAX, _cameraLAY, _cameraLAZ, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(MVPMatrix, 0, ProjectionMatrix, 0, ViewMatrix, 0);

        Matrix.invertM(InvertedProjectionMatrix, 0, MVPMatrix, 0);
    }

    public float[] GetVPMatrix() {
        return MVPMatrix;
    }

    public float[] GetViewMatrix() {
        return ViewMatrix;
    }

    public float[] GetProjectionMatrix() {
        return ProjectionMatrix;
    }

    public void Touch(Vector3f screenCoords) {
        float normalizedX = (screenCoords.X() / ScreenWidth) * 2 - 1;
        float normalizedY = -((screenCoords.Y() / ScreenHeight) * 2 - 1);

        // We'll convert these normalized device coordinates into world-space
        // coordinates. We'll pick a point on the near and far planes, and draw a
        // line between them. To do this transform, we need to first multiply by
        // the inverse matrix, and then we need to undo the perspective divide.
        float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        float[] farPointNdc = {normalizedX, normalizedY, 1, 1};

        float[] nearPointWorld = new float[4];
        float[] farPointWorld = new float[4];

        Matrix.multiplyMV(nearPointWorld, 0, InvertedProjectionMatrix, 0, nearPointNdc, 0);
        Matrix.multiplyMV(farPointWorld, 0, InvertedProjectionMatrix, 0, farPointNdc, 0);

        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Point3f nearPointRay = new Point3f(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Point3f farPointRay = new Point3f(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        Vector3f directinVector = new Vector3f().VectorBetween(nearPointRay, farPointRay);

        ray = new Ray3f(nearPointRay, directinVector);
    }

    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

    public Ray3f GetRay() {
        return ray;
    }
}
