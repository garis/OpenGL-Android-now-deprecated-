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
        super.Initialize(context);
        testo = new ArrayList<EngineComponentText>();
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        LoadItems();
        super.onSurfaceCreated(unused, config);

    }

    public void Update() {
        //for move or not the camera

        if ((this.GetLastTouch().X() < this._camera.GetScreenWidth() * 0.2f) && (this.GetLastTouch().Y() < this._camera.GetScreenHeight() * 0.2f)) {
            float x = 0 + (float) (Math.sin(this.getElaspedTime() * multiplier)) * 20;
            float z = (float) (Math.cos(this.getElaspedTime() * multiplier)) * 20;
            this._camera.SetCameraPosition(new Vector3f(x, 0, z));
        } else {
            this._camera.SetCameraPosition(new Vector3f(0, 0, -20));
            this._camera.SetCameraLookAt(new Vector3f(0, 0, 0));
        }

        bunny.Rotate(new Vector3f(0, this.getElaspedTime() / 10, 0));

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
        super.Update();
    }

    public void LoadItems() {

        this._camera.SetCameraPosition(new Vector3f(0, 0, -20));
        this._camera.SetCameraLookAt(new Vector3f(0, 0, 0));
        this.SetWaitTime(30);
        super.ClearItems();

        LoadTextTexture(R.drawable.font_0);

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

        Count = new EngineComponentText(this.chracterList, this.GetTextTextureGL_INDEX());
        Count.Move(new Vector3f(
                ((this._camera.GetCameraPosition().X()) + 11),
                ((this._camera.GetCameraPosition().Y())),
                0));
        Count.SetScale(new Vector3f(2.5f, 2.5f, 2.5f));
        Count.SetText(context, " ");
        Count.SetColor(new float[]{1.0f, 0.0f, 0.0f, 1.0f});
        Count.SetRenderDepth(0.8f);
        Count.SetID("Count");

        Count.SetRenderDepth(0);

        super.LoadItems(Count, false, null, null, false);

        boolean textStress = true;
        if (textStress) {
            int tot = 3;
            for (int i = 0; i < tot; i++) {
                for (int j = 0; j < tot; j++) {
                    EngineComponentText text = new EngineComponentText(this.chracterList, this.GetTextTextureGL_INDEX());
                    text.Move(new Vector3f(
                            ((this._camera.GetCameraPosition().X()) - (tot * 2.1f / 2) + (i * 2.1f)),
                            ((this._camera.GetCameraPosition().Y() - (tot * 2.1f / 2) + (j * 2.1f))),
                            0));
                    text.SetScale(new Vector3f(1.5f, 1.5f, 1.5f));
                    text.SetText(context, "" + i + ";" + j);
                    text.SetColor(new float[]{1.0f, 0.0f, 0.0f, 1.0f});
                    text.SetRenderDepth(0.8f);
                    text.SetID("" + (i + j));

                    super.LoadItems(text, false, null, null, false);
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
                line.Move(new Vector3f((this._camera.GetCameraPosition().X() - gap * 5) + (gap * i), this._camera.GetCameraPosition().Y(), 0));
                line.LoadFromOBJ(context, "plane");
                line.LoadGLTexture(context, R.drawable.white, false);
                line.SetColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
                line.SetScale(new Vector3f(0.1f, 100, 1));
                line.SetID("gridV" + i);
                super.LoadItems(line, false, null, null, false);
            }

            for (int i = 0; i < 3; i++) {
                EngineComponent3D line = new EngineComponent3D();
                line.Move(new Vector3f(this._camera.GetCameraPosition().X(), (this._camera.GetCameraPosition().Y() - gap * 5) + (gap * i), 0));
                line.LoadFromOBJ(context, "plane");
                line.LoadGLTexture(context, R.drawable.white, false);
                line.SetColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
                line.SetScale(new Vector3f(100, 0.1f, 1));
                line.SetID("gridO" + i);
                super.LoadItems(line, false, null, null, false);
            }
        }

        bunny = new EngineComponent3D();
        bunny.Move(new Vector3f((this._camera.GetCameraPosition().X()), this._camera.GetCameraPosition().Y(), 0));
        bunny.LoadFromOBJ(context, "bunny");
        // line.LoadGLTexture(context, R.drawable.white, false);
        bunny.SetColor(new float[]{0.67f, 0.67f, 0.67f, 1.0f});
        bunny.SetScale(new Vector3f(1, 1, 1));
        bunny.SetID("bunny");
        super.LoadItems(bunny, false, null, null, false);

        super.LoadItems();
    }
}
