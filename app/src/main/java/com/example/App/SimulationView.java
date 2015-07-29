package com.example.App;

import android.content.Context;

import com.example.App.GraphicsEngine.Engine.EngineComponentText;
import com.example.App.GraphicsEngine.Engine.EngineGLRenderer;
import com.example.App.GraphicsEngine.Utils.Vector3f;
import com.example.App.LatticeBoltzmannSim.EngineComponentSimColorPanel;
import com.example.App.LatticeBoltzmannSim.LatticeBoltzmann;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SimulationView extends EngineGLRenderer {

    boolean isStopped = false;
    boolean isTracersEnable = false;
    LatticeBoltzmann fluidSim;
    int value = 180;
    Vector3f dimension = new Vector3f(value, value / 16 * 9, 0);
    EngineComponentText text;
    EngineComponentText textTot;
    // EngineComponentParticles particles;
    int drawMode = 0;
    float contrast = 20;
    int textCount = 0;
    int tot = 0;
    private boolean drawBarrier;
    private EngineComponentSimColorPanel panel;
    private List<Vector3f> touchedPoints;

    public SimulationView(Context context) {
        super.Initialize(context);
    }

    public void Update() {

        if (!isStopped) {
            //long startTime = System.currentTimeMillis();

            fluidSim.SimStep();

            fluidSim.SimStep();

            //long finishSim = System.currentTimeMillis();

            fluidSim.computeColorMatrix(drawMode, contrast);

            if (isTracersEnable)
                fluidSim.moveTracers();

            //long finishColor = System.currentTimeMillis();

            panel.SetColorBuffer(fluidSim.getColorMatrix());
            //long finishDraw = System.currentTimeMillis();


            textCount++;
            tot++;
            tot++;
            textTot.SetText(context, "" + tot);
            if (textCount > 19) {
                textCount = 0;
                //text.SetText(context, "S " + ((int) (finishSim - startTime)) + " C " + ((int) (finishColor - startTime))
                //        + " D " + ((int) (finishDraw - startTime)));
            }
        }
        super.Update();
    }

    public void LoadItems() {
        //fluidSim.setVelocity(0.25f);
        //fluidSim.makeLine((int)((Math.min(dimension.X(),dimension.Y()))*0.3f));

        this.SetWaitTime(20);

        fluidSim = new LatticeBoltzmann((int) dimension.X(), (int) dimension.Y(), 0.08f, 1024, 255);
        fluidSim.setViscosity(0.01f);
        int[] dimension = fluidSim.getGridDimensions();

        panel = new EngineComponentSimColorPanel(dimension[0], dimension[1]);
        super.LoadItems(panel, false, null, null, false);

        LoadTextTexture(R.drawable.font_0);
        text = new EngineComponentText(this.chracterList, this.GetTextTextureGL_INDEX());
        text.Move(new Vector3f(-0.99f, -0.95f, 0));
        text.SetScale(new Vector3f(0.05f, 0.05f * 16 / 9, 0.05f));
        text.SetText(context, "V" + fluidSim.getVelocity());
        text.SetColor(new float[]{1.0f, 0.0f, 0.0f, 1.0f});
        //text.SetRenderDepth(0.8f);
        text.SetID("STATS");
        text.lockedOnUpperLeftCorner(true);
        text.isIn3dSpace(false);
        super.LoadItems(text, false, null, null, false);


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

/*
        particles=new EngineComponentParticles(10,10);
        super.LoadItems(particles, false, null, null,true);
*/
/*
        tracers[0]=new EngineComponentImage();
        tracers[0].SetScale(new Vector3f(0.05f,0.05f,0.05f));

        tracers[0].SetColor(new float[]{1.0f,1.0f,1.0f});
        super.LoadItems( tracers[0], false, null, null,false);
*/
        super._camera.SetCameraPosition(new Vector3f(0, 0, -10));
        super._camera.SetCameraLookAt(new Vector3f(0, 0, 0));
        super.LoadItems();
    }

    public void TouchDown(Vector3f screenCoords) {
        Vector3f objCoords = toScreenToSim(screenCoords);
        drawBarrier = !fluidSim.isBarrier((int) objCoords.X(), (int) objCoords.Y());
        touchedPoints = new ArrayList<Vector3f>();
        touchedPoints.add(objCoords);

    }

    public void TouchMove(Vector3f screenCoords) {
        Vector3f objCoords = toScreenToSim(screenCoords);
        touchedPoints.add(objCoords);
    }

    public void TouchUp(Vector3f screenCoords) {
        Vector3f objCoords = toScreenToSim(screenCoords);
        touchedPoints.add(objCoords);
        drawBarrier(touchedPoints);
    }

    private void drawBarrier(List<Vector3f> touchedPoints) {
        Iterator<Vector3f> iterator = touchedPoints.iterator();
        Vector3f prevPoint = iterator.next();
        while (iterator.hasNext()) {
            Vector3f point = iterator.next();

            if (point.X() != prevPoint.X()) {
                float m = ((point.Y() - prevPoint.Y()) / (point.X() - prevPoint.X()));
                float q = ((point.X() * prevPoint.Y()) - (prevPoint.X() * point.Y())) / (point.X() - prevPoint.X());
                int countX = (int) (Math.min(point.X(), prevPoint.X()));
                while (countX < (int) (Math.max(point.X(), prevPoint.X()))) {
                    Vector3f p = new Vector3f((float) countX, m * countX + q, 0);
                    fluidSim.setBarrier((int) p.X(), (int) p.Y(), drawBarrier);
                    countX++;
                }
                int countY = (int) (Math.min(point.Y(), prevPoint.Y()));
                while (countY < (int) (Math.max(point.Y(), prevPoint.Y()))) {
                    Vector3f p = new Vector3f((countY - q) / m, countY, 0);
                    fluidSim.setBarrier((int) p.X(), (int) p.Y(), drawBarrier);
                    countY++;
                }
            } else {
                int countY = (int) (Math.min(point.Y(), prevPoint.Y()));
                while (countY < (int) (Math.max(point.Y(), prevPoint.Y()))) {
                    Vector3f p = new Vector3f(point.X(), countY, 0);
                    fluidSim.setBarrier((int) p.X(), (int) p.Y(), drawBarrier);
                    countY++;
                }
            }
            prevPoint = point;
        }
    }

    private Vector3f toScreenToSim(Vector3f scrCoords) {
        return new Vector3f(scrCoords.X() / _camera.GetScreenWidth() * dimension.X(),
                scrCoords.Y() / _camera.GetScreenHeight() * dimension.Y(), 0);
    }

    public void changeSettings(int actionID) {
        switch (actionID) {
            case 0:
                fluidSim.initFluid();
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
                fluidSim.makeLine((int) (dimension.X() * 0.2f));
                break;
            case 9:
                fluidSim.makeCircle((int) (dimension.X() * 0.15f));
                break;
            case 10:
                float dim = dimension.X() * 0.04f;
                fluidSim.makeDrop(dim, dim * (_camera.GetScreenWidth() / _camera.GetScreenHeight()));
                break;
            case 11:
                fluidSim.makeBellMouth(0.2f, 0.1f);
                break;
            case 12:
                fluidSim.makeInvBellMouth(0.2f, 0.1f);
                break;
            case 13:
                fluidSim.resetBarrier();
                break;
            case 14:
                isStopped = !isStopped;
                break;
            case 15:
                isTracersEnable = !isTracersEnable;
                fluidSim.initTracers();
                break;
            case 16:
                fluidSim.setVelocity(fluidSim.getVelocity() + 0.01f);
                break;
            case 17:
                fluidSim.setVelocity(fluidSim.getVelocity() - 0.01f);
                break;
            case 18:
                fluidSim.setVelocity(fluidSim.getVelocity() * -1f);
                break;
        }
        text.SetText(context, "V" + fluidSim.getVelocity());
    }
}
