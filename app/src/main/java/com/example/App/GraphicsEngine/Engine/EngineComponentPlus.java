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

    public void Update(float dt) {
        _totalTime = _totalTime + dt;
        super.Update();
    }

    public float GetRenderDepth() {
        return renderDepth;
    }

    public void SetRenderDepth(float rd) {
        renderDepth = rd;
    }

    public void SetID(String id) {
        ID = id;
    }

    public String GetID() {
        return ID;
    }

    public void LinkEvent(EngineEvent a) {
        event = a;
    }

    public void DoAction(boolean restart) {
        if (restart)
            _eventTime = 0;
        if (event != null)
            event.DoAction();
    }
}
