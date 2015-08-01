package com.example.App;

import android.content.Context;

import com.example.App.GraphicsEngine.Engine.EngineComponent3D;
import com.example.App.GraphicsEngine.Engine.EngineComponentText;
import com.example.App.GraphicsEngine.Engine.EngineGLRenderer;
import com.example.App.GraphicsEngine.Utils.Vector3f;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class Game extends EngineGLRenderer {

    static boolean _gearAction;
    static boolean _camaroAction;
    int prev = 0;
    boolean prevCFR = false;
    int count = 0;
    EngineComponentText Count;
    EngineComponent3D bunny;
    float multiplier = 0.0001f;
    private EngineComponent3D camaro;
    private List<EngineComponentText> testo;

    public Game(Context context) {
        super.initialize(context);
        testo = new ArrayList<EngineComponentText>();
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        LoadItems();
        super.onSurfaceCreated(unused, config);

    }

    public void Update() {
        //for move or not the camera

        if ((this.getLastTouch().x() < this._camera.getScreenWidth() * 0.2f) && (this.getLastTouch().y() < this._camera.getScreenHeight() * 0.2f)) {
            float x = 0 + (float) (Math.sin(this.getElaspedTime() * multiplier)) * 20;
            float z = (float) (Math.cos(this.getElaspedTime() * multiplier)) * 20;
            this._camera.setCameraPosition(new Vector3f(x, 0, z));
        } else {
            this._camera.setCameraPosition(new Vector3f(0, 0, -20));
            this._camera.setCameraLookAt(new Vector3f(0, 0, 0));
        }

        bunny.rotate(new Vector3f(0, this.getElaspedTime() / 10, 0));

    	/*if(_camaroAction)
        {
    		camaro.Rotate(new Vector3f(0,this.GetTotalTime()*20,0));
    	}*/

        EngineComponentText txt;
        int time = (int) (this.getElaspedTime() * 100);
        if (time != prev) {
            /*prev=time;
    		Iterator<Text> iterator = testo.iterator();
    		int cfr=0;
    		prevCFR=!prevCFR;
    		if(!prevCFR)
    		{
    			cfr=1;
    		}
     		while (iterator.hasNext()) {
     			txt=iterator.next();
     		
     			//if(Integer.parseInt(txt.GetID())%2==cfr)
     			//	txt.SetText(context, "�");
     			//else
     			//	txt.SetText(context, "");
     			txt.SetText(context, ""+(int)(Math.random()*100000));
     			count++;
     			Count.SetText(context, ""+count);
     			//txt.SetColor(new float[]{(float) Math.random(),(float) Math.random(),(float) Math.random(),(float) Math.random()});
     		}*/
        }
        super.update();
    }

    public void LoadItems() {

        this._camera.setCameraPosition(new Vector3f(0, 0, -20));
        this._camera.setCameraLookAt(new Vector3f(0, 0, 0));
        this.setWaitTime(30);
        super.clearItems();

        loadTextTexture(R.drawable.font_0);

	    /*Text textB=new Text(this.chracterList);
	    textB.Move(new Vector3f(
				((this._camera.GetCameraPosition().X())),
				((this._camera.GetCameraPosition().Y())),
				0));
	    
	    textB.SetText(context, "B 0.3f");
	    textB.SetColor(new float[]{0.0f,0.0f,1.0f,1.0f});
	    textB.SetRenderDepth(0.8f);
	    textB.SetID("B");
	    
	    
	    Text textG=new Text(this.chracterList);
	    textG.Move(new Vector3f(
				((this._camera.GetCameraPosition().X())),
				((this._camera.GetCameraPosition().Y())),
				0));
	    
	   // textG.SetText(context, "G 0.5f");
	    textG.SetColor(new float[]{0.0f,1.0f,0.0f,1.0f});
	    textG.SetRenderDepth(0.5f);
	    textG.SetID("G");
	    
	    
	    Text textR=new Text(this.chracterList);
	    textR.Move(new Vector3f(
				((this._camera.GetCameraPosition().X())),
				((this._camera.GetCameraPosition().Y())),
				0));
	    
	    //textR.SetText(context, "R 0.7f");
	    textR.SetColor(new float[]{1.0f,0.0f,0.0f,1.0f});
	    textR.SetRenderDepth(0.7f);
	    textR.SetID("R");

	    super.LoadItems(textR);
	    super.LoadItems(textB);
	    super.LoadItems(textG);
	    */

        Count = new EngineComponentText(this.chracterList, this.getTextTextureGL_INDEX());
        Count.move(new Vector3f(
                ((this._camera.getCameraPosition().x()) + 11),
                ((this._camera.getCameraPosition().y())),
                0));
        Count.setScale(new Vector3f(2.5f, 2.5f, 2.5f));
        Count.setText(context, " ");
        Count.setColor(new float[]{1.0f, 0.0f, 0.0f, 1.0f});
        Count.setRenderDepth(0.8f);
        Count.setID("Count");

        Count.setRenderDepth(0);

        super.loadItems(Count, false, null, null, false);

        boolean textStress = true;
        if (textStress) {
            int tot = 3;
            for (int i = 0; i < tot; i++) {
                for (int j = 0; j < tot; j++) {
                    EngineComponentText text = new EngineComponentText(this.chracterList, this.getTextTextureGL_INDEX());
                    text.move(new Vector3f(
                            ((this._camera.getCameraPosition().x()) - (tot * 2.1f / 2) + (i * 2.1f)),
                            ((this._camera.getCameraPosition().y() - (tot * 2.1f / 2) + (j * 2.1f))),
                            0));
                    text.setScale(new Vector3f(1.5f, 1.5f, 1.5f));
                    text.setText(context, "" + i + ";" + j);
                    text.setColor(new float[]{1.0f, 0.0f, 0.0f, 1.0f});
                    text.setRenderDepth(0.8f);
                    text.setID("" + (i + j));

                    super.loadItems(text, false, null, null, false);
                    testo.add(text);
                }
            }
        }
	    
	    /*
	    Text text=new Text(this.chracterList);
	    text.Move(new Vector3f(
				((this._camera.GetCameraPosition().X())),
				((this._camera.GetCameraPosition().Y())),
				5));
	    
	    text.SetText(context, "Riccardo Gasparini");
	    super.LoadItems(text);
	          
	    Model3d gear = new Model3d();
		gear.Move(
				new Vector3f(
							((this._camera.GetCameraPosition().X()+10)),
							((this._camera.GetCameraPosition().Y()+10)),
							5)
				);
		gear.LoadFromOBJ(context, "gear");
		gear.LoadGLTexture(context, R.drawable.geartexture);
		gear.SetColor(new float[]{1.0f,1.0f,1.0f,1.0f});
		gear.SetScale(new Vector3f(3,3,3));
		gear.SetID("gear");
        super.LoadItems(gear);
        
        
	    camaro = new Model3d();
	    camaro.Move(
				new Vector3f(
							(this._camera.GetCameraPosition().X()-10),
							(this._camera.GetCameraPosition().Y()-10),
							5)
				);
		camaro.LoadFromOBJ(context, "camaro");
		camaro.LoadGLTexture(context, R.drawable.white);
		camaro.SetColor(new float[]{3.0f,3.0f,3.0f,3.0f});
		camaro.SetScale(new Vector3f(6,6,6));
		camaro.SetID("camaro");
        super.LoadItems(camaro);
        */


        boolean gridIsVisible = true;
        if (gridIsVisible) {
            float gap = 2.5f;

            for (int i = 0; i < 3; i++) {
                EngineComponent3D line = new EngineComponent3D();
                line.move(new Vector3f((this._camera.getCameraPosition().x() - gap * 5) + (gap * i), this._camera.getCameraPosition().y(), 0));
                line.loadFromOBJ(context, "plane");
                line.loadGLTexture(context, R.drawable.white, false);
                line.setColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
                line.setScale(new Vector3f(0.1f, 100, 1));
                line.setID("gridV" + i);
                super.loadItems(line, false, null, null, false);
            }

            for (int i = 0; i < 3; i++) {
                EngineComponent3D line = new EngineComponent3D();
                line.move(new Vector3f(this._camera.getCameraPosition().x(), (this._camera.getCameraPosition().y() - gap * 5) + (gap * i), 0));
                line.loadFromOBJ(context, "plane");
                line.loadGLTexture(context, R.drawable.white, false);
                line.setColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
                line.setScale(new Vector3f(100, 0.1f, 1));
                line.setID("gridO" + i);
                super.loadItems(line, false, null, null, false);
            }
        }

        bunny = new EngineComponent3D();
        bunny.move(new Vector3f((this._camera.getCameraPosition().x()), this._camera.getCameraPosition().y(), 0));
        bunny.loadFromOBJ(context, "bunny");
        // line.LoadGLTexture(context, R.drawable.white, false);
        bunny.setColor(new float[]{0.67f, 0.67f, 0.67f, 1.0f});
        bunny.setScale(new Vector3f(1, 1, 1));
        bunny.setID("bunny");
        super.loadItems(bunny, false, null, null, false);

        super.loadItems();
    }
}
