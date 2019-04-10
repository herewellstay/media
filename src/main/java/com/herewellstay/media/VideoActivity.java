package com.herewellstay.media;


import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import androidx.appcompat.app.AppCompatActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class VideoActivity extends AppCompatActivity {

    private static final String TAG = VideoActivity.class.getSimpleName();
    LoopingMediaSource loopingSource = null;
    private String path;
    private ExoPlayer player;
    private PlayerView playerView;
    private MediaSource mediaSource;
    private String userAgent = "Mozilla/5.0 (Android 4.4; Mobile; rv:41.0) Gecko/41.0 Firefox/41.0";
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);
        getSupportActionBar().hide();


        playerView = findViewById(R.id.player_view);


    }

    protected String getVideoPath() {
        return path;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    private void initializePlayer() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this,
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());
            playerView.setPlayer(player);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);
        }
        mediaSource = buildMediaSource(Uri.fromFile(new java.io.File(getVideoPath())));
        player.prepare(mediaSource, true, false);
        player.addListener(new ExoPlayer.EventListener() {


            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.v(TAG, "Listener-onTracksChanged... ");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.v(TAG, "Listener-onPlayerStateChanged..." + playbackState + "|||isDrawingCacheEnabled():" + playerView.isDrawingCacheEnabled());
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.v(TAG, "Listener-onPlayerError...");
                player.stop();
                player.prepare(loopingSource);
                player.setPlayWhenReady(true);
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }

    private MediaSource buildMediaSource(Uri uri) {
        MediaSource videoSource;
        //uri=Uri.parse("http://www.elahmad.com/tv/m3u8/alkass.m3u8");
        if (uri.getLastPathSegment().contains("m3u8") || uri.getLastPathSegment().contains("m3u")) {
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, userAgent);
            videoSource = new HlsMediaSource(uri, dataSourceFactory, 1, null, null);

        } else if (uri.getLastPathSegment().contains("ts")) {
            videoSource = new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);
        } else {
            videoSource = new ExtractorMediaSource
                    .Factory(new DefaultDataSourceFactory(this, Util.getUserAgent(this, "SongShakes")))
                    .createMediaSource(uri);
        }
        loopingSource = new LoopingMediaSource(videoSource);
        return videoSource;
        //uri=Uri.parse("http://www.elahmad.com/tv/m3u8/alkass.m3u8");
        //uri=Uri.parse("http://iptv.turk1299.com:8080/get.php?username=qFFASI7lE5&password=rkJ1mP1Jvs&type=m3u");
        //uri=Uri.parse("http://37.59.41.145:5050/live/admin/dreemedia/105.ts");
//
    }

    public void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }
}
