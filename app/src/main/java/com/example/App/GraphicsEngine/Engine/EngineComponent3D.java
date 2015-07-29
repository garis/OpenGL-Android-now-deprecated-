package com.example.App.GraphicsEngine.Engine;

import android.content.Context;

import com.example.App.GraphicsEngine.Utils.OBJParser;

public class EngineComponent3D extends EngineComponentPlus implements EngineComponent.EngineComponentInterface {

    public EngineComponent3D() {
    }

    public void LoadFromOBJ(Context context, String filename) {
        OBJParser objparser = new OBJParser();
        objparser.LoadFromOBJ(context, filename);
        float[] SquareCoords = objparser.GetVertices();
        float[] UVCoords = objparser.GetUVVertices();
        short[] DrawOrder = objparser.GetOrder();

        this.UpdateGeometryAndUVs(SquareCoords, UVCoords, DrawOrder);
    }
}
