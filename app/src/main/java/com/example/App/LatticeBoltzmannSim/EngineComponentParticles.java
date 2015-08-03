package com.example.App.LatticeBoltzmannSim;

import android.content.Context;
import android.opengl.GLES20;

import com.example.App.GraphicsEngine.Engine.EngineComponent;
import com.example.App.GraphicsEngine.Engine.EngineGLRenderer;
import com.example.App.GraphicsEngine.Utils.Ray3f;
import com.example.App.GraphicsEngine.Utils.Vector3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class EngineComponentParticles implements EngineComponent.EngineComponentInterface {

    int programHandle;

    int positionHandle;
    int pointSizeHandle;
    int mvProjProjection;
    float Vertices[] =
            {
                    -0.5f, 0.5f, 0.0f,
                    0.5f, 0.5f, 0.0f,
                    -0.5f, -0.5f, 0.0f,
                    0.5f, -0.5f, 0.0f,
            };
    int drawListBufferCapacity;
    int[] dimensions;
    float[] mvpMatrix = new float[]{1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f};
    private FloatBuffer vertexBuffer;
    private float[] color = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
    //private FloatBuffer colorBuffer;
    private ShortBuffer drawListBuffer;

    public EngineComponentParticles(int xDimension, int yDimension) {
        super();

        dimensions = new int[]{xDimension, yDimension};

        String vertexShader =
                "attribute vec4 position;" +
                        "uniform mat4 _mvProj;" +
                        "uniform float pointSize;" +
                        "void main(void) {" +
                        // "gl_Position = _mvProj * vec4(position, 1.0);"+
                        "gl_Position = position" +
                        "gl_PointSize = pointSize / gl_Position.w;" +
                        "}";

        String fragmentShader =
                "precision mediump float;" +
                        "void main(void)" +
                        "{" +
                        //"float alpha = 1.0-length((gl_PointCoord-0.5)*2.0);"+
                        "float alpha = 1.0;" +
                        "gl_FragColor = vec4(1.0,1.0,1.0,alpha);" +
                        "}";

        // prepare shaders and OpenGL program
        int vertexShaderHandle = EngineGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShader);

        int fragmentShaderHandle = EngineGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShader);


        programHandle = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(programHandle, vertexShaderHandle);   // add the vertex shader to program
        //GLES20.glAttachShader(mProgram, fragmentShaderOnlyColor); // add the fragment shader to program
        GLES20.glAttachShader(programHandle, fragmentShaderHandle);

        GLES20.glLinkProgram(programHandle);                  // create OpenGL program executables

        // Set program handles. These will later be used to pass in values to the program.
        positionHandle = GLES20.glGetAttribLocation(programHandle, "position");
        pointSizeHandle = GLES20.glGetAttribLocation(programHandle, "pointSize");
        mvProjProjection = GLES20.glGetAttribLocation(programHandle, "_mvProj");

        updateBuffer();
    }

    public void draw(float[] mViewMatrix, float[] mProjectionMatrix, float time) {

        if (mvProjProjection < 0) {
            positionHandle = GLES20.glGetAttribLocation(programHandle, "position");
            pointSizeHandle = GLES20.glGetAttribLocation(programHandle, "pointSize");
            mvProjProjection = GLES20.glGetAttribLocation(programHandle, "_mvProj");
        }
        // Add program to OpenGL environment
        GLES20.glUseProgram(programHandle);

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glUniform1f(pointSizeHandle, 0.1f);
        //  GLES20.glUniformMatrix4fv(mvProjProjection, 1, false, mvpMatrix, 0);//Apply the projection and view transformation

        //GLES20.glUniform4f(colorUniformHandle, color[0],color[1], color[2],color[3]);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, Vertices.length / 3);

    }


    @Override
    public void update(float dt) {

    }

    @Override
    public void doAction(boolean restart) {

    }

    @Override
    public boolean isIntersectingRay(Ray3f ray) {
        return false;
    }

    @Override
    public float getRenderDepth() {
        return 0;
    }

    @Override
    public void setRenderDepth(float rd) {

    }

    @Override
    public void setGL_POINTER(String vertexShader, String fragmentShder) {

    }

    private void updateBuffer() {
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                Vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(Vertices);
        vertexBuffer.position(0);

    }

    public void setVerticesBuffer(Vector3[][] positions) {
        /*
        int count=0;
        for(int y=0;y<dimensions[1];y++)
        {
            if(y==dimensions[1]-1)
            {
                int n=1+1;
            }
            for(int x=0;x<dimensions[0];x++)
            {
                Colors[count*4]=colori[x][y].X();
                Colors[count*4+1]=colori[x][y].Y();
                Colors[count*4+2]=colori[x][y].Z();
                count++;
            }
        }
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                Colors.length * 4);
        bb.order(ByteOrder.nativeOrder());
        colorBuffer = bb.asFloatBuffer();
        colorBuffer.put(Colors);
        colorBuffer.position(0);*/
    }

    @Override
    public void setGL_POINTER(int program) {
    }

    @Override
    public String getID() {
        return "";
    }

    @Override
    public void setID(String id) {

    }

    @Override
    public void loadGLTexture(Context context, int id, boolean LoadNow) {

    }

    @Override
    public int[] getTextureID() {
        return new int[]{0};
    }

    @Override
    public int[] getTextureGL_ID() {
        return new int[]{0};
    }

    @Override
    public void setTextureGL_ID(int id) {

    }

    @Override
    public boolean getCheckForReuse() {
        return false;
    }
}

