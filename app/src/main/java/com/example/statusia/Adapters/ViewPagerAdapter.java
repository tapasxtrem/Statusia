package com.example.statusia.Adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.statusia.R;
import com.example.statusia.Tools.PathMap;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {
    Context vContext;
    ArrayList<String> vList;
    ExoPlayer exoPlayer;
    PathMap pathMap;
    private LayoutInflater inflater;

    public ViewPagerAdapter(Context vContext, ArrayList<String> vList) {
        this.vContext = vContext;
        this.vList = vList;
        inflater = LayoutInflater.from(vContext);
    }

    @Override
    public int getCount() {
        return vList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView;
        if (vList.get(position).endsWith("jpg") ||
                vList.get(position).endsWith("jpeg") ||
                vList.get(position).endsWith("png") ||
                vList.get(position).endsWith("gif") ||
                vList.get(position).endsWith("bmp")) {
            Log.d(TAG, "instantiateItem: IMAGE");

            itemView = inflater.inflate(R.layout.image_viewpager_adapter, container, false);
            ImageView imageView = itemView.findViewById(R.id.vImageView);
            Glide.with(vContext)
                    .load(vList.get(position))
                    .thumbnail(Glide.with(vContext).load(R.drawable.loading))
                    .into(imageView);
        }
        else{
            Log.d(TAG, "instantiateItem: VIDEO");

            itemView = inflater.inflate(R.layout.video_viewpager_adapter, container, false);
            exoPlayer = new ExoPlayer.Builder(vContext).build();
            PlayerView playerView = itemView.findViewById(R.id.exo_player_view);
            playerView.setPlayer(exoPlayer);
            MediaItem mediaItem = MediaItem.fromUri(vList.get(position));
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
        }
        container.addView(itemView);
        return itemView;
//        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }

    //    @NonNull
//    @Override
//    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        View itemView = inflater.inflate(R.layout.image_page, container, false);
//        ImageView imageView = itemView.findViewById(R.id.imageView);
//        imageView.setImageResource(imageList.get(position));
//        container.addView(itemView);
//        return itemView;
//    }
//
//    @Override
//    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        container.removeView((View) object);
//    }

}
