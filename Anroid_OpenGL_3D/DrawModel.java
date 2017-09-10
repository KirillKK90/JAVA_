package kkkhome.kirillkandroidopengl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import android.opengl.GLES20;
import android.util.Log;




public class DrawModel
{
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;
    private FloatBuffer mNormalBuffer;
    private ShortBuffer mIndexBuffer;
    private int faceCount = 0;

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            //"#version 400" +
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    static final int COORDS_PER_VERTEX = 3;
    static final int BYTES_PER_VERTEX = 4;
    private int vertexCount;
    private final int vertexStride = COORDS_PER_VERTEX * BYTES_PER_VERTEX; // 4 bytes per vertex
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 0.0f };

    public DrawModel(XmlResourceParser xrp)
    {
        ReadFromOgreXml(xrp);

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        MyGLRenderer.checkGlError("loadShader - vertex");

        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        MyGLRenderer.checkGlError("loadShader - fragment");

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
    }


    private void ReadFromOgreXml(XmlResourceParser xrp)
    {
        float[] coords = null;
        float[] colcoords = null;
        short[] icoords = null;
        float[] ncoords = null;
        int vertexIndex = 0;
        int colorIndex = 0;
        int faceIndex = 0;
        int normalIndex = 0;
        try
        {
            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT)
            {
                if (xrp.getEventType() == XmlResourceParser.START_TAG)
                {
                    String s = xrp.getName();
                    if (s.equals("faces"))
                    {
                        int i = xrp.getAttributeIntValue(null, "count", 0);
                        // now we know how many faces, so we know how large the
                        // triangle array should be
                        faceCount = i * 3; // three vertexes per face v1,v2,v3
                        icoords = new short[faceCount];
                    }
                    if (s.equals("geometry"))
                    {
                        int i = xrp.getAttributeIntValue(null, "vertexcount", 0);
                        // now we know how many vertexes, so we know how large
                        // the vertex, normal, texture and color arrays should be
                        // three vertex attributes per vertex x,y,z
                        coords = new float[i * 3];
                        // three normal attributes per vertex x,y,z
                        ncoords = new float[i * 3];
                        // four color attributes per vertex r,g,b,a
                        colcoords = new float[i * 4];
                        vertexCount = i;
                    }
                    if (s.equals("position"))
                    {
                        float x = xrp.getAttributeFloatValue(null, "x", 0);
                        float y = xrp.getAttributeFloatValue(null, "y", 0);
                        float z = xrp.getAttributeFloatValue(null, "z", 0);
                        if (coords != null)
                        {
                            coords[vertexIndex++] = x;
                            coords[vertexIndex++] = y;
                            coords[vertexIndex++] = z;
                        }
                    }
                    if (s.equals("normal"))
                    {
                        float x = xrp.getAttributeFloatValue(null, "x", 0);
                        float y = xrp.getAttributeFloatValue(null, "y", 0);
                        float z = xrp.getAttributeFloatValue(null, "z", 0);
                        if (ncoords != null)
                        {
                            ncoords[normalIndex++] = x;
                            ncoords[normalIndex++] = y;
                            ncoords[normalIndex++] = z;
                        }
                    }
                    if (s.equals("face"))
                    {
                        short v1 = (short) xrp.getAttributeIntValue(null, "v1", 0);
                        short v2 = (short) xrp.getAttributeIntValue(null, "v2", 0);
                        short v3 = (short) xrp.getAttributeIntValue(null, "v3", 0);
                        if (icoords != null)
                        {
                            icoords[faceIndex++] = v1;
                            icoords[faceIndex++] = v2;
                            icoords[faceIndex++] = v3;
                        }
                    }
                    if (s.equals("colour_diffuse"))
                    {
                        String colorVal = (String) xrp.getAttributeValue(null, "value");
                        String[] colorVals = colorVal.split(" ");
                        colcoords[colorIndex++] = Float.parseFloat(colorVals[0]);
                        colcoords[colorIndex++] = Float.parseFloat(colorVals[1]);
                        colcoords[colorIndex++] = Float.parseFloat(colorVals[2]);
                        colcoords[colorIndex++] = 1f;
                    }
                }
                else if (xrp.getEventType() == XmlResourceParser.END_TAG)
                {
                    ;
                }
                else if (xrp.getEventType() == XmlResourceParser.TEXT)
                {
                    ;
                }
                xrp.next();
            }
            xrp.close();
        }
        catch (XmlPullParserException xppe)
        {
            Log.e("TAG", "Failure of .getEventType or .next, probably bad file format");
            xppe.toString();
        }
        catch (IOException ioe)
        {
            Log.e("TAG", "Unable to read resource file");
            ioe.printStackTrace();
        }
        mVertexBuffer = makeFloatBuffer(coords);
        mColorBuffer = makeFloatBuffer(colcoords);
        mNormalBuffer = makeFloatBuffer(ncoords);
        mIndexBuffer = makeShortBuffer(icoords);
    }



    private FloatBuffer makeFloatBuffer(float[] arr)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * BYTES_PER_VERTEX);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(arr);
        fb.position(0);
        return fb;
    }

    private ShortBuffer makeShortBuffer(short[] arr)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * BYTES_PER_VERTEX);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer ib = bb.asShortBuffer();
        ib.put(arr);
        ib.position(0);
        return ib;
    }

    public void draw(float[] mvpMatrix)
    {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, mVertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");


        // Draw the triangles
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, faceCount,
                GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);

        // Draw the triangle
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        MyGLRenderer.checkGlError("glDrawArrays");

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
