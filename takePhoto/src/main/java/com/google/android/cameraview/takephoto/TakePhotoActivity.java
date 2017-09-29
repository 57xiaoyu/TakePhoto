package com.google.android.cameraview.takephoto;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;
import com.google.android.cameraview.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;


/**
 * Created by Fstar on 2017/8/25.
 */

public class TakePhotoActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "TakePhotoActivity";
    public static final String SELECT_MODE_EXTRA = "select_mode_extra";
    public static final String RESULT_PATHS_EXTRA = "output_paths";
    public static final String RESULT_URIS_EXTRA = "output_uris";

    public static final int SELECT_MODE_SINGLE = 1;
    public static final int SELECT_MODE_MULTI = 2;

    public int mode;

    private CameraView mCameraView;
    private ImageView iv_preview;
    private Button bt_cancel;
    private Button bt_confirm;
    private Button bt_take_photo;


    private int mCurrentFlash;

    private static final int[] FLASH_OPTIONS = {
            CameraView.FLASH_AUTO,
            CameraView.FLASH_OFF,
            CameraView.FLASH_ON,
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
    };

    private RelativeLayout rl_bottom;
    private RelativeLayout.LayoutParams paramsRoot;
    private RelativeLayout.LayoutParams paramsCancel;
    private RelativeLayout.LayoutParams paramsTake;
    private RelativeLayout.LayoutParams paramsConfirm;
    private FrameLayout fl_switch_flash;
    private FrameLayout fl_switch_camera;
    private FrameLayout fl_done;
    private ImageView iv_switch_flash;
    private ImageView iv_switch_camera;
    private ArrayList<String> select_image_paths;
    private ArrayList<String> select_image_uris;
    private String current_outputpath;
    private LinearLayout ll_select_preview;
    private LinearLayout ll_select_content;
    private FrameLayout fl_selected;
    private ImageView iv_selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        setContentView(R.layout.activity_lib_take_photo);
        mode = getIntent().getIntExtra(SELECT_MODE_EXTRA, SELECT_MODE_SINGLE);
        select_image_paths = new ArrayList<String>();
        select_image_uris = new ArrayList<String>();
        initView();
    }

    private void initView() {
        bt_take_photo = (Button) findViewById(R.id.bt_take_photo);
        mCameraView = (CameraView) findViewById(R.id.camera);
        ll_select_preview = (LinearLayout) findViewById(R.id.ll_select_preview);
        ll_select_content = (LinearLayout) findViewById(R.id.ll_select_content);
        iv_preview = (ImageView) findViewById(R.id.iv_preview);

        bt_cancel = (Button) findViewById(R.id.bt_cancel);
        bt_take_photo = (Button) findViewById(R.id.bt_take_photo);
        bt_confirm = (Button) findViewById(R.id.bt_confirm);
        rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);

        fl_switch_flash = (FrameLayout) findViewById(R.id.fl_switch_flash);
        fl_switch_camera = (FrameLayout) findViewById(R.id.fl_switch_camera);
        fl_selected = (FrameLayout) findViewById(R.id.fl_selected);
        iv_selected = (ImageView) findViewById(R.id.iv_selected);

        fl_done = (FrameLayout) findViewById(R.id.fl_done);
        iv_switch_flash = (ImageView) findViewById(R.id.iv_switch_flash);
        iv_switch_camera = (ImageView) findViewById(R.id.iv_switch_camera);

        paramsRoot = (RelativeLayout.LayoutParams) rl_bottom.getLayoutParams();
        paramsCancel = (RelativeLayout.LayoutParams) bt_cancel.getLayoutParams();
        paramsTake = (RelativeLayout.LayoutParams) bt_take_photo.getLayoutParams();
        paramsConfirm = (RelativeLayout.LayoutParams) bt_confirm.getLayoutParams();

        fl_switch_flash.setOnClickListener(this);
        fl_switch_camera.setOnClickListener(this);
        fl_selected.setOnClickListener(this);
        fl_done.setOnClickListener(this);
        bt_take_photo.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);
        bt_confirm.setOnClickListener(this);
        bt_cancel.setVisibility(View.GONE);
        bt_confirm.setVisibility(View.GONE);
        bt_take_photo.setEnabled(true);
        iv_preview.setVisibility(View.GONE);
        mCameraView.addCallback(mPictureTakenCallback);
        //初始化
        mCurrentFlash = 0;
        iv_switch_flash.setImageResource(FLASH_ICONS[mCurrentFlash]);
        mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
    }

    CameraView.Callback mPictureTakenCallback = new CameraView.Callback() {
        @Override
        public void onCameraOpened(CameraView cameraView) {
            super.onCameraOpened(cameraView);
            Log.d(TAG, "onCameraOpened");
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            super.onCameraClosed(cameraView);
            Log.d(TAG, "onCameraClosed");
        }

        @Override
        public void onPictureTaken(CameraView cameraView, byte[] data) {
            super.onPictureTaken(cameraView, data);
            Log.d(TAG, "onPictureTaken");

            current_outputpath = TakePhotoUtils.getCameraOutputFile().getAbsolutePath();
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);

            TakePhotoUtils.saveBitmap(current_outputpath, bm, new TakePhotoUtils.SaveBitmapCallBack() {
                @Override
                public void done() {
                    TakePhotoUtils.displayLocalImage(current_outputpath, iv_preview);
//                ImageLoader.getInstance().displayImage("file://" + current_outputpath, iv_preview, mOptions);
                    iv_preview.setVisibility(View.VISIBLE);
                    bt_cancel.setVisibility(View.VISIBLE);
                    bt_confirm.setVisibility(View.VISIBLE);
                    bt_take_photo.setEnabled(false);
                }

                @Override
                public void error() {
                    Toast.makeText(TakePhotoActivity.this, "文件保存失败，请检查存储空间是否充足", Toast.LENGTH_SHORT).show();
                    iv_preview.setVisibility(View.GONE);
                    bt_cancel.setVisibility(View.GONE);
                    bt_confirm.setVisibility(View.GONE);
                    bt_take_photo.setEnabled(true);
                    // 可旋转
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            });
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged orientation:" + newConfig.orientation);
        //竖直方向：orientation=1 SCREEN_ORIENTATION_PORTRAIT
        //横向右：orientation=2
        //横向左：orientation=2

        //竖直方向
        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            paramsRoot.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                paramsRoot.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            } else {
                paramsRoot.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            }
            paramsRoot.height = (int) getResources().getDimension(R.dimen.takephoto_bottom_height);
            paramsRoot.width = ViewGroup.LayoutParams.MATCH_PARENT;

            paramsCancel.addRule(RelativeLayout.CENTER_VERTICAL);
            paramsCancel.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                paramsCancel.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                paramsCancel.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            }else {
                paramsCancel.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
                paramsCancel.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            }


            paramsConfirm.addRule(RelativeLayout.CENTER_VERTICAL);
            paramsConfirm.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                paramsConfirm.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                paramsConfirm.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            }else {
                paramsConfirm.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
                paramsConfirm.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            }

        } else {
            paramsRoot.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                paramsRoot.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            }else {
                paramsRoot.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            }
            paramsRoot.height = ViewGroup.LayoutParams.MATCH_PARENT;
            paramsRoot.width = (int) getResources().getDimension(R.dimen.takephoto_bottom_height);

            paramsCancel.addRule(RelativeLayout.CENTER_HORIZONTAL);
            paramsCancel.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                paramsCancel.removeRule(RelativeLayout.CENTER_VERTICAL);
                paramsCancel.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }else {
                paramsCancel.addRule(RelativeLayout.CENTER_VERTICAL, 0);
                paramsCancel.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            }


            paramsConfirm.addRule(RelativeLayout.CENTER_HORIZONTAL);
            paramsConfirm.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                paramsConfirm.removeRule(RelativeLayout.CENTER_VERTICAL);
                paramsConfirm.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }else {
                paramsConfirm.addRule(RelativeLayout.CENTER_VERTICAL, 0);
                paramsConfirm.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (mCameraView != null) {
            mCameraView.start();
        }
    }

    @Override
    protected void onPause() {
        if (mCameraView != null) {
            mCameraView.stop();
        }
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (mCameraView != null) {
            mCameraView.removeCallback(mPictureTakenCallback);
        }
        super.onDestroy();
    }


    private void notifyImage(File file) {
        // 把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_take_photo) {
            if (mCameraView != null) {
                mCameraView.takePicture();
            }
            dissmissSelectPreview();
        } else if (v.getId() == R.id.bt_cancel) {
            dissmissSelectPreview();
            File file = new File(current_outputpath);
            if (file.exists()) {
                file.delete();
            }
            iv_preview.setVisibility(View.GONE);
            bt_cancel.setVisibility(View.GONE);
            bt_confirm.setVisibility(View.GONE);
            bt_take_photo.setEnabled(true);
            // 可旋转
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else if (v.getId() == R.id.bt_confirm) {
            dissmissSelectPreview();
            File file = new File(current_outputpath);
            if (file.exists()) {
                select_image_paths.add(current_outputpath);
                select_image_uris.add("file://" + current_outputpath);
                notifyImage(file);
                if (mode == SELECT_MODE_SINGLE) {
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_PATHS_EXTRA, select_image_paths);
                    intent.putExtra(RESULT_URIS_EXTRA, select_image_uris);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    if (select_image_paths != null && select_image_paths.size() > 0) {
                        fl_done.setVisibility(View.VISIBLE);
                    } else {
                        fl_done.setVisibility(View.GONE);
                    }
                    iv_preview.setVisibility(View.GONE);
                    bt_cancel.setVisibility(View.GONE);
                    bt_confirm.setVisibility(View.GONE);
                    bt_take_photo.setEnabled(true);
                    // 可旋转
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

                    resetSelectImagePreview();
                }

            } else {
                Toast.makeText(this, "文件保存失败请重新拍摄", Toast.LENGTH_SHORT).show();
                //bt_cancel
                iv_preview.setVisibility(View.GONE);
                bt_cancel.setVisibility(View.GONE);
                bt_confirm.setVisibility(View.GONE);
                bt_take_photo.setEnabled(true);
                // 可旋转
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
        } else if (v.getId() == R.id.fl_switch_flash) {
            dissmissSelectPreview();
            if (mCameraView != null) {
                mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
                iv_switch_flash.setImageResource(FLASH_ICONS[mCurrentFlash]);
                mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
            }
        } else if (v.getId() == R.id.fl_switch_camera) {
            dissmissSelectPreview();
            if (mCameraView != null) {
                int facing = mCameraView.getFacing();
                mCameraView.setFacing(facing == CameraView.FACING_FRONT ?
                        CameraView.FACING_BACK : CameraView.FACING_FRONT);
            }
        } else if (v.getId() == R.id.fl_done) {

            if (select_image_paths != null && select_image_paths.size() > 0) {
                Intent intent = new Intent();
                intent.putExtra(RESULT_PATHS_EXTRA, select_image_paths);
                intent.putExtra(RESULT_URIS_EXTRA, select_image_uris);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        } else if (v.getId() == R.id.fl_selected) {
            if (select_image_paths != null && select_image_paths.size() > 0) {
                if (ll_select_preview.getVisibility() == View.VISIBLE) {
                    ll_select_preview.setVisibility(View.GONE);
                } else {
                    ll_select_preview.setVisibility(View.VISIBLE);
                }
            } else {
                ll_select_preview.setVisibility(View.GONE);
            }
        }
    }

    private void dissmissSelectPreview() {
        ll_select_preview.setVisibility(View.GONE);
    }

    private void resetSelectImagePreview() {
        if (select_image_paths != null && select_image_paths.size() > 0) {
            fl_selected.setVisibility(View.VISIBLE);
            String path = select_image_paths.get(select_image_paths.size() - 1);
            TakePhotoUtils.displayLocalImage(path, iv_selected);
            ll_select_content.removeAllViews();
            for (int i = 0; i < select_image_paths.size(); i++) {
                String p = select_image_paths.get(i);
                RelativeLayout item = new RelativeLayout(this);
                ImageView imageView = new ImageView(this);
                ImageView delete = new ImageView(this);
                delete.setTag(p);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String path = (String) v.getTag();
                        File file = new File(path);
                        if (file.exists()) {
                            file.delete();
                        }
                        select_image_paths.remove(path);
                        select_image_uris.remove("file://" + path);
                        resetSelectImagePreview();
                    }
                });
                delete.setImageResource(R.drawable.icon_delete);
                int deletesize = TakePhotoUtils.dp2px(20);
                RelativeLayout.LayoutParams deleteparams = new RelativeLayout.LayoutParams(deletesize, deletesize);
                deleteparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                deleteparams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


                item.addView(imageView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                item.addView(delete, deleteparams);


                float select_preview_height = getResources().getDimension(R.dimen.select_preview_height);
                //减去边距
                int size = (int) (select_preview_height - TakePhotoUtils.dp2px(10));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                if (i != 0) {
                    params.leftMargin = TakePhotoUtils.dp2px(5);
                }
                TakePhotoUtils.displayLocalImage(p, imageView);
                ll_select_content.addView(item, params);
            }
        } else {
            fl_selected.setVisibility(View.GONE);
            ll_select_content.removeAllViews();
            dissmissSelectPreview();
        }
    }

    @Override
    public void onBackPressed() {
        if (ll_select_preview.getVisibility() == View.VISIBLE) {
            dissmissSelectPreview();
        } else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }
}
