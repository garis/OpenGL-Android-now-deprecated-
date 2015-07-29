package com.example.App;

import android.content.Context;

import com.example.App.GraphicsEngine.Engine.EngineComponentText;
import com.example.App.GraphicsEngine.Engine.EngineGLRenderer;
import com.example.App.GraphicsEngine.Utils.Vector3f;

public class SensorExperiment extends EngineGLRenderer {

    EngineComponentText text;
    EngineComponentText textTot;
    int drawMode = 0;
    float contrast = 20;
    int textCount = 0;
    int tot = 0;

    public SensorExperiment(Context context) {
        super.Initialize(context);
    }

    public void Update() {

        textCount++;
        tot++;
        tot++;
        // textTot.SetText(context, "" + tot);
        if (textCount > 19) {
            textCount = 0;
            //text.SetText(context, "S " + ((int) (finishSim - startTime)) + " C " + ((int) (finishColor - startTime))
            //        + " D " + ((int) (finishDraw - startTime)));
        }
        super.Update();
    }

    public void LoadItems() {
        //fluidSim.setVelocity(0.25f);
        //fluidSim.makeLine((int)((Math.min(dimension.X(),dimension.Y()))*0.3f));

        LoadTextTexture(R.drawable.font_0);
        text = new EngineComponentText(this.chracterList, this.GetTextTextureGL_INDEX());
        text.Move(new Vector3f(-0.99f, -0.95f, 0));
        text.SetScale(new Vector3f(0.05f, 0.05f * 16 / 9, 0.05f));
        text.SetText(context, "V");
        text.SetColor(new float[]{1.0f, 0.0f, 0.0f, 1.0f});
        //text.SetRenderDepth(0.8f);
        text.SetID("STATS");
        // text.lockedOnUpperLeftCorner(true);
        //text.isIn3dSpace(false);
        super.LoadItems(text, false, null, null, false);

/*
        textTot = new EngineComponentText(this.chracterList, this.GetTextTextureGL_INDEX());
        textTot.Move(new Vector3f(-0.99f, -0.85f, 0));
        textTot.SetScale(new Vector3f(0.05f, 0.05f * 16 / 9, 0.05f));
        textTot.SetText(context, "");
        textTot.SetColor(new float[]{1.0f, 0.0f, 0.0f, 1.0f});
        //text.SetRenderDepth(0.8f);
        textTot.SetID("COUNT");
        textTot.lockedOnUpperLeftCorner(true);
        textTot.isIn3dSpace(false);
        super.LoadItems(textTot, false, null, null, false);
*/
        super._camera.SetCameraPosition(new Vector3f(0, 0, -10));
        super._camera.SetCameraLookAt(new Vector3f(0, 0, 0));
        super.LoadItems();
    }

    public void changeSettings(int actionID) {
        switch (actionID) {
            case 0:
                tot = 0;
            case 1:
                contrast = contrast + 2;
                break;
            case 2:
                contrast = contrast - 2;
                break;
            case 3:
                drawMode = 0;
                break;
            case 4:
                drawMode = 1;
                break;
            case 5:
                drawMode = 2;
                break;
            case 6:
                drawMode = 3;
                break;
            case 7:
                drawMode = 4;
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
            case 11:
                break;
            case 12:
                break;
            case 13:
                break;
            case 14:
                break;
            case 15:
                break;
            case 16:
                break;
            case 17:
                break;
            case 18:
                break;
        }
        text.SetText(context, "V");
    }
}

