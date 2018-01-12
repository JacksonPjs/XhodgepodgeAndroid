package ui.widget.camera.view.utils;

import android.media.MediaFormat;

/**
 * encoder baseic core
 * Created by panbin on 2017/12/10.
 */

public class EncodeCore {
    protected static final String TAG="EncodeCore";
    protected static final long QUEUE_TIMEOUT=1000;

    protected MuxerStartListner muxerListner;

    public void setMuxerListner(MuxerStartListner listner){
        this.muxerListner=listner;
    }

    public interface MuxerStartListner{
        void onStartMuxer(int flag);
        void onAddTrack(MediaFormat format, int flag);
        void onWriteData(MediaMuxerData data);
        void onVideoEncodeStop();
        void onAudioEncodeStop();
    }

}
