package com.example.statusia.Activitys;

import static android.content.ContentValues.TAG;
import static com.example.statusia.Activitys.MainActivity.statusDir;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.statusia.R;
import com.example.statusia.Tools.PathMap;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OpenVideoActivity extends AppCompatActivity {
    Uri videoUri;
    PlayerView exo_player_view;
    boolean fullscreen = false;
    ImageView fullScreenBtn;
    ImageView vidBackBtn;
    ImageView vidWhatsappBtn;
    ImageView vidDownloadBtn;
    ImageView vidShareBtn;
    ImageView []btns;
    ExoPlayer exoPlayer;
    MediaItem mediaItem;
    int currentWindow = 0;
    private long playbackPosition = 0;
    boolean playWhenReady = true;

    final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    int newUiOptions;

    ProgressBar videoProgress;

    PathMap pathMap;

    ImageButton prevBtn;
    ImageButton nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_video);
        exo_player_view = findViewById(R.id.exo_player_view);

        fullScreenBtn = exo_player_view.findViewById(R.id.full_screen_btn);
        vidBackBtn = exo_player_view.findViewById(R.id.vidBackBtn);
        vidWhatsappBtn = exo_player_view.findViewById(R.id.vidWhatsappBtn);
        vidShareBtn = exo_player_view.findViewById(R.id.vidShareBtn);
        vidDownloadBtn = exo_player_view.findViewById(R.id.vidDownloadBtn);
        videoProgress = exo_player_view.findViewById(R.id.videoProgress);
        prevBtn = exo_player_view.findViewById(R.id.exo_prev_btn);
        nextBtn = exo_player_view.findViewById(R.id.exo_next_btn);

        pathMap = new PathMap(this);

        videoProgress.setVisibility(View.VISIBLE);

        btns = new ImageView[]{fullScreenBtn,vidBackBtn,vidDownloadBtn,vidShareBtn,vidWhatsappBtn};

        Intent intent = getIntent();
        videoUri = Uri.parse(intent.getStringExtra("VIDEO_URI"));
        String frag = intent.getStringExtra("Fragment");
//        nextPrevBtnView();
        if(frag.equals("Saved")){
            vidDownloadBtn.setVisibility(View.GONE);
        }

        setButton();

        newUiOptions = getWindow().getDecorView().getSystemUiVisibility();
        newUiOptions &= ~View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        newUiOptions &= ~View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        newUiOptions &= ~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        newUiOptions &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions &= ~View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

//        prevBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(videoPos>0){
//                    videoPos--;
//                    videoUri = Uri.parse(videoList.get(videoPos));
//                    setDownloadButton();
//                    exoPlayer.stop();
//                    exoPlayer.release();
//                    initializePlayer();
//                    nextPrevBtnView();
//                }
//            }
//        });

//        nextBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(videoPos<videoList.size()-1) {
//                    videoPos++;
//                    videoUri = Uri.parse(videoList.get(videoPos));
//                    setDownloadButton();
//                    exoPlayer.stop();
//                    exoPlayer.release();
//                    initializePlayer();
//                    nextPrevBtnView();
//                }
//            }
//        });
    }

//    public void nextPrevBtnView(){
//        if(videoPos==0){
//            prevBtn.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.disable_white_btn)
//                    , android.graphics.PorterDuff.Mode.MULTIPLY);
//        }
//        else{
//            prevBtn.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.white)
//                    , android.graphics.PorterDuff.Mode.MULTIPLY);
//        }
//
//        if(videoPos==videoList.size()-1){
//            nextBtn.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.disable_white_btn)
//                    , android.graphics.PorterDuff.Mode.MULTIPLY);
//        }
//        else{
//            nextBtn.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.white)
//                    , android.graphics.PorterDuff.Mode.MULTIPLY);
//        }
//    }

    @Override
    public void onStart() {
        super.onStart();
        initializePlayer();
        Log.e("checkLog start","playbackPosition :"+playbackPosition);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUI();
        if (exoPlayer == null) {
            initializePlayer();
        }
        Log.e("checkLog resume","playbackPosition :"+playbackPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
        Log.e("checkLog pause","playbackPosition :"+playbackPosition);
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
        Log.e("checkLog stop","playbackPosition :"+playbackPosition);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(fullscreen) setPortraitMode();
        else{
            releasePlayer();
            super.onBackPressed();
        }
    }

    private void initializePlayer() {
        exoPlayer = new ExoPlayer.Builder(this).build();
        exo_player_view.setPlayer(exoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        exoPlayer.setPlayWhenReady(playWhenReady);
        exoPlayer.seekTo(playbackPosition);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();
        videoProgress.setVisibility(View.INVISIBLE);
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            playbackPosition = exoPlayer.getCurrentPosition();
            playWhenReady = exoPlayer.getPlayWhenReady();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void setButton() {
        for (ImageView btn : btns){
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(btn==fullScreenBtn){
                        if (fullscreen) {
                            setPortraitMode();
                        } else {
                            setLandScapeMode();
                        }
                    }
                    else if(btn==vidBackBtn){
                        releasePlayer();
                        finish();
                    }
                    else if(btn==vidWhatsappBtn){
                        sendWhatsapp(videoUri);
                    }
                    else if(btn==vidDownloadBtn){
                        if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.Q) {
                            saveVideoBelowR(videoUri);
                        }else {
                            saveVideo(videoUri);
                        }
                    }
                    else if(btn==vidShareBtn){
                        shareFile(videoUri);
                    }
                    else{

                    }
                }
            });
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void setPortraitMode(){
        fullScreenBtn.setImageResource(R.drawable.ic_fullscreen_on);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        fullscreen = false;
    }

    public void setLandScapeMode(){
        fullScreenBtn.setImageResource(R.drawable.ic_fullscreen_off);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        fullscreen = true;
    }

    private void saveVideoBelowR(Uri uri) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        String fileName = "VID_" + currentDateTime + ".mp4";

        try {
            File destFile = new File(statusDir.getPath(),fileName);
            File sourceFile = new File(uri.getPath());

            FileUtils.copyFile(sourceFile,destFile);
            if(destFile.setLastModified(System.currentTimeMillis())) Log.d(TAG, "saveVideo: Successfully");;

            pathMap.savePathMap(new File(uri.getPath()).getName(),fileName);

            Log.e("Download URI",Uri.fromFile(destFile)+"");
            Log.e("Input URI",uri+"");

            vidDownloadBtn.setImageResource(R.drawable.ic_baseline_check_24);

            Snackbar.make(findViewById(android.R.id.content),
                    "Downloaded successfully at: Download/Statusia",
                    Snackbar.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Log.e("Image download error",e.toString());
            Snackbar.make(findViewById(android.R.id.content),
                    "Download failed !",
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    private void saveVideo(Uri uri) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        String fileName = "VID_" + currentDateTime + ".mp4";

        try {
            File destFile = new File(statusDir.getPath(),fileName);
            InputStream in = getContentResolver().openInputStream(uri);
            org.apache.commons.io.FileUtils.copyInputStreamToFile(in,destFile);
            if(destFile.setLastModified(System.currentTimeMillis())) Log.d(TAG, "saveVideo: Successfully");;
            in.close();

            pathMap.savePathMap(new File(uri.getPath()).getName(),fileName);

            Log.e("Download URI",Uri.fromFile(destFile)+"");
            Log.e("Input URI",uri+"");

            vidDownloadBtn.setImageResource(R.drawable.ic_baseline_check_24);
            Snackbar.make(findViewById(android.R.id.content),
                    "Downloaded successfully at: Download/Statusia",
                    Snackbar.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Log.e("Image download error",e.toString());
            Snackbar.make(findViewById(android.R.id.content),
                    "Download failed !",
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    private void sendWhatsapp(Uri uri){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("video/*");
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);
    }

    public void shareFile(Uri uri){
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("video/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share video"));
    }

    public void setDownloadButton(){
        String savedFileName = pathMap.getMap().get(new File(videoUri.getPath()).getName());
        Log.d(TAG, "setDownloadButton: savedFileName: "+savedFileName);
        Log.d(TAG, "setDownloadButton: savedFiles(): "+savedFiles());

        if(savedFiles().contains(savedFileName)) {
            vidDownloadBtn.setImageResource(R.drawable.ic_baseline_check_24);
        }else{
            vidDownloadBtn.setImageResource(R.drawable.ic_baseline_download_24);
        }
    }

    public void setUI(){
        setDownloadButton();
        getWindow().setStatusBarColor(getColor(R.color.black));
        getWindow().setNavigationBarColor(getColor(R.color.black));
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_VISIBLE
                );
    }

    private void showSystemUI(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(null);
        setUI();
    }

    private void hideSystemUi() {

        getWindow().setNavigationBarColor(getColor(R.color.black));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        getWindow().getDecorView().setSystemUiVisibility(flags);

        // Code below is to handle presses of Volume up or Volume down.
        // Without this, after pressing volume buttons, the navigation bar will
        // show up and won't hide
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(flags);
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            fullScreenBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_off));
            fullscreen = true;
            hideSystemUi();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            fullScreenBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_on));
            fullscreen = false;
            showSystemUI();
        }
    }

    public ArrayList<String> savedFiles(){
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+
                "/Statusia";

        File file = new File(path);
        File []savedFiles = file.listFiles();
        ArrayList<String> fileNames = new ArrayList<>();
        if(savedFiles!=null) {
            for (File f : savedFiles) {
                fileNames.add(f.getName());
            }
        }
        return fileNames;
    }
}