package com.example.statusia.Adapters;

import static com.example.statusia.Fragments.Images.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.statusia.Activitys.OpenImageActivity;
import com.example.statusia.Activitys.OpenVideoActivity;
import com.example.statusia.R;

import java.util.ArrayList;

public class SavedViewAdapter extends RecyclerView.Adapter<SavedViewAdapter.ViewHolder> {
    Context sContext;
    ArrayList<String> sArrayList;

    public SavedViewAdapter(Context context, ArrayList<String> sArrayList){
        this.sContext = context;
        this.sArrayList = sArrayList;
    }

    @NonNull
    @Override
    public SavedViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(sContext);
        View view = inflater.inflate(R.layout.saved_view_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedViewAdapter.ViewHolder holder, int position) {
        Glide.with(sContext)
                .load(sArrayList.get(position))
                .centerCrop()
                .thumbnail(Glide.with(sContext).load(R.drawable.loading))
                .into(holder.savedImg);

        holder.savedImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                String sPath = sArrayList.get(holder.getBindingAdapterPosition());
                if(sPath.endsWith(".jpg") || sPath.endsWith(".jpeg") || sPath.endsWith(".png")) {
                    intent = new Intent(sContext, OpenImageActivity.class);
                }
                else{
                    intent = new Intent(sContext,OpenVideoActivity.class);
                }
                intent.putExtra("URI",sArrayList.get(holder.getBindingAdapterPosition()));
                intent.putExtra("Fragment","Saved");
                ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation(activity,holder.savedImg,"robot");

                sContext.startActivity(intent,options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return sArrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView savedImg;
        ImageView savedVideoPlayBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            savedImg = itemView.findViewById(R.id.savedImg);
            savedVideoPlayBtn = itemView.findViewById(R.id.savedVideoPlayBtn);
        }
    }
}


