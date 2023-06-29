package com.example.statusia.Activitys;

import static android.content.ContentValues.TAG;
import static com.example.statusia.Activitys.MainActivity.statusDir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.statusia.R;
import com.example.statusia.Tools.PathMap;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OpenImageActivity extends AppCompatActivity {
    ImageView backBtn;
    ImageView whatsappBtn;
    ImageView downloadBtn;
    ImageView shareBtn;
    FrameLayout OpenImageLayout;
    LinearLayout buttonsLayout;

    PathMap pathMap;
    ArrayList<String> imageList;
    boolean isFullScreen = false;
    ImageView vImageView;
    String IMAGE_URI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_image);
        backBtn = findViewById(R.id.backBtn);
        whatsappBtn = findViewById(R.id.whatsappBtn);
        downloadBtn = findViewById(R.id.downloadBtn);
        shareBtn = findViewById(R.id.shareBtn);
        OpenImageLayout = findViewById(R.id.OpenImageLayout);
        buttonsLayout = findViewById(R.id.buttonsLayout);
        vImageView = findViewById(R.id.vImageView);

        pathMap = new PathMap(getBaseContext());

        Intent intent = getIntent();
        String frag = intent.getStringExtra("Fragment");
        IMAGE_URI = intent.getStringExtra("IMAGE_URI");
        if (frag.equals("Saved")) {
            downloadBtn.setVisibility(View.GONE);
        }


        Glide.with(this)
                        .load(IMAGE_URI)
                        .thumbnail(Glide.with(this).load(R.drawable.loading))
                        .into(vImageView);

        downloadButtonState(IMAGE_URI);

        setUI();
        setButton();
    }

//    private void initShareDownloadButtons(String IMAGE_URI) {
//        if (IMAGE_URI.endsWith("jpg") || IMAGE_URI.endsWith("jpeg") || IMAGE_URI.endsWith("png") || IMAGE_URI.endsWith("gif") || IMAGE_URI.endsWith("bmp")) {
//            TranslateAnimation animation = new TranslateAnimation(
//                    Animation.RELATIVE_TO_SELF, 10f,
//                    Animation.RELATIVE_TO_SELF, 0f,
//                    Animation.RELATIVE_TO_SELF, 0f,
//                    Animation.RELATIVE_TO_SELF, 0f
//            );
//
//            animation.setDuration(500); // Set the duration of the animation in milliseconds
//
//            // Set an AnimationListener to handle animation events
//            animation.setAnimationListener(new Animation.AnimationListener() {
//                @Override
//                public void onAnimationStart(Animation animation) {
//                    // Animation start event
//                    // Set the element's visibility to visible before the animation starts
//                    OpenImageLayout.setVisibility(View.VISIBLE);
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    // Animation end event
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//                    // Animation repeat event
//                }
//            });
//
//            OpenImageLayout.startAnimation(animation);
//
//        } else {
//            TranslateAnimation animation = new TranslateAnimation(
//                    Animation.RELATIVE_TO_SELF, 0f,
//                    Animation.RELATIVE_TO_SELF, 10f,
//                    Animation.RELATIVE_TO_SELF, 0f,
//                    Animation.RELATIVE_TO_SELF, 0f
//            );
//
//            animation.setDuration(500); // Set the duration of the animation in milliseconds
//
//            // Set an AnimationListener to handle animation events
//            animation.setAnimationListener(new Animation.AnimationListener() {
//                @Override
//                public void onAnimationStart(Animation animation) {
//                    // Animation start event
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    // Animation end event
//                    // Hide the element after animation completes
//                    OpenImageLayout.setVisibility(View.GONE);
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//                    // Animation repeat event
//                }
//            });
//
//            OpenImageLayout.startAnimation(animation);
//
//        }
//    }

    private void setButton() {
        ImageView[] btns = new ImageView[]{backBtn, whatsappBtn, downloadBtn, shareBtn};
        for (ImageView btn : btns) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btn == backBtn) {
                        finish();
                    } else if (btn == whatsappBtn) {
                        sendWhatsapp(Uri.parse(IMAGE_URI));
                    } else if (btn == downloadBtn) {
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                            saveImageBelowR(Uri.parse(IMAGE_URI));
                        } else {
                            saveImage(Uri.parse(IMAGE_URI));
                        }
                    } else if (btn == shareBtn) {
                        shareFile(Uri.parse(IMAGE_URI));
                    } else {

                    }
                }
            });
        }
    }

    public void downloadButtonState(String IMAGE_URI) {
        String savedFileName = pathMap.getMap().get(new File(Uri.parse(IMAGE_URI).getPath()).getName());
        Log.d(TAG, "downloadButtonState: "+savedFileName);
//        String savedFileName = pathMap.getMap().get(new File(Uri.parse(imageList.get(pos)).getPath()).getName());
        if (savedFiles().contains(savedFileName)) {
            downloadBtn.setImageResource(R.drawable.ic_baseline_check_24);
            downloadBtn.setClickable(false);
        } else {
            downloadBtn.setImageResource(R.drawable.ic_baseline_download_24);
            downloadBtn.setClickable(true);
        }
    }

    private void setUI() {
        getWindow().setStatusBarColor(getColor(R.color.black));
        getWindow().setNavigationBarColor(getColor(R.color.black));
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                );
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        super.onBackPressed();
//        openImg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_out));
    }

    private void saveImageBelowR(Uri uri) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        String fileName = "IMG_" + currentDateTime + ".jpg";

        try {
            File destFile = new File(statusDir.getPath(), fileName);
            File sourceFile = new File(uri.getPath());

            FileUtils.copyFile(sourceFile, destFile);
            if (destFile.setLastModified(System.currentTimeMillis()))
                Log.d(TAG, "saveImage: Successfully");
            ;

            pathMap.savePathMap(new File(uri.getPath()).getName(), fileName);

            Log.e("Download URI", Uri.fromFile(destFile) + "");
            Log.e("Input URI", uri + "");

            downloadBtn.setImageResource(R.drawable.ic_baseline_check_24);

            Snackbar.make(findViewById(android.R.id.content),
                    "Downloaded successfully at: Download/.Statusia/Images",
                    Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Image download error", e.toString());
            Snackbar.make(findViewById(android.R.id.content),
                    "Download failed !",
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    private void saveImage(Uri uri) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        String fileName = "IMG_" + currentDateTime + ".jpg";

        try {
            File destFile = new File(statusDir.getPath(), fileName);
            InputStream in = getContentResolver().openInputStream(uri);
            org.apache.commons.io.FileUtils.copyInputStreamToFile(in, destFile);
            if (destFile.setLastModified(System.currentTimeMillis()))
                Log.d(TAG, "saveImage: Successfully");
            ;
            in.close();

            pathMap.savePathMap(new File(uri.getPath()).getName(), fileName);

            Log.e("Download URI", Uri.fromFile(destFile) + "");
            Log.e("Input URI", uri + "");

            downloadBtn.setImageResource(R.drawable.ic_baseline_check_24);

            Snackbar.make(findViewById(android.R.id.content),
                    "Downloaded successfully at: Download/.Statusia/Images",
                    Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Image download error", e.toString());
            Snackbar.make(findViewById(android.R.id.content), "Wrong Access", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void sendWhatsapp(Uri uri) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("image/*");
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);
    }

    public void shareFile(Uri uri) {
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share image"));
    }

    public ArrayList<String> savedFiles() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                "/Statusia";

        File file = new File(path);
        File[] savedFiles = file.listFiles();
        ArrayList<String> fileNames = new ArrayList<>();
        if (savedFiles != null) {
            for (File f : savedFiles) {
                fileNames.add(f.getName());
            }
        }
        return fileNames;
    }
}