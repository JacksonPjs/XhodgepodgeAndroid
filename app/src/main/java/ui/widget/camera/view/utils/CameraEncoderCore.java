package ui.widget.camera.view.utils;

import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;
import android.view.Surface;

import java.util.Date;
import java.util.Vector;

/**
 * Created by panbin on 2017/12/13.
 */

public class CameraEncoderCore implements EncodeCore.MuxerStartListner {
    private static final String TAG = "CAMERAENCODE";
    private MediaMuxer mMuxer;
    private VideoEncodeCore videoCore;
    private int videoTrack;
    private boolean isVideoTrackReady;
    private AudioEncodeCore audioCore;
    private int audioTrack;
    private boolean isAudioTrackReady;
    private boolean muxerStarted;
    private boolean isAudioRecordDone;
    private boolean isVideoRecordDone;
    private Vector<MediaMuxerData> queueData;
    private LoopTask writeTask;
    private long presentationTimeUs = 0;

    public CameraEncoderCore(EncodeConfig cfg) {
        try {
            if (cfg == null || cfg.mOutputFile == null) {
                throw new RuntimeException("encode config error");
            }
            mMuxer = new MediaMuxer(cfg.mOutputFile.toString(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            videoCore = new VideoEncodeCore(cfg.mWidth, cfg.mHeight, cfg.mFrameRate, cfg.mFrameInterval, cfg.mBitRate);
            videoCore.setMuxerListner(this);
            audioCore = new AudioEncodeCore(cfg.mAudioSource, cfg.mAudioSampleRate);
            audioCore.setMuxerListner(this);
            queueData = new Vector<MediaMuxerData>();
            isAudioRecordDone = false;
            isVideoRecordDone = false;
            isAudioTrackReady = false;
            isVideoTrackReady = false;
        } catch (Exception e) {
            Log.e(TAG, "create camera encode core exception:" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("create CameraEncoderCore error");
        }
    }

    public Surface getEncodeSurface() {
        if (videoCore != null) {
            return videoCore.getInputSurface();
        }
        return null;
    }

    public void startAudioRecord() {
        audioCore.startRecord();
    }

    public void stopAudioRecord() {
        audioCore.onStopInputTask();
    }

    public void drawFrame(boolean endofstream) {
        try {
            videoCore.encodeOutputFrame(endofstream);
        } catch (Exception e) {
            Log.e(TAG, "draw video frame exception:" + e.getMessage());
            e.printStackTrace();
        }
    }


    private void startWriteTask() {
        if (writeTask != null) {
            writeTask.onStop();
            writeTask = null;
        }
        writeTask = new LoopTask(new LoopTask.Task() {
            @Override
            public void onRun() {

            }

            MediaMuxerData data = null;

            @Override
            public void onLoop() {
                if (queueData.size() < 1) {
                    //Log.w(TAG,"data not ready");
                    return;
                }
                data = queueData.get(0);
                writeSample(data);
                queueData.remove(0);
                data = null;
            }
        }).onStart();
    }

    private void writeSample(MediaMuxerData data) {
        int track = data.dataFlag == MediaMuxerData.AUDIO_FLAG ? audioTrack : videoTrack;
        data.bufferInfo.presentationTimeUs = getTimeUs();
        try {
            mMuxer.writeSampleData(track, data.data, data.bufferInfo);
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }

    @Override
    public void onStartMuxer(int flag) {
        if (flag == MediaMuxerData.AUDIO_FLAG) {
            isAudioTrackReady = true;
        } else if (flag == MediaMuxerData.VIDEO_FLAG) {
            isVideoTrackReady = true;
        }
        if (isVideoTrackReady && isAudioTrackReady) {
            if (mMuxer != null && !muxerStarted) {
                muxerStarted = true;
                mMuxer.start();
//                startWriteTask();
            }
        }
    }

    int count=0;

    @Override
    public void onAddTrack(MediaFormat format, int flag) {
        if (mMuxer != null && !muxerStarted) {
            int index = mMuxer.addTrack(format);
            if (flag == MediaMuxerData.VIDEO_FLAG) {
                videoTrack = index;
                count++;
            } else if (flag == MediaMuxerData.AUDIO_FLAG) {
                audioTrack = index;
                count++;
            }
        }
    }

    @Override
    public void onWriteData(MediaMuxerData muxerData) {
        queueData.add(muxerData);

        if (!muxerStarted) {
            return;
        }
        muxerData.bufferInfo.presentationTimeUs = getTimeUs();
        int track = muxerData.dataFlag == MediaMuxerData.AUDIO_FLAG ? audioTrack : videoTrack;
        mMuxer.writeSampleData(track, muxerData.data, muxerData.bufferInfo);
    }

    public void startWrite() {

    }

    @Override
    public void onAudioEncodeStop() {
        Log.i(TAG, "on audio encode stop");
        isAudioRecordDone = true;
        audioCore.onStopOutputTask();
        audioCore.release();
        stopMuxer();
    }

    @Override
    public void onVideoEncodeStop() {
        Log.i(TAG, "on video encode stop");
        isVideoRecordDone = true;
        videoCore.stopRecord();
        videoCore.release();
        stopMuxer();
    }

    private void stopMuxer() {
        Log.i(TAG, "isVideoRecordDone==" + isVideoRecordDone + "isAudioRecordDone==" + isAudioRecordDone);
        if (isVideoRecordDone && isAudioRecordDone) {
            mMuxer.stop();
            mMuxer.release();
        }
    }


    private long getTimeUs() {
        long current = new Date().getTime() * 1000;
        return current - presentationTimeUs;
    }
}
