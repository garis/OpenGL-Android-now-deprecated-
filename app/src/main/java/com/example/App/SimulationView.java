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
        super.initialize(context);
    }

    public synchronized void update() {

        if (!isStopped) {
            fluidSim.SimStep();

            fluidSim.SimStep();


            fluidSim.computeColorMatrix(drawMode, contrast);


            if (isTracersEnable)
                fluidSim.moveTracers();
            panel.setColorBuffer(fluidSim.getColorMatrix());

            textCount++;
            tot++;
            tot++;
            textTot.setText(context, "" + tot);
            //if (textCount > 19) {
            //textCount = 0;
            }

        super.update();
    }

    public void loadItems() {
        this.setWaitTime(20);

        fluidSim = new LatticeBoltzmann((int) dimension.x(), (int) dimension.y(), 0.08f, 1024, 255);
        fluidSim.setViscosity(0.01f);
        int[] dimension = fluidSim.getGridDimensions();

        panel = new EngineComponentSimColorPanel(dimension[0], dimension[1]);
        super.loadItems(panel, false, null, null, false);

        loadTextTexture(R.drawable.font_0);
        text = new EngineComponentText(this.chracterList, this.getTextTextureGL_INDEX());
        text.move(new Vector3f(-0.99f, -0.95f, 0));
        text.setScale(new Vector3f(0.05f, 0.05f * 16 / 9, 0.05f));
        text.setText(context, "V" + fluidSim.getVelocity());
        text.setColor(new float[]{1.0f, 0.0f, 0.0f, 1.0f});
        //text.SetRenderDepth(0.8f);
        text.setID("STATS");
        text.lockedOnUpperLeftCorner(true);
        text.isIn3dSpace(false);
        super.loadItems(text, false, null, null, false);


        textTot = new EngineComponentText(this.chracterList, this.getTextTextureGL_INDEX());
        textTot.move(new Vector3f(-0.99f, -0.85f, 0));
        textTot.setScale(new Vector3f(0.05f, 0.05f * 16 / 9, 0.05f));
        textTot.setText(context, "");
        textTot.setColor(new float[]{1.0f, 0.0f, 0.0f, 1.0f});
        //text.SetRenderDepth(0.8f);
        textTot.setID("COUNT");
        textTot.lockedOnUpperLeftCorner(true);
        textTot.isIn3dSpace(false);
        super.loadItems(textTot, false, null, null, false);

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
        super._camera.setCameraPosition(new Vector3f(0, 0, -10));
        super._camera.setCameraLookAt(new Vector3f(0, 0, 0));
        super.loadItems();
    }

    public void touchDown(Vector3f screenCoords) {
        Vector3f objCoords = toScreenToSim(screenCoords);
        drawBarrier = !fluidSim.isBarrier((int) objCoords.x(), (int) objCoords.y());
        touchedPoints = new ArrayList<Vector3f>();
        touchedPoints.add(objCoords);

    }

    public void touchMove(Vector3f screenCoords) {
        Vector3f objCoords = toScreenToSim(screenCoords);
        touchedPoints.add(objCoords);
    }

    public void touchUp(Vector3f screenCoords) {
        Vector3f objCoords = toScreenToSim(screenCoords);
        touchedPoints.add(objCoords);
        drawBarrier(touchedPoints);
    }

    private void drawBarrier(List<Vector3f> touchedPoints) {
        Iterator<Vector3f> iterator = touchedPoints.iterator();
        Vector3f prevPoint = iterator.next();
        while (iterator.hasNext()) {
            Vector3f point = iterator.next();

            if (point.x() != prevPoint.x()) {
                float m = ((point.y() - prevPoint.y()) / (point.x() - prevPoint.x()));
                float q = ((point.x() * prevPoint.y()) - (prevPoint.x() * point.y())) / (point.x() - prevPoint.x());
                int countX = (int) (Math.min(point.x(), prevPoint.x()));
                while (countX < (int) (Math.max(point.x(), prevPoint.x()))) {
                    Vector3f p = new Vector3f((float) countX, m * countX + q, 0);
                    fluidSim.setBarrier((int) p.x(), (int) p.y(), drawBarrier);
                    countX++;
                }
                int countY = (int) (Math.min(point.y(), prevPoint.y()));
                while (countY < (int) (Math.max(point.y(), prevPoint.y()))) {
                    Vector3f p = new Vector3f((countY - q) / m, countY, 0);
                    fluidSim.setBarrier((int) p.x(), (int) p.y(), drawBarrier);
                    countY++;
                }
            } else {
                int countY = (int) (Math.min(point.y(), prevPoint.y()));
                while (countY < (int) (Math.max(point.y(), prevPoint.y()))) {
                    Vector3f p = new Vector3f(point.x(), countY, 0);
                    fluidSim.setBarrier((int) p.x(), (int) p.y(), drawBarrier);
                    countY++;
                }
            }
            prevPoint = point;
        }
    }

    private Vector3f toScreenToSim(Vector3f scrCoords) {
        return new Vector3f(scrCoords.x() / _camera.getScreenWidth() * dimension.x(),
                scrCoords.y() / _camera.getScreenHeight() * dimension.y(), 0);
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
                fluidSim.makeLine((int) (dimension.x() * 0.2f));
                break;
            case 9:
                fluidSim.makeCircle((int) (dimension.x() * 0.15f));
                break;
            case 10:
                float dim = dimension.x() * 0.04f;
                fluidSim.makeDrop(dim, dim * (_camera.getScreenWidth() / _camera.getScreenHeight()));
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
        text.setText(context, "V" + fluidSim.getVelocity());
    }
}
