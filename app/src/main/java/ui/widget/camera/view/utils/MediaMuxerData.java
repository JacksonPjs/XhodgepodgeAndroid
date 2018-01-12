package ui.widget.camera.view.utils;

import android.media.MediaCodec;

import java.nio.ByteBuffer;

public class MediaMuxerData {
    public static final int VIDEO_FLAG=0;
    public static final int AUDIO_FLAG=1;
    public ByteBuffer data;
    public MediaCodec.BufferInfo bufferInfo;
    public int dataFlag;//1=video 2=audio
    public String timestamp;

    public MediaMuxerData(ByteBuffer data, MediaCodec.BufferInfo bufferInfo, int dataFlag){
        this.data=data;
        this.bufferInfo=bufferInfo;
        this.dataFlag=dataFlag;
        timestamp=DateUtils.getTimestamp();
    }

    @Override
    public String toString() {
        return "MuxerData|data="+data+"|bufferInfo="+bufferInfo+"|dataFlag="+dataFlag+"|timestamp="+timestamp;
    }
}
