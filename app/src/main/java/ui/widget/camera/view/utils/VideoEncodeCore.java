package ui.widget.camera.view.utils;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.nio.ByteBuffer;

/**
 * Created by panbin on 2017/12/10.
 */

public class VideoEncodeCore extends EncodeCore{
    private int trackIndex;
    private MediaCodec encoder;
    private int mWidth;
    private int mHeight;
    private int mFrameRate = 30;
    private int biteRate;
    private int keyFrameInterval=0;
    private Surface inputSurface;
    private static final String VIDEO_MIME="video/avc";
    private MediaCodec.BufferInfo bufferInfo;
    private ByteBuffer[] outputBufs;
    public boolean isVideoTrackReady;

    public VideoEncodeCore(int width, int height, Integer frameRate, Integer frameInterval, Integer bitRate)throws Exception {
        this.mWidth=width;
        this.mHeight=height;
        if (frameRate != null) {
            mFrameRate=frameRate;
        }
        if (frameInterval != null) {
            keyFrameInterval=frameInterval;
        }
        if (bitRate != null) {
            this.biteRate=bitRate;
        }else{
            biteRate = width*height*10;
        }
        MediaFormat format=createVideoFormat();
        encoder= MediaCodec.createEncoderByType(VIDEO_MIME);
        encoder.configure(format,null,null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        inputSurface=encoder.createInputSurface();
        bufferInfo=new MediaCodec.BufferInfo();
        encoder.start();
        outputBufs=encoder.getOutputBuffers();
        isVideoTrackReady=false;
        trackIndex=-1;

    }

    public MediaFormat createVideoFormat(){
        MediaFormat format= MediaFormat.createVideoFormat(VIDEO_MIME,mWidth,mHeight);
        //色彩空间
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        //设置比特率
        format.setInteger(MediaFormat.KEY_BIT_RATE, biteRate);
        //设置帧率
        format.setInteger(MediaFormat.KEY_FRAME_RATE, mFrameRate);
        //设置码率模式
        format.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ);
        //设置关键帧
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, keyFrameInterval);
        //MediaCodecInfo.CodecProfileLevel.AVCProfileHigh
        //format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE,mWidth * mHeight * 10);
        return format;
    }

    public Surface getInputSurface(){
        return inputSurface;
    }


    public void encodeOutputFrame(boolean endofstream)throws Exception {
        if (endofstream) {
            encoder.signalEndOfInputStream();
        }
        //ByteBuffer[] outputBufs=encoder.getOutputBuffers();
        while (true) {
            int index=encoder.dequeueOutputBuffer(bufferInfo,QUEUE_TIMEOUT);
            if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!endofstream) {
                    break;
                }
            } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (isVideoTrackReady) {
                    throw new IllegalStateException("video output format change twice");
                }
                MediaFormat format=encoder.getOutputFormat();
                //trackIndex=mMuxer.addTrack(format);
                isVideoTrackReady=true;
                muxerListner.onAddTrack(format,MediaMuxerData.VIDEO_FLAG);
                muxerListner.onStartMuxer(MediaMuxerData.VIDEO_FLAG);
            } else if (index == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                outputBufs=encoder.getOutputBuffers();
            } else if(index < 0){
                Log.w(TAG,"unexpect output index:"+index);
            }else {

                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    bufferInfo.size=0;
                }
                if (bufferInfo.size != 0) {
                    ByteBuffer buf=outputBufs[index];
                    buf.position(bufferInfo.offset);
                    buf.limit(bufferInfo.offset+ bufferInfo.size);
                    muxerListner.onWriteData(new MediaMuxerData(buf,bufferInfo,MediaMuxerData.VIDEO_FLAG));
                    //mMuxer.writeSampleData(trackIndex,buf,bufferInfo);
                }
                encoder.releaseOutputBuffer(index,false);
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    muxerListner.onVideoEncodeStop();
                    break;
                }
            }
        }
    }

    public void stopRecord(){
        if (encoder != null) {
            encoder.stop();
        }
    }
    public void release(){
        try {
            if (encoder != null) {
                encoder.release();
                encoder=null;
            }

        } catch (Exception e) {
            Log.e(TAG,"release video encoder exception:"+e.getMessage());
            e.printStackTrace();
        }
    }


}
