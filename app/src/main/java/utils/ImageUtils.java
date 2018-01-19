package utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.ExifInterface;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pvj.xlibrary.log.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * author: jk
 * created on: 2016/8/16 10:23
 * description: 图片处理工具类
 */
public class ImageUtils {
    private static final String TAG = ImageUtils.class.getSimpleName();

    ImageUtils() {
    }

    // 图片加载的缓存工具类，安卓自带的方法
    public static BitmapFactory.Options getHeapOpts(File file) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 数字越大读出的图片占用的heap必须越小，不然总是溢出
        if (file.length() < 20480) { // 0-20k 获取文件长度，即字节数。
            opts.inSampleSize = 1;// 这里意为缩放的大小 ，数字越多缩放得越厉害
        } else if (file.length() < 51200) { // 20-50k
            opts.inSampleSize = 2;
        } else if (file.length() < 307200) { // 50-300k
            opts.inSampleSize = 4;
        } else if (file.length() < 819200) { // 300-800k
            opts.inSampleSize = 6;
        } else if (file.length() < 1048576) { // 800-1024k
            opts.inSampleSize = 8;
        } else {
            opts.inSampleSize = 10;
        }
        return opts;
    }


    /**
     * image 转 jpg
     *
     * @param fileName
     * @param image
     */
    public static void compressToJpeg(String fileName, Image image) {
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(fileName);
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to create output file " + fileName, ioe);
        }
        Rect rect = image.getCropRect();

        YuvImage yuvImage = new YuvImage(getDataFromImage(image, COLOR_FormatNV21), ImageFormat.NV21, rect.width(), rect.height(), null);
        yuvImage.compressToJpeg(rect, 100, outStream);

        //上面生成的图片方向是不正确的，下面的代码是将图片转向
        Bitmap cbitmap = BitmapFactory.decodeFile(fileName);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap bitmap = Bitmap.createBitmap(cbitmap, 0, 0, rect.width(), rect.height(), matrix, true);
        saveImage(bitmap, fileName);
    }

    public static void saveImage(Bitmap bmp, String filePath) {
        File appDir = new File(FileUtil.getCameraFile().toString());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(filePath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isImageFormatSupported(Image image) {
        int format = image.getFormat();
        switch (format) {
            case ImageFormat.YUV_420_888:
            case ImageFormat.NV21:
            case ImageFormat.YV12:
                return true;
        }
        return false;
    }


    /**
     * 流色彩空间转换
     *
     * @param image
     * @param colorFormat
     * @return
     */
    private static final int COLOR_FormatI420 = 1;
    private static final int COLOR_FormatNV21 = 2;

    public static byte[] getDataFromImage(Image image, int colorFormat) {
        if (colorFormat != COLOR_FormatI420 && colorFormat != COLOR_FormatNV21) {
            throw new IllegalArgumentException("only support COLOR_FormatI420 " + "and COLOR_FormatNV21");
        }
        if (!isImageFormatSupported(image)) {
            throw new RuntimeException("can't convert Image to byte array, format " + image.getFormat());
        }
        Rect crop = image.getCropRect();

        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];
//        if (VERBOSE) Log.v(TAG, "get data1 from " + planes.length + " planes");
        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    if (colorFormat == COLOR_FormatI420) {
                        channelOffset = width * height;
                        outputStride = 1;
                    } else if (colorFormat == COLOR_FormatNV21) {
                        channelOffset = width * height + 1;
                        outputStride = 2;
                    }
                    break;
                case 2:
                    if (colorFormat == COLOR_FormatI420) {
                        channelOffset = (int) (width * height * 1.25);
                        outputStride = 1;
                    } else if (colorFormat == COLOR_FormatNV21) {
                        channelOffset = width * height;
                        outputStride = 2;
                    }
                    break;
            }
            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();
//            if (VERBOSE) {
//                Log.v(TAG, "pixelStride " + pixelStride);
//                Log.v(TAG, "rowStride " + rowStride);
//                Log.v(TAG, "width " + width);
//                Log.v(TAG, "height " + height);
//                Log.v(TAG, "buffer size " + buffer.remaining());
//            }
            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
//            if (VERBOSE) Log.v(TAG, "Finished reading data1 from plane " + i);
        }
        return data;
    }

    public static void setImage(String url, ImageView imageView, Context context) {
        if (url.contains("jpg")) {
            Glide.with(context).load(Uri.fromFile(new File(url))).into(imageView);
        } else if (url.contains("mp4")) {
            //视频
            Glide.with(context).load(Uri.fromFile(new File(url))).thumbnail(0.1f).into(imageView);
        }
    }

    public static boolean canPlay(String pt, Context mContext) {
        MediaPlayer mp = MediaPlayer.create(mContext, Uri.parse(pt));
        boolean can = false;
        int duration = -1;
        if (mp != null) {
            duration = mp.getDuration();
            mp.release();
            if (duration > 300) {
                can = true;
            }
        }
        Logger.d("CameraDisplayFragment2q", "pt = " + pt + "duration = " + duration + "can = " + can);
        return can;

    }

    public static String getPlayDetial(String pt, Context mContext) {
        String time = null;
        int videoHeight = 1920;
        int videoWidth = 1080;
        Logger.d(TAG, "getDuration: uri---" + Uri.parse(pt));
        MediaPlayer mp = MediaPlayer.create(mContext, Uri.parse(pt));
        if (mp != null) {
            int duration = mp.getDuration();

            videoHeight = mp.getVideoHeight();
            videoWidth = mp.getVideoWidth();
            mp.release();
            duration /= 1000;
            if (duration < 60) {
                time = "00:" + transform(duration);
            } else if (duration >= 60 && duration < 60 * 60) {
                int min = duration / 60;
                time = transform(min) + ":" + transform(duration % 60);
            } else {
                int hour = duration / 60 / 60;
                int min = 0;
                int sec = 0;
                if (duration - 3600 >= 60) {
                    min = (duration - 3600) / 60;
                    sec = (duration - 3600) % 60;
                } else if (duration - 3600 < 60) {
                    min = 00;
                    sec = duration - 3600;
                }
                time = transform(hour) + ":" + transform(min) + ":" + transform(sec);
            }
        }
        if (time == null) {
            time = "00:00";
        }
        return time + "#" + videoWidth + "x" + videoHeight;
    }

    public static String transform(int i) {
        if (i >= 10) {
            return i + "";
        } else if (i < 10) {
            return "0" + i;
        }
        return null;
    }

    public static String url2date(String url) {
//        2017_0307_15_51_13.mp4        ----> 2017-03-07 15:51
        StringBuffer date = new StringBuffer();
        try {
            date.append(url.substring(0, 4));
            date.append("-");
            date.append(url.substring(5, 7));
            date.append("-");
            date.append(url.substring(7, 9));
            date.append(" ");

            date.append(url.substring(10, 12));
            date.append(":");
            date.append(url.substring(13, 15));

        } catch (Exception e) {

        }
        return date.toString();
    }


    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }
}
