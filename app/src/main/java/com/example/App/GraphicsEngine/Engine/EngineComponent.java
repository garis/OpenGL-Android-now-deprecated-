package com.example.App.GraphicsEngine.Engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.example.App.GraphicsEngine.Utils.BoundingSphere;
import com.example.App.GraphicsEngine.Utils.Point3f;
import com.example.App.GraphicsEngine.Utils.Ray3f;
import com.example.App.GraphicsEngine.Utils.Vector3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class EngineComponent {

    // number of coordinates per vertex in this array
    private final int COORDS_PER_VERTEX = 3;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    float color[] = {1.0f, 1.0f, 1.0f, 1.0f};
    int drawListBufferCapacity;
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private int UV_COORDS_PER_VERTEX = 2;
    private float squareCoords[] = {
            -1.0f, 1.0f, 0.0f,   // top left
            -1.0f, -1.0f, 0.0f,   // bottom left
            1.0f, -1.0f, 0.0f,   // bottom right
            1.0f, 1.0f, 0.0f}; // top right
    private short drawOrder[] = {0, 1, 2, 0, 2, 3}; // order to draw vertices
    private float _x;
    private float _y;
    private float _z;
    private float _scaleX;
    private float _scaleY;
    private float _scaleZ;
    private float _angleX;
    private float _angleY;


    //Texture stuffs
    private float _angleZ;
    private FloatBuffer uvBuffer;  // buffer holding the texture coordinates
    private float[] uvCoords = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
    };
    /**
     * The texture pointer
     */
    private int[] textures = new int[1];
    private int textureID[] = new int[1];
    private int fragmentShaderTexture;
    private int fragmentShaderOnlyColor;
    private boolean checkForReuse;
    private boolean texturized = false;

    private BoundingSphere boundingSphere;

    private int mProgram;
    private int mPositionHandle;
    private int colorUniformHandle;
    private int textureCoordinateHandle;
    private int textureUniformHandle;
    private int mMVPMatrixHandle;

    private boolean isIn3DSpace;


    public EngineComponent() {

        isIn3DSpace = true;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        _scaleX = 1;
        _scaleY = 1;
        _scaleZ = 1;

        _x = 0;
        _y = 0;
        _z = 0;

        _angleX = 0;
        _angleY = 0;
        _angleZ = 0;

        drawListBufferCapacity = drawListBuffer.capacity();

        boundingSphere = new BoundingSphere();
        UpdateBoundsSphere();

        UpdatePointerVariable();

    }

    public void SetGL_POINTER(int program) {
        mProgram = program;
        UpdatePointerVariable();
    }

    public void SetGL_POINTER(String vertexShaderCode, String fragmentShaderCode) {
        // prepare shaders and OpenGL program
        int vertexShader = EngineGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);

        fragmentShaderTexture = EngineGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);


        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        //GLES20.glAttachShader(mProgram, fragmentShaderOnlyColor); // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShaderTexture);

        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

        UpdatePointerVariable();
    }

    protected void UpdateBoundsSphere() {
        boundingSphere = new BoundingSphere(new Point3f(_x, _y, _z), _scaleX / 2);
    }

    public int[] GetTextureID() {
        return textureID;
    }

    public int[] GetTextureGL_ID() {
        return textures;
    }

    public void SetTextureGL_ID(int id) {
        texturized = true;
        textures[0] = id;
        ByteBuffer bb = ByteBuffer.allocateDirect(uvCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvCoords);
        uvBuffer.position(0);
    }

    public boolean GetCheckForReuse() {
        return checkForReuse;
    }

    public void LoadGLTexture(Context context, int id, boolean loadNowOrCheckForReuse) {
        textureID[0] = id;
        checkForReuse = !loadNowOrCheckForReuse;

        if (loadNowOrCheckForReuse) {

            //change shader from onlyColor to texture AND color
            //GLES20.glDetachShader(mProgram, fragmentShaderOnlyColor);
            //GLES20.glAttachShader(mProgram, fragmentShaderTexture); // add the fragment shader to program

            // The texture buffer
            ByteBuffer bb = ByteBuffer.allocateDirect(uvCoords.length * 4);
            bb.order(ByteOrder.nativeOrder());
            uvBuffer = bb.asFloatBuffer();
            uvBuffer.put(uvCoords);
            uvBuffer.position(0);

            drawListBufferCapacity = drawListBuffer.capacity();

            // loading texture
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);

            //ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(bitmap.getWidth()*bitmap.getHeight()*4);
            //bitmap.copyPixelsToBuffer(pixelBuffer);

            //switches on the ability to use two-dimensional images
            GLES20.glEnable(GLES20.GL_TEXTURE_2D);
            //ability to combine images in interesting ways by specifying how the source and destination will be combined
            GLES20.glEnable(GLES20.GL_BLEND);
            //defines how the source image and the destination image or surface are combined
            //GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_SRC_COLOR);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            // generate one texture pointer
            GLES20.glGenTextures(1, textures, 0);
            // ...and bind it to our array
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            //load texture

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            //GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

            // Clean up
            bitmap.recycle();

            texturized = true;
        }
    }

    protected void UpdateGeometryAndUVs(float[] SquareCoords, float[] UVCoords, short[] DrawOrder) {
        squareCoords = SquareCoords;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        drawOrder = DrawOrder;
        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        drawListBufferCapacity = drawListBuffer.capacity();

        uvCoords = UVCoords;
        UV_COORDS_PER_VERTEX = 2;

        // The texture buffer
        bb = ByteBuffer.allocateDirect(uvCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvCoords);
        uvBuffer.position(0);

        UpdateBoundsSphere();
    }

    private void UpdatePointerVariable() {
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");    // get handle to vertex shader's vPosition member
        colorUniformHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        textureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_texCoord");
        textureUniformHandle = GLES20.glGetUniformLocation(mProgram, "s_texture");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");//get handle to shape's transformation matrix

    }

    public void Draw(float[] mViewMatrix, float[] mProjectionMatrix, float time) {

        //error 1280 caused by the texture
        boolean ErroreRisolto = false;

        float[] Model = new float[16];
        float[] temp = new float[16];
        float[] mvpMatrix = new float[16];

        Matrix.setIdentityM(Model, 0); // initialize to identity matrix

        Matrix.translateM(Model, 0, _x, _y, _z);

        Matrix.scaleM(Model, 0, _scaleX, _scaleY, _scaleZ);

        Matrix.rotateM(Model, 0, _angleX, 1, 0, 0);
        Matrix.rotateM(Model, 0, _angleY, 0, 1, 0);
        Matrix.rotateM(Model, 0, _angleZ, 0, 0, 1);

        if (isIn3DSpace) {
            Matrix.multiplyMM(temp, 0, mViewMatrix, 0, Model, 0);
            Matrix.multiplyMM(mvpMatrix, 0, mProjectionMatrix, 0, temp, 0);

        } else {
            mvpMatrix = Model;
        }

        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);


        if (ErroreRisolto)
            EngineGLRenderer.checkGlError("glGetAttribLocation");

        GLES20.glEnableVertexAttribArray(mPositionHandle);    //Enable a handle to the triangle vertices
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);    // Prepare the triangle coordinate data


        if (texturized) {
            if (ErroreRisolto)
                EngineGLRenderer.checkGlError("glGetUniformLocation");

            GLES20.glUniform4f(colorUniformHandle, color[0], color[1], color[2], color[3]);

            GLES20.glVertexAttribPointer(textureCoordinateHandle, UV_COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, uvBuffer);
            GLES20.glEnableVertexAttribArray(textureCoordinateHandle);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glUniform1i(textureUniformHandle, 0);

            if (ErroreRisolto)
                EngineGLRenderer.checkGlError("glUniform1i");
        } else {
            GLES20.glUniform4f(colorUniformHandle, color[0], color[1], color[2], color[3]);
        }

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);//Apply the projection and view transformation

        try {
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawListBufferCapacity, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);//Draw
        } catch (Exception ex) {
            Log.d("DEBUG", "" + drawListBufferCapacity + ";" + drawListBuffer.capacity());
        }
        GLES20.glDisableVertexAttribArray(mPositionHandle);// Disable vertex array

    }

    public float Distance(float Wx, float Wy) {
        return (float) Math.sqrt((Math.pow(_x - Wx, 2) + Math.pow(_y - Wy, 2)));
    }

    public void SetColor(float[] value) {
        color = value;
    }


    public void SetScale(Vector3f scale) {
        _scaleX = scale.X();
        _scaleY = scale.Y();
        _scaleZ = scale.Z();

        UpdateBoundsSphere();
    }

    public void Move(Vector3f position) {
        _x = position.X();
        _y = position.Y();
        _z = position.Z();

        UpdateBoundsSphere();
    }

    //in degree
    public void Rotate(Vector3f rotationVector) {
        _angleX = rotationVector.X();
        _angleY = rotationVector.Y();
        _angleZ = rotationVector.Z();
    }

    public void Update() {
    }

    public float GetPosition(int n) {
        switch (n) {
            case 0:
                return _x;
            case 1:
                return _y;
            case 2:
                return _z;
        }
        return _x;
    }

    public float GetScale(int n) {
        switch (n) {
            case 0:
                return _scaleX;
            case 1:
                return _scaleY;
            case 2:
                return _scaleZ;
        }
        return _x;
    }

    public boolean IsIntersectingRay(Ray3f ray) {
        return boundingSphere.intersects(boundingSphere, ray);
    }

    public void isIn3dSpace(boolean flag) {
        isIn3DSpace = flag;
    }

    public static interface EngineComponentInterface {

        float RenderDepth = -1;
        String id = "";

        int[] texturesIDs = new int[1];
        int[] texturesGL_IDs = new int[1];
        boolean checkForReuse = true;

        public void Update(float dt);

        public void Draw(float[] mViewMatrix, float[] mProjectionMatrix, float time);

        public void DoAction(boolean restart);

        public boolean IsIntersectingRay(Ray3f ray);

        public float GetRenderDepth();

        public void SetRenderDepth(float rd);

        public void SetGL_POINTER(String vertexShader, String fragmentShder);

        public void SetGL_POINTER(int program);

        public String GetID();

        public void SetID(String id);

        public void LoadGLTexture(Context context, int id, boolean LoadNow);

        public int[] GetTextureID();

        public int[] GetTextureGL_ID();

        public void SetTextureGL_ID(int id);    //this set only the texturesID[0]

        public boolean GetCheckForReuse();
    }
}
