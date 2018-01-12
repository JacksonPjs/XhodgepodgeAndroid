package ui.widget.camera.view.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * audio record proccess: new AudioEncodeCore --> startRecord --> stopRecord.
 * Created by panbin on 2017/12/10.
 */

public class AudioEncodeCore extends EncodeCore{
    private static final String AUDIO_MIME="audio/mp4a-latm";
    private MediaCodec encoder;
    private AudioRecord audioRecord;
    private int trackIndex;
    private boolean recordStarted;
    private int audioBufferSize=0;
    private int audioSource;
    private int sampleRate;
    private MediaCodec.BufferInfo bufferInfo;
    private ByteBuffer[] inputBufs;
    private ByteBuffer[] outputBufs;
    private long presentationTimeUs;
    public boolean isAudioTrackReady;

    public AudioEncodeCore(Integer asource, Integer sampleRate)throws Exception {
        if (asource != null) {
            this.audioSource = asource;
        } else {
            this.audioSource = MediaRecorder.AudioSource.MIC;
        }

        if (sampleRate != null) {
            this.sampleRate = sampleRate;
        } else {
            this.sampleRate=44100;
        }
        MediaFormat format=createAudioFormat();
        encoder= MediaCodec.createEncoderByType(AUDIO_MIME);
        encoder.configure(format,null,null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        audioBufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(audioSource, sampleRate, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, audioBufferSize);
        encoder.start();
        bufferInfo=new MediaCodec.BufferInfo();
        inputBufs=encoder.getInputBuffers();
        outputBufs=encoder.getOutputBuffers();
        recordStarted=false;
        isAudioTrackReady=false;
        trackIndex=-1;
    }
    public MediaFormat createAudioFormat(){
        //类型、采样、通道
        MediaFormat format = MediaFormat.createAudioFormat(AUDIO_MIME, sampleRate, 2);
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        //码率:64000,256000
        format.setInteger(MediaFormat.KEY_BIT_RATE, 256000);
        return format;
    }



    private void onInput(){
        int index=encoder.dequeueInputBuffer(QUEUE_TIMEOUT);
        if (index > -1) {
            ByteBuffer buf=inputBufs[index];
            int size=audioRecord.read(buf,audioBufferSize);
            if (size > -1) {
                //为保证音视频 同步 此处 时间戳写为0 在 muxer写入数据的时候才添加时间戳
                encoder.queueInputBuffer(index,0,size,0,0);
            }
        }
    }

    public void onStopInputTask(){
        recordStarted=false;
        if (inputTask != null) {
            inputTask.onStop();
            Log.i(TAG,"stop audio encoder input");
        }
    }

    private void onOutput(){
        int index=encoder.dequeueOutputBuffer(bufferInfo,QUEUE_TIMEOUT);
        switch (index) {
            case MediaCodec.INFO_TRY_AGAIN_LATER:
                break;
            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                if (isAudioTrackReady) {
                    throw new IllegalStateException("output format changed twice");
                }
                //trackIndex=mMuxer.addTrack(encoder.getOutputFormat());
                isAudioTrackReady=true;
                muxerListner.onAddTrack(encoder.getOutputFormat(),MediaMuxerData.AUDIO_FLAG);
                muxerListner.onStartMuxer(MediaMuxerData.AUDIO_FLAG);
                break;
            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                outputBufs=encoder.getOutputBuffers();
                inputBufs=encoder.getInputBuffers();
                break;
            default:
                if (index < 0 || bufferInfo.size == 0) {
                    encoder.releaseOutputBuffer(index,false);
                    break;
                }
                if (!recordStarted) {
                    muxerListner.onAudioEncodeStop();
                    break;
                }
                ByteBuffer buf=outputBufs[index];
                buf.position(bufferInfo.offset);
                buf.limit(bufferInfo.size+bufferInfo.offset);
                muxerListner.onWriteData(new MediaMuxerData(buf,bufferInfo,MediaMuxerData.AUDIO_FLAG));
                //mMuxer.writeSampleData(trackIndex,buf,bufferInfo);
                encoder.releaseOutputBuffer(index,false);
                break;
        }
    }

    private LoopTask inputTask;
    private LoopTask outputTask;
    private void onStartInput(){
        if (inputTask != null) {
            inputTask.onStop();
            inputTask=null;
        }
        if (audioRecord == null || audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.w(TAG,"AudioRecord not ready......");
            return;
        }
        inputTask=new LoopTask(new LoopTask.Task() {
            @Override
            public void onRun() {
                audioRecord.startRecording();
            }
            @Override
            public void onLoop() {
                try {
                    onInput();
                }catch (Exception e){
                    Log.e(TAG,"audio encoder input task exception:"+e.getMessage());
                    e.printStackTrace();
                }
            }
        }).onStart();
    }
    private void onStartOutput(){
        if (outputTask != null) {
            outputTask.onStop();
            outputTask=null;
        }
        outputTask=new LoopTask(new LoopTask.Task() {
            @Override
            public void onRun() {

            }
            @Override
            public void onLoop() {
                try {
                    onOutput();
                }catch (Exception e){
                    Log.e(TAG,"audio encoder output task exception:"+e.getMessage());
                    e.printStackTrace();
                }
            }
        }).onStart();
    }

    public void onStopOutputTask(){
        if (outputTask != null) {
            outputTask.onStop();
        }
    }

    public void startRecord(){
        if (recordStarted) {
            return;
        }
        recordStarted=true;
        onStartInput();
        onStartOutput();
    }


    public void release(){
        try {
            if (recordStarted) {
                Log.w(TAG,"audio encoder release failed record state:"+recordStarted);
                return;
            }
            inputTask=null;
            outputTask=null;
            if (encoder != null) {
                encoder.release();
            }
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
            }
        }catch (Exception e){
            Log.e(TAG,"audio encoder release:"+e.getMessage());
            e.printStackTrace();
        }
    }

}
