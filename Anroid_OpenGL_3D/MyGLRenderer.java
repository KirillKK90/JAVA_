package kkkhome.kirillkandroidopengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {
    Context mContext;
    private static final String TAG = "MyGLRenderer";
    private Triangle mTriangle;
    private Square   mSquare;

    private DrawModel model;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] mRotationMatrix = new float[16];

    private float mAngle;

    private float mX;
    private float mY;
    private float mPreviousX;
    private float mPreviousY;

    private float mScreenWidth;
    private float mScreenHeight;

    private float[] mQuat = new float[4]; // x y z w axis, angle

    public MyGLRenderer(Context context) {
        mContext = context;
        //quat set identity
        mQuat[0] = 0;
        mQuat[1] = 0;
        mQuat[2] = 0;
        mQuat[3] = 1;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        Matrix.setIdentityM(mRotationMatrix, 0);
        mTriangle = new Triangle();
        mSquare   = new Square();
        model = new DrawModel(mContext.getResources().getXml(R.xml.robo8_full_only_robot));
    }

    void combine2rotationsQuats(float[] a, float[] b, float[] res) {
        res[0] = a[0] * b[0] - a[1] * b[1] - a[2] * b[2] - a[3] * b[3];
        res[1] = a[0] * b[1] + a[1] * b[0] + a[2] * b[3] - a[3] * b[2];
        res[2] = a[0] * b[2] - a[1] * b[3] + a[2] * b[0] + a[3] * b[1];
        res[3] = a[0] * b[3] + a[1] * b[2] - a[2] * b[1] + a[3] * b[0];
    }

    public void get3DRotation(float[] angleAxis)
    {
        VirtualTrackball vT = new VirtualTrackball();
        float[] thisRotQuat = new float[4];

        float sceneX = (mX / mScreenWidth)*2.0f - 1.0f;
        float sceneY = (mY / mScreenHeight)*-2.0f + 1.0f;

        float scenePrevX = (mPreviousX / mScreenWidth)*2.0f - 1.0f;
        float scenePrevY = (mPreviousY / mScreenHeight)*-2.0f + 1.0f;

        vT.gfs_gl_trackball(thisRotQuat, scenePrevX, scenePrevY, sceneX, sceneY);

        float[] resQuat = new float[4];
        vT.gfs_gl_add_quats(thisRotQuat, mQuat, resQuat);
        // combine2rotationsQuats(thisRotQuat, mQuat, resQuat);
        mQuat[0] = resQuat[0];
        mQuat[1] = resQuat[1];
        mQuat[2] = resQuat[2];
        mQuat[3] = resQuat[3];

        vT.given_quaternion_get_angleaxis(resQuat, angleAxis);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        int eyeZPos = -3;
        float[] scratch = new float[16];

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, eyeZPos, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        if (mPreviousY > 0.0f && mPreviousX > 0.0f)
        {
            // angle, x, y, z
            float[] angleAxis = new float[4];
            angleAxis[0] = 0.0f;
            angleAxis[1] = 0.0f;
            angleAxis[2] = 0.0f;
            angleAxis[3] = 0.0f;
            get3DRotation(angleAxis);
            Matrix.setRotateM(mRotationMatrix, 0, angleAxis[0] * 57.296f, angleAxis[1], angleAxis[2], angleAxis[3]);
        }
        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        model.draw(scratch);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        mScreenWidth = width;
        mScreenHeight = height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

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
    *
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

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    public float getAngle() {
        return mAngle;
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float angle) {
        mAngle = angle;
    }

    public void setXY(float x, float y) {
        mPreviousX = mX;
        mPreviousY = mY;
        mX = x;
        mY = y;
    }
}