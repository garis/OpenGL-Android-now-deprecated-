package com.example.App.GraphicsEngine.Engine;

import com.example.App.GraphicsEngine.Utils.Vector3f;

public class EngineComponentImage extends EngineComponentPlus implements EngineComponent.EngineComponentInterface {

    private EngineEvent event;
    private boolean selected;

    public EngineComponentImage() {
        super();

        selected = false;
    }

    public void draw(float[] mViewMatrix, float[] mProjectionMatrix, float time) {
        super.draw(mViewMatrix, mProjectionMatrix, time);
    }

    public void update(float dt) {
        if (selected) {
            this.rotate(new Vector3f(0, 0, _totalTime * 20));
        }
    }

    public void linkEvent(EngineEvent a) {
        event = a;
    }

    public void doAction(boolean restart) {
        if (restart)
            _eventTime = 0;
        event.doAction();
    }

    public void setSelected(boolean flag) {
        selected = flag;
    }

    public void toogleSelected() {
        selected = !selected;
    }
}