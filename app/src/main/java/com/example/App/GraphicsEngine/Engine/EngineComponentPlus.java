package com.example.App.GraphicsEngine.Engine;

public class EngineComponentPlus extends EngineComponent implements EngineComponent.EngineComponentInterface {
    protected float _totalTime;
    protected float _eventTime;
    private String ID;
    //render depth:0 = near //  1 = far
    private float renderDepth;
    private EngineEvent event;

    public EngineComponentPlus() {
        ID = "";
        _totalTime = 0;
        _eventTime = 0;
        renderDepth = -1;
    }

    public void update(float dt) {
        _totalTime = _totalTime + dt;
        super.update();
    }

    public float getRenderDepth() {
        return renderDepth;
    }

    public void setRenderDepth(float rd) {
        renderDepth = rd;
    }

    public String getID() {
        return ID;
    }

    public void setID(String id) {
        ID = id;
    }

    public void linkEvent(EngineEvent a) {
        event = a;
    }

    public void doAction(boolean restart) {
        if (restart)
            _eventTime = 0;
        if (event != null)
            event.doAction();
    }
}
