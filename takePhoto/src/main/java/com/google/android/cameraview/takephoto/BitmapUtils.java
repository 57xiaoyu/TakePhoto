package com.google.android.cameraview.takephoto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

/**
 * Created by Fstar on 2017/9/6.
 */

public class BitmapUtils {


    /**
     * obtain the image's width and height
     *
     * @param imagePath the path of image
     */
    private static int[] getImageSize(String imagePath) {
        int[] res = new int[2];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        BitmapFactory.decodeFile(imagePath, options);

        res[0] = options.outWidth;
        res[1] = options.outHeight;

        return res;
    }

    public static void getSimpleBitmap(final String imagePath, final SimpleBitmapCallback callback) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                final Bitmap bitmap = getSimpleBitmap(imagePath);
                TakePhotoUtils.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.done(bitmap);
                        }
                    }
                });
            }
        }.start();
    }

    public static Bitmap getSimpleBitmap(String imagePath) {
        //the file size of image   期望大小
        double size;
        String filePath = imagePath;
        File file = new File(filePath);
        int width = getImageSize(filePath)[0];
        int height = getImageSize(filePath)[1];
        int thumbW = width % 2 == 1 ? width + 1 : width;
        int thumbH = height % 2 == 1 ? height + 1 : height;

        width = thumbW > thumbH ? thumbH : thumbW;
        height = thumbW > thumbH ? thumbW : thumbH;

        double scale = ((double) width / height);

        if (scale <= 1 && scale > 0.5625) {
            if (height < 1664) {
                if (file.length() / 1024 < 150) {
                    thumbW = width;
                    thumbH = height;
                }
                size = (width * height) / Math.pow(1664, 2) * 150;
                size = size < 60 ? 60 : size;
            } else if (height >= 1664 && height < 4990) {
                thumbW = width / 2;
                thumbH = height / 2;
                size = (thumbW * thumbH) / Math.pow(2495, 2) * 300;
                size = size < 60 ? 60 : size;
            } else if (height >= 4990 && height < 10240) {
                thumbW = width / 4;
                thumbH = height / 4;
                size = (thumbW * thumbH) / Math.pow(2560, 2) * 300;
                size = size < 100 ? 100 : size;
            } else {
                int multiple = height / 1280 == 0 ? 1 : height / 1280;
                thumbW = width / multiple;
                thumbH = height / multiple;
                size = (thumbW * thumbH) / Math.pow(2560, 2) * 300;
                size = size < 100 ? 100 : size;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            if (height < 1280 && file.length() / 1024 < 200) {
                thumbW = width;
                thumbH = height;
            }
            int multiple = height / 1280 == 0 ? 1 : height / 1280;
            thumbW = width / multiple;
            thumbH = height / multiple;
            size = (thumbW * thumbH) / (1440.0 * 2560.0) * 400;
            size = size < 100 ? 100 : size;
        } else {
            int multiple = (int) Math.ceil(height / (1280.0 / scale));
            thumbW = width / multiple;
            thumbH = height / multiple;
            size = ((thumbW * thumbH) / (1280.0 * (1280 / scale))) * 500;
            size = size < 100 ? 100 : size;
        }

        return compress(imagePath, thumbW, thumbH);
    }

    private static Bitmap compress(String imagePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        int outH = options.outHeight;
        int outW = options.outWidth;
        int inSampleSize = 1;

        if (outH > height || outW > width) {
            int halfH = outH / 2;
            int halfW = outW / 2;

            while ((halfH / inSampleSize) > height && (halfW / inSampleSize) > width) {
                inSampleSize *= 2;
            }
        }

        options.inSampleSize = inSampleSize;

        options.inJustDecodeBounds = false;

        int heightRatio = (int) Math.ceil(options.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(options.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio;
            } else {
                options.inSampleSize = widthRatio;
            }
        }
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(imagePath, options);
    }


    public interface SimpleBitmapCallback {
        void done(Bitmap bitmap);
    }
}
