package com.example.App.GraphicsEngine.Engine;

import android.opengl.GLSurfaceView;

import com.example.App.GraphicsEngine.Utils.Vector3f;

public interface EngineGLRendererInterface extends GLSurfaceView.Renderer {
    public void LoadItems();

    public void TouchDown(Vector3f screenCoords);

    public void TouchMove(Vector3f screenCoords);

    public void TouchUp(Vector3f screenCoords);
}
