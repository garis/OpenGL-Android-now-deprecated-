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

    private FontCharacter[] vectorChracter;

    public void ListCharacter() {
    }

    public FontCharacter GetFontCharacter(char Char) {
        return vectorChracter[(int) Character.valueOf(Char)];
    }

    public void LoadFNT_File(Context context, String name) {
        BufferedReader br = null;
        String sCurrentLine = "";
        List<String> file = new ArrayList<String>();

        //load the file
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

        //load font information
        List<FontCharacter> chracterList = new ArrayList<FontCharacter>();
        Iterator<String> iterator = file.iterator();
        iterator.next();
        String[] line = iterator.next().split(" ");
        Vector3 textureDim = new Vector3();
        if (line[0].compareTo("common") == 0) {
            for (int i = 0; i < line.length; i++) {
                String[] segment = line[i].split("=");
                if (segment[0].compareTo("scaleW") == 0) {
                    textureDim.x(Float.parseFloat(segment[1]));
                }
                if (segment[0].compareTo("scaleH") == 0) {
                    textureDim.y(Float.parseFloat(segment[1]));
                }
            }
        }

        //search for the max height, width and ascii values
        float maxHeight = 0;
        float maxWidth = 0;
        int maxASCIIValue = 0;
        while (iterator.hasNext()) {
            FontCharacter character = new FontCharacter();
            if (character.decodeFromString(iterator.next(), textureDim)) {
                chracterList.add(character);
                if (character.getHeight() > maxHeight)
                    maxHeight = character.getHeight();
                if (character.getWidth() > maxWidth)
                    maxWidth = character.getWidth();
                if ((int) character.getID() > maxASCIIValue)
                    maxASCIIValue = (int) character.getID();
            }
        }

        //normalize dimensions
        Iterator<FontCharacter> normIterator = chracterList.iterator();
        while (normIterator.hasNext()) {
            normIterator.next().normalizeDimension(new Vector3(maxWidth, maxHeight, 0));
        }

        //fill the array with only the stuff we have, it's better later when we search in it
        vectorChracter = new FontCharacter[maxASCIIValue + 1];
        Iterator<FontCharacter> iteratorChar = chracterList.listIterator();
        while (iteratorChar.hasNext()) {
            FontCharacter c = iteratorChar.next();
            vectorChracter[c.getID()] = c;
        }
    }
}
