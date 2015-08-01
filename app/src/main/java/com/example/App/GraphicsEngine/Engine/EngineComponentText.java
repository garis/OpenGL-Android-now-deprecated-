package com.example.App.GraphicsEngine.Engine;

import android.content.Context;

import com.example.App.GraphicsEngine.Utils.FontCharacter;
import com.example.App.GraphicsEngine.Utils.ListCharacter;
import com.example.App.GraphicsEngine.Utils.Vector3f;

public class EngineComponentText extends EngineComponentPlus implements EngineComponent.EngineComponentInterface {

    private ListCharacter chracterList;
    private boolean selected;
    private boolean textureLoaded;
    private boolean isLockedOnUpperLeftCorner;

    private int textureID;

    public EngineComponentText(ListCharacter ChracterList, int textureGL_INDEX) {
        textureLoaded = false;
        isLockedOnUpperLeftCorner = false;
        chracterList = ChracterList;
        textureID = textureGL_INDEX;
    }

    public void update(float dt) {
        if (selected) {
            this.rotate(new Vector3f(0, this._totalTime, 0));
        }
        super.update(dt);
    }

    public void lockedOnUpperLeftCorner(boolean flag) {
        isLockedOnUpperLeftCorner = flag;
    }

    public void toogleSelected() {
        selected = !selected;
    }

    public void setText(Context context, String text) {
        if (text.length() > 0) {
            float[] squareCoords = new float[text.length() * 12];
            float[] uvCoords = new float[text.length() * 8];
            short[] drawOrder = new short[text.length() * 6];
            String txt = "";
            for (int i = text.length() - 1; i >= 0; i--) {
                txt = txt + text.charAt(i);
            }
            text = txt;
            float x = 0;
            for (int i = 0; i < text.length(); i++) {
                FontCharacter fontChar = chracterList.GetFontCharacter(text.charAt(i));
                if (fontChar != null) {
                    float prevX = x;
                    x = x + fontChar.getNormWidth();
                    float y = fontChar.getNormHeight();
                    squareCoords[12 * i] = -prevX;
                    squareCoords[1 + 12 * i] = y / 2;
                    squareCoords[2 + 12 * i] = 0.0f;
                    squareCoords[3 + 12 * i] = -prevX;
                    squareCoords[4 + 12 * i] = -y / 2;
                    squareCoords[5 + 12 * i] = 0.0f;
                    squareCoords[6 + 12 * i] = -x;
                    squareCoords[7 + 12 * i] = -y / 2;
                    squareCoords[8 + 12 * i] = 0.0f;
                    squareCoords[9 + 12 * i] = -x;
                    squareCoords[10 + 12 * i] = y / 2;
                    squareCoords[11 + 12 * i] = 0.0f;

                    uvCoords[8 * i] = fontChar.getX() + fontChar.getWidth();
                    uvCoords[1 + 8 * i] = fontChar.getY();
                    uvCoords[2 + 8 * i] = fontChar.getX() + fontChar.getWidth();
                    uvCoords[3 + 8 * i] = fontChar.getY() + fontChar.getHeight();
                    uvCoords[4 + 8 * i] = fontChar.getX();
                    uvCoords[5 + 8 * i] = fontChar.getY() + fontChar.getHeight();
                    uvCoords[6 + 8 * i] = fontChar.getX();
                    uvCoords[7 + 8 * i] = fontChar.getY();

                    drawOrder[6 * i] = (short) (4 * i);
                    drawOrder[1 + 6 * i] = (short) (1 + 4 * i);
                    drawOrder[2 + 6 * i] = (short) (2 + 4 * i);
                    drawOrder[3 + 6 * i] = (short) (4 * i);
                    drawOrder[4 + 6 * i] = (short) (2 + 4 * i);
                    drawOrder[5 + 6 * i] = (short) (3 + 4 * i);

                }
            }

            if (!isLockedOnUpperLeftCorner) {
                x = x / 2;
            } else {
                x = -x;
            }

            for (int i = 0; i < squareCoords.length / 12; i++) {
                squareCoords[12 * i] = squareCoords[12 * i] - x;
                squareCoords[3 + 12 * i] = squareCoords[3 + 12 * i] - x;
                squareCoords[6 + 12 * i] = squareCoords[6 + 12 * i] - x;
                squareCoords[9 + 12 * i] = squareCoords[9 + 12 * i] - x;
            }
            this.updateGeometryAndUVs(squareCoords, uvCoords, drawOrder);

            if (!textureLoaded) {
                //LoadGLTexture(context, R.drawable.font_0);
                setTextureGL_ID(textureID);
                textureLoaded = true;
                this.updateGeometryAndUVs(squareCoords, uvCoords, drawOrder);
            }
        } else {
            float[] squareCoords = {
                    -1.0f, 1.0f, 0.0f,
                    -1.0f, -1.0f, 0.0f,
                    1.0f, -1.0f, 0.0f,
                    1.0f, 1.0f, 0.0f};

            float[] uvCoords = {
                    0.0f, 0.0f,
                    0.0f, 0.0f,
                    0.0f, 0.0f,
                    0.0f, 0.0f};

            short[] drawOrder = {0, 1, 2, 0, 2, 3};

            this.updateGeometryAndUVs(squareCoords, uvCoords, drawOrder);
        }
    }
}
