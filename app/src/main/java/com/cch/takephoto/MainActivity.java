package com.cch.takephoto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.cch.takephoto.adapter.CommonAdapter;
import com.cch.takephoto.adapter.ViewHolder;
import com.google.android.cameraview.takephoto.TakePhotoActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private GridView gv_gridview;
    private ArrayList<String> datas;
    private CommonAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findViewById(R.id.bt_single).setOnClickListener(this);
        findViewById(R.id.bt_multi).setOnClickListener(this);

        gv_gridview = (GridView) findViewById(R.id.gv_gridview);
        datas = new ArrayList<>();
        adapter = new CommonAdapter<String>(this, datas, R.layout.item_image) {
            @Override
            public void convert(ViewHolder helper, String item) {
               ImageView iv_image= helper.getView(R.id.iv_image);
                ViewGroup.LayoutParams params = iv_image.getLayoutParams();
                if(params==null){
                    params =new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    iv_image.setLayoutParams(params);
                }
                params.height=gv_gridview.getColumnWidth();
                params.width=gv_gridview.getColumnWidth();

                ImageLoader.getInstance().displayImage(item,iv_image);
            }
        };

        gv_gridview.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_single:
                takePhoto(true);
                break;
            case R.id.bt_multi:
                takePhoto(false);
                break;
        }
    }

    private void takePhoto(boolean single) {
        Intent intent = new Intent(this, TakePhotoActivity.class);
        if (single) {
            intent.putExtra(TakePhotoActivity.SELECT_MODE_EXTRA, TakePhotoActivity.SELECT_MODE_SINGLE);
        } else {
            intent.putExtra(TakePhotoActivity.SELECT_MODE_EXTRA, TakePhotoActivity.SELECT_MODE_MULTI);
        }
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                ArrayList<String> list = data.getStringArrayListExtra(TakePhotoActivity.RESULT_URIS_EXTRA);
                if (list != null) {
                    Log.d("RESULT_URIS_EXTRA", list.toArray().toString());
                    datas.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
