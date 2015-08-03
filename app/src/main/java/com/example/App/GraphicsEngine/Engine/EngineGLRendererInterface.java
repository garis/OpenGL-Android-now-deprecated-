package com.example.App.GraphicsEngine.Engine;

import android.opengl.GLSurfaceView;

import com.example.App.GraphicsEngine.Utils.Vector3;

public interface EngineGLRendererInterface extends GLSurfaceView.Renderer {
    public void loadItems();

    public void touchDown(Vector3 screenCoords);

    public void touchMove(Vector3 screenCoords);

    public void touchUp(Vector3 screenCoords);
}
