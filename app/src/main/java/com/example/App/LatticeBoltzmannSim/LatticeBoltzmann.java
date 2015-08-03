package com.example.App.LatticeBoltzmannSim;

import android.graphics.Color;

import com.example.App.GraphicsEngine.Utils.Vector3;

import java.nio.FloatBuffer;

public class LatticeBoltzmann {

    boolean debug = false;
    // dimensions of lattice
    int xdim;
    int ydim;
    // Boolean array, true at sites that contain barriers:
    boolean[][] barrier = new boolean[xdim][ydim];
    // Here are the arrays of densities by velocity, named by velocity directions with north up:
    double[][] n0;
    double[][] nN;
    double[][] nS;
    double[][] nE;
    double[][] nW;
    double[][] nNW;
    double[][] nNE;
    double[][] nSW;
    double[][] nSE;
    // Other arrays calculated from the above:
    double[][] density;        // total density
    double[][] xvel;        // macroscopic x velocity
    double[][] yvel;        // macroscopic y velocity
    double[][] speed2;        // macroscopic speed squared
    double[][] curl;
    float[] colorVector;
    int time;

    int nTracers = 256;
    double[] tracerx;
    double[] tracery;
    boolean running;

    int stepTime;            // performance measure: time in ms for a single iteration of the algorithm
    int collideTime;
    int streamTime;
    int paintTime;

    // calculation short-cuts:
    double four9ths;
    double one9th;
    double one36th;

    float viscosity;
    float initialVelocity;

    boolean isTracerEnable;

    Vector3[] vectorColors;

    int drawType = 0;

    public LatticeBoltzmann(int numberOfColors) {
        xdim = 300;
        ydim = 100;
        nTracers = 256;
        Initialize(numberOfColors);

        initFluid();    // initialize the fluid state
        initTracers();    // place the tracer particles
    }

    public LatticeBoltzmann(int xDim, int yDim, float velocity, int numberOfColors, int tracers) {
        xdim = xDim;
        ydim = yDim;
        nTracers = tracers;
        Initialize(numberOfColors);

        setVelocity(velocity);
        initFluid();    // initialize the fluid state

        initTracers();    // place the tracer particles
    }

    private void Initialize(int numberOfColors) {
        n0 = new double[xdim][ydim];
        nN = new double[xdim][ydim];
        nS = new double[xdim][ydim];
        nE = new double[xdim][ydim];
        nW = new double[xdim][ydim];
        nNW = new double[xdim][ydim];
        nNE = new double[xdim][ydim];
        nSW = new double[xdim][ydim];
        nSE = new double[xdim][ydim];

        density = new double[xdim][ydim];
        xvel = new double[xdim][ydim];
        yvel = new double[xdim][ydim];
        speed2 = new double[xdim][ydim];
        curl = new double[xdim][ydim];
        colorVector = new float[xdim * ydim * 4];

        barrier = new boolean[xdim][ydim];

        time = 0;

        tracerx = new double[nTracers];
        tracery = new double[nTracers];

        running = false;

        stepTime = 0;
        collideTime = 0;
        streamTime = 0;
        paintTime = 0;

        four9ths = 4.0 / 9;
        one9th = 1.0 / 9;
        one36th = 1.0 / 36;

        viscosity = 0.02f;
        initialVelocity = 0;

        isTracerEnable = false;

        initColorArray(numberOfColors);
    }

    public void resetBarrier() {
        barrier = new boolean[xdim][ydim];
    }

    public float getVelocity() {
        return initialVelocity;
    }

    public void setVelocity(float velocity) {
        initialVelocity = velocity;
    }

    public void setViscosity(float visc) {
        viscosity = visc;
    }

    public int[] getGridDimensions() {
        return new int[]{xdim, ydim};
    }

    public void initColorArray(int nColors) {
        //Color[] shade = new Color[nColors];
        vectorColors = new Vector3[nColors];

        float HUE = 0;
        float HSV = 1f;
        float gap = 360 / (float) (nColors);
        for (int i = 0; i < nColors; i++) {
            HUE = HUE + gap;
            int color = Color.HSVToColor(new float[]{HUE, HSV, 0.3f});
            vectorColors[i] = new Vector3((float) (Color.red(color)) / 255, (float) (Color.green(color)) / 255, (float) (Color.blue(color)) / 255);    // store each color as an integer
        }
    }

    // Initialize tracer positions, equally spaced:
    public void initTracers() {
        int nRow = (int) Math.sqrt(nTracers);    // number of tracers in a row
        nTracers = nRow * nRow;                    // force nTracers to be a perfect square
        double dx = xdim * 1.0 / nRow;
        double dy = ydim * 1.0 / nRow;
        int next = 0;
        for (int x = 0; x < nRow; x++) {
            for (int y = 0; y < nRow; y++) {
                tracerx[next] = (x + 0.5) * dx;
                tracery[next] = (y + 0.5) * dy;
                next++;
            }
        }
    }

    // Initialize the fluid with density 1 and user-chosen speed in x direction:
    public synchronized void initFluid() {
        double v = initialVelocity;
        for (int x = 0; x < xdim; x++) {
            for (int y = 0; y < ydim; y++) {
                if (barrier[x][y]) {
                    zeroSite(x, y);
                } else {
                    n0[x][y] = four9ths * (1 - 1.5 * v * v);
                    nE[x][y] = one9th * (1 + 3 * v + 3 * v * v);
                    nW[x][y] = one9th * (1 - 3 * v + 3 * v * v);
                    nN[x][y] = one9th * (1 - 1.5 * v * v);
                    nS[x][y] = one9th * (1 - 1.5 * v * v);
                    nNE[x][y] = one36th * (1 + 3 * v + 3 * v * v);
                    nSE[x][y] = one36th * (1 + 3 * v + 3 * v * v);
                    nNW[x][y] = one36th * (1 - 3 * v + 3 * v * v);
                    nSW[x][y] = one36th * (1 - 3 * v + 3 * v * v);
                    density[x][y] = 1;
                    xvel[x][y] = v;
                    yvel[x][y] = 0;
                    speed2[x][y] = v * v;
                }
            }
        }
        time = 0;    // reset time variable
    }

    //set all densities at a site to zero:
    private void zeroSite(int x, int y) {
        n0[x][y] = 0;
        nE[x][y] = 0;
        nW[x][y] = 0;
        nN[x][y] = 0;
        nS[x][y] = 0;
        nNE[x][y] = 0;
        nNW[x][y] = 0;
        nSE[x][y] = 0;
        nSW[x][y] = 0;
        xvel[x][y] = 0;
        yvel[x][y] = 0;
        speed2[x][y] = 0;
    }

    public void SimStep() {
        doStep();
    }

    // Execute a single step of the algorithm:
    // Times are on 3.06 GHz iMac, Java 6. On 2.4GHz MacBook Pro, all times are about 30% longer.
    private synchronized void doStep() {

        //if (debug) Log.d("DEBUG", "START_STEP");
        //long startTime = System.currentTimeMillis();
        //force();
        //long forceTime = System.currentTimeMillis();

        collide();

        //long afterCollideTime = System.currentTimeMillis();
        //collideTime = (int) (afterCollideTime - forceTime);        // 23-24 ms for 600x600 grid

        stream();

        //streamTime = (int) (System.currentTimeMillis() - afterCollideTime);    // 9-10 ms for 600x600 grid

        bounce();

        if (isTracerEnable) moveTracers();

        //stepTime = (int) (System.currentTimeMillis() - startTime);    // 33-35 ms for 600x600 grid

        time++;

        //dataCanvas.repaint();
    }

    // Collide particles within each cell.  Adapted from Wagner's D2Q9 code.
    private void collide() {
        double n, one9thn, one36thn, vx, vy, vx2, vy2, vx3, vy3, vxvy2, v2, v215;
        double omega = 1 / (3 * viscosity + 0.5);    // reciprocal of tau, the relaxation time
        for (int x = 0; x < xdim; x++) {
            for (int y = 0; y < ydim; y++) {
                if (!barrier[x][y]) {
                    n = n0[x][y] + nN[x][y] + nS[x][y] + nE[x][y] + nW[x][y] + nNW[x][y] + nNE[x][y] + nSW[x][y] + nSE[x][y];

                    one9thn = one9th * n;
                    one36thn = one36th * n;
                    if (n > 0) {
                        vx = (nE[x][y] + nNE[x][y] + nSE[x][y] - nW[x][y] - nNW[x][y] - nSW[x][y]) / n;
                    } else vx = 0;

                    if (n > 0) {
                        vy = (nN[x][y] + nNE[x][y] + nNW[x][y] - nS[x][y] - nSE[x][y] - nSW[x][y]) / n;
                    } else vy = 0;

                    vx3 = 3 * vx;
                    vy3 = 3 * vy;
                    vx2 = vx * vx;
                    vy2 = vy * vy;
                    vxvy2 = 2 * vx * vy;
                    v2 = vx2 + vy2;

                    v215 = 1.5 * v2;
                    n0[x][y] += omega * (four9ths * n * (1 - v215) - n0[x][y]);
                    nE[x][y] += omega * (one9thn * (1 + vx3 + 4.5 * vx2 - v215) - nE[x][y]);
                    nW[x][y] += omega * (one9thn * (1 - vx3 + 4.5 * vx2 - v215) - nW[x][y]);
                    nN[x][y] += omega * (one9thn * (1 + vy3 + 4.5 * vy2 - v215) - nN[x][y]);
                    nS[x][y] += omega * (one9thn * (1 - vy3 + 4.5 * vy2 - v215) - nS[x][y]);
                    nNE[x][y] += omega * (one36thn * (1 + vx3 + vy3 + 4.5 * (v2 + vxvy2) - v215) - nNE[x][y]);
                    nNW[x][y] += omega * (one36thn * (1 - vx3 + vy3 + 4.5 * (v2 - vxvy2) - v215) - nNW[x][y]);
                    nSE[x][y] += omega * (one36thn * (1 + vx3 - vy3 + 4.5 * (v2 - vxvy2) - v215) - nSE[x][y]);
                    nSW[x][y] += omega * (one36thn * (1 - vx3 - vy3 + 4.5 * (v2 + vxvy2) - v215) - nSW[x][y]);
                    switch (drawType) {
                        case 0:
                            speed2[x][y] = v2;
                            break;
                        case 1:
                            xvel[x][y] = vx;
                            break;
                        case 2:
                            yvel[x][y] = vy;
                            break;
                        case 3:
                            xvel[x][y] = vx;
                            yvel[x][y] = vy;
                            density[x][y] = n;
                            break;
                        case 4:
                            density[x][y] = n;
                            break;
                    }
                }
            }
        }
    }

    // Stream particles into neighboring cells:
    void stream() {
        for (int x = 0; x < xdim - 1; x++) {        // first start in NW corner...
            for (int y = ydim - 1; y > 0; y--) {
                nN[x][y] = nN[x][y - 1];        // move the north-moving particles
                nNW[x][y] = nNW[x + 1][y - 1];    // and the northwest-moving particles
            }
        }
        for (int x = xdim - 1; x > 0; x--) {        // now start in NE corner...
            for (int y = ydim - 1; y > 0; y--) {
                nE[x][y] = nE[x - 1][y];        // move the east-moving particles
                nNE[x][y] = nNE[x - 1][y - 1];    // and the northeast-moving particles
            }
        }
        for (int x = xdim - 1; x > 0; x--) {        // now start in SE corner...
            for (int y = 0; y < ydim - 1; y++) {
                nS[x][y] = nS[x][y + 1];        // move the south-moving particles
                nSE[x][y] = nSE[x - 1][y + 1];    // and the southeast-moving particles
            }
        }
        for (int x = 0; x < xdim - 1; x++) {        // now start in the SW corner...
            for (int y = 0; y < ydim - 1; y++) {
                nW[x][y] = nW[x + 1][y];        // move the west-moving particles
                nSW[x][y] = nSW[x + 1][y + 1];    // and the southwest-moving particles
            }
        }
        // We missed a few at the left and right edges:
        for (int y = 0; y < ydim - 1; y++) {
            nS[0][y] = nS[0][y + 1];
        }
        for (int y = ydim - 1; y > 0; y--) {
            nN[xdim - 1][y] = nN[xdim - 1][y - 1];
        }
        // Now handle left boundary as in Pullan's example code:
        // Stream particles in from the non-existent space to the left, with the
        // user-determined speed:
        double v = initialVelocity;
        for (int y = 0; y < ydim; y++) {
            if (!barrier[0][y]) {
                nE[0][y] = one9th * (1 + 3 * v + 3 * v * v);
                nNE[0][y] = one36th * (1 + 3 * v + 3 * v * v);
                nSE[0][y] = one36th * (1 + 3 * v + 3 * v * v);
            }
        }
        // Try the same thing at the right edge and see if it works:
        for (int y = 0; y < ydim; y++) {
            if (!barrier[0][y]) {
                nW[xdim - 1][y] = one9th * (1 - 3 * v + 3 * v * v);
                nNW[xdim - 1][y] = one36th * (1 - 3 * v + 3 * v * v);
                nSW[xdim - 1][y] = one36th * (1 - 3 * v + 3 * v * v);
            }
        }
        // Now handle top and bottom edges:
        for (int x = 0; x < xdim; x++) {
            n0[x][0] = four9ths * (1 - 1.5 * v * v);
            nE[x][0] = one9th * (1 + 3 * v + 3 * v * v);
            nW[x][0] = one9th * (1 - 3 * v + 3 * v * v);
            nN[x][0] = one9th * (1 - 1.5 * v * v);
            nS[x][0] = one9th * (1 - 1.5 * v * v);
            nNE[x][0] = one36th * (1 + 3 * v + 3 * v * v);
            nSE[x][0] = one36th * (1 + 3 * v + 3 * v * v);
            nNW[x][0] = one36th * (1 - 3 * v + 3 * v * v);
            nSW[x][0] = one36th * (1 - 3 * v + 3 * v * v);
            n0[x][ydim - 1] = four9ths * (1 - 1.5 * v * v);
            nE[x][ydim - 1] = one9th * (1 + 3 * v + 3 * v * v);
            nW[x][ydim - 1] = one9th * (1 - 3 * v + 3 * v * v);
            nN[x][ydim - 1] = one9th * (1 - 1.5 * v * v);
            nS[x][ydim - 1] = one9th * (1 - 1.5 * v * v);
            nNE[x][ydim - 1] = one36th * (1 + 3 * v + 3 * v * v);
            nSE[x][ydim - 1] = one36th * (1 + 3 * v + 3 * v * v);
            nNW[x][ydim - 1] = one36th * (1 - 3 * v + 3 * v * v);
            nSW[x][ydim - 1] = one36th * (1 - 3 * v + 3 * v * v);
        }
    }

    // Bounce particles off of barriers:
    // (The ifs are needed to prevent array index out of bounds errors. Could handle edges
    //  separately to avoid this.)
    void bounce() {
        for (int x = 0; x < xdim; x++) {
            for (int y = 0; y < ydim; y++) {
                if (barrier[x][y]) {
                    if (nN[x][y] > 0) {
                        nS[x][y - 1] += nN[x][y];
                        nN[x][y] = 0;
                    }
                    if (nS[x][y] > 0) {
                        nN[x][y + 1] += nS[x][y];
                        nS[x][y] = 0;
                    }
                    if (nE[x][y] > 0) {
                        nW[x - 1][y] += nE[x][y];
                        nE[x][y] = 0;
                    }
                    if (nW[x][y] > 0) {
                        nE[x + 1][y] += nW[x][y];
                        nW[x][y] = 0;
                    }
                    if (nNW[x][y] > 0) {
                        nSE[x + 1][y - 1] += nNW[x][y];
                        nNW[x][y] = 0;
                    }
                    if (nNE[x][y] > 0) {
                        nSW[x - 1][y - 1] += nNE[x][y];
                        nNE[x][y] = 0;
                    }
                    if (nSW[x][y] > 0) {
                        nNE[x + 1][y + 1] += nSW[x][y];
                        nSW[x][y] = 0;
                    }
                    if (nSE[x][y] > 0) {
                        nNW[x - 1][y + 1] += nSE[x][y];
                        nSE[x][y] = 0;
                    }
                }
            }
        }
        // Last but not least, stream particles in from non-existent space to the right,
        // assuming the left-moving densities are the same as they are immediately to the left:
        for (int y = 0; y < ydim; y++) {
            if (!barrier[xdim - 1][y]) {
                nW[xdim - 1][y] = nW[xdim - 2][y];
                nNW[xdim - 1][y] = nNW[xdim - 2][y];
                nSW[xdim - 1][y] = nSW[xdim - 2][y];
                // normalize density to 1 (this seems to prevent density build-up over time):
                double dens = n0[xdim - 1][y] + nE[xdim - 1][y] + nW[xdim - 1][y] + nN[xdim - 1][y] + nS[xdim - 1][y] +
                        nNE[xdim - 1][y] + nNW[xdim - 1][y] + nSE[xdim - 1][y] + nSW[xdim - 1][y];
                n0[xdim - 1][y] /= dens;
                nE[xdim - 1][y] /= dens;
                nW[xdim - 1][y] /= dens;
                nN[xdim - 1][y] /= dens;
                nS[xdim - 1][y] /= dens;
                nNE[xdim - 1][y] /= dens;
                nNW[xdim - 1][y] /= dens;
                nSE[xdim - 1][y] /= dens;
                nSW[xdim - 1][y] /= dens;
            }
        }
    }

    // Move the tracer particles according to the macroscopic velocity:
    public void moveTracers() {
        Vector3 white = new Vector3(1.0f, 1.0f, 1.0f);
        for (int t = 0; t < nTracers; t++) {
            int x = (int) tracerx[t];                    // convert coordinates to integers
            int y = (int) tracery[t];
            tracerx[t] += xvel[x][y];                    // move 'em along the flow
            tracery[t] += yvel[x][y];
            if (tracerx[t] < 0) tracerx[t] = 0;            // don't let 'em go out of bounds
            if (tracerx[t] >= xdim) tracerx[t] = 0;        // recycle when it exits to the right
            if (tracery[t] < 0) tracery[t] = 0;
            if (tracery[t] >= ydim) tracery[t] = ydim - 1;

            setColorVector((int) tracerx[t], (int) tracery[t], white);
        }

        // Compute the curl of the velocity field, paying special attention to edges:
    }

    private void setColorVector(int x, int y, Vector3 value) {
        int pos = y * xdim * 4 + x * 4;
        //int pos = x * ydim + y;
        colorVector[pos] = (float) value.x();
        colorVector[pos + 1] = (float) value.y();
        colorVector[pos + 2] = (float) value.z();
        //alpha value, in another words the transparency
        colorVector[pos + 3] = 1.0f;
    }

    void computeCurl() {
        for (int x = 1; x < xdim - 1; x++) {
            for (int y = 1; y < ydim - 1; y++) {
                curl[x][y] = (yvel[x + 1][y] - yvel[x - 1][y]) - (xvel[x][y + 1] - xvel[x][y - 1]);
            }
        }
        for (int y = 1; y < ydim - 1; y++) {
            curl[0][y] = 2 * (yvel[1][y] - yvel[0][y]) - (xvel[0][y + 1] - xvel[0][y - 1]);
            curl[xdim - 1][y] = 2 * (yvel[xdim - 1][y] - yvel[xdim - 2][y]) - (xvel[xdim - 1][y + 1] - xvel[xdim - 1][y - 1]);
        }
    }

    /**
     * Write on a matrix the current state.
     * <p/>
     * Use {@link #SimStep()} to advance of one step the simulation.
     *
     * @param plotType 0 for velocity, 1 for x velocity, 2 for y velocity, 3 for curl, 4 for density
     * @param contrast rank from which a piece is being moved
     */
    public void computeColor(int plotType, float contrast) {

        drawType = plotType;
        if (plotType == 3) {
            computeCurl();
        }
        int nColors = vectorColors.length;
        for (int y = ydim - 1; y >= 0; y--) {        // note that we loop over y (row number) first, high to low
            for (int x = 0; x < xdim; x++) {
                if (barrier[x][y]) {
                    setColorVector(x, y, new Vector3(0.0f, 0.0f, 0.0f));
                } else {
                    int colorIndex;
                    if (plotType == 0) {
                        colorIndex = (int) (Math.sqrt(speed2[x][y]) * nColors * contrast * 0.2);
                        // (could avoid sqrt with clever color scheme but it doesn't seem to be a performance bottleneck)
                    } else if (plotType == 1) {
                        colorIndex = (int) (nColors * (0.5 + xvel[x][y] * contrast * 0.2));
                    } else if (plotType == 2) {
                        colorIndex = (int) (nColors * (0.5 + yvel[x][y] * contrast * 0.2));
                    } else if (plotType == 3) {
                        colorIndex = (int) (nColors * (0.5 + curl[x][y] * contrast * 0.3));
                    } else {
                        colorIndex = (int) (nColors * (0.5 + (density[x][y] - 1) * contrast * 0.3));
                    }
                    if (colorIndex < 0) colorIndex = 0;
                    if (colorIndex >= vectorColors.length) colorIndex = vectorColors.length - 1;

                    setColorVector(x, y, vectorColors[colorIndex]);
                }
            }
        }
    }

    public FloatBuffer fillBuffer(FloatBuffer buffer) {
        buffer.position(0);
        buffer.put(colorVector);
        buffer.position(0);
        return buffer;
    }

    public void isDebug(boolean flag) {
        debug = flag;
    }

    public void setBarrier(int x, int y, boolean isBarrier) {
        if ((x < xdim - 1) && (x > 1) && (y < ydim - 1) && (y > 1)) {
            barrier[x][y] = isBarrier;
        }
    }

    public boolean isBarrier(int x, int y) {
        return barrier[x][y];
    }

    public void makeCircle(int diameter) {
        double radius = (diameter - 1) / 2.0;        // 1->0, 2->.5, 3->1, 4->1.5, etc.
        double centerY = ydim / 2 - 1;
        if (diameter % 2 == 0) centerY -= 0.5;    // shift down a bit if diameter is an even number
        double centerX = xdim * 0.4f;
        for (double theta = 0; theta < 2 * Math.PI; theta += 0.1 / radius) {
            int x = (int) Math.round(centerX + radius * Math.cos(theta));
            int y = (int) Math.round(centerY + radius * Math.sin(theta));
            setBarrier(x, y, true);
            if (radius > 1) {
                x = (int) Math.round(centerX + (radius - 0.5) * Math.cos(theta));
                y = (int) Math.round(centerY + (radius - 0.5) * Math.sin(theta));
                setBarrier(x, y, true);
            }
        }
    }

    public void makeLine(int length) {
        int x = (int) (xdim * 0.4f);
        for (int y = ydim / 2 - length / 2 - 1; y < ydim / 2 - length / 2 + length - 1; y++) {
            setBarrier(x, y, true);
        }
    }

    /*
    a is for X extension
    b is for Y extension
     */
    public void makeDrop(float a, float b) {
        /*
        x=a(1−sint)cost
        y=b(sint−1)
        a=1,b=52:
        */
        for (float t = 0; t < 2 * 3.14; t = t + 0.01f) {
            int y = (int) (a * (1 - Math.sin(t)) * Math.cos(t) + ydim / 2);
            int x = (int) (b * (Math.sin(t) - 1) + xdim * 0.45f);
            setBarrier(x, y, true);
        }
    }

    //xLenght between 0.0001f and 0.499999f
    //yAperture 0.1f is a good value, however best if <0.5f
    public void makeBellMouth(float xLenght, float yAperture) {
        //ln(100)=4.6_ _ _ _ _
        int stopPoint = 100;

        float lenght = (xdim * (0.5f + xLenght)) - (xdim * (0.5f - xLenght));
        float step = (stopPoint / lenght) / (xdim * 0.01f);
        for (float t = 1; t <= stopPoint; t = t + 0.05f) {
            int x = (int) (step * t + xdim * 0.3f);
            int y = (int) ((Math.log(stopPoint) - Math.log(t)) * ydim / 2 / (Math.log(stopPoint)) + ydim / 2 + ydim * yAperture);
            setBarrier(x, y, true);
            y = (int) ((Math.log(stopPoint) - Math.log(t)) * -ydim / 2 / (Math.log(stopPoint)) + ydim / 2 - ydim * yAperture);
            setBarrier(x, y, true);
        }
    }

    public void makeInvBellMouth(float xLenght, float yAperture) {
        //ln(100)=4.6_ _ _ _ _
        int stopPoint = 100;

        float lenght = (xdim * (0.5f + xLenght)) - (xdim * (0.5f - xLenght));
        float step = (stopPoint / lenght) / (xdim * 0.01f);
        for (float t = 1; t <= stopPoint; t = t + 0.05f) {
            int x = (int) ((step * stopPoint) - (step * t) + xdim * 0.3f);
            int y = (int) ((Math.log(stopPoint) - Math.log(t)) * ydim / 2 / (Math.log(stopPoint)) + ydim / 2 + ydim * yAperture);
            setBarrier(x, y, true);
            y = (int) ((Math.log(stopPoint) - Math.log(t)) * -ydim / 2 / (Math.log(stopPoint)) + ydim / 2 - ydim * yAperture);
            setBarrier(x, y, true);
        }
    }
}
