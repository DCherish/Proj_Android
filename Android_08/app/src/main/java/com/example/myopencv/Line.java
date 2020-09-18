package com.example.myopencv;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Line {
    private FloatBuffer vertexBuffer;
    private float[] colors;
    public Line(float vertices[], float colors[]) {
        // colors.length must be 4 ! // RED / GREEN / BLUE / ALPHA(0~1.0)
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4); // (number of coordinate values * 4 bytes per float)
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(vertices);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        // initialize color
        this.colors = colors;
    }

    public void draw(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //gl.glLineWidth(5);  // line thickness // onSurfaceCreated 에 선언되어 있음
        gl.glColor4f(colors[0], colors[1], colors[2], colors[3]);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);    // size : axis number (X,Y,Z) -> 3D
        gl.glDrawArrays(GL10.GL_LINES, 0, 2); // count (2) : start_point, end_point
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}
