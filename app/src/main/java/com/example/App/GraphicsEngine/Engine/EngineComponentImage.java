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
package com.example.App.GraphicsEngine.Engine;

import com.example.App.GraphicsEngine.Utils.Vector3f;


/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class EngineComponentImage extends EngineComponentPlus implements EngineComponent.EngineComponentInterface {

    private EngineEvent event;
    private boolean selected;

    public EngineComponentImage() {
        super();

        selected = false;
    }

    public void Draw(float[] mViewMatrix, float[] mProjectionMatrix, float time) {
        super.Draw(mViewMatrix, mProjectionMatrix, time);
    }

    public void Update(float dt) {
        if (selected) {
            this.Rotate(new Vector3f(0, 0, _totalTime * 20));
        }
    }

    public void LinkEvent(EngineEvent a) {
        event = a;
    }

    public void DoAction(boolean restart) {
        if (restart)
            _eventTime = 0;
        event.DoAction();
    }

    public void SetSelected(boolean flag) {
        selected = flag;
    }

    public void ToogleSelected() {
        selected = !selected;
    }
}