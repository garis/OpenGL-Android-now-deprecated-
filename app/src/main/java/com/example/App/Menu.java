package com.example.App;

import com.example.App.GraphicsEngine.Engine.EngineComponent3D;
import com.example.App.GraphicsEngine.Engine.EngineEvent;
import com.example.App.GraphicsEngine.Engine.EngineGLRenderer;
import com.example.App.GraphicsEngine.Utils.Vector3f;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Menu extends EngineGLRenderer {

    public Menu() {
        super.initialize(context);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        LoadItems();
        super.onSurfaceCreated(unused, config);

    }

    public void Update() {
        this._camera.setCameraPosition(new Vector3f(0, 0, -5 + (float) Math.sin(getElaspedTime() * 0.4f)));

        super.update();
    }

    public void LoadItems() {
        this._camera.setCameraLookAt(new Vector3f(0, 0, 0));
        this._camera.setCameraPosition(new Vector3f(0, 0, -5));

        EngineComponent3D currentObject = new EngineComponent3D();
        currentObject.move(new Vector3f(4.6f, 0, 0));
        currentObject.loadFromOBJ(context, "plane");
        currentObject.setColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        super.loadItems(currentObject, false, null, null, false);

        EngineEvent anA = new EngineEvent(currentObject) {
            @Override
            public void doAction() {
            }
        };
        currentObject.linkEvent(anA);

        currentObject = new EngineComponent3D();
        currentObject.move(new Vector3f(-4.6f, 0, 0));
        currentObject.loadFromOBJ(context, "thirdstCube");
        super.loadItems(currentObject, false, null, null, false);

        anA = new EngineEvent(currentObject) {
            @Override
            public void doAction() {
            }
        };
        currentObject.linkEvent(anA);
    }
}
