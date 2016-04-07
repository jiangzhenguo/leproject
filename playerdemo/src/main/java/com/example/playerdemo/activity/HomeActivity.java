package com.example.playerdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.lecloud.lecloudsdkdemo.R;
import com.letv.controller.PlayProxy;
import com.letv.proxy.LeCloudProxy;
import com.letv.simple.utils.LetvParamsUtils;
import com.letv.universal.iplay.EventPlayProxy;

public class HomeActivity extends Activity implements OnClickListener {

    String liveId = "201504213000012";
    /**
     * pano
     */
    String uuid = "4d707a5d3f";
    String vuid = "8f549b8c7e";
    // 广告
    // String uuid = "487c884e76";
    // String vuid = "e5a4fb751e";

    String actvieId = "A201603280000074";

    private String streamId = "152915-live-xxx123";


    private EditText etUUID;
    private EditText etVUID;
    private EditText etLiveId;
    private EditText etActionId;
    private RadioButton rb1;
    private RadioButton rb2;

    private RadioButton rb3;
    private RadioButton rb4;
    private CheckBox ckHasSkin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initClickEvent();
        findViewById(R.id.vod).requestFocus();
    }

    private void initClickEvent() {
        findViewById(R.id.live).setOnClickListener(this);
        findViewById(R.id.vod).setOnClickListener(this);
        findViewById(R.id.downoadList_btn).setOnClickListener(this);
        findViewById(R.id.activity_live_btn).setOnClickListener(this);
        findViewById(R.id.list_view_btn).setOnClickListener(this);
        findViewById(R.id.more_view_btn).setOnClickListener(this);
    }

    private void initView() {
        etLiveId = (EditText) findViewById(R.id.et_liveID);
        etUUID = (EditText) findViewById(R.id.et_uuid);
        etVUID = (EditText) findViewById(R.id.et_vuid);
        etUUID.setText(uuid);
        etVUID.setText(vuid);
        etLiveId.setText(liveId);
        rb1 = (RadioButton) findViewById(R.id.rb_1);
        rb2 = (RadioButton) findViewById(R.id.rb_2);
        rb1.setChecked(true);

        rb3 = (RadioButton) findViewById(R.id.rb_3);
        rb4 = (RadioButton) findViewById(R.id.rb_4);
        rb3.setChecked(true);
        ckHasSkin = (CheckBox) findViewById(R.id.ck_has_skin);
        etActionId = (EditText) findViewById(R.id.et_actionID);
        etActionId.setText(actvieId);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.live:
                startLeCloudLive();
                break;
            case R.id.vod:
                startLecloudVod();
                break;
            case R.id.downoadList_btn:
                Intent intent = new Intent(HomeActivity.this, DownloadActivity.class);
                startActivity(intent);
                break;
            case R.id.activity_live_btn:
                startLeCloudActionLive();
                break;
            case R.id.list_view_btn:
                startListViewVod();
                break;
            case R.id.more_view_btn:
                startMorePlayerVod();
                break;
            default:
                break;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////

    /**
     * 启动双播放器
     */
    private void startMorePlayerVod() {
        Intent intent = new Intent(HomeActivity.this, MorePlayerActivity.class);
        startActivity(intent);
    }

    /**
     * 在listView 中添加视频
     */
    private void startListViewVod() {
        Intent intent = new Intent(HomeActivity.this, ListPlayerViewActivity.class);
        startActivity(intent);
    }

    /**
     * 乐视云直播
     */
    private void startLeCloudLive() {
        Intent intent;
        if (ckHasSkin.isChecked()) {
            intent = new Intent(HomeActivity.this, PlayActivity.class);
        } else {
            intent = new Intent(HomeActivity.this, PlayNoSkinActivity.class);
        }
        String liveId = null;
        String streamId = null;
        if (rb3.isChecked()) {
            liveId = etLiveId.getText().toString().trim();
        } else {
            streamId = etLiveId.getText().toString().trim();
        }
        intent.putExtra(PlayActivity.DATA, LetvParamsUtils.setLiveParams(streamId, liveId, rb2.isChecked(), false));
        startActivity(intent);
    }

    /**
     * 乐视云点播
     */
    private void startLecloudVod() {
        Intent intent = getStartActivity();
        Bundle bundle = LetvParamsUtils.setVodParams(etUUID.getText().toString().trim(), etVUID.getText().toString().trim(), "", "151398", "");
        intent.putExtra(PlayActivity.DATA, bundle);
        startActivity(intent);
    }


    private Intent getStartActivity() {
        return ckHasSkin.isChecked() ? new Intent(HomeActivity.this, PlayActivity.class) : new Intent(HomeActivity.this, PlayNoSkinActivity.class);
    }

    /**
     * 乐视云活动直播
     */
    private void startLeCloudActionLive() {
        Intent intent = getStartActivity();
        intent.putExtra(PlayActivity.DATA, LetvParamsUtils.setActionLiveParams(etActionId.getText().toString().trim(), rb2.isChecked()));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exit();
    }

    private void exit() {
        //应用程序彻底退出销毁cde service
        LeCloudProxy.destory();
        System.exit(0);
    }
}
