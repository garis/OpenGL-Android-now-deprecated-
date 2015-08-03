package com.example.App.GraphicsEngine.Utils;

public class FontCharacter {

    float x, y, width, height;
    float normWidth, normHeight;
    int ID;

    public FontCharacter() {
        x = 0;
        y = 0;
        width = 0;
        height = 0;
        normWidth = 0;
        normHeight = 0;
        ID = -1;
    }

    public int getID() {
        return ID;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getNormWidth() {
        return normWidth;
    }

    public float getNormHeight() {
        return normHeight;
    }

    public boolean decodeFromString(String str, Vector3 textureDimension) {
        if (str.compareTo("") != 0) {
            String[] line = str.split(" ");

            if (line[0].compareTo("char") == 0) {
                for (int i = 0; i < line.length; i++) {
                    String[] segment = line[i].split("=");
                    if (segment[0].compareTo("id") == 0) {
                        ID = Integer.parseInt(segment[1]);
                    } else if (segment[0].compareTo("x") == 0) {
                        x = Float.parseFloat(segment[1]);
                    } else if (segment[0].compareTo("y") == 0) {
                        y = Float.parseFloat(segment[1]);
                    } else if (segment[0].compareTo("width") == 0) {
                        width = Float.parseFloat(segment[1]);
                    } else if (segment[0].compareTo("height") == 0) {
                        height = Float.parseFloat(segment[1]);
                    }
                }
            }
        }

        if (ID != -1) {
            normalize(textureDimension);
            return true;
        }

        return false;
    }

    private void normalize(Vector3 textureDimension) {
        y = (float) (y / textureDimension.y());
        x = (float) (x / textureDimension.x());
        width = (float) (width / textureDimension.x());
        height = (float) (height / textureDimension.y());
    }

    public void normalizeDimension(Vector3 value) {
        normWidth = (float) (width / value.x());
        normHeight = (float) (height / value.y());
    }
}
