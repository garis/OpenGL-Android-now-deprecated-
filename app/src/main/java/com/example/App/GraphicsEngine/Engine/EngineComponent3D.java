package com.example.App.GraphicsEngine.Engine;

import android.content.Context;

import com.example.App.GraphicsEngine.Utils.OBJParser;

public class EngineComponent3D extends EngineComponentPlus implements EngineComponent.EngineComponentInterface {

    public EngineComponent3D() {
    }

    public void loadFromOBJ(Context context, String filename) {
        OBJParser objparser = new OBJParser();
        objparser.loadFromOBJ(context, filename);
        float[] SquareCoords = objparser.getVertices();
        float[] UVCoords = objparser.getUVVertices();
        short[] DrawOrder = objparser.getOrder();

        this.updateGeometryAndUVs(SquareCoords, UVCoords, DrawOrder);
    }
}
