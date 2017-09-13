package com.cch.takephoto;

import android.app.Application;
import android.os.Environment;

import com.google.android.cameraview.takephoto.TakePhotoUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

/**
 * Created by Fstar on 2017/9/6.
 */

public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        TakePhotoUtils.init(getApplicationContext(),createCachePath());
        initImageLoader();
    }

    private void initImageLoader() {
        //Universal Image Loader初始化
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)// 保留图片文件头信息
                .showImageForEmptyUri(R.drawable.default_img)
                .showImageOnFail(R.drawable.default_img);
        ImageLoaderConfiguration conf = new ImageLoaderConfiguration
                .Builder(this)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheSize(100 * 1024 * 1024)
                .memoryCacheSize(50 * 1024 * 1024)
                .defaultDisplayImageOptions(builder.build())
//                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(conf);
    }

    private String createCachePath() {
        String filePath = null;
        String foder = "image";
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) { // SD卡根目录
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator+foder+File.separator;
        } else {  // 系统下载缓存根目录
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator+foder+File.separator;
        }
        File f1 = new File(filePath);
        if (!f1.exists()) {
            f1.mkdirs();
        }
        return filePath;
    }
}
