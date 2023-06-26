package com.example.statusia.Adapters;

import static android.content.ContentValues.TAG;

import static com.example.statusia.Activitys.MainActivity.imageDir;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.statusia.R;
import com.example.statusia.Tools.PathMap;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.jsibbold.zoomage.ZoomageView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ViewPagerAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context vContext;
    ArrayList<String> vList;
    ExoPlayer exoPlayer;
    PathMap pathMap;

    public ViewPagerAdapter2(Context vContext, ArrayList<String> vList) {
        this.vContext = vContext;
        this.vList = vList;
        pathMap = new PathMap(vContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==0) {
            View view = LayoutInflater.from(vContext).inflate(R.layout.image_viewpager_adapter, parent, false);
            return new ImageViewHolder(view);
        }else{
            View view = LayoutInflater.from(vContext).inflate(R.layout.video_viewpager_adapter, parent, false);
            return new VideoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Log.d(TAG, "onBindViewHolder: pos: "+position+"vList.get(position)");

        if (vList.get(position).toLowerCase().endsWith("jpg") ||
                vList.get(position).toLowerCase().endsWith("jpeg") ||
                vList.get(position).toLowerCase().endsWith("png") ||
                vList.get(position).toLowerCase().endsWith("gif") ||
                vList.get(position).toLowerCase().endsWith("bmp")) {
            ImageViewHolder imageHolder = (ImageViewHolder) holder;
            Glide.with(vContext)
                    .load(vList.get(position))
                    .thumbnail(Glide.with(vContext).load(R.drawable.loading))
                    .into(imageHolder.vImageView);
        }
        else {
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
            initializePlayer(videoViewHolder, Uri.parse(vList.get(position)));
            downloadButtonState(position, videoViewHolder);
            videoViewHolder.exo_player_view.findViewById(R.id.whatsappBtn2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendWhatsapp(Uri.parse(vList.get(position)));
                }
            });

            videoViewHolder.exo_player_view.findViewById(R.id.downloadBtn2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String savedFileName = pathMap.getMap().get(new File(Uri.parse(vList.get(position)).getPath()).getName());

                    if (!savedFiles().contains(savedFileName)) {
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                            saveImageBelowR(Uri.parse(vList.get(position)),videoViewHolder);
                        } else {
                            saveImage(Uri.parse(vList.get(position)),videoViewHolder);
                        }
                    }

                }
            });

            videoViewHolder.exo_player_view.findViewById(R.id.shareBtn2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareFile(Uri.parse(vList.get(position)));
                }
            });
        }

        // Set up other configurations and listeners as needed
    }

    public void downloadButtonState(int pos, VideoViewHolder holder) {

        String savedFileName = pathMap.getMap().get(new File(Uri.parse(vList.get(pos)).getPath()).getName());
        ImageView img = holder.exo_player_view.findViewById(R.id.downloadBtn2);
        if (savedFiles().contains(savedFileName)) {
            img.setImageResource(R.drawable.ic_baseline_check_24);
            img.setClickable(false);
        } else {
            img.setImageResource(R.drawable.ic_baseline_download_24);
            img.setClickable(true);
        }
    }

    private void saveImageBelowR(Uri uri,VideoViewHolder holder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        String fileName = "IMG_" + currentDateTime + ".jpg";

        try {
            File destFile = new File(imageDir.getPath(), fileName);
            File sourceFile = new File(uri.getPath());

            FileUtils.copyFile(sourceFile, destFile);
            if (destFile.setLastModified(System.currentTimeMillis()))
                Log.d(TAG, "saveImage: Successfully");
            ;

            pathMap.savePathMap(new File(uri.getPath()).getName(), fileName);

            Log.e("Download URI", Uri.fromFile(destFile) + "");
            Log.e("Input URI", uri + "");

            ImageView dImg = holder.exo_player_view.findViewById(R.id.downloadBtn2);
            dImg.setImageResource(R.drawable.ic_baseline_check_24);

            Toast.makeText(vContext, "Downloaded successfully at: Download/.Statusia/Images", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("Image download error", e.toString());
            Toast.makeText(vContext, "Download failed !", Toast.LENGTH_SHORT).show();

        }
    }

    private void saveImage(Uri uri, VideoViewHolder holder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        String fileName = "IMG_" + currentDateTime + ".jpg";

        try {
            File destFile = new File(imageDir.getPath(), fileName);
            InputStream in = vContext.getContentResolver().openInputStream(uri);
            org.apache.commons.io.FileUtils.copyInputStreamToFile(in, destFile);
            if (destFile.setLastModified(System.currentTimeMillis()))
                Log.d(TAG, "saveImage: Successfully");
            ;
            in.close();

            pathMap.savePathMap(new File(uri.getPath()).getName(), fileName);

            Log.e("Download URI", Uri.fromFile(destFile) + "");
            Log.e("Input URI", uri + "");

            ImageView dImg = holder.exo_player_view.findViewById(R.id.downloadBtn2);
            dImg.setImageResource(R.drawable.ic_baseline_check_24);
            dImg.setImageResource(R.drawable.ic_baseline_check_24);

            Toast.makeText(vContext, "Downloaded successfully at: Download/.Statusia/Images", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Image download error", e.toString());
            Toast.makeText(vContext, "Wrong Access", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendWhatsapp(Uri uri) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("image/*");
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setPackage("com.whatsapp");
        vContext.startActivity(sendIntent);
    }

    public void shareFile(Uri uri) {
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        vContext.startActivity(Intent.createChooser(share, "Share image"));
    }

    public ArrayList<String> savedFiles() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                "/.Statusia/Images";

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

    private void initializePlayer(VideoViewHolder holder, Uri videoUri) {
        exoPlayer = new ExoPlayer.Builder(vContext).build();
        holder.exo_player_view.setPlayer(exoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
//        exoPlayer.setPlayWhenReady(playWhenReady);
//        exoPlayer.seekTo(playbackPosition);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
//        exoPlayer.play();
        holder.exo_player_view.findViewById(R.id.videoProgress).setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return vList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (vList.get(position).toLowerCase().endsWith("jpg") || vList.get(position).toLowerCase().endsWith("jpeg") || vList.get(position).toLowerCase().endsWith("png") || vList.get(position).toLowerCase().endsWith("gif") || vList.get(position).toLowerCase().endsWith("bmp")) {
            return 0; // Image view type
        } else {
            return 1; // Video view type
        }
    }

//    public static class ViewHolder extends RecyclerView.ViewHolder{
//        ZoomageView vImageView;
//        PlayerView exo_player_view;
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            vImageView = itemView.findViewById(R.id.vImageView);
//            exo_player_view = itemView.findViewById(R.id.exo_player_view);
//        }
//    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ZoomageView vImageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            vImageView = itemView.findViewById(R.id.vImageView);
        }
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        PlayerView exo_player_view;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            exo_player_view = itemView.findViewById(R.id.exo_player_view);
        }
    }
}
