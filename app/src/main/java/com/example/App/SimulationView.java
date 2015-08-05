package com.example.App;

import android.content.Context;

import com.example.App.GraphicsEngine.Engine.EngineComponentText;
import com.example.App.GraphicsEngine.Engine.EngineGLRenderer;
import com.example.App.GraphicsEngine.Utils.Vector3;
import com.example.App.LatticeBoltzmannSim.EngineComponentSimColorPanel;
import com.example.App.LatticeBoltzmannSim.LatticeBoltzmann;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SimulationView extends EngineGLRenderer {

    boolean isStopped = false;
    boolean isTracersEnable = false;
    LatticeBoltzmann fluidSim;
    int value = 220;
    Vector3 dimension = new Vector3(value, value / 16 * 9, 0);
    EngineComponentText text;
    EngineComponentText textTot;
    // EngineComponentParticles particles;
    int drawMode = 0;
    float contrast = 20;
    int textCount = 0;
    int tot = 0;
    private boolean drawBarrier;
    private EngineComponentSimColorPanel panel;
    private List<Vector3> touchedPoints;

    private FloatBuffer colorBuffer;

    public SimulationView(Context context) {
        super.initialize(context);
    }

    public synchronized void update() {

        if (!isStopped) {
            fluidSim.SimStep();

            fluidSim.SimStep();


            fluidSim.computeColor(drawMode, contrast);


            if (isTracersEnable)
                fluidSim.moveTracers();
            //panel.setColorBuffer(fluidSim.getColorMatrix());
            fluidSim.fillBuffer(colorBuffer);

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
        text.move(new Vector3(-0.99f, -0.95f, 0));
        text.setScale(new Vector3(0.05f, 0.05f * 16 / 9, 0.05f));
        text.setText(context, "V" + fluidSim.getVelocity());
        text.setColor(new float[]{1.0f, 0.0f, 0.0f, 1.0f});
        //text.SetRenderDepth(0.8f);
        text.setID("STATS");
        text.lockedOnUpperLeftCorner(true);
        text.isIn3dSpace(false);
        super.loadItems(text, false, null, null, false);


        textTot = new EngineComponentText(this.chracterList, this.getTextTextureGL_INDEX());
        textTot.move(new Vector3(-0.99f, -0.85f, 0));
        textTot.setScale(new Vector3(0.05f, 0.05f * 16 / 9, 0.05f));
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
        tracers[0].SetScale(new Vector3(0.05f,0.05f,0.05f));

        tracers[0].SetColor(new float[]{1.0f,1.0f,1.0f});
        super.LoadItems( tracers[0], false, null, null,false);
*/
        colorBuffer = panel.getColorBufferPointer();

        super._camera.setCameraPosition(new Vector3(0, 0, -10));
        super._camera.setCameraLookAt(new Vector3(0, 0, 0));
        super.loadItems();
    }

    public void touchDown(Vector3 screenCoords) {
        Vector3 objCoords = toScreenToSim(screenCoords);
        drawBarrier = !fluidSim.isBarrier((int) objCoords.x(), (int) objCoords.y());
        touchedPoints = new ArrayList<Vector3>();
        touchedPoints.add(objCoords);

    }

    public void touchMove(Vector3 screenCoords) {
        Vector3 objCoords = toScreenToSim(screenCoords);
        touchedPoints.add(objCoords);
    }

    public void touchUp(Vector3 screenCoords) {
        Vector3 objCoords = toScreenToSim(screenCoords);
        touchedPoints.add(objCoords);
        drawBarrier(touchedPoints);
    }

    private void drawBarrier(List<Vector3> touchedPoints) {
        Iterator<Vector3> iterator = touchedPoints.iterator();
        Vector3 prevPoint = iterator.next();
        while (iterator.hasNext()) {
            Vector3 point = iterator.next();

            if (point.x() != prevPoint.x()) {
                double m = ((point.y() - prevPoint.y()) / (point.x() - prevPoint.x()));
                double q = ((point.x() * prevPoint.y()) - (prevPoint.x() * point.y())) / (point.x() - prevPoint.x());
                double countX = (Math.min(point.x(), prevPoint.x()));
                while (countX < Math.max(point.x(), prevPoint.x())) {
                    Vector3 p = new Vector3(countX, (m * countX + q), 0);
                    fluidSim.setBarrier((int) p.x(), (int) p.y(), drawBarrier);
                    countX = countX + 0.2D;
                }
                double countY = Math.min(point.y(), prevPoint.y());
                while (countY < Math.max(point.y(), prevPoint.y())) {
                    Vector3 p = new Vector3((countY - q) / m, countY, 0);
                    fluidSim.setBarrier((int) p.x(), (int) p.y(), drawBarrier);
                    countY = countY + 0.2D;
                }
            } else {
                double countY = Math.min(point.y(), prevPoint.y());
                while (countY < Math.max(point.y(), prevPoint.y())) {
                    Vector3 p = new Vector3(point.x(), countY, 0);
                    fluidSim.setBarrier((int) p.x(), (int) p.y(), drawBarrier);
                    countY = countY + 0.2D;
                }
            }
            prevPoint = point;
        }
    }

    private Vector3 toScreenToSim(Vector3 scrCoords) {
        return new Vector3(scrCoords.x() / _camera.getScreenWidth() * dimension.x(),
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
                double dim = dimension.x() * 0.04d;
                fluidSim.makeDrop((float) dim, (float) (dim * (_camera.getScreenWidth() / _camera.getScreenHeight())));
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
