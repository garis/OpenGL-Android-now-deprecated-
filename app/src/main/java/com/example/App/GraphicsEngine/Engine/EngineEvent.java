package com.example.App.GraphicsEngine.Engine;

public class EngineEvent {
    public EngineComponent.EngineComponentInterface im;

    public EngineEvent(EngineComponent.EngineComponentInterface obj) {
        im = obj;
    }

    public void DoAction() {
    }
}
