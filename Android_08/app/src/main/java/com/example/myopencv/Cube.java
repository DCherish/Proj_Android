package com.example.myopencv;

import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Cube {
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    public static float square_size = 0.028f;
    static float cubeCoords[] = {
            0.0f * square_size, 0.0f * square_size, 0.0f * square_size,
            1.0f * square_size, 0.0f * square_size, 0.0f * square_size,
            1.0f * square_size, 1.0f * square_size, 0.0f * square_size,
            0.0f * square_size, 1.0f * square_size, 0.0f * square_size,

            0.0f * square_size, 0.0f * square_size, 1.0f * square_size,
            1.0f* square_size, 0.0f * square_size, 1.0f * square_size,
            1.0f * square_size, 1.0f * square_size, 1.0f * square_size,
            0.0f * square_size,  1.0f * square_size, 1.0f * square_size
    };
    private short drawOrder[] = {
            0, 4, 5, 0, 5, 1,
            1, 5, 6, 1, 6, 2,
            2, 6, 7, 2, 7, 3,
            3, 7, 4, 3, 4, 0,
            4, 7, 6, 4, 6, 5,
            3, 0, 1, 3, 1, 2
    }; // order to draw vertices

    private FloatBuffer mCubeColors;
    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { // Front face (red)
            0, 0, 0, 1, 1, 0, 0, 1,
            1, 1, 0, 1, 0, 1, 0, 1,
            0, 0, 1, 1, 1, 0, 1, 1,
            1, 1, 1, 1, 0, 1, 1, 1,
    };

    public Cube(Context context) {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                cubeCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(cubeCoords);
        vertexBuffer.position(0);
        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // coloring
        mCubeColors = ByteBuffer.allocateDirect(color.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeColors.put(color).position(0);
    }

    public void draw(GL10 gl) {
        // Enable use of vertex arrays
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        // Draw the triangle
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, mCubeColors);
        gl.glDrawElements(GL10.GL_TRIANGLES, drawOrder.length,
                GL10.GL_UNSIGNED_SHORT, drawListBuffer);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }
}
