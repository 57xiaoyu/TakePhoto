package com.cch.takephoto;

import android.app.Application;

import com.google.android.cameraview.takephoto.TakePhotoUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Fstar on 2017/9/6.
 */

public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        TakePhotoUtils.init(getApplicationContext(),"");
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
}
