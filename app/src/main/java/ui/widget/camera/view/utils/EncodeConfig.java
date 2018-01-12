package ui.widget.camera.view.utils;

import android.opengl.EGLContext;

import java.io.File;

/**
 * Created by panbin on 2017/12/11.
 */

public class EncodeConfig {
    final File mOutputFile;
    final int mWidth;
    final int mHeight;
    final int mBitRate;
    final int mFrameRate;
    final int mFrameInterval;
    EGLContext mEglContext;
    final int mAudioSource;
    final int mAudioSampleRate;

    public EncodeConfig(File outputFile, int width, int height, int bitRate, int frameRate, int frameInterval, int audioSource, int audioSampleRate) {
        mOutputFile = outputFile;
        mWidth = width;
        mHeight = height;
        mBitRate = bitRate;
        mFrameInterval=frameInterval;
        mFrameRate=frameRate;
        mAudioSource=audioSource;
        mAudioSampleRate=audioSampleRate;
    }

    public void updateEglContext(EGLContext eglContext) {
        mEglContext = eglContext;
    }
}
