package com.example.App;

import com.example.App.GraphicsEngine.Engine.EngineComponent3D;
import com.example.App.GraphicsEngine.Engine.EngineEvent;
import com.example.App.GraphicsEngine.Engine.EngineGLRenderer;
import com.example.App.GraphicsEngine.Utils.Vector3f;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Menu extends EngineGLRenderer {

    public Menu() {
        super.Initialize(context);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        LoadItems();
        super.onSurfaceCreated(unused, config);

    }

    public void Update() {
        this._camera.SetCameraPosition(new Vector3f(0, 0, -5 + (float) Math.sin(getElaspedTime() * 0.4f)));

        super.Update();
    }

    public void LoadItems() {
        this._camera.SetCameraLookAt(new Vector3f(0, 0, 0));
        this._camera.SetCameraPosition(new Vector3f(0, 0, -5));

        EngineComponent3D currentObject = new EngineComponent3D();
        currentObject.Move(new Vector3f(4.6f, 0, 0));
        currentObject.LoadFromOBJ(context, "plane");
        currentObject.SetColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        super.LoadItems(currentObject, false, null, null, false);

        EngineEvent anA = new EngineEvent(currentObject) {
            @Override
            public void DoAction() {
            }
        };
        currentObject.LinkEvent(anA);

        currentObject = new EngineComponent3D();
        currentObject.Move(new Vector3f(-4.6f, 0, 0));
        currentObject.LoadFromOBJ(context, "thirdstCube");
        super.LoadItems(currentObject, false, null, null, false);

        anA = new EngineEvent(currentObject) {
            @Override
            public void DoAction() {
            }
        };
        currentObject.LinkEvent(anA);
    }
}
