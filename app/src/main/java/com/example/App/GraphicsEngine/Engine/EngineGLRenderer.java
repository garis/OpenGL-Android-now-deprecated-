
package com.example.App.GraphicsEngine.Engine;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build;
import android.util.Log;

import com.example.App.GraphicsEngine.Utils.Camera;
import com.example.App.GraphicsEngine.Utils.ListCharacter;
import com.example.App.GraphicsEngine.Utils.Vector3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class EngineGLRenderer implements EngineGLRendererInterface {
    private static final String TAG = "MyGLRenderer";
    private final String vertexShaderCode =
           /* This matrix member variable provides a hook to manipulate
             the coordinates of the objects that use this vertex shader*/
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
            /*texture location as input*/
                    "attribute vec2 a_texCoord;" +
            /*...and gives it to the fragment shader */
                    "varying vec2 v_texCoord;" +

                    "void main() {" +
            /*
             The matrix must be included as a modifier of gl_Position.
             Note that the uMVPMatrix factor *must be first* in order
             for the matrix multiplication product to be correct.
            */
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  v_texCoord = a_texCoord;" +
                    "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec2 v_texCoord;" +
                    "uniform sampler2D s_texture;" +
                    "uniform vec4 vColor;" +

                    "void main() {" +
                    "  gl_FragColor = vColor * texture2D( s_texture, v_texCoord );" +
                    "}";
    public EngineComponent.EngineComponentInterface currentObject;
    public Camera _camera;
    protected Context context;
    protected ListCharacter chracterList;
    protected int[] textures = new int[1];
    long startTime;
    boolean GameOn;
    long wait = 35;
    long endTime;
    float tot = 0;
    float time = 0;
    //Thread thread;
    private List<EngineComponent.EngineComponentInterface> selectedObjects;
    private List<EngineComponent.EngineComponentInterface> _images;
    private long _dt;
    private float _totalTime;
    private Vector3 _lastTouch;
    private float ZNear;
    private float ZFar;
    private boolean isUpdated;
    private boolean drawCalled = false;
    private int[] GL_INDEX = new int[]{-1, -1, -1};
    private boolean isReadyToStart = false;
    private long begining = 0;

    /**
     * Utility method for compiling a OpenGL shader.
     * <p/>
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type       - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     * <p/>
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    public void initialize(Context androidContext) {
        begining = System.currentTimeMillis();
        isUpdated = false;
        context = androidContext;
        _dt = 0;
        _totalTime = 0;

        _camera = new Camera();

        _lastTouch = new Vector3(0, 0, 0);

        selectedObjects = new ArrayList<EngineComponent.EngineComponentInterface>();
        _images = new ArrayList<EngineComponent.EngineComponentInterface>();

        chracterList = new ListCharacter();
        chracterList.LoadFNT_File(context, "font");
    }

    public int getTextTextureGL_INDEX() {
        return textures[0];
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        //Event example
        /*
           _littleMan = new Image();

        Event anA = new Event() {
        	  @Override
        	  public void DoAction() {
        		  _littleMan.SetColor(new float[]{0,0,0,0});
        	  }
        	};
        	_littleMan.LinkEvent(anA);
        	*/
    }

    //[0]=vertex;[1]=fragment;[2]=program
    private void compileShader() {
        if (GL_INDEX[0] == -1) {
            // prepare shaders and OpenGL program
            GL_INDEX[0] = EngineGLRenderer.loadShader(
                    GLES20.GL_VERTEX_SHADER,
                    vertexShaderCode);

            GL_INDEX[1] = EngineGLRenderer.loadShader(
                    GLES20.GL_FRAGMENT_SHADER,
                    fragmentShaderCode);


            GL_INDEX[2] = GLES20.glCreateProgram();             // create empty OpenGL Program
            GLES20.glAttachShader(GL_INDEX[2], GL_INDEX[0]);   // add the vertex shader to program
            //GLES20.glAttachShader(mProgram, fragmentShaderOnlyColor); // add the fragment shader to program
            GLES20.glAttachShader(GL_INDEX[2], GL_INDEX[1]);

            GLES20.glLinkProgram(GL_INDEX[2]);                  // create OpenGL program executables
        }
    }

    public void loadTextTexture(int fontID) {
        if (textures[0] == 0) {
            //LOAD FONT TEXTURE
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), fontID);

            //switches on the ability to use two-dimensional images
            GLES20.glEnable(GLES20.GL_TEXTURE_2D);
            //ability to combine images in interesting ways by specifying how the source and destination will be combined
            GLES20.glEnable(GLES20.GL_BLEND);
            //defines how the source image and the destination image or surface are combined
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            // generate one texture pointer
            GLES20.glGenTextures(1, textures, 0);
            // ...and bind it to our array
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            //load texture

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        }
    }

    public boolean isReadyToStart() {
        return isReadyToStart;
    }

    public void loadItems() {
        isReadyToStart = true;
    }

    public void loadItems(EngineComponent.EngineComponentInterface obj, boolean orderByRenderDepth, String vertexShader, String fragmentShader, boolean hasPrivateShaderCode) {
        if (!hasPrivateShaderCode) {
            if ((vertexShader == null) && (fragmentShader == null)) {
                compileShader();
                obj.setGL_POINTER(GL_INDEX[2]);
            } else {
                if ((vertexShader != null) && (fragmentShader == null)) {
                    obj.setGL_POINTER(vertexShader, fragmentShaderCode);
                } else {
                    obj.setGL_POINTER(vertexShader, fragmentShader);
                }
            }
        }

        if (obj.getRenderDepth() < 0) {
            if (_images.size() > 0)
                obj.setRenderDepth(_images.get(_images.size() - 1).getRenderDepth() - 0.1f);
            else
                obj.setRenderDepth(0.5f);
        }

        boolean result = false;

        Iterator<EngineComponent.EngineComponentInterface> iterator = _images.iterator();
        while ((iterator.hasNext()) && (!result)) {
            EngineComponent.EngineComponentInterface im = iterator.next();
            if ((im.getTextureID()[0] == obj.getTextureID()[0]) && (im.getTextureID()[0] != 0)) {
                obj.setTextureGL_ID(im.getTextureGL_ID()[0]);
                result = true;
            }
        }

        if ((!result) && (obj.getTextureID()[0] != 0)) {
            obj.loadGLTexture(context, obj.getTextureID()[0], true);
        }

        _images.add(obj);
        if (orderByRenderDepth)
            orderByRenderDepth();
    }

    public long getWaitTime() {
        return wait;
    }

    protected void setWaitTime(int time) {
        wait = time;
    }

    public boolean isGameOn() {
        return GameOn;
    }

    public void setGameOn(boolean flag) {
        GameOn = flag;
    }

    private void orderByRenderDepth() {
        EngineComponent.EngineComponentInterface[] temp = new EngineComponent.EngineComponentInterface[_images.size()];
        _images.toArray(temp);

        for (int i = 0; i < temp.length - 1; i++) {
            for (int j = i + 1; j < temp.length; j++) {
                if (temp[i].getRenderDepth() < temp[j].getRenderDepth()) {
                    EngineComponent.EngineComponentInterface grCmp = temp[i];
                    temp[i] = temp[j];
                    temp[j] = grCmp;
                }
            }
        }
        float max = 0;
        for (int i = 0; i < temp.length; i++) {
            if (max < temp[i].getRenderDepth()) {
                max = temp[i].getRenderDepth();
            }
        }
        _images.clear();
        for (int i = 0; i < temp.length; i++) {
            temp[i].setRenderDepth(temp[i].getRenderDepth() / max);
            _images.add(temp[i]);
        }
    }

    public void clearItems() {
        _images.clear();
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        draw();
    }

    public float getElaspedTime() {
        return _totalTime;
    }

    public void updateElaspedTime(float time) {
        _totalTime = time;
    }

    public void update() {
        Iterator<EngineComponent.EngineComponentInterface> iterator = _images.iterator();
        while (iterator.hasNext()) {
            EngineComponent.EngineComponentInterface im = iterator.next();
            im.update(_dt);
        }
    }

    private void draw() {
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0, 0, 0.8f, 0.5f);

        Iterator<EngineComponent.EngineComponentInterface> iterator = _images.iterator();
        while (iterator.hasNext()) {
            iterator.next().draw(_camera.getViewMatrix(), _camera.getProjectionMatrix(), time);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation

        GLES20.glViewport(0, 0, width, height);

        ZNear = 1;
        ZFar = 100;

        _camera.setProjectionMatrix(width, height, ZNear, ZFar);

        loadItems();
    }

    protected float getDT() {
        return _dt;
    }

    protected Vector3 getLastTouch() {
        return _lastTouch;
    }

    public void touchDown(Vector3 screenCoords) {
        _camera.touch(screenCoords);
        testIntersection();

        _lastTouch = screenCoords;
    }

    @Override
    public void touchMove(Vector3 screenCoords) {

    }

    @Override
    public void touchUp(Vector3 screenCoords) {

    }

    private void testIntersection() {
        Iterator<EngineComponent.EngineComponentInterface> iterator = _images.iterator();

        selectedObjects.clear();

        //check for some selected object
        while (iterator.hasNext()) {
            EngineComponent.EngineComponentInterface im = iterator.next();
            if (im.isIntersectingRay(_camera.getRay())) {
                selectedObjects.add(im);
            }
        }

        //execute action for all the selected object
        iterator = selectedObjects.iterator();
        while (iterator.hasNext()) {
            iterator.next().doAction(false);
        }
    }
}