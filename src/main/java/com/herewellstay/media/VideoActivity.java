package com.herewellstay.media;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
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
import com.herewellstay.events.Event;
import com.herewellstay.events.OnEventListener;

import androidx.appcompat.app.AppCompatActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class VideoActivity extends AppCompatActivity {

    private static final String TAG = VideoActivity.class.getSimpleName();
    LoopingMediaSource loopingSource = null;
    private String path;
    private Playlist playlist;
    private ExoPlayer player;
    private PlayerView playerView;
    private MediaSource mediaSource;
    private String userAgent = "Mozilla/5.0 (Android 4.4; Mobile; rv:41.0) Gecko/41.0 Firefox/41.0";
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private PlaylistView playlistView;
    private View playlistShowButton;
    private View playlistDrawer;
    private boolean isPlaylistShown=true;
    private View playlistHideButton;
    private View nextButton;
    private View previousButton;
    private View exoController;
    private ViewGroup playlistBanner;
    private View exoBack;
    private TextView exoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        askForFullScreen();


        playerView = findViewById(R.id.player_view);
        exoController=playerView.findViewById(R.id.exoPlayerController);
        nextButton = findViewById(R.id.exo_next);
        previousButton = findViewById(R.id.exo_prev);
        playlistView =findViewById(R.id.playlistView);
        playlistView.setPlaylist(getPlaylist());
        playlistDrawer=findViewById(R.id.playlistDrawer);
        playlistShowButton =findViewById(R.id.playlistShowButton);
        playlistHideButton =findViewById(R.id.playlistHideButton);
        playlistBanner =findViewById(R.id.playlistBanner);
        exoBack =findViewById(R.id.exoPlayerBack);
        exoName =findViewById(R.id.exoPlayerVideoName);
        exoName.setText(getPlaylist().current().getName());
        View playlistBannerView = getPlaylistBannerView();
        if(playlistBannerView!=null){
            playlistBanner.addView(playlistBannerView);
        }
        playlistView.sync();
        exoBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                VideoActivity.this.finish();
                return true;
            }
        });
        playerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePlaylistDrawer();
            }
        });
        exoController.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!playerView.isControllerVisible()){
                    playerView.showController();
                }
                return false;
            }
        });
        exoController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!playerView.isControllerVisible()){
                    playerView.showController();
                }
            }
        });

        getPlaylist().addOnEventListener(new OnEventListener() {
            @Override
            public void onEvent(Event e) {
                if (e.getName().equals(Playlist.EVENT_POSITION_CHANGE)){

                    PlaylistItem item=getPlaylist().get((int)e.get("position"));
                    initializePlayer(item.getPath());
                    exoName.setText(item.getName());

                }
            }
        });
        if(getPlaylist().isEmpty()){
            previousButton.setClickable(false);
            nextButton.setClickable(false);
            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
        }
        playlistShowButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                          showPlaylistDrawer();
                          return true;

            }


        });
        playlistHideButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hidePlaylistDrawer();

                return true;
            }


        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPlaylist().previous();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPlaylist().next();
            }
        });


    }

    private void togglePlaylistDrawer() {
        if(isPlaylistShown){
            hidePlaylistDrawer();
        }
        else {
            showPlaylistDrawer();
        }
    }

    private void showPlaylistDrawer() {
        playlistDrawer.setVisibility(View.VISIBLE);
        playlistDrawer.setAlpha(0.0f);
        playlistDrawer.animate()
                .translationX(0)
                .setDuration(1000)
                .alpha(1.0f)
                .setListener(null);
        isPlaylistShown=true;
    }

    private void hidePlaylistDrawer() {
        playlistDrawer.animate()
                .setDuration(1000)
                .translationX(-playlistDrawer.getWidth())
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        playlistDrawer.setVisibility(View.GONE);
                    }
                });
        isPlaylistShown=false;
    }

    protected String getVideoPath() {
        return path;
    }
    protected Playlist getPlaylist() {
        return playlist;
    }
    protected View getPlaylistBannerView(){
        return null;
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    private void initializePlayer(String path) {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this,
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());
            playerView.setPlayer(player);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);
        }
        mediaSource = buildMediaSource(path);
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

    private MediaSource buildMediaSource(String path) {
        MediaSource videoSource;
        Uri uri=Uri.parse(path);
//        Uri uri=Uri.parse("http://185.132.134.32:1100/pak/geonews/playlist.m3u8?wmsAuthSign=c2VydmVyX3RpbWU9NC8xMC8yMDE5IDk6MzY6MjMgUE0maGFzaF92YWx1ZT1GKzAvUExQUVBrazAyb0orZnR3cFd3PT0mdmFsaWRtaW51dGVzPTI=");
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
//
//        //uri=Uri.parse("http://www.elahmad.com/tv/m3u8/alkass.m3u8");
//        System.out.println("Extension: "+extension);
//        videoSource = new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
//                       .createMediaSource(uri);
        if(path.startsWith("http")){
            if (extension.contains("m3u")) {
                videoSource = new HlsMediaSource(uri, new DefaultHttpDataSourceFactory(userAgent), 1, null, null);

            } else{
                videoSource = new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                        .createMediaSource(uri);
            }
        }
        else{
            if (extension.contains("m3u")) {
                videoSource = new HlsMediaSource(uri, new DefaultDataSourceFactory(this, userAgent), 1, null, null);

            } else{

                videoSource = new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(this, Util.getUserAgent(this, "SongShakes")))
                        .createMediaSource(uri);
            }
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
        initializePlayer(getVideoPath());
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializePlayer(getVideoPath());
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
    private void askForFullScreen()
    {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

    }
    private void moveOutOfFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
