package com.example.statusia.Adapters;

import static com.example.statusia.Fragments.Images.activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.statusia.Activitys.OpenVideoActivity;
import com.example.statusia.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class VideosViewAdapter extends RecyclerView.Adapter<VideosViewAdapter.ViewHolder> {
    Context vContext;
    ArrayList<String> vArrayList;
    ArrayList<String> selectedList;
    ImageButton checkAllBtn;
    MaterialButton multiSaveBtn;
    boolean isSelectModeEnabled;
    TextView videoFragHeading;

    public VideosViewAdapter(Context context, ArrayList<String> iArrayList,
                             ImageButton checkAllBtn, MaterialButton multiSaveBtn,
                             TextView videoFragHeading,
                             ArrayList<String> selectedList,boolean isSelectModeEnabled){
        this.vContext = context;
        this.vArrayList = iArrayList;
        this.selectedList = selectedList;
        this.checkAllBtn = checkAllBtn;
        this.multiSaveBtn = multiSaveBtn;
        this.videoFragHeading = videoFragHeading;
        this.isSelectModeEnabled = isSelectModeEnabled;
    }

    @NonNull
    @Override
    public VideosViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(vContext);
        View view = inflater.inflate(R.layout.videos_view_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosViewAdapter.ViewHolder holder, int position) {

        if(isSelectModeEnabled && selectedList.contains(vArrayList.get(holder.getBindingAdapterPosition()))){
            holder.selectVideo.setVisibility(View.VISIBLE);
        }

        Glide.with(vContext)
                .load(vArrayList.get(position))
                .centerCrop()
                .thumbnail(Glide.with(vContext).load(R.drawable.loading))
                .into(holder.vid);

        holder.vid.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if(!isSelectModeEnabled) {
                    Intent intent = new Intent(vContext, OpenVideoActivity.class);
                    intent.putExtra("URI", vArrayList.get(holder.getBindingAdapterPosition()));
                    intent.putStringArrayListExtra("LIST", vArrayList);
                    intent.putExtra("POS", holder.getBindingAdapterPosition());
                    intent.putExtra("Fragment", "Videos");
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(activity, holder.vid, "robot");

                    vContext.startActivity(intent, options.toBundle());
                }
                else {
                    if (selectedList.contains(vArrayList.get(holder.getBindingAdapterPosition()))) {
                        selectedList.remove(vArrayList.get(holder.getBindingAdapterPosition()));
                        holder.selectVideo.setVisibility(View.GONE);
                    } else {
                        selectedList.add(vArrayList.get(holder.getBindingAdapterPosition()));
                        holder.selectVideo.setVisibility(View.VISIBLE);
                    }
                    videoFragHeading.setText(selectedList.size()+" Selected");

                    if (selectedList.size() == 0) {
                        isSelectModeEnabled = false;
                        setButtonViews(View.GONE);
                        videoFragHeading.setText("Videos");
                    }
                }
            }
        });

        holder.vid.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onLongClick(View v) {
                isSelectModeEnabled = true;
                setButtonViews(View.VISIBLE);
                if(selectedList.contains(vArrayList.get(holder.getBindingAdapterPosition()))){
                    selectedList.remove(vArrayList.get(holder.getBindingAdapterPosition()));
                    holder.selectVideo.setVisibility(View.GONE);
                }else {
                    selectedList.add(vArrayList.get(holder.getBindingAdapterPosition()));
                    holder.selectVideo.setVisibility(View.VISIBLE);
                }
                videoFragHeading.setText(selectedList.size()+" Selected");

                if(selectedList.size()==0){
                    isSelectModeEnabled = false;
                    setButtonViews(View.GONE);
                    videoFragHeading.setText("Videos");
                }
                return true;
            }
        });
    }

    public void setButtonViews(int visibility){
        checkAllBtn.setVisibility(visibility);
        multiSaveBtn.setVisibility(visibility);
    }

    @Override
    public int getItemCount() {
        return vArrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView vid;
        FrameLayout selectVideo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            vid = itemView.findViewById(R.id.vid);
            selectVideo = itemView.findViewById(R.id.selectVideo);
        }
    }
}

