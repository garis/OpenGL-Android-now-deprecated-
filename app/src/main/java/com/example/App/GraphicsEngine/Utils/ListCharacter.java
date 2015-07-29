package com.example.App.GraphicsEngine.Utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListCharacter {

    public static List<FontCharacter> chracterList;
    private FontCharacter[] vectorChracterList;

    public void ListCharacter() {
        chracterList = new ArrayList<FontCharacter>();
    }

    public FontCharacter GetFontCharacter(char Char) {
        return SearchCharacter((int) Char, 0, vectorChracterList.length - 1);
    }

    private FontCharacter SearchCharacter(int n, int min, int max) {
        while (max >= min) {
            int mid = min + (max - min / 2);
            if (vectorChracterList[mid].GetID() == n)
                return vectorChracterList[mid];
            else if (vectorChracterList[mid].GetID() < n) {
                min = min + 1;
            } else if (vectorChracterList[mid].GetID() > n) {
                max = max - 1;
            }
        }
        return new FontCharacter();
    }

    public void LoadFNT_File(Context context, String name) {
        BufferedReader br = null;
        String sCurrentLine = "";
        List<String> file = new ArrayList<String>();

        try {
            InputStream is = context.getAssets().open("font/" + name + ".fnt");
            InputStreamReader inputStreamReader = new InputStreamReader(is);

            br = new BufferedReader(inputStreamReader);
            while ((sCurrentLine = br.readLine()) != null) {
                if (sCurrentLine.length() >= 1) {
                    file.add(sCurrentLine);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


        chracterList = new ArrayList<FontCharacter>();
        Iterator<String> iterator = file.iterator();
        iterator.next();
        String[] line = iterator.next().split(" ");
        Vector3f textureDim = new Vector3f();
        if (line[0].compareTo("common") == 0) {
            for (int i = 0; i < line.length; i++) {
                String[] segment = line[i].split("=");
                if (segment[0].compareTo("scaleW") == 0) {
                    textureDim.X(Float.parseFloat(segment[1]));
                }
                if (segment[0].compareTo("scaleH") == 0) {
                    textureDim.Y(Float.parseFloat(segment[1]));
                }
            }
        }
        float maxHeight = 0;
        float maxWidth = 0;
        while (iterator.hasNext()) {
            FontCharacter character = new FontCharacter();
            if (character.DecodeFromString(iterator.next(), textureDim)) {
                chracterList.add(character);
                if (character.GetHeight() > maxHeight)
                    maxHeight = character.GetHeight();
                if (character.GetWidth() > maxWidth)
                    maxWidth = character.GetWidth();

            }
        }

        Iterator<FontCharacter> normIterator = chracterList.iterator();
        while (normIterator.hasNext()) {
            normIterator.next().NormalizeDimension(new Vector3f(maxWidth, maxHeight, 0));
        }

        vectorChracterList = new FontCharacter[chracterList.size()];
        chracterList.toArray(vectorChracterList);
    }
}
