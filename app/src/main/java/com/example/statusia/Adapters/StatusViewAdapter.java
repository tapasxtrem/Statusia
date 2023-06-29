package com.example.statusia.Adapters;

import static com.example.statusia.Fragments.Statuses.activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.example.statusia.Activitys.OpenImageActivity;
import com.example.statusia.Activitys.OpenVideoActivity;
import com.example.statusia.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class StatusViewAdapter extends RecyclerView.Adapter<StatusViewAdapter.ViewHolder> {
    Context iContext;
    ArrayList<String> iArrayList;
    boolean isSelectModeEnabled = false;
    ArrayList<String> selectedList;
    ImageButton checkAllBtn;
    MaterialButton multiSaveBtn;
    TextView imageFragHeading;

    public StatusViewAdapter(Context context, ArrayList<String> iArrayList,
                             ImageButton checkAllBtn, MaterialButton multiSaveBtn,
                             TextView imageFragHeading,
                             ArrayList<String> selectedList, boolean isSelectModeEnabled){
        this.iContext = context;
        this.iArrayList = iArrayList;
        this.selectedList = selectedList;
        this.checkAllBtn = checkAllBtn;
        this.imageFragHeading = imageFragHeading;
        this.multiSaveBtn = multiSaveBtn;
        this.isSelectModeEnabled = isSelectModeEnabled;
    }

    @NonNull
    @Override
    public StatusViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(iContext);
        View view = inflater.inflate(R.layout.status_view_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewAdapter.ViewHolder holder, int position) {
        Log.e("path",""+iArrayList.get(position));

        if(isSelectModeEnabled && selectedList.contains(iArrayList.get(holder.getBindingAdapterPosition()))){
            holder.selectImage.setVisibility(View.VISIBLE);
        }

        Glide.with(iContext)
                .load(iArrayList.get(position))
                .centerCrop()
                .thumbnail(Glide.with(iContext).load(R.drawable.loading))
                .into(holder.img);

        int pos = holder.getBindingAdapterPosition();
        if(iArrayList.get(pos).endsWith(".mp4") || iArrayList.get(pos).endsWith(".mkv") || iArrayList.get(pos).equals(".3gp") || iArrayList.get(pos).equals(".mov")){
            holder.VideoPlayBtn.setVisibility(View.VISIBLE);
        }
        else holder.VideoPlayBtn.setVisibility(View.GONE);

        holder.img.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if(!isSelectModeEnabled) {
                    if (iArrayList.get(pos).endsWith("jpg") || iArrayList.get(pos).endsWith("jpeg") || iArrayList.get(pos).endsWith("png") || iArrayList.get(pos).endsWith("gif") || iArrayList.get(pos).endsWith("bmp")) {

                        Intent intent = new Intent(iContext, OpenImageActivity.class);
                        intent.putExtra("Fragment", "Images");
                        intent.putExtra("IMAGE_URI", iArrayList.get(holder.getBindingAdapterPosition()));

                        ActivityOptions options =
                                ActivityOptions.makeSceneTransitionAnimation(activity, holder.img, "image_or_video");

                        iContext.startActivity(intent, options.toBundle());
                    }else{
                        Intent intent = new Intent(iContext, OpenVideoActivity.class);
                        intent.putExtra("VIDEO_URI", iArrayList.get(holder.getBindingAdapterPosition()));
                        intent.putExtra("Fragment", "Videos");
                        ActivityOptions options =
                                ActivityOptions.makeSceneTransitionAnimation(activity, holder.img, "image_or_video");

                        iContext.startActivity(intent, options.toBundle());
                    }
                }else{
                    if(selectedList.contains(iArrayList.get(holder.getBindingAdapterPosition()))){
                        selectedList.remove(iArrayList.get(holder.getBindingAdapterPosition()));
                        holder.selectImage.setVisibility(View.GONE);
                    }else {
                        selectedList.add(iArrayList.get(holder.getBindingAdapterPosition()));
                        holder.selectImage.setVisibility(View.VISIBLE);
                    }

                    imageFragHeading.setText(selectedList.size()+" Selected");

                    if(selectedList.size()==0){
                        isSelectModeEnabled = false;
                        setButtonViews(View.GONE);
                        imageFragHeading.setText("Images");
                    }
                }
            }
        });

        holder.img.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onLongClick(View v) {
                isSelectModeEnabled = true;
                setButtonViews(View.VISIBLE);
                if(selectedList.contains(iArrayList.get(holder.getBindingAdapterPosition()))){
                    selectedList.remove(iArrayList.get(holder.getBindingAdapterPosition()));
                    holder.selectImage.setVisibility(View.GONE);
                }else {
                    selectedList.add(iArrayList.get(holder.getBindingAdapterPosition()));
                    holder.selectImage.setVisibility(View.VISIBLE);
                }
                imageFragHeading.setText(selectedList.size()+" Selected");

                if(selectedList.size()==0){
                    isSelectModeEnabled = false;
                    setButtonViews(View.GONE);
                    imageFragHeading.setText("Images");
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
        return iArrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        FrameLayout selectImage;
        ImageView VideoPlayBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            selectImage = itemView.findViewById(R.id.selectImage);
            VideoPlayBtn = itemView.findViewById(R.id.VideoPlayBtn);
        }
    }
}
