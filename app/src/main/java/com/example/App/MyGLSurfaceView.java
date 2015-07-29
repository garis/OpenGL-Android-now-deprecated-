/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.App;


import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Debug;
import android.view.MotionEvent;

import com.example.App.GraphicsEngine.Engine.EngineGLRenderer;
import com.example.App.GraphicsEngine.Utils.Vector3f;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    boolean DebugTracing = false;
    Thread thread;
    long startTime;
    //private float _totalTime;
    boolean GameOn;
    long endTime;
    int count = 0;
    private EngineGLRenderer mRenderer;
    private Menu menu;
    private Game game;
    private SimulationView sim;
    private SensorExperiment exp;
    private EngineGLRenderer active;
    private long begining = 0;
    private long _dt = 0;
    private long creationTIme;

    public MyGLSurfaceView(Context context) {
        super(context);

        if (DebugTracing)
            Debug.startMethodTracing("dmtraceNew", 60000000);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);


        //this is the method to create a new "glRender"
        /*game=new Game(context);
        setRenderer(game);
        active = game;*/

        //FOR THE LATTICE BOLTZMANN SIMULATION
        sim = new SimulationView(context);
        setRenderer(sim);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        active = sim;

        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setAndStart();

        /*exp=new SensorExperiment(context);
        setRenderer(exp);
        active = exp;*/
    }

    private void setAndStart() {
        creationTIme = System.currentTimeMillis();
        thread = new Thread() {
            @Override
            public void run() {
                Cicle();
            }
        };
        active.setGameOn(true);
        thread.start();
    }

    private void Cicle() {
        while (active.isGameOn()) {
            startTime = System.currentTimeMillis();

            active.updateElaspedTime(startTime - creationTIme);

            if (active.isReadyToStart()) {
                active.Update();

                this.requestRender();
            }

            endTime = System.currentTimeMillis();
            _dt = (endTime - startTime);

            if (_dt < active.getWaitTime()) {
                try {
                    thread.sleep(active.getWaitTime() - _dt);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            count++;
            if (count > 50)
                if (DebugTracing)
                    Debug.stopMethodTracing();
        }
    }

    public void changeSettings(int actionID) {
        sim.changeSettings(actionID);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getPointerCount() > 1) {
        } else {
            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    //active.MoveScreenAction(e.getX(), e.getY());

                    active.TouchMove(new Vector3f(e.getX(), e.getY(), 0));
                    break;

                case MotionEvent.ACTION_DOWN:
                    //active.TouchedScreen(e.getX(),e.getY());
                    //active.IntersectObject(new Vector3f(e.getX(),e.getY(),0));
                    active.TouchDown(new Vector3f(e.getX(), e.getY(), 0));
                    break;

                case MotionEvent.ACTION_UP:
                    active.TouchUp(new Vector3f(e.getX(), e.getY(), 0));
                    break;
            }
        }
        return true;
    }
}
