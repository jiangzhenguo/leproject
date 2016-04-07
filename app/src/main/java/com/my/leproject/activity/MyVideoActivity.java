package com.my.leproject.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.skin.v4.V4PlaySkin;
import com.my.leproject.R;
import com.my.leproject.unit.LetvNormalAndPanoHelper;
import com.my.leproject.unit.LetvParamsUtils;
import com.my.leproject.unit.StaticContent;

/**
 * Created by jhon on 2016/3/31.
 */
public class MyVideoActivity extends AppCompatActivity {
    private V4PlaySkin skin;
    //如果不使用全景 请将LetvNormalAndPanoHelper 换成 LetvNormalVideoHelper
    private LetvNormalAndPanoHelper playHelper;
    private Bundle bundle;
    private TextView console;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        loadData();
        skin = (V4PlaySkin) findViewById(R.id.videobody);
        playHelper = new LetvNormalAndPanoHelper();
        playHelper.init(this.getApplicationContext(), bundle, skin);
    }

    private void loadData(){
        Intent intent = getIntent();
        if (intent != null) {
            bundle = intent.getBundleExtra("data");
            if (bundle == null) {
                Toast.makeText(this, "no data", Toast.LENGTH_LONG).show();
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playHelper != null) {
            playHelper.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (playHelper != null) {
            playHelper.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playHelper != null) {
            playHelper.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (playHelper != null) {
            playHelper.onConfigurationChanged(newConfig);
        }
    }


    public static void launch(Context context, String data){
        Intent in = new Intent(context,MyVideoActivity.class);
        Bundle bundle = LetvParamsUtils.setVodParams(StaticContent.uuid,data,"",StaticContent.key,"");
        in.putExtra("data",bundle);
        context.startActivity(in);
    }

    public static void launch2(Context context,String mydata){
        Intent in = new Intent(context,MyVideoActivity.class);
        in.putExtra("data", LetvParamsUtils.setActionLiveParams("A20160407000007k",false));
        context.startActivity(in);
    }
}
