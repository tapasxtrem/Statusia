package com.example.statusia.Activitys;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.statusia.Adapters.WhatsappSpinnerItemAdapter;
import com.example.statusia.Fragments.Images;
import com.example.statusia.Fragments.Saved;
import com.example.statusia.Fragments.Videos;
import com.example.statusia.R;
import com.example.statusia.classes.FileCopyObserver;
import com.example.statusia.classes.WhatsappSpinnerItem;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView ImagesBtnTxt;
    TextView VideosBtnTxt;
    TextView SavedBtnTxt;
    View ImagesBtnView;
    View VideosBtnView;
    View SavedBtnView;
    MaterialCardView ImagesBtn;
    MaterialCardView VideosBtn;
    MaterialCardView SavedBtn;

    FrameLayout fragmentContainer;

    Fragment ImageFragment;
    Fragment VideosFragment;
    Fragment SavedFragment;

    ArrayList<WhatsappSpinnerItem> wSpinnerList;
    WhatsappSpinnerItemAdapter WSAdapter;
    public static String WhatsAppPackage = "com.whatsapp";

    public static File imageDir;
    public static File videoDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImagesBtn = findViewById(R.id.ImagesBtn);
        VideosBtn = findViewById(R.id.VideosBtn);
        SavedBtn = findViewById(R.id.SavedBtn);

        ImagesBtnView = findViewById(R.id.ImagesBtnView);
        VideosBtnView = findViewById(R.id.VideosBtnView);
        SavedBtnView = findViewById(R.id.SavedBtnView);

        ImagesBtnTxt = findViewById(R.id.ImagesBtnTxt);
        VideosBtnTxt = findViewById(R.id.VideosBtnTxt);
        SavedBtnTxt = findViewById(R.id.SavedBtnTxt);

        fragmentContainer = findViewById(R.id.fragmentContainer);

        ImageFragment = new Images();
        VideosFragment = new Videos();
        SavedFragment = new Saved();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, ImageFragment).commit();

        MaterialCardView[] cardViews = new MaterialCardView[]{ImagesBtn, VideosBtn, SavedBtn};

        for (MaterialCardView cardView : cardViews) {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeBtn(cardView);
                    callFragment(cardView);
                }
            });
        }

        createDir();
    }

    public void initFileCopyObserver() {
        SharedPreferences preferences = getSharedPreferences("FILE_URI", MODE_PRIVATE);
        String UriPath;
//        if(sPos == 0)
        UriPath = preferences.getString("WHATSAPP_URI", "NULL");
//        else UriPath = preferences.getString("WHATSAPP_BUSINESS_URI","NULL");

        Uri URI = Uri.parse(UriPath);
        Log.d(TAG, "initFileCopyObserver: "+URI);
        Context context = getApplicationContext(); // Replace with your activity or service context
        String destinationFolderPath = imageDir.getPath();

        FileCopyObserver fileObserver = new FileCopyObserver(context, URI, destinationFolderPath);
        context.getContentResolver().registerContentObserver(URI, true, fileObserver);

    }

    public void createDir() {
        boolean[] dirCreateRes = new boolean[2];
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // For android 10+
            imageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    ".Statusia/Images");
            videoDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    ".Statusia/Videos");
        } else {
            imageDir = Environment.getExternalStoragePublicDirectory(".Statusia/Images");
            videoDir = Environment.getExternalStoragePublicDirectory(".Statusia/Videos");
        }
        if (!imageDir.exists()) dirCreateRes[0] = imageDir.mkdirs();
        if (!videoDir.exists()) dirCreateRes[1] = videoDir.mkdirs();

        if (dirCreateRes[0] && dirCreateRes[1])
            Log.d("Directory : ", "Created Successfully");
    }

    private void callFragment(MaterialCardView cardView) {
        if (cardView == ImagesBtn) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, ImageFragment)
                    .commit();
        } else if (cardView == VideosBtn) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, VideosFragment).commit();
        } else if (cardView == SavedBtn) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, SavedFragment).commit();
        } else {

        }
    }

    public void changeBtn(MaterialCardView cardView) {
        if (cardView == ImagesBtn) {
            ImagesBtnView.setBackgroundColor(getColor(R.color.black));
            VideosBtnView.setBackgroundColor(getColor(R.color.white));
            SavedBtnView.setBackgroundColor(getColor(R.color.white));

            ImagesBtnTxt.setTextColor(getColor(R.color.black));
            VideosBtnTxt.setTextColor(getColor(R.color.nBlack));
            SavedBtnTxt.setTextColor(getColor(R.color.nBlack));
        } else if (cardView == VideosBtn) {
            ImagesBtnView.setBackgroundColor(getColor(R.color.white));
            VideosBtnView.setBackgroundColor(getColor(R.color.black));
            SavedBtnView.setBackgroundColor(getColor(R.color.white));

            ImagesBtnTxt.setTextColor(getColor(R.color.nBlack));
            VideosBtnTxt.setTextColor(getColor(R.color.black));
            SavedBtnTxt.setTextColor(getColor(R.color.nBlack));
        } else if (cardView == SavedBtn) {
            ImagesBtnView.setBackgroundColor(getColor(R.color.white));
            VideosBtnView.setBackgroundColor(getColor(R.color.white));
            SavedBtnView.setBackgroundColor(getColor(R.color.black));

            ImagesBtnTxt.setTextColor(getColor(R.color.nBlack));
            VideosBtnTxt.setTextColor(getColor(R.color.nBlack));
            SavedBtnTxt.setTextColor(getColor(R.color.black));
        } else {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        createDir();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        createDir();
    }
}