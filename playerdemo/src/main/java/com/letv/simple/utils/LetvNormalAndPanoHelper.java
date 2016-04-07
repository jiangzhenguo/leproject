package com.letv.simple.utils;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.letv.controller.PlayProxy;
import com.letv.pano.OnPanoViewTapUpListener;
import com.letv.pano.PanoVideoControllerView;
import com.letv.pano.PanoVideoView;
import com.letv.skin.v4.V4PlaySkin;
import com.letv.universal.iplay.ISplayer;
import com.letv.universal.widget.ISurfaceListener;
import com.letv.universal.widget.ReSurfaceView;

public class LetvNormalAndPanoHelper extends LetvBaseHelper {
   private boolean isLocalPano;
    @Override
    public void init(Context mContext, Bundle mBundle, V4PlaySkin skin) {
        super.init(mContext, mBundle, skin);
        isLocalPano = mBundle.getBoolean(LetvParamsUtils.IS_LOCAL_PANO);
        createOnePlayer(null);
    }

    private void initNormalVideoView() {
        if (videoView == null || !(videoView instanceof ReSurfaceView)) {
            videoView = new ReSurfaceView(mContext);
            videoView.getHolder().addCallback(surfaceCallback);
            videoView.setVideoContainer(null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            skin.addVideoView(videoView.getMysef(), params);
        }
    }

    private void initPanoVideoView() {
        if (videoView == null || !(videoView.getMysef() instanceof PanoVideoView)) {
            final PanoVideoControllerView panoVideoView = new PanoVideoControllerView(mContext);
            panoVideoView.registerSurfacelistener(new ISurfaceListener() {
                @Override
                public void setSurface(Surface surface) {
                    player.setDisplay(surface);
                }
            });

            // 设置手势操作层的touch事件
            // 如果手势不起作用有可能是您的layout把panovideoview的手势事件覆盖 这里也可以设置您的layout中最上层view
            panoVideoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return panoVideoView.onPanoTouch(v, event);
                }
            });

            // 设置video的单击事件 通知上层唤醒播控控件等
            panoVideoView.setTapUpListener(new OnPanoViewTapUpListener() {
                @Override
                public void onSingleTapUp(MotionEvent e) {
                    skin.performClick();
                }
            });
            panoVideoView.init();
            videoView = panoVideoView;
            videoView.getHolder().addCallback(surfaceCallback);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            skin.addVideoView(videoView.getMysef(), params);
            skin.initPanoView();
        }
    }

    @Override
    public void handlePlayState(int state, Bundle bundle) {
        super.handlePlayState(state, bundle);
        if (state == ISplayer.PLAYER_EVENT_PREPARE_VIDEO_VIEW) {
            boolean pano = bundle != null ? bundle.getBoolean("pano", false) : false;
            if (isLocalPano || pano) {
                initPanoVideoView();
            } else {
                initNormalVideoView();
            }
            playContext.setVideoContentView(videoView.getMysef());
        }
    }

}
