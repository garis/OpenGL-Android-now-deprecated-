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

    public int GetID() {
        return ID;
    }

    public float GetX() {
        return x;
    }

    public float GetY() {
        return y;
    }

    public float GetWidth() {
        return width;
    }

    public float GetHeight() {
        return height;
    }

    public float GetNormWidth() {
        return normWidth;
    }

    public float GetNormHeight() {
        return normHeight;
    }

    public boolean DecodeFromString(String str, Vector3f textureDimension) {
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
            Normalize(textureDimension);

            return true;
        }

        return false;
    }

    private void Normalize(Vector3f textureDimension) {
        y = (y / textureDimension.Y());
        x = x / textureDimension.X();
        width = width / textureDimension.X();
        height = height / textureDimension.Y();
    }

    public void NormalizeDimension(Vector3f value) {
        normWidth = width / value.X();
        normHeight = height / value.Y();
    }
}
