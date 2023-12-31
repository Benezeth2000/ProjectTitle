package com.getjobtzben.projecttitle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.getjobtzben.projecttitle.R;
import com.getjobtzben.projecttitle.model.Project;

import java.util.List;

public class MainScreenAdapter extends RecyclerView.Adapter<MainScreenAdapter.ScreenViewHolder> {
    private Context mContext;
    private List<Project> mProjects;

    public MainScreenAdapter(Context mContext, List<Project> mProjects) {
        this.mContext = mContext;
        this.mProjects = mProjects;
    }

    @NonNull
    @Override
    public MainScreenAdapter.ScreenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_screen_items, parent, false);
        return new ScreenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainScreenAdapter.ScreenViewHolder holder, int position) {
        Project uploadCurrent = mProjects.get(position);
        holder.title.setText(uploadCurrent.getTitle());

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL); // Cache the original image and resized versions

        Glide.with(mContext)
                .load(uploadCurrent.getImage())
                .apply(requestOptions)
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mProjects.size();
    }

    public static class ScreenViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title;

        public ScreenViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.project_images);
            title = itemView.findViewById(R.id.project_title);
        }
    }
}
