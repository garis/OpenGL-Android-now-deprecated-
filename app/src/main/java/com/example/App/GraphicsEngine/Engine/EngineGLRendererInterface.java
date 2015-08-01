package com.example.App.GraphicsEngine.Engine;

import android.opengl.GLSurfaceView;

import com.example.App.GraphicsEngine.Utils.Vector3f;

public interface EngineGLRendererInterface extends GLSurfaceView.Renderer {
    public void loadItems();

    public void touchDown(Vector3f screenCoords);

    public void touchMove(Vector3f screenCoords);

    public void touchUp(Vector3f screenCoords);
}
