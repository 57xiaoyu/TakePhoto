package com.google.android.cameraview.takephoto;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Fstar on 2017/9/5.
 */

public class TakePhotoUtils {

    private static final float UNIT_DP = Resources.getSystem().getDisplayMetrics().density;

    public static Context context;
    public static String fileSavePath;
    private static Handler handler =new Handler();

    public static void init(Context _context) {
        init(_context,null);
    }
    public static void init(Context _context, String _fileSavePath) {
        context = _context;
        fileSavePath = _fileSavePath;
    }

    public static void displayLocalImage(String path,final ImageView imageView) {
        if (path.startsWith("file://")) {
            path = path.substring("file://".length());
        }
        imageView.setImageBitmap(null);
        BitmapUtils.getSimpleBitmap(path, new BitmapUtils.SimpleBitmapCallback() {
            @Override
            public void done(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });

    }

    public static File getCameraOutputFile() {
        String fileName = String.format(Locale.CHINA, "IMG_%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS%1$tL.jpg",
                Calendar.getInstance());
        return new File(getCameraOutputFoder(), fileName);
    }

    public static File getCameraOutputFoder() {
        if(!TextUtils.isEmpty(fileSavePath)){
            File file = new File(fileSavePath);
            if(!file.exists()){
                file.mkdirs();
            }
            return file;
        }
        File dir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            dir = new File(dir, "Camera");
        } else {
            dir = context.getCacheDir();
        }
        return dir;
    }

    /**
     * dp转px
     **/
    public static int dp2px(int dp) {
        return (int) (UNIT_DP * dp);
    }

    public static void saveBitmap(final String current_outputpath,final Bitmap bm, final SaveBitmapCallBack callback) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    File file = new File(current_outputpath);
                    if (file.exists()) {
                        file.delete();
                    }
                    File dir = new File(file.getParent());
                    dir.mkdirs();
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    bos.flush();
                    bos.close();
                    getHandler().post(new Runnable() {
                       @Override
                       public void run() {
                           if (callback != null) {
                               callback.done();
                           }
                       }
                   });
                } catch (Exception e) {
                    e.printStackTrace();
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.error();
                            }
                        }
                    });
                }

            }
        }.start();

    }

    public static Handler getHandler() {
        if(handler==null){
            handler=new Handler();
        }
        return handler;
    }

    public interface SaveBitmapCallBack {
        void done();
        void error();
    }
}
