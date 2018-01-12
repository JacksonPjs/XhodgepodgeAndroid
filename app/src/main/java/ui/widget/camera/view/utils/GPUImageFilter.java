package ui.widget.camera.view.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.PointF;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;

/**
 * 保持原画滤镜
 */

public class GPUImageFilter {

    public static final String NO_FILTER_VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "}";
    public static final String NO_FILTER_FRAGMENT_SHADER = "" +
            "#extension GL_OES_EGL_image_external : require\n" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform samplerExternalOES inputTexture;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "     gl_FragColor = texture2D(inputTexture, textureCoordinate);\n" +
            "}";


    private final LinkedList<Runnable> mRunOnDraw;
    private final String mVertexShader;
    private final String mFragmentShader;
    protected int mGLProgId;
    protected int mGLAttribPosition;
    protected int mGLUniformTexture;
    protected int mGLAttribTextureCoordinate;
    protected int mOutputWidth;
    protected int mOutputHeight;
    protected boolean mIsInitialized;

    protected FloatBuffer mGLCubeBuffer;
    protected FloatBuffer mGLTextureBuffer;

    protected int rotation;

    public GPUImageFilter() {
        this(NO_FILTER_VERTEX_SHADER, NO_FILTER_FRAGMENT_SHADER);
    }


    public GPUImageFilter(final String vertexShader, final String fragmentShader) {
        mRunOnDraw = new LinkedList<Runnable>();
        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;
    }

    public void setRotation(int degress){
        this.rotation=degress;
        Log.e("rotation==",rotation+"");
    }

    public final void init() {
        onInit();
        mIsInitialized = true;
        onInitialized();
    }


    public void onInit() {
        mGLProgId = OpenGLUtils.loadProgram(mVertexShader, mFragmentShader);
        //获取Shader中定义的变量在program中的位置
        mGLAttribPosition = GLES20.glGetAttribLocation(mGLProgId, "position");
        OpenGLUtils.checkGlError("glGetAttribLocation");
        mGLUniformTexture = GLES20.glGetUniformLocation(mGLProgId, "inputTexture");
        OpenGLUtils.checkGlError("glGetUniformLocation");
        mGLAttribTextureCoordinate = GLES20.glGetAttribLocation(mGLProgId, "inputTextureCoordinate");
        OpenGLUtils.checkGlError("glGetAttribLocation");
        mGLCubeBuffer = ByteBuffer.allocateDirect(OpenGLUtils.CUBE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLCubeBuffer.put(OpenGLUtils.CUBE).position(0);
        float []  rotatedTex;
        switch (rotation) {
            case 0:
//                rotatedTex=TextureRotationUtil.TEXTURE_NO_ROTATION;
//                rotatedTex = new float[]{
//                        TextureRotationUtil.flip(rotatedTex[0]), rotatedTex[1],
//                        TextureRotationUtil.flip(rotatedTex[2]), rotatedTex[3],
//                        TextureRotationUtil.flip(rotatedTex[4]), rotatedTex[5],
//                        TextureRotationUtil.flip(rotatedTex[6]), rotatedTex[7],
//                };
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION);
                break;
            case 90:
//                rotatedTex=TextureRotationUtil.TEXTURE_ROTATED_90;
//                rotatedTex = new float[]{
//                        rotatedTex[0], TextureRotationUtil.flip(rotatedTex[1]),
//                        rotatedTex[2], TextureRotationUtil.flip(rotatedTex[3]),
//                        rotatedTex[4], TextureRotationUtil.flip(rotatedTex[5]),
//                        rotatedTex[6], TextureRotationUtil.flip(rotatedTex[7]),
//                };
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_ROTATED_90.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_ROTATED_90);
                break;
            case 180:
                rotatedTex=TextureRotationUtil.TEXTURE_ROTATED_180;
                rotatedTex = new float[]{
                        TextureRotationUtil.flip(rotatedTex[0]), rotatedTex[1],
                        TextureRotationUtil.flip(rotatedTex[2]), rotatedTex[3],
                        TextureRotationUtil.flip(rotatedTex[4]), rotatedTex[5],
                        TextureRotationUtil.flip(rotatedTex[6]), rotatedTex[7],
                };
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_ROTATED_180.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(rotatedTex);
                break;
            case 270:
                rotatedTex=TextureRotationUtil.TEXTURE_ROTATED_270;
                rotatedTex = new float[]{
                        rotatedTex[0], TextureRotationUtil.flip(rotatedTex[1]),
                        rotatedTex[2], TextureRotationUtil.flip(rotatedTex[3]),
                        rotatedTex[4], TextureRotationUtil.flip(rotatedTex[5]),
                        rotatedTex[6], TextureRotationUtil.flip(rotatedTex[7]),
                };
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_ROTATED_270.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(rotatedTex);
                break;
            default:
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION);
                break;
        }
        //mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        //mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_ROTATED_90.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        //mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION);
        //mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_ROTATED_90);
        mIsInitialized = true;
    }

    public void onInitialized() {
    }

    public void onChangedRotation(int degress){
        this.rotation=degress;
        float []  rotatedTex;

        switch (rotation) {
            case 0:
//                rotatedTex=TextureRotationUtil.TEXTURE_NO_ROTATION;
//                rotatedTex = new float[]{
//                        TextureRotationUtil.flip(rotatedTex[0]), rotatedTex[1],
//                        TextureRotationUtil.flip(rotatedTex[2]), rotatedTex[3],
//                        TextureRotationUtil.flip(rotatedTex[4]), rotatedTex[5],
//                        TextureRotationUtil.flip(rotatedTex[6]), rotatedTex[7],
//                };
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION);
                break;
            case 90:
//                rotatedTex=TextureRotationUtil.TEXTURE_ROTATED_90;
//                rotatedTex = new float[]{
//                        rotatedTex[0], TextureRotationUtil.flip(rotatedTex[1]),
//                        rotatedTex[2], TextureRotationUtil.flip(rotatedTex[3]),
//                        rotatedTex[4], TextureRotationUtil.flip(rotatedTex[5]),
//                        rotatedTex[6], TextureRotationUtil.flip(rotatedTex[7]),
//                };
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_ROTATED_90.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_ROTATED_90);
                break;
            case 180:
//                rotatedTex=TextureRotationUtil.TEXTURE_ROTATED_180;
//                rotatedTex = new float[]{
//                        TextureRotationUtil.flip(rotatedTex[0]), rotatedTex[1],
//                        TextureRotationUtil.flip(rotatedTex[2]), rotatedTex[3],
//                        TextureRotationUtil.flip(rotatedTex[4]), rotatedTex[5],
//                        TextureRotationUtil.flip(rotatedTex[6]), rotatedTex[7],
//                };
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_ROTATED_180.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_ROTATED_180);
                break;
            case 270:
                rotatedTex=TextureRotationUtil.TEXTURE_ROTATED_270;
                rotatedTex = new float[]{
                        rotatedTex[0], TextureRotationUtil.flip(rotatedTex[1]),
                        rotatedTex[2], TextureRotationUtil.flip(rotatedTex[3]),
                        rotatedTex[4], TextureRotationUtil.flip(rotatedTex[5]),
                        rotatedTex[6], TextureRotationUtil.flip(rotatedTex[7]),
                };
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_ROTATED_270.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(rotatedTex);
                break;
            default:
                mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION);
                break;
        }
    }

    public final void destroy() {
        mIsInitialized = false;
        GLES20.glDeleteProgram(mGLProgId);
        onDestroy();
    }

    public void onDestroy() {
    }

    public void onOutputSizeChanged(final int width, final int height) {
        mOutputWidth = width;
        mOutputHeight = height;
    }

    public void onDraw(final int textureId, final FloatBuffer cubeBuffer, final FloatBuffer textureBuffer) {
        GLES20.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        if (!mIsInitialized) {
            return;
        }
        //顶点坐标从位置0开始读取
        cubeBuffer.position(0);
        //顶点坐标每次读取两个顶点值，之后间隔16（每行4个值 * 4个字节）的字节继续读取两个顶点值
//        glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 16, mDataBuffer);
        GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
        OpenGLUtils.checkGlError("glVertexAttribPointer");
        //使能顶点属性
        GLES20.glEnableVertexAttribArray(mGLAttribPosition);
        OpenGLUtils.checkGlError("glEnableVertexAttribArray");


        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);

        OpenGLUtils.checkGlError("glVertexAttribPointer");
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);
        OpenGLUtils.checkGlError("glEnableVertexAttribArray");
        if (textureId != OpenGLUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            OpenGLUtils.checkGlError("glActiveTexture");
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
            OpenGLUtils.checkGlError("glBindTexture");
            GLES20.glUniform1i(mGLUniformTexture, 0);
            OpenGLUtils.checkGlError("glUniform1i");
        }
        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        OpenGLUtils.checkGlError("glDrawArrays");
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        OpenGLUtils.checkGlError("glDisableVertexAttribArray");
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        OpenGLUtils.checkGlError("glDisableVertexAttribArray");
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        OpenGLUtils.checkGlError("glBindTexture");
    }


    public void onDraw(final int textureId) {
        GLES20.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        if (!mIsInitialized) {
            return;
        }
        mGLCubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer);
        OpenGLUtils.checkGlError("glVertexAttribPointer");
        GLES20.glEnableVertexAttribArray(mGLAttribPosition);
        OpenGLUtils.checkGlError("glEnableVertexAttribArray");
        mGLTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer);
        OpenGLUtils.checkGlError("glVertexAttribPointer");
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);
        OpenGLUtils.checkGlError("glEnableVertexAttribArray");
        if (textureId != OpenGLUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            OpenGLUtils.checkGlError("glActiveTexture");
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
            OpenGLUtils.checkGlError("glBindTexture");
            GLES20.glUniform1i(mGLUniformTexture, 0);
            OpenGLUtils.checkGlError("glUniform1i");
        }
        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        OpenGLUtils.checkGlError("glDrawArrays");
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        OpenGLUtils.checkGlError("glDisableVertexAttribArray");
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        OpenGLUtils.checkGlError("glDisableVertexAttribArray");
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        OpenGLUtils.checkGlError("glBindTexture");
    }



    protected void onDrawArraysPre() {}

    protected void runPendingOnDrawTasks() {
        while (!mRunOnDraw.isEmpty()) {
            mRunOnDraw.removeFirst().run();
        }
    }

    public boolean isInitialized() {
        return mIsInitialized;
    }

    public int getOutputWidth() {
        return mOutputWidth;
    }

    public int getOutputHeight() {
        return mOutputHeight;
    }

    public int getProgram() {
        return mGLProgId;
    }

    public int getAttribPosition() {
        return mGLAttribPosition;
    }

    public int getAttribTextureCoordinate() {
        return mGLAttribTextureCoordinate;
    }

    public int getUniformTexture() {
        return mGLUniformTexture;
    }

    protected void setInteger(final int location, final int intValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1i(location, intValue);
            }
        });
    }

    protected void setFloat(final int location, final float floatValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1f(location, floatValue);
            }
        });
    }

    protected void setFloatVec2(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatVec3(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform3fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatVec4(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform4fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatArray(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1fv(location, arrayValue.length, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setPoint(final int location, final PointF point) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                float[] vec2 = new float[2];
                vec2[0] = point.x;
                vec2[1] = point.y;
                GLES20.glUniform2fv(location, 1, vec2, 0);
            }
        });
    }

    protected void setUniformMatrix3f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES20.glUniformMatrix3fv(location, 1, false, matrix, 0);
            }
        });
    }

    protected void setUniformMatrix4f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES20.glUniformMatrix4fv(location, 1, false, matrix, 0);
            }
        });
    }

    protected void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.addLast(runnable);
        }
    }

    public static String loadShader(String file, Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream ims = assetManager.open(file);

            String re = convertStreamToString(ims);
            ims.close();
            return re;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}
