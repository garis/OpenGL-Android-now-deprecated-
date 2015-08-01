package com.example.App.GraphicsEngine.Utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OBJParser {

    private float[] V = {            //vertices coords
            -1.0f, 1.0f, 0.0f,   // top left
            -1.0f, -1.0f, 0.0f,   // bottom left
            1.0f, -1.0f, 0.0f,   // bottom right
            1.0f, 1.0f, 0.0f}; // top right;

    private float[] VT = {        //texture coords
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f};

    private short F0[] = {0, 1, 2, 0, 2, 3}; // order to draw vertices

    private List<Point> VList;
    private List<Point> VTList;
    private List<Integer> F0List;
    private List<Integer> F1List;
    private List<String> file;

    public OBJParser() {
    }

    public boolean loadFromOBJ(Context context, String name) {
        BufferedReader br = null;
        String sCurrentLine = "";
        file = new ArrayList<String>();
        boolean flag = false;

        try {
            InputStream is = context.getAssets().open(name + ".obj");
            InputStreamReader inputStreamReader = new InputStreamReader(is);

            br = new BufferedReader(inputStreamReader);
            while ((sCurrentLine = br.readLine()) != null) {
                if ((sCurrentLine.length() >= 1) && (sCurrentLine.charAt(0) != '#')) {
                    file.add(sCurrentLine);
                }
            }
            flag = true;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return flag;
    }

    private void createVertices() {
        VList = new ArrayList<Point>();
        VTList = new ArrayList<Point>();
        F0List = new ArrayList<Integer>();
        F1List = new ArrayList<Integer>();

        int max = 0;

        Iterator<String> iterator = file.iterator();
        while (iterator.hasNext()) {
            String[] str = iterator.next().split("\\s+");

            if (str[0].length() == 1) {
                if (str[0].charAt(0) == 'v') {
                    VList.add(new Point(Float.parseFloat(str[1]), Float.parseFloat(str[2]), Float.parseFloat(str[3]), 0));
                } else if (str[0].charAt(0) == 'f') {
                    for (int i = 1; i < str.length; i++) {
                        String[] item = str[i].split("/");
                        //vertex
                        F0List.add(Integer.parseInt(item[0]) - 1);
                        //uv and max index
                        int n = Integer.parseInt(item[1]) - 1;
                        if (max < n)
                            max = Integer.parseInt(item[1]);
                        F1List.add(n);
                    }
                }
            } else if ((str[0].length() == 2) && (str[0].charAt(0) == 'v') && (str[0].charAt(1) == 't')) {
                VTList.add(new Point(Float.parseFloat(str[1]), Float.parseFloat(str[2]), 0, 0));
            }
        }

        V = new float[max * 3 + 3];
        VT = new float[max * 2 + 2];
        F0 = new short[F1List.size()];

        Iterator<Integer> iterator0 = F0List.iterator();
        Iterator<Integer> iterator1 = F1List.iterator();

        int count0 = 0;

        while (iterator0.hasNext()) {
            int n0 = iterator0.next();
            int n1 = iterator1.next();

            V[n1 * 3] = VList.get(n0).getX();
            V[n1 * 3 + 1] = VList.get(n0).getY();
            V[n1 * 3 + 2] = VList.get(n0).getZ();

            VT[n1 * 2] = VTList.get(n1).getX();
            VT[n1 * 2 + 1] = VTList.get(n1).getY();
        }

        //draw order
        Iterator<Integer> iteratorInteger = F1List.iterator();
        count0 = 0;
        while (iteratorInteger.hasNext()) {
            F0[count0] = (Short.parseShort(iteratorInteger.next().toString()));
            count0++;
        }
    }

    public float[] getVertices() {
        createVertices();
        return V;
    }

    public float[] getUVVertices() {
        return VT;
    }

    public short[] getOrder() {
        return F0;
    }

    class Point {

        float x, y, z;
        int index;

        public Point() {

        }

        public Point(float valueX, float valueY, float valueZ, int valueIndex) {
            x = valueX;
            y = valueY;
            z = valueZ;
            index = valueIndex;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getZ() {
            return z;
        }
    }
}
