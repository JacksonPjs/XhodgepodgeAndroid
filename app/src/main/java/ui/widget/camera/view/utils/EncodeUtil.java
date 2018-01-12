package ui.widget.camera.view.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.Surface;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

/**
 * 编码工具类
 *标准输出：mp4 1080p acc 30rate
 */
public class EncodeUtil {

    private static final String TAG="ENCODEUTIL";
    private String videoMime="video/avc";
    private String audioMime="audio/mp4a-latm";
    private MediaFormat videoFormat;
    private MediaFormat audioFormat;
    private Surface encodeSurface;
    private MediaCodec videoEncoder;
    private MediaCodec audioEncoder;
    private int videoTrack;
    private int audioTrack;
    private MediaMuxer muxer;
    private int width;
    private int height;
    private int frameRate;
    private String path;
    //private int colorFormat;
    private int audioBufferSize = 0;
    private AudioRecord audioRecord;
    private boolean isRecording;
    private boolean isMuxerReady;
    private boolean isVideoEncoderReady;
    private boolean isAudioEncoderReady;
    //encoder init status
    public  boolean initStatus = false;
    private Integer rotation;
    private long presentationTimeUs;
    private Vector<MuxerData> dataQueue;
    private LoopTask writeTask;


    public void init(Integer videoWidth, Integer videoHeight, Integer videoRate, String videoPath, Integer videoRotation)throws Exception {
        width = videoWidth;
        height = videoHeight;
        frameRate = videoRate;
        path = videoPath;
        rotation = videoRotation;
        if (path == null || path.length() < 1) {
            throw new IllegalArgumentException();
        }
        if (videoWidth == null || width == 0) {
            width=1920;
        }
        if (videoHeight == null || height == 0) {
            height = 1080;
        }
        if (videoRate == null || frameRate == 0) {
            frameRate = 30;
        }
        //colorFormat = chooseColorFormat();
        videoFormat = createVideoFormat();
        audioFormat = createAudioFormat();
        //init video encoder
        videoEncoder = MediaCodec.createEncoderByType(videoMime);
        //set callback before configure
        //videoEncoder.setCallback(videoEncoderCallback);
        videoEncoder.configure(videoFormat,null,null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        encodeSurface = videoEncoder.createInputSurface();
        videoEncoder.start();

        //init audio encoder
        audioEncoder = MediaCodec.createEncoderByType(audioMime);
        audioEncoder.configure(audioFormat,null,null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        audioBufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, audioBufferSize);
        audioEncoder.start();
        //init muxer
        muxer=new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        if (rotation != null) {
            muxer.setOrientationHint(rotation);
        }
        initStatus=true;
        isRecording=false;
        isMuxerReady=false;
        isVideoEncoderReady=false;
        isAudioEncoderReady=false;
        presentationTimeUs=0;
    }

    public MediaFormat createVideoFormat(){
        MediaFormat format= MediaFormat.createVideoFormat(videoMime,width,height);
        //色彩空间
        //format.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);

        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        //设置比特率
        format.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 10);
        //设置帧率
        format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
        //角度
        if (rotation != null) {
            format.setInteger(MediaFormat.KEY_ROTATION, rotation);
        }
        //设置关键帧
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 0);

        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE,width * height * 10);

        return format;
    }

    public MediaFormat createAudioFormat(){
        //类型、采样、通道
        MediaFormat format = MediaFormat.createAudioFormat(audioMime, 44100, 2);
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        //码率:64000,256000
        format.setInteger(MediaFormat.KEY_BIT_RATE, 256000);
        return format;
    }

    public int chooseColorFormat(){
        int numCodecs = MediaCodecList.getCodecCount();
        MediaCodecInfo codecInfo = null;
        for (int i = 0; i < numCodecs && codecInfo == null; i++) {
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
            if (!info.isEncoder()) {
                continue;
            }
            String[] types = info.getSupportedTypes();
            boolean found = false;
            //轮训所要的解码器
            for (int j = 0; j < types.length && !found; j++) {
                if (types[j].equals(videoMime)) {
                    Log.i(TAG,"found supported codec type:"+types[j]);
                    found = true;
                }
            }
            if (!found) {
                continue;
            }
            codecInfo = info;
        }
        int format = 0;
        boolean isI420Support=false;
        boolean isNV12Support=false;
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType("video/avc");
        System.out.println("length-" + capabilities.colorFormats.length + "==" + Arrays.toString(capabilities.colorFormats));
        for (int i = 0; i < capabilities.colorFormats.length; i++) {
            format = capabilities.colorFormats[i];
            if(format == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar){
                isI420Support=true;
            }else if(format == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar){
                isNV12Support=true;
            }
        }
        if (isI420Support) {
            return  MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar;
        } else if (isNV12Support) {
            return MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;
        }else{
            return MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible;
        }
    }

    private void onVideoEncoderStart(){
        if (initStatus && !isRecording) {
            onVideoOutput();
        }

    }

    private void onAudioEncoderStart(){
        if (initStatus && !isRecording) {
            onAudioInput();
            onAudioOutput();
        }
    }

    private void onVideoEncoderStop(){
        if (!isRecording) {
            videoOutputTask.onStop();
            //videoEncoder.signalEndOfInputStream();
            videoEncoder.stop();
        }
    }

    private void onAudioEncoderStop(){
        if (!isRecording) {
            audioOutputTask.onStop();
            audioInputTask.onStop();
            audioEncoder.stop();
        }
    }

    public void onEncodeStart(){
        if (!isRecording) {
            initTimeUs();
            if (dataQueue != null) {
                dataQueue.clear();
                dataQueue=null;
            }
            dataQueue=new Vector<MuxerData>();
            onVideoEncoderStart();
            onAudioEncoderStart();
            isRecording=true;
        }
    }

    public void onEncodeStop(){
        if (isRecording) {
            isRecording=false;
            onAudioEncoderStop();
            onVideoEncoderStop();
            writeTask.onStop();
            destroy();
        }
    }

    public Surface getEncodeSurface(){
        return encodeSurface;
    }

//    public int getColorFormat(){
//        return colorFormat;
//    }

    private void initTimeUs(){
        presentationTimeUs = new Date().getTime()*1000;
    }

    private long getTimeUs(){
        long current=new Date().getTime()*1000;
        return current - presentationTimeUs;
    }

    public void destroy(){
        try {
            if (isRecording) {
                return;
            }
            if (videoEncoder != null) {
                //videoEncoder.stop();
                videoEncoder.release();
            }
            if (audioEncoder != null) {
                //audioEncoder.stop();
                audioEncoder.release();
            }

            if (audioInputTask != null) {
                //audioInputTask.onStop();
                audioInputTask=null;
            }
            if (videoOutputTask != null) {
                //videoOutputTask.onStop();
                videoOutputTask=null;
            }
            if (writeTask != null) {
                //writeTask.onStop();
                writeTask=null;
            }
            if (dataQueue != null) {
                dataQueue.clear();
                dataQueue=null;
            }
            if (muxer != null) {
                muxer.release();
            }
            if (audioRecord != null) {
                //audioRecord.stop();
                audioRecord.release();
            }

        } catch (Exception e) {
            Log.e(TAG,"encoder destroy exception:"+e.getMessage());
            e.printStackTrace();
        }
    }

    //video encoder callback
//    private MediaCodec.Callback videoEncoderCallback=new MediaCodec.Callback() {
//        @Override
//        public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
//            Log.i(TAG,"video encoder on input buffer available");
//        }
//
//        @Override
//        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
//            if (index < 0 || info.size == 0) {
//                Log.w(TAG,"video encoder on output not available buffer index:"+index+" buffer size:"+info.size);
//            }
//            ByteBuffer buf=videoEncoder.getOutputBuffer(index);
//            buf.position(info.offset);
//            buf.limit(info.offset+info.size);
//            dataQueue.add(new MuxerData(buf,info,videoTrack));
//            videoEncoder.releaseOutputBuffer(index,false);
//        }
//
//        @Override
//        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
//            Log.w(TAG,"video encoder encoding exception:"+e.getMessage());
//        }
//
//        @Override
//        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
//            videoTrack=muxer.addTrack(format);
//            isVideoEncoderReady=true;
//            startMuxer();
//        }
//    };

    //audio encoder callback
//    private MediaCodec.Callback audioEncoderCallback=new MediaCodec.Callback() {
//        @Override
//        public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
//            //read data from audio record,queue to audio encoder.
//            ByteBuffer inputBuf=audioEncoder.getInputBuffer(index);
//            inputBuf.clear();
//            int length=audioRecord.read(inputBuf,audioBufferSize);
//            if (length >= 0) {
//                audioEncoder.queueInputBuffer(index,0,length,getTimeUs(),0);
//            }
//        }
//
//        @Override
//        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
//            //get the encoded data from audio encoder and send to dataQueue
//            if (index < 0 || info.size == 0) {
//                Log.w(TAG,"audio encoder on output not available buffer index:"+index+" buffer size:"+info.size);
//                return;
//            }
//            ByteBuffer outputBuf=audioEncoder.getOutputBuffer(index);
//            outputBuf.position(info.offset);
//            outputBuf.limit(info.size+info.offset);
//            dataQueue.add(new MuxerData(outputBuf,info,audioTrack));
//            audioEncoder.releaseOutputBuffer(index,false);
//        }
//
//        @Override
//        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
//            Log.w(TAG,"audio encoder encoding exception"+e.getMessage());
//        }
//
//        @Override
//        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
//            //init audio format into muxer get the audio encoder ready
//            audioTrack=muxer.addTrack(format);
//            isAudioEncoderReady=true;
//            startMuxer();
//
//        }
//    };

    private ByteBuffer[] audioInputBuffer;
    private ByteBuffer[] audioOutputBuffer;
    private LoopTask audioInputTask;
    private LoopTask audioOutputTask;
    private MediaCodec.BufferInfo bufferInfo;
    private  static final long QUEUE_DATA_TIMEOUT=1000;
    private void onAudioInput(){
        if (!initStatus ) {
            return;
        }
        audioInputBuffer = audioEncoder.getInputBuffers();
        audioInputTask = new LoopTask(new LoopTask.Task() {
            @Override
            public void onRun() {
                if (audioRecord != null && initStatus) {
                    audioRecord.startRecording();
                }
            }
            @Override
            public void onLoop() {
                try {
                    int index=audioEncoder.dequeueInputBuffer(QUEUE_DATA_TIMEOUT);
                    if (index >= 0) {
                        ByteBuffer buf=audioEncoder.getInputBuffer(index);
                        int size=audioRecord.read(buf,audioBufferSize);
                        if (size >= 0) {
                            audioEncoder.queueInputBuffer(index,0,size,getTimeUs(),0);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG,"audio encoder input data exception:"+e.getMessage());
                    e.printStackTrace();
                }

            }
        }).onStart();
    }

    private void onAudioOutput(){
        if (!initStatus) {
            return;
        }
        audioOutputBuffer=audioEncoder.getOutputBuffers();
        audioOutputTask = new LoopTask(new LoopTask.Task() {
            @Override
            public void onRun() {
                if (bufferInfo == null) {
                    bufferInfo=new MediaCodec.BufferInfo();
                    bufferInfo.flags= MediaCodec.BUFFER_FLAG_KEY_FRAME;
                }
            }

            @Override
            public void onLoop() {
                try {
                    if (!isRecording) {
                        return;
                    }
                    int index=audioEncoder.dequeueOutputBuffer(bufferInfo,QUEUE_DATA_TIMEOUT);
                    switch (index) {
                        case MediaCodec.INFO_TRY_AGAIN_LATER:
                            break;
                        case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                            audioTrack=muxer.addTrack(audioEncoder.getOutputFormat());
                            isAudioEncoderReady=true;
                            startMuxer();
                            break;
                        case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                            audioOutputBuffer=audioEncoder.getOutputBuffers();
                            break;
                        default:
                            if (index < 0 || bufferInfo.size == 0) {
                                break;
                            }
                            ByteBuffer buf=audioEncoder.getOutputBuffer(index);
                            buf.position(bufferInfo.offset);
                            buf.limit(bufferInfo.size+bufferInfo.offset);
                            dataQueue.add(new MuxerData(buf,bufferInfo,audioTrack));
                            audioEncoder.releaseOutputBuffer(index,false);

                    }
                }catch (Exception e){
                    Log.e(TAG,"audio encoder outptut data exception:"+e.getMessage());
                    e.printStackTrace();
                }

            }
        }).onStart();
    }


//    private ByteBuffer[] videoInputBuffer;
    private ByteBuffer[] videoOutputBuffer;
//    private LoopTask videoInputTask;
    private LoopTask videoOutputTask;
    private MediaCodec.BufferInfo videoBufferInfo;
    //on surface input status not need
//    private void onVideoInput(){
//
//    }

    private void onVideoOutput(){
        if (!initStatus) {
            return;
        }
        videoOutputBuffer=videoEncoder.getOutputBuffers();
        videoOutputTask=new LoopTask(new LoopTask.Task() {
            @Override
            public void onRun() {
                if (videoBufferInfo == null) {
                    videoBufferInfo=new MediaCodec.BufferInfo();
                    videoBufferInfo.flags= MediaCodec.BUFFER_FLAG_KEY_FRAME;
                }
            }
            @Override
            public void onLoop() {
                try {
                    if (!isRecording) {
                        return;
                    }
                    int index=videoEncoder.dequeueOutputBuffer(videoBufferInfo,QUEUE_DATA_TIMEOUT);
                    switch (index) {
                        case MediaCodec.INFO_TRY_AGAIN_LATER:
                            break;
                        case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                            videoTrack=muxer.addTrack(videoEncoder.getOutputFormat());
                            isVideoEncoderReady=true;
                            startMuxer();
                            break;
                        case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                            videoOutputBuffer=videoEncoder.getOutputBuffers();
                            break;
                        default:
                            if (index < 0 || videoBufferInfo.size < 1) {
                                break;
                            }
                            ByteBuffer buf=videoEncoder.getOutputBuffer(index);
                            buf.position(videoBufferInfo.offset);
                            buf.limit(videoBufferInfo.offset+videoBufferInfo.size);
                            dataQueue.add(new MuxerData(buf,videoBufferInfo,videoTrack));
                            videoEncoder.releaseOutputBuffer(index,false);
                            break;
                    }
                } catch (Exception e) {
                    Log.e(TAG,"on video encoder outptut exception:"+e.getMessage());
                    e.printStackTrace();
                }
            }
        }).onStart();
    }


    private synchronized void startMuxer(){
        if (isAudioEncoderReady && isVideoEncoderReady) {
            isMuxerReady=true;
            muxer.start();
            onEncoderWrite();
        }
    }



    private void onEncoderWrite(){
        writeTask=new LoopTask(new LoopTask.Task() {
            @Override
            public void onRun() {

            }
            MuxerData md=null;
            @Override
            public void onLoop() {
                if (!isMuxerReady || dataQueue.size() < 1) {
                    //Log.w(TAG,"muxer or data is not available");
                    return;
                }
                md=dataQueue.get(0);
                writeSampleOnMuxer(md.trackIndex,md.data,md.bufferInfo);
                dataQueue.remove(0);
                md= null;
            }
        }).onStart();
    }

    private synchronized void writeSampleOnMuxer(int trackIndex, ByteBuffer buf, MediaCodec.BufferInfo bufferInfo){
        muxer.writeSampleData(trackIndex,buf,bufferInfo);
    }

    public class MuxerData{
        public ByteBuffer data;
        public MediaCodec.BufferInfo bufferInfo;
        public int trackIndex;
        public String timestamp;

        public MuxerData(ByteBuffer data, MediaCodec.BufferInfo bufferInfo, int trackIndex){
            this.data=data;
            this.bufferInfo=bufferInfo;
            this.trackIndex=trackIndex;
            timestamp=DateUtils.getTimestamp();
        }

        @Override
        public String toString() {
            return "MuxerData|data="+data+"|bufferInfo="+bufferInfo+"|trackIndex="+trackIndex+"|timestamp="+timestamp;
        }
    }


}
